package ohnosequences.scarph

object evals {

  import monoidalStructures._
  import ohnosequences.cosas._, types._, fns._
  import graphTypes._, morphisms._


  trait AnyEvalPath {

    type Morph <: AnyGraphMorphism

    type InVal
    type OutVal

    type Input = Morph#In := InVal
    type Output = Morph#Out := OutVal

    def apply(morph: Morph)(input: Input): Output
  }

  @annotation.implicitNotFound(msg = "Can't evaluate morph ${P} with\n\tinput: ${I}\n\toutput: ${O}")
  trait EvalPathOn[I, P <: AnyGraphMorphism, O] extends AnyEvalPath {

    type InVal = I
    type OutVal = O
    type Morph = P
  }

  trait FlattenVals[F[_], G[_], X] extends Fn1[F[G[X]]]

  trait MergeVals[F, S] extends Fn2[F, S]

  object AnyEvalPath {

    implicit def evalComposition[
      I, 
      F <: AnyGraphMorphism,
      S <: AnyGraphMorphism { type In = F#Out },
      X, O
    ](implicit
      evalFirst:  EvalPathOn[I, F, X],
      evalSecond: EvalPathOn[X, S, O]
    ):  EvalPathOn[I, F >=> S, O] = 
    new EvalPathOn[I, F >=> S, O] {

      def apply(morph: Morph)(input: Input): Output = {
        val firstResult = evalFirst(morph.first)(input)
        evalSecond(morph.second)(morph.second.in := firstResult.value)
      }
    }

    implicit def evalTensor[
      FI, SI,
      F <: AnyGraphMorphism, S <: AnyGraphMorphism,
      FO, SO
    ](implicit
      eval1: EvalPathOn[FI, F, FO], 
      eval2: EvalPathOn[SI, S, SO]
    ):  EvalPathOn[(FI, SI), TensorMorph[F, S], (FO, SO)] = 
    new EvalPathOn[(FI, SI), TensorMorph[F, S], (FO, SO)] {
      def apply(morph: Morph)(input: Input): Output = {
        morph.out := ((
          eval1(morph.left) ( (morph.left.in: F#In)  := input.value._1 ).value,
          eval2(morph.right)( (morph.right.in: S#In) := input.value._2 ).value
        ))
      }
    }

    implicit def evalBiproduct[
      FI, SI,
      F <: AnyGraphMorphism, S <: AnyGraphMorphism,
      FO, SO
    ](implicit
      eval1: EvalPathOn[FI, F, FO], 
      eval2: EvalPathOn[SI, S, SO]
    ):  EvalPathOn[(FI, SI), BiproductMorph[F, S], (FO, SO)] = 
    new EvalPathOn[(FI, SI), BiproductMorph[F, S], (FO, SO)] {
      def apply(morph: Morph)(input: Input): Output = {
        morph.out := ((
          eval1(morph.left) ( (morph.left.in: F#In)  := input.value._1 ).value,
          eval2(morph.right)( (morph.right.in: S#In) := input.value._2 ).value
        ))
      }
    }

    implicit def evalFork[
      I, T <: AnyGraphObject
    ]:  EvalPathOn[I, fork[T], (I, I)] = 
    new EvalPathOn[I, fork[T], (I, I)] {
      def apply(morph: Morph)(input: Input): Output = morph.out := ( (input.value, input.value) )
    }

    // TODO: it should compare values
    implicit def evalMerge[
      I, T <: AnyGraphObject
    ]:  EvalPathOn[(I, I), merge[T], I] = 
    new EvalPathOn[(I, I), merge[T], I] {
      def apply(morph: Morph)(input: Input): Output = morph.out := input.value._1
    }

    implicit def evalEither[
      I, T <: AnyGraphObject
    ]:  EvalPathOn[I, either[T], (I, I)] = 
    new EvalPathOn[I, either[T], (I, I)] {
      def apply(morph: Morph)(input: Input): Output = morph.out := ( (input.value, input.value) )
    }

    // TODO: think better about this
    implicit def evalAnyOf[
      I, T <: AnyGraphObject
    ]:  EvalPathOn[(I, I), anyOf[T], I] = 
    new EvalPathOn[(I, I), anyOf[T], I] {
      def apply(morph: Morph)(input: Input): Output = morph.out := input.value._1
    }

    implicit def evalLeftProj[
      L, R, B <: AnyBiproductObj
    ]:  EvalPathOn[(L, R), leftProj[B], L] = 
    new EvalPathOn[(L, R), leftProj[B], L] {
      def apply(morph: Morph)(input: Input): Output = (morph.out: Morph#Out) := input.value._1
    }

    implicit def evalRightProj[
      L, R, B <: AnyBiproductObj
    ]:  EvalPathOn[(L, R), rightProj[B], R] = 
    new EvalPathOn[(L, R), rightProj[B], R] {
      def apply(morph: Morph)(input: Input): Output = (morph.out: Morph#Out) := input.value._2
    }

  }
}
