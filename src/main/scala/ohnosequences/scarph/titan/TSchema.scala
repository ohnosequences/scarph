package ohnosequences.scarph.titan

import ohnosequences.scarph._
import com.thinkaurelius.titan.core._
import ohnosequences.typesets._
import shapeless._, poly._

object TSchema {

  abstract class ArityMaker[ET <: AnyEdgeType](et: ET) {
    def apply: (LabelMaker => LabelMaker)
  }

  object ArityMaker {
    implicit def manyToManyMaker[ET <: ManyIn with ManyOut](et: ET): ArityMaker[ET] = 
      new ArityMaker[ET](et) { def apply = _.manyToMany }
    implicit def  manyToOneMaker[ET <: ManyIn with OneOut](et: ET): ArityMaker[ET] = 
      new ArityMaker[ET](et) { def apply = _.manyToOne }
    implicit def  oneToManyMaker[ET <: OneIn with ManyOut](et: ET): ArityMaker[ET] = 
      new ArityMaker[ET](et) { def apply = _.oneToMany }
    implicit def   oneToOneMaker[ET <: OneIn with OneOut](et: ET): ArityMaker[ET] = 
      new ArityMaker[ET](et) { def apply = _.oneToOne }
  }

  // object mkKey extends Poly2 {
  //   implicit def default[P <: AnyProperty](implicit c: ClassTag[P#Raw]) = 
  //     at[TitanGraph, P]{ (g, p) => g.addPropertyKey(p); p }
  // }

  // object mkLabel extends Poly2 {
  //   implicit def default[E <: AnyEdge](implicit ar: E#Tpe => ArityMaker[E#Tpe]) = 
  //     at[TitanGraph, E]{ (g, e) => g.addEdgeLabel(e); e }
  // }

  implicit def tSchemaOps(g: TitanGraph): TSchemaOps = TSchemaOps(g)
  case class   TSchemaOps(g: TitanGraph) {

    import scala.reflect._

    // TODO: add uniqueness and indexing parameters
    def addPropertyKey[P <: AnyProperty](p: P)(implicit c: ClassTag[P#Raw]): TitanKey = {
      val clazz = c.runtimeClass.asInstanceOf[Class[p.Raw]]
      g.makeKey(p.label).dataType(clazz).single.make
    }

    // TODO: add sortKey/signature parameters
    def addEdgeLabel[E <: AnyEdge](e: E)(implicit arityMaker: E#Tpe => ArityMaker[E#Tpe]): TitanLabel = {
        val arity = arityMaker(e.tpe)
        arity.apply(g.makeLabel(e.tpe.label).directed).make
    }

    object mkKey extends Poly1 {
      implicit def default[P <: AnyProperty](implicit c: ClassTag[P#Raw]) = 
        at[P]{ p => g.addPropertyKey(p) }
    }

    object mkLabel extends Poly1 {
      implicit def default[E <: AnyEdge](implicit arityMaker: E#Tpe => ArityMaker[E#Tpe]) = 
        at[E]{ e => g.addEdgeLabel(e) }
    }

    def createSchema[S <: AnySchema](s: S)(implicit
        pm: ListMapper[mkKey.type, s.PropertyTypes],
        em: ListMapper[mkLabel.type, s.EdgeTypes]
      ): TitanGraph = {
        s.propertyTypes.mapList(mkKey)
        s.edgeTypes.mapList(mkLabel)
        g
      }
  }
}
