package ohnosequences.scarph

import ohnosequences.cosas._

// NOTE: maybe this should be Fn2
trait AnyEvalPath[P <: AnyPath] {

  type Path = P

  type InVal
  type OutVal

  type In = InVal LabeledBy Path#In
  type Out = OutVal LabeledBy Path#Out

  // implicitly:
  // type PackedOut
  // val pack: Pack[OutVal LabeledBy Path#OutT, Path#OutArity] { type Out = PackedOut }

  def apply(in: In, p: Path): Out

  /* This returns the result of eval with respect to the out-arity */
  // def apply(in: In, p: Path): PackedOut = pack(eval(in, p))
}

trait EvalPath[I, P <: AnyPath, O] 
  extends AnyEvalPath[P] {

  type InVal = I
  type OutVal = O
}

object AnyEvalPath {

  // implicit def evalComposition[
  //   F <: AnyPath, 
  //   S <: AnyPath { type InT = F#OutT },
  //   I, M, O
  // ](implicit
  //   evalFirst:  EvalPath[I, F, M],
  //   evalSecond: EvalPath[M, S, O]
  // ):  EvalPath[I, Compose[F, S], O] =
  // new EvalPath[I, Compose[F, S], O] {

  //   def apply(in: In, p: Path): Out = {
  //     // val bodyOut: M LabeledBy F#Out = evalFirst(in, p.first: F#First)
      
  //     // bodyOut.flatMap{ b => evalSecond(b, p.second) }
  //   }
  // }
}
