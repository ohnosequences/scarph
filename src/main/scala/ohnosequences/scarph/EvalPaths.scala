package ohnosequences.scarph

import ohnosequences.cosas._

// NOTE: maybe this should be Fn2
trait AnyEvalPath[P <: AnyPath] {

  type Path = P

  type InVal
  type OutVal

  type In = InVal LabeledBy Path#InT
  type Out = List[OutVal LabeledBy Path#OutT]

  def apply(in: In, p: Path): Out
}

trait EvalPath[I, P <: AnyPath, O] 
  extends AnyEvalPath[P] {

  type InVal = I
  type OutVal = O
}

object AnyEvalPath {

  implicit def evalComposition[
    F <: AnyPath, 
    S <: AnyPath { type InT = F#OutT },
    A <: AnyArity,
    I, M, O
  ](implicit
    evalFirst:  EvalPath[I, F, M],
    evalSecond: EvalPath[M, S, O]
  ):  EvalPath[I, Compose[F, S, A], O] =
  new EvalPath[I, Compose[F, S, A], O] {

    def apply(in: In, p: Path): Out = {
      val bodyOut: List[M LabeledBy F#OutT] = evalFirst(in, p.first)
      bodyOut.flatMap{ b => evalSecond(b, p.second) }
    }
  }
}
