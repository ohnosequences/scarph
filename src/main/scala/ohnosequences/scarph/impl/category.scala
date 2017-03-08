package ohnosequences.scarph.impl

import ohnosequences.scarph._

trait DaggerCategory {

  implicit final def eval_id[X <: AnyGraphObject, I]:
      Eval[id[X], I, I] =
  new Eval[id[X], I, I]({ _ => raw_input => raw_input })

  // F >=> S
  implicit final def eval_composition[
    I, X, O,
    F <: AnyGraphMorphism,
    S <: AnyGraphMorphism {
      type In = F#Out
    }
  ](implicit
    evalFirst:  Eval[F, I, X],
    evalSecond: Eval[S, X, O]
  ):  Eval[F >=> S, I, O] =
  new Eval[F >=> S, I, O]({ morph => raw_input =>

        val firstResult = evalFirst.raw_apply(morph.first)(raw_input)
        evalSecond.raw_apply(morph.second)(firstResult)
      }) {

    override def present(morph: InMorph): Seq[String] =
      ("(" +: evalFirst.present(morph.first)) ++
      (" >=> " +: evalSecond.present(morph.second) :+ ")")
  }
}
