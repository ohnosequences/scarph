package ohnosequences.scarph.titan

import ohnosequences.scarph._
import com.thinkaurelius.titan.core._
import ohnosequences.typesets._
import shapeless._, poly._
import scala.reflect._

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

  implicit def tSchemaOps(g: TitanGraph): TSchemaOps = TSchemaOps(g)
  case class   TSchemaOps(g: TitanGraph) {

    // TODO: add uniqueness and indexing parameters
    def addPropertyKey[P <: AnyProperty](p: P)(implicit c: ClassTag[P#Raw]) = {
      val clazz = c.runtimeClass.asInstanceOf[Class[p.Raw]]
      // FIXME: WARNING! This try-catch is needed only because in the gods-graph test some keys are repeated (remove this after changing the test)
      try {g.makeKey(p.label).dataType(clazz).single.make}
      catch { case _: java.lang.IllegalArgumentException => {} }
    }

    // TODO: add sortKey/signature parameters
    def addEdgeLabel[ET <: AnyEdgeType](et: ET)(implicit arityMaker: ET => ArityMaker[ET]) = {
        val arity = arityMaker(et)
        // FIXME: WARNING! This try-catch is needed only because in the gods-graph test some keys are repeated (remove this after changing the test)
        try {arity.apply(g.makeLabel(et.label).directed).make}
        catch { case _: java.lang.IllegalArgumentException => {} }
    }

    def createSchema[S <: AnySchema](s: S)(implicit
        mkPropertyKeys: MkPropertyKeys[s.PropertyTypes],
        mkEdgeLabels: MkEdgeLabels[s.EdgeTypes]
      ): TitanGraph = {
        mkPropertyKeys(g, s.propertyTypes)
        mkEdgeLabels(g, s.edgeTypes)
        g
      }
  }
}

trait MkPropertyKeys[Ps <: TypeSet] {
  def apply(g: TitanGraph, ps: Ps): TitanGraph
}

object MkPropertyKeys {
  import TSchema._

  def apply[Ps <: TypeSet](implicit mk: MkPropertyKeys[Ps]): MkPropertyKeys[Ps] = mk

  implicit val emptyMkKeys: MkPropertyKeys[∅] =
    new MkPropertyKeys[∅] {
      def apply(g: TitanGraph, ps: ∅) = g
    }

  implicit def consMkKeys[H <: AnyProperty, T <: TypeSet]
    (implicit 
      c: ClassTag[H#Raw], 
      t: MkPropertyKeys[T]
    ):  MkPropertyKeys[H :~: T] =
    new MkPropertyKeys[H :~: T] {
      def apply(g: TitanGraph, ps: H :~: T) = {
        g.addPropertyKey(ps.head)
        t(g, ps.tail)
        g
      }
    }
}

trait MkEdgeLabels[Es <: TypeSet] {
  def apply(g: TitanGraph, es: Es): TitanGraph
}

object MkEdgeLabels {
  import TSchema._

  def apply[Es <: TypeSet](implicit mk: MkEdgeLabels[Es]): MkEdgeLabels[Es] = mk

  implicit val emptyMkKeys: MkEdgeLabels[∅] =
    new MkEdgeLabels[∅] {
      def apply(g: TitanGraph, es: ∅) = g
    }

  implicit def consMkKeys[H <: AnyEdgeType, T <: TypeSet]
    (implicit 
      a: H => ArityMaker[H], 
      t: MkEdgeLabels[T]
    ):  MkEdgeLabels[H :~: T] =
    new MkEdgeLabels[H :~: T] {
      def apply(g: TitanGraph, es: H :~: T) = {
        g.addEdgeLabel(es.head)
        t(g, es.tail)
        g
      }
    }
}
