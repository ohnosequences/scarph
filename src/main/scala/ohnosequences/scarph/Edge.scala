package ohnosequences.scarph

import ohnosequences.typesets._

trait AnyEdge extends Denotation[AnyEdgeType] with CanGetProperties { edge =>

  // NOTE: if I remove this from here type inference fails. Most likely a bug
  type Tpe <: AnyEdgeType

  type Source <: AnyVertex.ofType[Tpe#SourceType]
  val  source: Source

  type Target <: AnyVertex.ofType[Tpe#TargetType]
  val  target: Target

  /* Get source/target from this representation */
  abstract class GetSource {
    type Out = source.Rep
    def apply(edgeRep: edge.Rep): Out
  }
  abstract class GetTarget { 
    type Out = target.Rep 
    def apply(edgeRep: edge.Rep): Out
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

  type RepOf[E <: Singleton with AnyEdge] = AnyTag.TaggedWith[E]
}

trait AnySealedEdge extends AnyEdge { sealedEdge =>
  
  type Tpe <: AnySealedEdgeType

  final type Raw = raw
  type Other

  case class raw(val fields: tpe.record.Rep, val other: Other)
  // double tagging FTW!
  final def fields[R <: TypeSet](r: R)(implicit
    p: R ~> tpe.record.Raw
  ): tpe.record.Rep = ( tpe.record ->> p(r) )

  implicit def propertyOps(rep: sealedEdge.Rep): tpe.record.PropertyOps = tpe.record.PropertyOps(rep.fields) 
}

abstract class SealedEdge [
  S <: AnyVertex.ofType[ET#SourceType],
  ET <: AnySealedEdgeType,
  T <: AnyVertex.ofType[ET#TargetType]
](
  val source: S,
  val tpe: ET,
  val target: T
) 
extends AnySealedEdge { 

  type Source = S
  type Tpe = ET 
  type Target = T
}