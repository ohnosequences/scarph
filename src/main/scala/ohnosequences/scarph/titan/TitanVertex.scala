package ohnosequences.scarph.titan

import ohnosequences.scarph._
import ohnosequences.pointless._, AnyTaggedType._

trait AnyTitanVertex extends AnyVertex { tvertex =>

  final type Raw = com.thinkaurelius.titan.core.TitanVertex

  /* Getting a property from any TitanVertex */
  implicit def unsafeGetProperty[P <: AnyProperty: AnyProperty.Of[Tpe]#is](p: P) = 
    new PropertyGetter[P](p) {
      def apply(rep: Tagged[Me]): RawOf[P] = rep.getProperty[RawOf[P]](p.label)
    }

  // TODO: provide ReadFrom for %:

  /* Retrieving edges */
  import com.tinkerpop.blueprints.Direction
  import scala.collection.JavaConversions._

  // TODO: when we get all edges with the given label, they can come from vertices with the wrong type

  /* OUT */
  implicit def unsafeGetOneOutEdge [
    E <: Singleton with AnyTitanEdge { type Tpe <: From[tvertex.Tpe] with OneOut }
  ]
  (e: E): GetOutEdge[E] = new GetOutEdge[E](e) {

      def apply(rep: Tagged[Me]): E#Tpe#Out[Tagged[E]] = {
        
        val it = rep.getEdges( Direction.OUT, e.tpe.label )
          .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]

        it.headOption map { (e:E) =>> _ }
      }
    }

  implicit def unsafeGetManyOutEdge [
    OE <: AnyTitanEdge { type Tpe <: From[tvertex.Tpe] with ManyOut }
  ]
  (edge: OE)
  : GetOutEdge[OE] = new GetOutEdge[OE](edge) {

      def apply(rep: Tagged[Me]): OE#Tpe#Out[Tagged[OE]] = {

        val it = rep.getEdges( Direction.OUT, edge.tpe.label )
          .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]

        it.toList map { (edge: OE) =>> _ }
      }
    }

  /* IN */
  implicit def unsafeGetOneInEdge [
    IE <: Singleton with AnyTitanEdge { type Tpe <: To[tvertex.Tpe] with OneIn }
  ]
  (edge: IE): GetInEdge[IE] = new GetInEdge[IE](edge) {

      def apply(rep: Tagged[Me]): IE#Tpe#In[Tagged[IE]] = {

        val it = rep.getEdges(Direction.IN, edge.tpe.label)
          .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]

        it.headOption map { (edge: IE) =>> _ }
      }
    }

  implicit def unsafeGetManyInEdge [
    IE <: Singleton with AnyTitanEdge { type Tpe <: To[tvertex.Tpe] with ManyIn }
  ]
  (edge: IE): GetInEdge[IE] = new GetInEdge[IE](edge) {
        
      def apply(rep: Tagged[Me]): edge.tpe.In[Tagged[IE]] = {

        val it = rep.getEdges( Direction.IN, edge.tpe.label )
          .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]

        it.toList map { (edge: IE) =>> _ }
      }
    }

}

class TitanVertex[VT <: AnyVertexType](val tpe: VT) 
  extends AnyTitanVertex { type Tpe = VT }

object AnyTitanVertex {
  type ofType[VT <: AnyVertexType] = AnyTitanVertex { type Tpe = VT }
}
