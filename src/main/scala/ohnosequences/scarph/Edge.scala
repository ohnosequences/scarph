package ohnosequences.scarph

trait AnyEdge extends Denotation[AnyEdgeType] with PropertyGetters { edge =>

  // NOTE: if I remove this from here type inference fails. Most likely a bug
  type Tpe <: AnyEdgeType

  type Source <: AnyVertex.ofType[Tpe#SourceType]
  val source: Source

  type Target <: AnyVertex.ofType[Tpe#TargetType]
  val target: Target

  /* Get source/target from this representation */
  abstract class GetSource[S <: AnyVertex.ofType[Tpe#SourceType]](val source: S) {
    def apply(edgeRep: edge.Rep): source.Rep
  }
  abstract class GetTarget {
    def apply(edgeRep: edge.Rep): target.Rep
  }

}

class Edge[
    S <: AnyVertex.ofType[ET#SourceType], 
    ET <: AnyEdgeType, 
    T <: AnyVertex.ofType[ET#TargetType]
  ](val source: S, val tpe: ET, val target: T) extends AnyEdge { 
    type Source = S
    type Tpe = ET 
    type Target = T
  }

object AnyEdge {
  import AnyEdgeType._

  type ofType[ET <: AnyEdgeType] = AnyEdge { type Tpe = ET }

  type -->[S <: AnyVertexType, T <: AnyVertexType] = AnyEdge { type Tpe <: S ==> T }
}

object Edge {
  type RepOf[E <: Singleton with AnyEdge] = AnyDenotation.TaggedWith[E]
}
