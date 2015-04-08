package ohnosequences.scarph

object evals {

  import monoidalStructures._
  import ohnosequences.cosas._, types._, fns._
  import graphTypes._, morphisms._, implementations._


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


  object structural {

    // X = X (does nothing)
    implicit def eval_id[
      I, X <: AnyGraphObject
    ]:  EvalPathOn[I, id[X], I] =
    new EvalPathOn[I, id[X], I] {
      def apply(morph: Morph)(input: Input): Output = input
    }


    // F >=> S
    implicit def eval_composition[
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

    // IL ⊗ IR → OL ⊗ OR
    implicit def eval_tensor[
      IL, IR, I,
      L <: AnyGraphMorphism, R <: AnyGraphMorphism,
      OL, OR, O
    ](implicit
      inTens:  TensorImpl[IL, IR] { type Impl = I },
      outTens: TensorImpl[OL, OR] { type Impl = O },
      evalLeft:  EvalPathOn[IL, L, OL],
      evalRight: EvalPathOn[IR, R, OR]
    ):  EvalPathOn[I, TensorMorph[L, R], O] =
    new EvalPathOn[I, TensorMorph[L, R], O] {
      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outTens(
          evalLeft(morph.left)  ( (morph.left.in:  L#In) := inTens.leftProj(input.value) ).value,
          evalRight(morph.right)( (morph.right.in: R#In) := inTens.rightProj(input.value) ).value
        )
      }
    }

    // IL ⊕ IR → OL ⊕ OR
    implicit def eval_biproduct[
      IL, IR, I,
      F <: AnyGraphMorphism, S <: AnyGraphMorphism,
      OL, OR, O
    ](implicit
      inBip:  TensorImpl[IL, IR] { type Impl = I },
      outBip: TensorImpl[OL, OR] { type Impl = O },
      evalLeft:  EvalPathOn[IL, F, OL],
      evalRight: EvalPathOn[IR, S, OR]
    ):  EvalPathOn[I, BiproductMorph[F, S], O] =
    new EvalPathOn[I, BiproductMorph[F, S], O] {
      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outBip(
          evalLeft(morph.left)  ( (morph.left.in:  F#In) := inBip.leftProj(input.value) ).value,
          evalRight(morph.right)( (morph.right.in: S#In) := inBip.rightProj(input.value) ).value
        )
      }
    }

    // △: X → X ⊗ X
    implicit def eval_duplicate[
      I, T <: AnyGraphObject, O
    ](implicit
      outTens: TensorImpl[I, I] { type Impl = O }
    ):  EvalPathOn[I, duplicate[T], O] =
    new EvalPathOn[I, duplicate[T], O] {
      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outTens(input.value, input.value)
      }
    }

    // X → X ⊕ X
    implicit def eval_split[
      I, T <: AnyGraphObject, O
    ](implicit
      outBip: BiproductImpl[I, I] { type Impl = O }
    ):  EvalPathOn[I, split[T], O] =
    new EvalPathOn[I, split[T], O] {
      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outBip(input.value, input.value)
      }
    }

    // L → L ⊕ R
    implicit def eval_leftInj[
      L <: AnyGraphObject, R <: AnyGraphObject,
      OL, OR, O
    ](implicit
      outBip: BiproductImpl[OL, OR] { type Impl = O }
    ):  EvalPathOn[OL, leftInj[L ⊕ R], O] =
    new EvalPathOn[OL, leftInj[L ⊕ R], O] {
      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outBip.leftInj(input.value)
      }
    }

    // R → L ⊕ R
    implicit def eval_rightInj[
      L <: AnyGraphObject, R <: AnyGraphObject,
      OL, OR, O
    ](implicit
      outBip: BiproductImpl[OL, OR] { type Impl = O }
    ):  EvalPathOn[OR, rightInj[L ⊕ R], O] =
    new EvalPathOn[OR, rightInj[L ⊕ R], O] {
      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outBip.rightInj(input.value)
      }
    }

    // L ⊕ R → L
    implicit def eval_leftProj[
      IL, IR, I,
      L <: AnyGraphObject, R <: AnyGraphObject
    ](implicit
      outBip: BiproductImpl[IL, IR] { type Impl = I }
    ):  EvalPathOn[I, leftProj[L ⊕ R], IL] =
    new EvalPathOn[I, leftProj[L ⊕ R], IL] {
      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outBip.leftProj(input.value)
      }
    }

    // L ⊕ R → R
    implicit def eval_rightProj[
      IL, IR, I,
      L <: AnyGraphObject, R <: AnyGraphObject
    ](implicit
      outBip: BiproductImpl[IL, IR] { type Impl = I }
    ):  EvalPathOn[I, rightProj[L ⊕ R], IR] =
    new EvalPathOn[I, rightProj[L ⊕ R], IR] {
      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outBip.rightProj(input.value)
      }
    }

    // 0 → X
    implicit def eval_fromZero[
      I, X <: AnyGraphObject, O
    ](implicit
      outZero: AnyZeroImpl { type Impl = O }
    ):  EvalPathOn[I, fromZero[X], O] =
    new EvalPathOn[I, fromZero[X], O] {
      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outZero()
      }
    }

    // X → 0
    implicit def eval_toZero[
      I, T, X <: AnyGraphObject, O
    ](implicit
      inZero:  ZeroImpl[T] { type Impl = I },
      outZero: ZeroImpl[T] { type Impl = O }
    ):  EvalPathOn[I, toZero[X], O] =
    new EvalPathOn[I, toZero[X], O] {
      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outZero()
      }
    }

    // TODO: matchUp & merge
    // TODO: fromUnit & toUnit

  }
}
