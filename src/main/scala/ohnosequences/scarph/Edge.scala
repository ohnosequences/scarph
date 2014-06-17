package ohnosequences.scarph

trait AnyEdge extends Denotation[AnyEdgeType] with PropertyGetters { edge =>

  // NOTE: if I remove this from here type inference fails. Most likely a bug
  type Tpe <: AnyEdgeType

  /* Get source/target from this representation */
  abstract class GetSource[S <: AnyVertex.ofType[Tpe#SourceType]](val source: S) {
    def apply(edgeRep: edge.Rep): source.Rep
  }
  abstract class GetTarget[T <: AnyVertex.ofType[Tpe#TargetType]](val target: T) {
    def apply(edgeRep: edge.Rep): target.Rep
  }

}

class Edge[ET <: AnyEdgeType](val tpe: ET) 
  extends AnyEdge { type Tpe = ET }

object AnyEdge {
  import AnyEdgeType._

  type ofType[ET <: AnyEdgeType] = AnyEdge { type Tpe = ET }

  type -->[S <: AnyVertexType, T <: AnyVertexType] = AnyEdge { type Tpe <: S ==> T }
}

object Edge {
  type RepOf[E <: Singleton with AnyEdge] = AnyDenotation.TaggedWith[E]
}
