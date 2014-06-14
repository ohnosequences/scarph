package ohnosequences.scarph.titan

import ohnosequences.scarph._

trait AnyTVertex extends AnyVertex { tvertex =>

  final type Raw = com.thinkaurelius.titan.core.TitanVertex

  /* Getting a property from any TitanVertex */
  implicit def unsafeGetProperty[P <: AnyProperty: Property.Of[this.Tpe]#is](p: P) = 
    new GetProperty[P](p) {
      def apply(rep: Rep): p.Raw = rep.getProperty[p.Raw](p.label)
    }

  // TODO: provide ReadFrom for %:

  /* Retrieving edges */
  import com.tinkerpop.blueprints.Direction
  import scala.collection.JavaConversions._

  // TODO: when we get all edges with the given label, they can come from vertices with the wrong type

  /* OUT */
  implicit def unsafeRetrieveOneOutEdge[
    E <: Singleton with AnyTEdge { type Tpe <: From[tvertex.Tpe] with OneOut }
  ](e: E): RetrieveOutEdge[E] = new RetrieveOutEdge[E](e) {

      def apply(rep: tvertex.Rep): e.tpe.Out[e.Rep] = {
        
        val it = rep.getEdges(Direction.OUT, e.tpe.label).asInstanceOf[java.lang.Iterable[e.Rep]]
        it.headOption: Option[e.Rep]
      }
    }

  implicit def unsafeRetrieveManyOutEdge[
    E <: Singleton with AnyTEdge { type Tpe <: From[tvertex.Tpe] with ManyOut }
  ](e: E): RetrieveOutEdge[E] = new RetrieveOutEdge[E](e) {

      def apply(rep: tvertex.Rep): e.tpe.Out[e.Rep] = {
        val it = rep.getEdges(Direction.OUT, e.tpe.label).asInstanceOf[java.lang.Iterable[e.Rep]]
        it.toList: List[e.Rep]
      }
    }

  /* IN */
  implicit def unsafeRetrieveOneInEdge[
    E <: Singleton with AnyTEdge { type Tpe <: To[tvertex.Tpe] with OneIn }
  ](e: E): RetrieveInEdge[E] = new RetrieveInEdge[E](e) {

      def apply(rep: tvertex.Rep): e.tpe.In[e.Rep] = {
        val it = rep.getEdges(Direction.IN, e.tpe.label).asInstanceOf[java.lang.Iterable[e.Rep]]
        it.headOption: Option[e.Rep]
      }
    }

  implicit def unsafeRetrieveManyInEdge[
    E <: Singleton with AnyTEdge { type Tpe <: To[tvertex.Tpe] with ManyIn }
  ](e: E): RetrieveInEdge[E] = new RetrieveInEdge[E](e) {

      def apply(rep: tvertex.Rep): e.tpe.In[e.Rep] = {
        val it = rep.getEdges(Direction.IN, e.tpe.label).asInstanceOf[java.lang.Iterable[e.Rep]]
        it.toList: List[e.Rep]
      }
    }

}

class TVertex[VT <: AnyVertexType](val tpe: VT) 
  extends AnyTVertex { type Tpe = VT }
