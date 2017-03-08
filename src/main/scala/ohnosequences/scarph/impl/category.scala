package ohnosequences.scarph.impl

import ohnosequences.scarph._

trait DaggerCategory {

  implicit final def eval_id[X <: AnyGraphObject, I]:
      Eval[I, id[X], I] =
  new Eval[I, id[X], I] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = { raw_input: RawInput => raw_input }
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

    def raw_apply(morph: InMorph): RawInput => RawOutput =
      { raw_input: RawInput =>

        val firstResult = evalFirst.raw_apply(morph.first)(raw_input)
        evalSecond.raw_apply(morph.second)(firstResult)
      }

    override def present(morph: InMorph): Seq[String] =
      ("(" +: evalFirst.present(morph.first)) ++
      (" >=> " +: evalSecond.present(morph.second) :+ ")")
  }
}
