package ohnosequences.scarph

object evals {

  import monoidalStructures._
  import ohnosequences.cosas._, types._, fns._
  import graphTypes._, morphisms._, implementations._


  trait AnyEval {

    type Morph <: AnyGraphMorphism

    type InVal
    type OutVal

    type Input = Morph#In := InVal
    type Output = Morph#Out := OutVal

    def apply(morph: Morph)(input: Input): Output

    def present(morph: Morph): String
  }

  trait Eval[M <: AnyGraphMorphism] extends AnyEval { type Morph = M }

  trait InVal[I] extends AnyEval { type InVal = I }
  trait OutVal[O] extends AnyEval { type OutVal = O }

  //@annotation.implicitNotFound(msg = "Can't evaluate morphism ${M}")*/
  trait EvalOn[I, M <: AnyGraphMorphism, O] extends Eval[M] with InVal[I] with OutVal[O]


  object DefaultEvals {

//    // X = X (does nothing)
//    implicit def eval_id[
//      I, X <: AnyGraphObject
//    ]:  EvalPathOn[id[X]] =
//    new EvalPathOn[id[X]] {
//      type InVal = I
//      type OutVal = I
//
//      def apply(morph: Morph)(input: Input): Output = input
//
//      def present(morph: Morph): String = morph.label
//    }


