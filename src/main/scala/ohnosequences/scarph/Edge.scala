package ohnosequences.scarph

trait AnyEdge extends Denotation[AnyEdgeType] with CanHaveProperties { edge =>

  // NOTE: if I remove this from here type inference fails. Most likely a bug
  type Tpe <: AnyEdgeType

  /* Get source/target from this representation */
  abstract class GetSource[S <: AnyVertex.ofType[Tpe#SourceType]](val source: S) {
    def apply(edgeRep: edge.Rep): source.Rep
  }
  abstract class GetTarget[T <: AnyVertex.ofType[Tpe#TargetType]](val target: T) {
    def apply(edgeRep: edge.Rep): target.Rep
  }

  implicit def edgeOps(edgeRep: edge.Rep) = EdgeOps(edgeRep)
  case class   EdgeOps(edgeRep: edge.Rep) {

    def source[S <: Singleton with AnyVertex.ofType[Tpe#SourceType]](implicit getter: GetSource[S]) = getter(edgeRep)

    def target[T <: Singleton with AnyVertex.ofType[Tpe#TargetType]](implicit getter: GetTarget[T]) = getter(edgeRep)
  }

}

class Edge[ET <: AnyEdgeType](val tpe: ET) 
  extends AnyEdge { type Tpe = ET }

object AnyEdge {
  import AnyEdgeType._

  type -->[S <: AnyVertexType, T <: AnyVertexType] = AnyEdge { type Tpe <: S ==> T }
}
