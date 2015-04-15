package ohnosequences.scarph

object evals {

  import monoidalStructures._
  import ohnosequences.cosas._, types._, fns._
  import graphTypes._, morphisms._, implementations._, predicates._


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

  //@annotation.implicitNotFound(msg = "Can't evaluate morphism ${M}")*/
  trait EvalOn[I, M <: AnyGraphMorphism, O] extends Eval[M] {

    type InVal = I
    type OutVal = O
  }


  trait DefaultEvals {

    // X = X (does nothing)
    implicit def eval_id[
      I, X <: AnyGraphObject
    ]:  EvalOn[I, id[X], I] =
    new EvalOn[I, id[X], I] {

      def apply(morph: Morph)(input: Input): Output = input

      def present(morph: Morph): String = morph.label
    }


    // F >=> S
    implicit def eval_composition[
      I,
      F <: AnyGraphMorphism,
      S <: AnyGraphMorphism { type In = F#Out },
      X, O
    ](implicit
      evalFirst:  EvalOn[I, F, X],
      evalSecond: EvalOn[X, S, O]
    ):  EvalOn[I, F >=> S, O] =
    new EvalOn[I, F >=> S, O] {

      def apply(morph: Morph)(input: Input): Output = {
        val firstResult = evalFirst(morph.first)(input)
        evalSecond(morph.second)(morph.second.in := firstResult.value)
      }

      def present(morph: Morph): String = s"(${evalFirst.present(morph.first)} >=> ${evalSecond.present(morph.second)})"
    }

    // IL ⊗ IR → OL ⊗ OR
    implicit def eval_tensor[
      IL, IR, I,
      L <: AnyGraphMorphism, R <: AnyGraphMorphism,
      OL, OR, O
    ](implicit
      inTens:  TensorImpl[I, IL, IR],
      outTens: TensorImpl[O, OL, OR],
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

      def present(morph: Morph): String = s"(${evalLeft.present(morph.left)} ⊗ ${evalRight.present(morph.right)})"
    }

    // IL ⊕ IR → OL ⊕ OR
    implicit def eval_biproduct[
      IL, IR, I,
      L <: AnyGraphMorphism, R <: AnyGraphMorphism,
      OL, OR, O
    ](implicit
      inBip:  BiproductImpl[I, IL, IR],
      outBip: BiproductImpl[O, OL, OR],
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

      def present(morph: Morph): String = s"(${evalLeft.present(morph.left)} ⊕ ${evalRight.present(morph.right)})"
    }

    // △: X → X ⊗ X
    implicit def eval_duplicate[
      I, T <: AnyGraphObject, O
    ](implicit
      outTens: TensorImpl[O, I, I]
    ):  EvalOn[I, duplicate[T], O] =
    new EvalOn[I, duplicate[T], O] {

      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outTens(input.value, input.value)
      }

      def present(morph: Morph): String = morph.label
    }

    // ▽: X ⊗ X → X
    implicit def eval_matchUp[
      I, T <: AnyGraphObject, O
    ](implicit
      tensImpl: TensorImpl[I, O, O],
      matchImpl: MatchUpImpl[O]
    ):  EvalOn[I, matchUp[T], O] =
    new EvalOn[I, matchUp[T], O] {

      def apply(morph: Morph)(input: Input): Output = {
        morph.out := matchImpl.matchUp(tensImpl.leftProj(input.value), tensImpl.rightProj(input.value))
      }

      def present(morph: Morph): String = morph.label
    }

    // X → X ⊕ X
    implicit def eval_split[
      I, T <: AnyGraphObject, O
    ](implicit
      outBip: BiproductImpl[O, I, I]
    ):  EvalOn[I, split[T], O] =
    new EvalOn[I, split[T], O] {

      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outBip(input.value, input.value)
      }

      def present(morph: Morph): String = morph.label
    }

    // X ⊕ X → X
    implicit def eval_merge[
      I, T <: AnyGraphObject, O
    ](implicit
      bipImpl: BiproductImpl[I, O, O],
      mergeImpl: MergeImpl[O]
    ):  EvalOn[I, merge[T], O] =
    new EvalOn[I, merge[T], O] {

      def apply(morph: Morph)(input: Input): Output = {
        morph.out := mergeImpl.merge(bipImpl.leftProj(input.value), bipImpl.rightProj(input.value))
      }

      def present(morph: Morph): String = morph.label
    }

