package ohnosequences.scarph.titan

import ohnosequences.scarph._

trait AnyTVertex extends AnyVertex { tvertex =>

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
  implicit def superAutoManyOutEdges[
    // E <: Singleton with AnyTEdge { type Tpe <: From[tvertex.Tpe] with ManyOut }
    ET <: From[tvertex.Tpe] with ManyOut //, E <: Singleton with TEdge[TVertex[ET#SourceType], ET, TVertex[ET#TargetType]]
  ](et: ET): GetOutEdgeT[ET] = new GetOutEdgeT[ET](et) {

      val src  = new TVertex(et.sourceType)
      val trgt = new TVertex(et.targetType)
      val e = new TEdge[src.type, et.type, trgt.type](src, et, trgt)
      type Rep = e.Rep

      def apply(rep: tvertex.Rep): Out = {
        val it = rep.getEdges(Direction.OUT, et.label)
        it.toList.map{ e ->> _.asInstanceOf[e.Raw] }
      }
    }

  implicit def unsafeGetOneOutEdge[
    E <: Singleton with AnyTEdge { type Tpe <: From[tvertex.Tpe] with OneOut }
  ](e: E): GetOutEdge[E] = new GetOutEdge[E](e) {

      def apply(rep: tvertex.Rep): e.tpe.Out[e.Rep] = {
        val it = rep.getEdges(Direction.OUT, e.tpe.label)
        it.headOption.map{ e ->> _.asInstanceOf[e.Raw] }
      }
    }

  implicit def unsafeGetManyOutEdge[
    E <: Singleton with AnyTEdge { type Tpe <: From[tvertex.Tpe] with ManyOut }
  ](e: E): GetOutEdge[E] = new GetOutEdge[E](e) {

      def apply(rep: tvertex.Rep): e.tpe.Out[e.Rep] = {
        val it = rep.getEdges(Direction.OUT, e.tpe.label)
        it.toList.map{ e ->> _.asInstanceOf[e.Raw] }
      }
    }

  /* IN */
  implicit def unsafeGetOneInEdge[
    E <: Singleton with AnyTEdge { type Tpe <: To[tvertex.Tpe] with OneIn }
  ](e: E): GetInEdge[E] = new GetInEdge[E](e) {

      def apply(rep: tvertex.Rep): e.tpe.In[e.Rep] = {
        val it = rep.getEdges(Direction.IN, e.tpe.label)
        it.headOption.map{ e ->> _.asInstanceOf[e.Raw] }
      }
    }

  implicit def unsafeGetManyInEdge[
    E <: Singleton with AnyTEdge { type Tpe <: To[tvertex.Tpe] with ManyIn }
  ](e: E): GetInEdge[E] = new GetInEdge[E](e) {

      def apply(rep: tvertex.Rep): e.tpe.In[e.Rep] = {
        val it = rep.getEdges(Direction.IN, e.tpe.label)
        it.toList.map{ e ->> _.asInstanceOf[e.Raw] }
      }
    }

}

class TVertex[VT <: AnyVertexType](val tpe: VT) 
  extends AnyTVertex { type Tpe = VT }

object AnyTVertex {
  type ofType[VT <: AnyVertexType] = AnyTVertex { type Tpe = VT }
}
