package ohnosequences.scarph.impl

import ohnosequences.scarph._
import ohnosequences.cosas._, types._

/* Transforms a morphism to a function */
trait AnyEval extends AnyMorphismTransform {

  type RawInput
  type RawOutput

  def raw_apply(morph: InMorph): RawInput => RawOutput

  // Same but with tags:
  type Input  = InMorph#In  := RawInput
  type Output = InMorph#Out := RawOutput

  type OutMorph = Input => Output

  final def apply(morph: InMorph): OutMorph = { input: Input =>
    (morph.out: InMorph#Out) := raw_apply(morph)(input.value)
  }

  def present(morph: InMorph): Seq[String] = Seq(morph.label)
}

@annotation.implicitNotFound(msg = "Cannot evaluate morphism ${M} on input ${I}, output ${O}")
case class Eval[M <: AnyGraphMorphism, I, O](
  val raw: M => (I => O)
) extends AnyEval {

  type InMorph = M
  type RawInput = I
  type RawOutput = O

  final def raw_apply(morph: InMorph): RawInput => RawOutput = raw(morph)
}

final class evaluate[M <: AnyGraphMorphism, I, O](
  val f: M,
  val eval: Eval[M, I, O]
) {

  final def on(input: M#In := I): M#Out := O = eval(f).apply(input)

  // FIXME: this should output the computational behavior of the eval here
  final def evalPlan: String = eval.present(f).mkString("")
}

class evalWithIn[M <: AnyGraphMorphism, I] {

  def apply[O](m: M)(implicit
    eval: Eval[M, I, O]
  ):  evaluate[M, I, O] =
  new evaluate[M, I, O](m, eval)
}

class evalWithInOut[M <: AnyGraphMorphism, I, O] {

  def apply(m: M)(implicit
    eval: Eval[M, I, O]
  ):  evaluate[M, I, O] =
  new evaluate[M, I, O](m, eval)
}


case object evaluate {

  def apply[M <: AnyGraphMorphism, I, O](m: M)(i: M#In := I)
    (implicit eval: Eval[M, I, O]): M#Out := O =
      new evaluate[M, I, O](m, eval).on(i)

  def withIn[M <: AnyGraphMorphism, I]:
      evalWithIn[M, I] =
  new evalWithIn[M, I] {}

  def withInOut[M <: AnyGraphMorphism, I, O]:
      evalWithInOut[M, I, O] =
  new evalWithInOut[M, I, O] {}

}
