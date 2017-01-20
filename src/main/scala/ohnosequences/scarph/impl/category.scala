package ohnosequences.scarph.impl

import ohnosequences.scarph._

trait DaggerCategory {

  implicit final def eval_id[X <: AnyGraphObject, I]:
      Eval[I, ohnosequences.scarph.id[X], I] =
  new Eval[I, ohnosequences.scarph.id[X], I] {

    def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal => inVal }

    final def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }

  // F >=> S
  implicit final def eval_composition[
    I, X, O,
    F <: AnyGraphMorphism,
    S <: AnyGraphMorphism {
      type In = F#Out
    }
  ](implicit
    evalFirst:  Eval[I, F, X],
    evalSecond: Eval[X, S, O]
  ):  Eval[I, F >=> S, O] =
  new Eval[I, F >=> S, O] {

    def rawApply(morph: InMorph): InVal => OutVal =
      { inVal: InVal =>

        val firstResult = evalFirst.rawApply(morph.first)(inVal)
        evalSecond.rawApply(morph.second)(firstResult)
      }

    def present(morph: InMorph): Seq[String] =
      ("(" +: evalFirst.present(morph.first)) ++
      (" >=> " +: evalSecond.present(morph.second) :+ ")")
  }
}
