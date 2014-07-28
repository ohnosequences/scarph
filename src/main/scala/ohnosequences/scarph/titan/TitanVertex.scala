package ohnosequences.scarph.titan

import ohnosequences.scarph._
import ohnosequences.typesets._

trait AnyTitanVertex extends AnyVertex { tvertex =>

  final type Raw = com.thinkaurelius.titan.core.TitanVertex

  /* Getting a property from any TitanVertex */
  implicit def unsafeGetProperty[P <: Singleton with AnyProperty: Property.Of[this.Tpe]#is](p: P) = 
    new PropertyGetter[P](p) {
      def apply(rep: Rep): p.Raw = rep.getProperty[p.Raw](p.label)
    }

  // TODO: provide ReadFrom for %:

  /* Retrieving edges */
  import com.tinkerpop.blueprints.Direction
  import scala.collection.JavaConversions._

  // TODO: when we get all edges with the given label, they can come from vertices with the wrong type

  /* OUT */
  implicit def unsafeGetOneOutEdge[
    E <: Singleton with AnyTitanEdge { type Tpe <: From[tvertex.Tpe] with OneOut }
  ](e: E): GetOutEdge[E] = new GetOutEdge[E](e) {

      def apply(rep: tvertex.Rep): e.tpe.Out[e.Rep] = {
        
        val it = rep.getEdges(Direction.OUT, e.tpe.label).asInstanceOf[java.lang.Iterable[e.Rep]]
        it.headOption: Option[e.Rep]
      }
    }

  implicit def unsafeGetManyOutEdge[
    E <: Singleton with AnyTitanEdge { type Tpe <: From[tvertex.Tpe] with ManyOut }
  ](e: E): GetOutEdge[E] = new GetOutEdge[E](e) {

      def apply(rep: tvertex.Rep): e.tpe.Out[e.Rep] = {
        val it = rep.getEdges(Direction.OUT, e.tpe.label).asInstanceOf[java.lang.Iterable[e.Rep]]
        it.toList: List[e.Rep]
      }
    }

  /* IN */
  implicit def unsafeGetOneInEdge[
    E <: Singleton with AnyTitanEdge { type Tpe <: To[tvertex.Tpe] with OneIn }
  ](e: E): GetInEdge[E] = new GetInEdge[E](e) {

      def apply(rep: tvertex.Rep): e.tpe.In[e.Rep] = {
        val it = rep.getEdges(Direction.IN, e.tpe.label).asInstanceOf[java.lang.Iterable[e.Rep]]
        it.headOption: Option[e.Rep]
      }
    }

  implicit def unsafeGetManyInEdge[
    E <: Singleton with AnyTitanEdge { type Tpe <: To[tvertex.Tpe] with ManyIn }
  ](e: E): GetInEdge[E] = new GetInEdge[E](e) {

      def apply(rep: tvertex.Rep): e.tpe.In[e.Rep] = {
        val it = rep.getEdges(Direction.IN, e.tpe.label).asInstanceOf[java.lang.Iterable[e.Rep]]
        it.toList: List[e.Rep]
      }
    }

}

class TitanVertex[VT <: AnyVertexType](val tpe: VT) 
  extends AnyTitanVertex { type Tpe = VT }

object AnyTitanVertex {
  type ofType[VT <: AnyVertexType] = AnyTitanVertex { type Tpe = VT }
}
