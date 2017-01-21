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


  implicit final def eval_outV[
    E <: AnyEdge
  ]:  Eval[RawSource, outV[E], RawTarget] =
  new Eval[RawSource, outV[E], RawTarget] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = raw_outV(morph.relation)

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }

  implicit final def eval_inV[
    E <: AnyEdge
  ]:  Eval[RawTarget, inV[E], RawSource] =
  new Eval[RawTarget, inV[E], RawSource] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = raw_inV(morph.relation)

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }


  implicit final def eval_outE[
    E <: AnyEdge
  ]:  Eval[RawSource, outE[E], RawEdge] =
  new Eval[RawSource, outE[E], RawEdge] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = raw_outE(morph.relation)

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }

  implicit final def eval_source[
    E <: AnyEdge
  ]:  Eval[RawEdge, source[E], RawSource] =
  new Eval[RawEdge, source[E], RawSource] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = raw_source(morph.relation)

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }


  implicit final def eval_inE[
    E <: AnyEdge
  ]:  Eval[RawTarget, inE[E], RawEdge] =
  new Eval[RawTarget, inE[E], RawEdge] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = raw_inE(morph.relation)

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }

  implicit final def eval_target[
    E <: AnyEdge
  ]:  Eval[RawEdge, target[E], RawTarget] =
  new Eval[RawEdge, target[E], RawTarget] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = raw_target(morph.relation)

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }

}
