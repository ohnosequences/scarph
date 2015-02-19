package ohnosequences.scarph

object evals {

  import ohnosequences.cosas._, types._, fns._
  import graphTypes._, steps._


  // NOTE: maybe this should be Fn2
  trait AnyEvalPath {

    type Tpe <: AnyGraphType

    type InVal
    type OutVal

    type Input = Tpe#In := InVal
    type Output = Tpe#Out := OutVal

    def apply(tpe: Tpe)(input: Input): Output
  }

  @annotation.implicitNotFound(msg = "Can't evaluate tpe ${P} with\n\tinput: ${I}\n\toutput: ${O}")
  trait EvalPathOn[I, P <: AnyGraphType, O] extends AnyEvalPath {

    type InVal = I
    type OutVal = O
    type Tpe = P
  }

  trait FlattenVals[F[_], G[_], X] extends Fn1[F[G[X]]]

  trait MergeVals[F, S] extends Fn2[F, S]

  object AnyEvalPath {

    implicit def evalId[
      I, T <: AnyGraphObject
    ]:  EvalPathOn[I, T, I] = 
    new EvalPathOn[I, T, I] {
      def apply(tpe: Tpe)(input: Input): Output = input
    }

    implicit def evalComposition[
      I, 
      F <: AnyGraphType,
      S <: AnyGraphType { type In = F#Out },
      X, O
    ](implicit
      evalFirst:  EvalPathOn[I, F, X],
      evalSecond: EvalPathOn[X, S, O]
    ):  EvalPathOn[I, F >=> S, O] = 
    new EvalPathOn[I, F >=> S, O] {

      def apply(tpe: Tpe)(input: Input): Output = {
        val firstResult = evalFirst(tpe.first)(input)
        evalSecond(tpe.second)(tpe.second.in := firstResult.value)
      }
    }

    implicit def evalFork[
      I, T <: AnyGraphType
    ]:  EvalPathOn[I, Fork[T], (I, I)] = 
    new EvalPathOn[I, Fork[T], (I, I)] {
      def apply(tpe: Tpe)(input: Input): Output = tpe.out := ( (input.value, input.value) )
    }

    implicit def evalMerge[
      I, T <: AnyGraphType
    ]:  EvalPathOn[(I, I), Merge[T], I] = 
    new EvalPathOn[(I, I), Merge[T], I] {
      def apply(tpe: Tpe)(input: Input): Output = tpe.out := input.value._1
    }

    implicit def evalTensor[
      FI, SI,
      F <: AnyGraphType, S <: AnyGraphType,
      FO, SO
    ](implicit
      eval1: EvalPathOn[FI, F, FO], 
      eval2: EvalPathOn[SI, S, SO]
    ):  EvalPathOn[(FI, SI), F ⊗ S, (FO, SO)] = 
    new EvalPathOn[(FI, SI), F ⊗ S, (FO, SO)] {
      def apply(tpe: Tpe)(input: Input): Output = {
        tpe.out := ((
          eval1(tpe.left) ( (tpe.left.in: F#In)  := input.value._1 ).value,
          eval2(tpe.right)( (tpe.right.in: S#In) := input.value._2 ).value
        ))
      }
    }

  }

}
