package ohnosequences.scarph

object evals {

  import ohnosequences.cosas._, types._, fns._
  import graphTypes._, paths._, steps._, combinators._


  // NOTE: maybe this should be Fn2
  trait AnyEvalPath {

    type Path <: AnyPath

    type InVal
    type OutVal

    type Input = Path#In := InVal
    type Output = Path#Out := OutVal

    def apply(path: Path)(input: Input): Output
  }

  @annotation.implicitNotFound(msg = "Can't evaluate path ${P} with\n\tinput: ${I}\n\toutput: ${O}")
  trait EvalPathOn[I, P <: AnyPath, O] extends AnyEvalPath {

    type InVal = I
    type OutVal = O
    type Path = P
  }

  trait FlattenVals[F[_], G[_], X] extends Fn1[F[G[X]]]

  trait MergeVals[F, S] extends Fn2[F, S]

  object AnyEvalPath {

    implicit def evalComposition[
      I, 
      F <: AnyPath,
      S <: AnyPath { type In = F#Out },
      X, O
    ](implicit
      evalFirst:  EvalPathOn[I, F, X],
      evalSecond: EvalPathOn[X, S, O]
    ):  EvalPathOn[I, F >=> S, O] = 
    new EvalPathOn[I, F >=> S, O] {

      def apply(path: Path)(input: Input): Output = {
        val firstResult = evalFirst(path.first)(input)
        evalSecond(path.second)(path.second.in := firstResult.value)
      }
    }

    implicit def evalFork[
      I, T <: AnyGraphType
    ]:  EvalPathOn[I, Fork[T], (I, I)] = 
    new EvalPathOn[I, Fork[T], (I, I)] {
      def apply(path: Path)(input: Input): Output = path.out := ( (input.value, input.value) )
    }

    implicit def evalMerge[
      I, T <: AnyGraphType
    ]:  EvalPathOn[(I, I), Merge[T], I] = 
    new EvalPathOn[(I, I), Merge[T], I] {
      def apply(path: Path)(input: Input): Output = path.out := input.value._1
    }

    // implicit def evalBiproduct[
    //   FI, SI,
    //   F <: AnyPath, S <: AnyPath,
    //   FO, SO
    // ](implicit
    //   eval1: EvalPathOn[FI, F, FO], 
    //   eval2: EvalPathOn[SI, S, SO]
    // ):  EvalPathOn[(FI, SI), F ⊕ S, (FO, SO)] = 
    // new EvalPathOn[(FI, SI), F ⊕ S, (FO, SO)] {
    //   def apply(path: Path)(input: Input): Output = {
    //     path.out := (
    //       eval1(path.left) ( (path.left.in: F#In)  := input.value._1 ).value,
    //       eval2(path.right)( (path.right.in: S#In) := input.value._2 ).value
    //     )
    //   }
    // }

  }

}
