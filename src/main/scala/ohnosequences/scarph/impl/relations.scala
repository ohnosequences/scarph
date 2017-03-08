package ohnosequences.scarph.impl

import ohnosequences.scarph._

trait Relations {

  type RawEdge
  type RawSource
  type RawTarget

  def raw_outV(edge: AnyEdge)(v: RawSource): RawTarget
  def raw_inV(edge: AnyEdge)(v: RawTarget): RawSource

  def raw_outE(edge: AnyEdge)(v: RawSource): RawEdge
  def raw_source(edge: AnyEdge)(e: RawEdge): RawSource

  def raw_inE(edge: AnyEdge)(v: RawTarget): RawEdge
  def raw_target(edge: AnyEdge)(e: RawEdge): RawTarget


  implicit final def eval_outV[E <: AnyEdge]: Eval[outV[E], RawSource, RawTarget] = new Eval( morph => raw_outV(morph.relation) )
  implicit final def eval_inV[E <: AnyEdge]:  Eval[inV[E],  RawTarget, RawSource] = new Eval( morph => raw_inV(morph.relation) )

  implicit final def eval_inE[E <: AnyEdge]:  Eval[inE[E],  RawTarget, RawEdge] = new Eval( morph => raw_inE(morph.relation) )
  implicit final def eval_outE[E <: AnyEdge]: Eval[outE[E], RawSource, RawEdge] = new Eval( morph => raw_outE(morph.relation) )

  implicit final def eval_source[E <: AnyEdge]: Eval[source[E], RawEdge, RawSource] = new Eval( morph => raw_source(morph.relation) )
  implicit final def eval_target[E <: AnyEdge]: Eval[target[E], RawEdge, RawTarget] = new Eval( morph => raw_target(morph.relation) )

}
