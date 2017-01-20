package ohnosequences.scarph.impl

import ohnosequences.scarph._

trait Relations {

  type RawEdge
  type RawSource
  type RawTarget

  def outVRaw(edge: AnyEdge)(v: RawSource): RawTarget
  def inVRaw(edge: AnyEdge)(v: RawTarget): RawSource

  def outERaw(edge: AnyEdge)(v: RawSource): RawEdge
  def sourceRaw(edge: AnyEdge)(e: RawEdge): RawSource

  def inERaw(edge: AnyEdge)(v: RawTarget): RawEdge
  def targetRaw(edge: AnyEdge)(e: RawEdge): RawTarget


  implicit final def eval_outV[
    E <: AnyEdge
  ]:  Eval[RawSource, outV[E], RawTarget] =
  new Eval[RawSource, outV[E], RawTarget] {

    def rawApply(morph: InMorph): InVal => OutVal = outVRaw(morph.relation)

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }

  implicit final def eval_inV[
    E <: AnyEdge
  ]:  Eval[RawTarget, inV[E], RawSource] =
  new Eval[RawTarget, inV[E], RawSource] {

    def rawApply(morph: InMorph): InVal => OutVal = inVRaw(morph.relation)

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }


  implicit final def eval_outE[
    E <: AnyEdge
  ]:  Eval[RawSource, outE[E], RawEdge] =
  new Eval[RawSource, outE[E], RawEdge] {

    def rawApply(morph: InMorph): InVal => OutVal = outERaw(morph.relation)

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }

  implicit final def eval_source[
    E <: AnyEdge
  ]:  Eval[RawEdge, source[E], RawSource] =
  new Eval[RawEdge, source[E], RawSource] {

    def rawApply(morph: InMorph): InVal => OutVal = sourceRaw(morph.relation)

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }


  implicit final def eval_inE[
    E <: AnyEdge
  ]:  Eval[RawTarget, inE[E], RawEdge] =
  new Eval[RawTarget, inE[E], RawEdge] {

    def rawApply(morph: InMorph): InVal => OutVal = inERaw(morph.relation)

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }

  implicit final def eval_target[
    E <: AnyEdge
  ]:  Eval[RawEdge, target[E], RawTarget] =
  new Eval[RawEdge, target[E], RawTarget] {

    def rawApply(morph: InMorph): InVal => OutVal = targetRaw(morph.relation)

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }

}
