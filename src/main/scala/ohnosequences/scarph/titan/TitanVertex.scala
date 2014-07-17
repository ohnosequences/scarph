package ohnosequences.scarph.titan

import ohnosequences.scarph._

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
  import java.lang.{Iterable => jIterable}

  implicit def cnvrtOpt[T](it: jIterable[T]): Option[T] = iterableAsScalaIterable(it).headOption 
  implicit def cnvrtLst[T](it: jIterable[T]): List[T] = iterableAsScalaIterable(it).toList

  /* OUT */
  implicit def unsafeGetOneOutEdge[
    E <: Singleton with AnyTitanEdge { type Tpe <: From[tvertex.Tpe] with OneOut }
  ](e: E): GetOutEdge[E] = new GetOutEdge[E](e) {

      def apply(rep: tvertex.Rep): e.tpe.Out[e.Rep] = {
        rep.getEdges(Direction.OUT, e.tpe.label).asInstanceOf[jIterable[e.Rep]]
      }
    }

  implicit def unsafeGetManyOutEdge[
    E <: Singleton with AnyTitanEdge { type Tpe <: From[tvertex.Tpe] with ManyOut }
  ](e: E): GetOutEdge[E] = new GetOutEdge[E](e) {

      def apply(rep: tvertex.Rep): e.tpe.Out[e.Rep] = {
        rep.getEdges(Direction.OUT, e.tpe.label).asInstanceOf[jIterable[e.Rep]]
      }
    }

  /* IN */
  implicit def unsafeGetOneInEdge[
    E <: Singleton with AnyTitanEdge { type Tpe <: To[tvertex.Tpe] with OneIn }
  ](e: E): GetInEdge[E] = new GetInEdge[E](e) {

      def apply(rep: tvertex.Rep): e.tpe.In[e.Rep] = {
        rep.getEdges(Direction.IN, e.tpe.label).asInstanceOf[jIterable[e.Rep]]
      }
    }

  implicit def unsafeGetManyInEdge[
    E <: Singleton with AnyTitanEdge { type Tpe <: To[tvertex.Tpe] with ManyIn }
  ](e: E): GetInEdge[E] = new GetInEdge[E](e) {

      def apply(rep: tvertex.Rep): e.tpe.In[e.Rep] = {
        rep.getEdges(Direction.IN, e.tpe.label).asInstanceOf[jIterable[e.Rep]]
      }
    }

}

class TitanVertex[VT <: AnyVertexType](val tpe: VT) 
  extends AnyTitanVertex { type Tpe = VT }

object AnyTitanVertex {
  type ofType[VT <: AnyVertexType] = AnyTitanVertex { type Tpe = VT }
}