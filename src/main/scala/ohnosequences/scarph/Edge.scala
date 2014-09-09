package ohnosequences.scarph

import ohnosequences.pointless._, AnyWrap._, AnyTypeSet._, AnyFn._
import AnyEdgeType._, AnyEdge._

trait AnyEdge extends Denotation[AnyEdgeType] {

  // NOTE: if I remove this from here type inference fails. Most likely a bug
  type DenotedType <: AnyEdgeType

  type Source <: AnyVertex.ofType[SourceTypeOf[DenotedType]]
  val  source: Source

  type Target <: AnyVertex.ofType[TargetTypeOf[DenotedType]]
  val  target: Target
}

abstract class Edge[
  S <: AnyVertex.ofType[SourceTypeOf[ET]],
  ET <: AnyEdgeType, 
  T <: AnyVertex.ofType[TargetTypeOf[ET]]
](val  source : S, val  denotedType : ET, val  target : T) extends AnyEdge { 
  type Source = S; type DenotedType = ET; type Target = T
}

object AnyEdge {

  import AnyEdgeType._

  type withSource[S <: AnyVertex] = AnyEdge { type Source = S }
  type withTarget[T <: AnyVertex] = AnyEdge { type Target = T }

  type ofType[ET <: AnyEdgeType] = AnyEdge { type DenotedType = ET }

  type EdgeTypeOf[E <: AnyEdge] = E#DenotedType
  type SourceOf[E <: AnyEdge] = E#Source
  type TargetOf[E <: AnyEdge] = E#Target

  type -->[S <: AnyVertexType, T <: AnyVertexType] = AnyEdge { type DenotedType <: S ==> T }

  implicit def edgeRawOps[E <: AnyEdge](rep: ValueOf[E]): 
        EdgeRawOps[E] = 
    new EdgeRawOps[E](rep.raw)
}

class EdgeRawOps[E <: AnyEdge](val raw: RawOf[E]) extends AnyVal {
  import ohnosequences.scarph.ops.edge._

  def get[P <: AnyProperty](prop: P)
    (implicit getter: GetProperty[E, P]): ValueOf[P] = getter(raw, prop)

  def src(implicit src: GetSource[E]): ValueOf[SourceOf[E]] = src(raw)

  def tgt(implicit tgt: GetTarget[E]): ValueOf[TargetOf[E]] = tgt(raw)

}
