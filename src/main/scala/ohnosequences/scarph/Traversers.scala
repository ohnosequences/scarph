package ohnosequences.scarph

import ohnosequences.cosas._

// NOTE: maybe this should be Fn2
trait AnyTraverser[P <: AnyPath] {

  type Path = P

  type InVal
  type OutVal

  type In = InVal LabeledBy Path#InT
  type Out = List[OutVal LabeledBy Path#OutT]

  def apply(in: In, p: Path): Out
}

trait Traverser[I, P <: AnyPath, O] 
  extends AnyTraverser[P] {

  type InVal = I
  type OutVal = O
}

object AnyTraverser {

  implicit def evalIdStep[T <: AnyLabelType, X]:
      Traverser[X, IdStep[T], X] =
  new Traverser[X, IdStep[T], X] {

    def apply(in: In, p: Path): Out = List(in)
  }

  implicit def evalComposition[
    F <: AnyPath, 
    S <: AnyPath { type InT = F#OutT },
    A <: AnyArity,
    I, M, O
  ](implicit
    evalFirst:  Traverser[I, F, M],
    evalSecond: Traverser[M, S, O]
  ):  Traverser[I, Compose[F, S, A], O] =
  new Traverser[I, Compose[F, S, A], O] {

    def apply(in: In, p: Path): Out = {
      val bodyOut: List[M LabeledBy F#OutT] = evalFirst(in, p.first)
      bodyOut.flatMap{ b => evalSecond(b, p.second) }
    }
  }
}