    // L → L ⊕ R
    implicit def eval_leftInj[
      L <: AnyGraphObject, R <: AnyGraphObject,
      I, OR, O
    ](implicit
      outBip: BiproductImpl[O, I, OR]
    ):  EvalOn[I, leftInj[L ⊕ R], O] =
    new EvalOn[I, leftInj[L ⊕ R], O] {

      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outBip.leftInj(input.value)
      }

      def present(morph: Morph): String = morph.label
    }

    // R → L ⊕ R
    implicit def eval_rightInj[
      L <: AnyGraphObject, R <: AnyGraphObject,
      OL, OR, O
    ](implicit
      outBip: BiproductImpl[O, OL, OR]
    ):  EvalOn[OR, rightInj[L ⊕ R], O] =
    new EvalOn[OR, rightInj[L ⊕ R], O] {

      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outBip.rightInj(input.value)
      }

      def present(morph: Morph): String = morph.label
    }

    // L ⊕ R → L
    implicit def eval_leftProj[
      IL, IR, I,
      L <: AnyGraphObject, R <: AnyGraphObject
    ](implicit
      outBip: BiproductImpl[I, IL, IR]
    ):  EvalOn[I, leftProj[L ⊕ R], IL] =
    new EvalOn[I, leftProj[L ⊕ R], IL] {

      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outBip.leftProj(input.value)
      }

      def present(morph: Morph): String = morph.label
    }

    // L ⊕ R → R
    implicit def eval_rightProj[
      IL, IR, I,
      L <: AnyGraphObject, R <: AnyGraphObject
    ](implicit
      outBip: BiproductImpl[I, IL, IR]
    ):  EvalOn[I, rightProj[L ⊕ R], IR] =
    new EvalOn[I, rightProj[L ⊕ R], IR] {

      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outBip.rightProj(input.value)
      }

      def present(morph: Morph): String = morph.label
    }

    // 0 → X
    implicit def eval_fromZero[
      I, X <: AnyGraphObject, O
    ](implicit
      inZero:  ZeroImpl[I],
      outZero: ZeroImpl[O]
    ):  EvalOn[I, fromZero[X], O] =
    new EvalOn[I, fromZero[X], O] {

      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outZero()
      }

      def present(morph: Morph): String = morph.label
    }

    // X → 0
    implicit def eval_toZero[
      I, T, X <: AnyGraphObject, O
    ](implicit
      inZero:  ZeroImpl[I],
      outZero: ZeroImpl[O]
    ):  EvalOn[I, toZero[X], O] =
    new EvalOn[I, toZero[X], O] {

      def apply(morph: Morph)(input: Input): Output = {
        morph.out := outZero()
      }

      def present(morph: Morph): String = morph.label
    }

    implicit def eval_inE[
      I, E <: AnyEdge, IE, IV
    ](implicit
      vImpl:  VertexInImpl[E, I, IE, IV]
    ):  EvalOn[I, inE[E], IE] =
    new EvalOn[I, inE[E], IE] {

      def apply(morph: Morph)(input: Input): Output = {
        morph.out := vImpl.inE(input.value, morph.edge)
      }

      def present(morph: Morph): String = morph.label
    }

    implicit def eval_outE[
      I, E <: AnyEdge, OE, OV
    ](implicit
      vImpl:  VertexOutImpl[E, I, OE, OV]
    ):  EvalOn[I, outE[E], OE] =
    new EvalOn[I, outE[E], OE] {

      def apply(morph: Morph)(input: Input): Output = {
        morph.out := vImpl.outE(input.value, morph.edge)
      }

      def present(morph: Morph): String = morph.label
    }

    implicit def eval_inV[
      I, E <: AnyEdge, IE, IV
    ](implicit
      vImpl:  VertexInImpl[E, I, IE, IV]
    ):  EvalOn[I, inV[E], IV] =
    new EvalOn[I, inV[E], IV] {

      def apply(morph: Morph)(input: Input): Output = {
        morph.out := vImpl.inV(input.value, morph.edge)
      }

      def present(morph: Morph): String = morph.label
    }

    implicit def eval_outV[
      I, E <: AnyEdge, OE, OV
    ](implicit
      vImpl:  VertexOutImpl[E, I, OE, OV]
    ):  EvalOn[I, outV[E], OV] =
    new EvalOn[I, outV[E], OV] {

      def apply(morph: Morph)(input: Input): Output = {
        (morph.out: Morph#Out) := vImpl.outV(input.value, morph.edge)
      }

      def present(morph: Morph): String = morph.label
    }

    implicit def eval_source[
      E <: AnyEdge, I, S, T
    ](implicit
      eImpl: EdgeImpl[I, S, T]
    ):  EvalOn[I, source[E], S] =
    new EvalOn[I, source[E], S] {

      def apply(morph: Morph)(input: Input): Output = {
        (morph.out: Morph#Out) := eImpl.source(input.value)
      }

      def present(morph: Morph): String = morph.label
    }

    implicit def eval_target[
      E <: AnyEdge, I, S, T
    ](implicit
      eImpl: EdgeImpl[I, S, T]
    ):  EvalOn[I, target[E], T] =
    new EvalOn[I, target[E], T] {

      def apply(morph: Morph)(input: Input): Output = {
        (morph.out: Morph#Out) := eImpl.target(input.value)
      }

      def present(morph: Morph): String = morph.label
    }


    // I → X
    implicit def eval_fromUnit[
      O <: AnyGraphObject, RawObj, RawUnit
    ](implicit
      unitImpl:  UnitImpl[O, RawObj, RawUnit]
    ):  EvalOn[RawUnit, fromUnit[O], RawObj] =
    new EvalOn[RawUnit, fromUnit[O], RawObj] {

      def apply(morph: Morph)(input: Input): Output = {
        morph.out := unitImpl.fromUnit(input.value, morph.obj)
      }

      def present(morph: Morph): String = morph.label
    }

    // X → I
    implicit def eval_toUnit[
      O <: AnyGraphObject, RawObj, RawUnit
    ](implicit
      unitImpl:  UnitImpl[O, RawObj, RawUnit]
    ):  EvalOn[RawObj, toUnit[O], RawUnit] =
    new EvalOn[RawObj, toUnit[O], RawUnit] {

      def apply(morph: Morph)(input: Input): Output = {
        morph.out := unitImpl.toUnit(input.value)
      }

      def present(morph: Morph): String = morph.label
    }


    implicit def eval_get[
      P <: AnyGraphProperty, RawElem, RawValue
    ](implicit
      propImpl: PropertyImpl[P, RawElem, RawValue]
    ):  EvalOn[RawElem, get[P], RawValue] =
    new EvalOn[RawElem, get[P], RawValue] {

      def apply(morph: Morph)(input: Input): Output = {
        (morph.out: Morph#Out) := propImpl.get(input.value, morph.property)
      }

      def present(morph: Morph): String = morph.label
    }

    implicit def eval_lookup[
      P <: AnyGraphProperty, RawElem, RawValue
    ](implicit
      propImpl: PropertyImpl[P, RawElem, RawValue]
    ):  EvalOn[RawValue, lookup[P], RawElem] =
    new EvalOn[RawValue, lookup[P], RawElem] {

      def apply(morph: Morph)(input: Input): Output = {
        (morph.out: Morph#Out) := propImpl.lookup(input.value, morph.property)
      }

      def present(morph: Morph): String = morph.label
    }


    implicit def eval_quantify[
      P <: AnyPredicate, RawPred, RawElem
    ](implicit
      predImpl: PredicateImpl[RawPred, RawElem]
    ):  EvalOn[RawElem, quantify[P], RawPred] =
    new EvalOn[RawElem, quantify[P], RawPred] {

      def apply(morph: Morph)(input: Input): Output = {
        (morph.out: Morph#Out) := predImpl.quantify(input.value, morph.predicate)
      }

      def present(morph: Morph): String = morph.label
    }


    implicit def eval_coerce[
      P <: AnyPredicate, RawPred, RawElem
    ](implicit
      predImpl: PredicateImpl[RawPred, RawElem]
    ):  EvalOn[RawPred, coerce[P], RawElem] =
    new EvalOn[RawPred, coerce[P], RawElem] {

      def apply(morph: Morph)(input: Input): Output = {
        (morph.out: Morph#Out) := predImpl.coerce(input.value)
      }

      def present(morph: Morph): String = morph.label
    }

  }

}
