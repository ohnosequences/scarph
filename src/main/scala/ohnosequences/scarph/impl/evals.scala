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

  def present(morph: InMorph): Seq[String]
}

@annotation.implicitNotFound(msg = "Cannot evaluate morphism ${M} on input ${I}, output ${O}")
trait Eval[I, M <: AnyGraphMorphism, O] extends AnyEval {

  type InMorph = M
  type RawInput = I
  type RawOutput = O
}

final class evaluate[I, M <: AnyGraphMorphism, O](
  val f: M,
  val eval: Eval[I, M, O]
) {

  final def on(input: M#In := I): M#Out := O = eval(f).apply(input)

  // FIXME: this should output the computational behavior of the eval here
  final def evalPlan: String = eval.present(f).mkString("")
}

class evalWithIn[I, IM <: AnyGraphMorphism] {

  def apply[O](m: IM)(implicit
    eval: Eval[I, IM, O]
  ):  evaluate[I, IM, O] =
  new evaluate[I, IM, O](m, eval)
}

class evalWithInOut[I, IM <: AnyGraphMorphism, O] {

  def apply(m: IM)(implicit
    eval: Eval[I, IM, O]
  ):  evaluate[I, IM, O] =
  new evaluate[I, IM, O](m, eval)
}


case object evaluate {

  def apply[I, IM <: AnyGraphMorphism, O](m: IM)(i: IM#In := I)(implicit eval: Eval[I, IM, O]): IM#Out := O =
    new evaluate[I, IM, O](m, eval).on(i)

  def withIn[I, M <: AnyGraphMorphism]: evalWithIn[I,M] =
    new evalWithIn[I, M] {}

  def withInOut[I, IM <: AnyGraphMorphism, O]: evalWithInOut[I, IM, O] =
    new evalWithInOut[I, IM, O] {}

}
