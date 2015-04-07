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

  trait AnyImpl {

    type Impl
  }

  trait FlattenVals[F[_], G[_], X] extends Fn1[F[G[X]]]

  trait MergeVals[F, S] extends Fn2[F, S]


  trait AnyTensorImpl extends AnyImpl {

    type Left
    def leftProj(i: Impl): Left

    type Right
    def rightProj(i: Impl): Right

    def apply(l: Left, r: Right): Impl
  }

  abstract class TensorImpl[L, R] extends AnyTensorImpl {

    type Left = L
    type Right = R
  }


  trait AnyBiproductImpl extends AnyImpl {

    type Left
    def leftProj(i: Impl): Left

    type Right
    def rightProj(i: Impl): Right

    def apply(l: Left, r: Right): Impl

    def leftInj(l: Left): Impl
    def rightInj(r: Right): Impl
  }

  abstract class BiproductImpl[L, R] extends AnyBiproductImpl {

    type Left = L
    type Right = R
  }


  object generalEvals {

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
      IL, IR, I,
      L <: AnyGraphMorphism, R <: AnyGraphMorphism,
      OL, OR, O
    ](implicit
      inTens:  TensorImpl[IL, IR] { type Impl = I },
      outTens: TensorImpl[OL, OR] { type Impl = O },
      eval1: EvalPathOn[IL, L, OL],
      eval2: EvalPathOn[IR, R, OR]
    ):  EvalPathOn[I, TensorMorph[L, R], O] =
    new EvalPathOn[I, TensorMorph[L, R], O] {
      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outTens(
          eval1(morph.left) ( (morph.left.in: L#In)  := inTens.leftProj(input.value) ).value,
          eval2(morph.right)( (morph.right.in: R#In) := inTens.rightProj(input.value) ).value
        )
      }
    }

    implicit def evalBiproduct[
      IL, IR, I,
      F <: AnyGraphMorphism, S <: AnyGraphMorphism,
      OL, OR, O
    ](implicit
      inBip:  TensorImpl[IL, IR] { type Impl = I },
      outBip: TensorImpl[OL, OR] { type Impl = O },
      eval1: EvalPathOn[IL, F, OL],
      eval2: EvalPathOn[IR, S, OR]
    ):  EvalPathOn[I, BiproductMorph[F, S], O] =
    new EvalPathOn[I, BiproductMorph[F, S], O] {
      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outBip(
          eval1(morph.left) ( (morph.left.in: F#In)  := inBip.leftProj(input.value) ).value,
          eval2(morph.right)( (morph.right.in: S#In) := inBip.rightProj(input.value) ).value
        )
      }
    }

    // △: X → X ⊗ X
    implicit def evalDuplicate[
      I, T <: AnyGraphObject, O
    ](implicit
      outTens: TensorImpl[I, I] { type Impl = O }
    ):  EvalPathOn[I, duplicate[T], O] =
    new EvalPathOn[I, duplicate[T], O] {
      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outTens(input.value, input.value)
      }
    }

    // X -> X ⊕ X
    implicit def evalSplit[
      I, T <: AnyGraphObject, O
    ](implicit
      outBip: BiproductImpl[I, I] { type Impl = O }
    ):  EvalPathOn[I, split[T], O] =
    new EvalPathOn[I, split[T], O] {
      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outBip(input.value, input.value)
      }
    }

/*
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
*/

  }
}
