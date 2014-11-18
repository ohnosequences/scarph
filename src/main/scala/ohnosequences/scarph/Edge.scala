package ohnosequences.scarph

import ohnosequences.cosas._, AnyWrap._, AnyTypeSet._, AnyFn._
import AnyEdgeType._, AnyEdge._

trait AnyEdge extends AnyElementOf[AnyEdgeType] {

  // NOTE: if I remove this from here type inference fails. Most likely a bug
  type Tpe <: AnyEdgeType

  type Source <: AnyVertex.ofType[SourceTypeOf[Tpe]]
  val  source: Source

  type Target <: AnyVertex.ofType[TargetTypeOf[Tpe]]
  val  target: Target
}

abstract class Edge[
  S <: AnyVertex.ofType[SourceTypeOf[ET]],
  ET <: AnyEdgeType, 
  T <: AnyVertex.ofType[TargetTypeOf[ET]]
](val  source : S, val  tpe : ET, val  target : T) extends AnyEdge { 
  type Source = S; type Tpe = ET; type Target = T
}

object AnyEdge {

  import AnyEdgeType._

  type withSource[S <: AnyVertex] = AnyEdge { type Source = S }
  type withTarget[T <: AnyVertex] = AnyEdge { type Target = T }

  type ofType[ET <: AnyEdgeType] = AnyEdge { type Tpe = ET }

  type EdgeTypeOf[E <: AnyEdge] = E#Tpe
  type SourceOf[E <: AnyEdge] = E#Source
  type TargetOf[E <: AnyEdge] = E#Target

  type -->[S <: AnyVertexType, T <: AnyVertexType] = AnyEdge { type Tpe <: S ==> T }
}