    // F >=> S
    def eval_composition[
      I,
      F <: AnyGraphMorphism,
      S <: AnyGraphMorphism { type In = F#Out },
      X, O
    ](implicit
      evalFirst:  Eval[F] { type InVal = I; type OutVal = X},
      evalSecond: Eval[S] { type InVal = X; type OutVal = O}
    ):  Eval[F >=> S] with InVal[I] with OutVal[O] =
    new Eval[F >=> S] with InVal[I] with OutVal[O] {

      def apply(morph: Morph)(input: Input): Output = {
        val firstResult = evalFirst(morph.first)(input)
        evalSecond(morph.second)(morph.second.in := firstResult.value)
      }

      def present(morph: Morph): String = s"${evalFirst.present(morph.first)} >=>\n${evalSecond.present(morph.second)}"
    }

    // IL ⊗ IR → OL ⊗ OR
    implicit def eval_tensor[
      IL, IR, I,
      L <: AnyGraphMorphism, R <: AnyGraphMorphism,
      OL, OR, O
    ](implicit
      inTens:  TensorImpl[I] { type Left = IL; type Right = IR },
      outTens: TensorImpl[O] { type Left = OL; type Right = OR },
      evalLeft:  EvalOn[IL, L, OL],
      evalRight: EvalOn[IR, R, OR]
    ):  EvalOn[I, TensorMorph[L, R], O] =
    new EvalOn[I, TensorMorph[L, R], O] {

      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outTens(
          evalLeft(morph.left)  ( (morph.left.in:  L#In) := inTens.leftProj(input.value) ).value,
          evalRight(morph.right)( (morph.right.in: R#In) := inTens.rightProj(input.value) ).value
        )
      }

      def present(morph: Morph): String = s"${evalLeft.present(morph.left)} ⊗ ${evalRight.present(morph.right)}"
    }

    // IL ⊕ IR → OL ⊕ OR
    implicit def eval_biproduct[
      IL, IR, I,
      L <: AnyGraphMorphism, R <: AnyGraphMorphism,
      OL, OR, O
    ](implicit
      inBip:  BiproductImpl[I] { type Left = IL; type Right = IR },
      outBip: BiproductImpl[O] { type Left = OL; type Right = OR },
      evalLeft:  EvalOn[IL, L, OL],
      evalRight: EvalOn[IR, R, OR]
    ):  EvalOn[I, BiproductMorph[L, R], O] =
    new EvalOn[I, BiproductMorph[L, R], O] {

      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outBip(
          evalLeft(morph.left)  ( (morph.left.in:  L#In) := inBip.leftProj(input.value) ).value,
          evalRight(morph.right)( (morph.right.in: R#In) := inBip.rightProj(input.value) ).value
        )
      }

      def present(morph: Morph): String = s"${evalLeft.present(morph.left)} ⊕ ${evalRight.present(morph.right)}"
    }

    // △: X → X ⊗ X
    implicit def eval_duplicate[
      I, T <: AnyGraphObject, O
    ](implicit
      outTens: TensorImpl[O] { type Left = I; type Right = I }
    ):  EvalOn[I, duplicate[T], O] =
    new EvalOn[I, duplicate[T], O] {

      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outTens(input.value, input.value)
      }

      def present(morph: Morph): String = morph.label
    }

    // X → X ⊕ X
    implicit def eval_split[
      I, T <: AnyGraphObject, O
    ](implicit
      outBip: BiproductImpl[O] { type Left = I; type Right = I }
    ):  EvalOn[I, split[T], O] =
    new EvalOn[I, split[T], O] {

      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outBip(input.value, input.value)
      }

      def present(morph: Morph): String = morph.label
    }

    // L → L ⊕ R
    implicit def eval_leftInj[
      L <: AnyGraphObject, R <: AnyGraphObject,
      I, OR, O
    ](implicit
      outBip: BiproductImpl[O] { type Left = I; type Right = OR }
    ):  EvalOn[I, leftInj[L ⊕ R], O] =
    new EvalOn[I, leftInj[L ⊕ R], O] {

      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outBip.leftInj(input.value)
      }

      def present(morph: Morph): String = morph.label
    }

//    // R → L ⊕ R
//    implicit def eval_rightInj[
//      L <: AnyGraphObject, R <: AnyGraphObject,
//      OL, OR, O
//    ](implicit
//      outBip: BiproductImpl[O] { type Left = OL; type Right = OR }
//    ):  EvalPathOn[OR, rightInj[L ⊕ R], O] =
//    new EvalPathOn[OR, rightInj[L ⊕ R], O] {
//
//      def apply(morph: Morph)(input: Input): Output = {
//        morph.out := outBip.rightInj(input.value)
//      }
//
//      def present(morph: Morph): String = morph.label
//    }
//
//    // L ⊕ R → L
//    implicit def eval_leftProj[
//      IL, IR, I,
//      L <: AnyGraphObject, R <: AnyGraphObject
//    ](implicit
//      outBip: BiproductImpl[I] { type Left = IL; type Right = IR }
//    ):  EvalPathOn[I, leftProj[L ⊕ R], IL] =
//    new EvalPathOn[I, leftProj[L ⊕ R], IL] {
//
//      def apply(morph: Morph)(input: Input): Output = {
//        morph.out := outBip.leftProj(input.value)
//      }
//
//      def present(morph: Morph): String = morph.label
//    }
//
//    // L ⊕ R → R
//    implicit def eval_rightProj[
//      IL, IR, I,
//      L <: AnyGraphObject, R <: AnyGraphObject
//    ](implicit
//      outBip: BiproductImpl[I] { type Left = IL; type Right = IR }
//    ):  EvalPathOn[I, rightProj[L ⊕ R], IR] =
//    new EvalPathOn[I, rightProj[L ⊕ R], IR] {
//
//      def apply(morph: Morph)(input: Input): Output = {
//        morph.out := outBip.rightProj(input.value)
//      }
//
//      def present(morph: Morph): String = morph.label
//    }
//
//    // 0 → X
//    implicit def eval_fromZero[
//      I, X <: AnyGraphObject, O
//    ](implicit
//      outZero: ZeroImpl[O]
//    ):  EvalPathOn[I, fromZero[X], O] =
//    new EvalPathOn[I, fromZero[X], O] {
//
//      def apply(morph: Morph)(input: Input): Output = {
//        morph.out := outZero()
//      }
//
//      def present(morph: Morph): String = morph.label
//    }
//
//    // X → 0
//    implicit def eval_toZero[
//      I, T, X <: AnyGraphObject, O
//    ](implicit
//      inZero:  ZeroImpl[I] { type Inside = T },
//      outZero: ZeroImpl[O] { type Inside = T }
//    ):  EvalPathOn[I, toZero[X], O] =
//    new EvalPathOn[I, toZero[X], O] {
//
//      def apply(morph: Morph)(input: Input): Output = {
//        morph.out := outZero()
//      }
//
//      def present(morph: Morph): String = morph.label
//    }

    // TODO: matchUp & merge
    // TODO: fromUnit & toUnit

  }
}
