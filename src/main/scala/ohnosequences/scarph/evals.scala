package ohnosequences.scarph

object evals {

  import ohnosequences.cosas.types._
  import objects._, morphisms._, implementations._

  /* Transforms a morphism to a function */
  trait AnyEval extends AnyMorphismTransform {

    type InVal
    type OutVal

    type Input = InMorph#In := InVal
    type Output = InMorph#Out := OutVal

    type OutMorph = Input => Output

    def rawApply(morph: InMorph): InVal => OutVal

    // same but with tags:
    final def apply(morph: InMorph): OutMorph = { input: Input =>
      (morph.out: InMorph#Out) := rawApply(morph)(input.value)
    }

    def present(morph: InMorph): String
  }

  @annotation.implicitNotFound(msg = "Cannot evaluate morphism ${M} on input ${I}, output ${O}")
  trait Eval[I, M <: AnyGraphMorphism, O] extends AnyEval {

    type InMorph = M
    type InVal = I
    type OutVal = O
  }


  final class evaluate[I, M <: AnyGraphMorphism, O](val f: M, val eval: Eval[I, M, O]) {

    final def on(input: M#In := I): M#Out := O = eval(f).apply(input)

    // TODO: this should output the computational behavior of the eval here
    final def evalPlan: String = eval.present(f)
  }

  class preeval[I] {

    def apply[IM <: AnyGraphMorphism, O](m: IM)(implicit
      eval: Eval[I, IM, O]
    ):  evaluate[I, IM, O] =
    new evaluate[I, IM, O](m, eval)
  }

  def evalOn[I]: preeval[I] = new preeval[I] {}


  trait AnyStructure {

    type RawObject
  }

  trait CategoryStructure extends AnyStructure {

    implicit final def eval_id[
      I <: RawObject, X <: AnyGraphObject
    ]:  Eval[I, id[X], I] =
    new Eval[I, id[X], I] {

      def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal => inVal }

      final def present(morph: InMorph): String = morph.label
    }


    // F >=> S
    implicit final def eval_composition[
      F <: AnyGraphMorphism,
      S <: AnyGraphMorphism { type In = F#Out },
      I <: RawObject, X <: RawObject, O <: RawObject
    ](implicit
      evalFirst:  Eval[I, F, X],
      evalSecond: Eval[X, S, O]
    ):  Eval[I, F >=> S, O] =
    new Eval[I, F >=> S, O] {

      def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal =>

        val firstResult = evalFirst.rawApply(morph.first)(inVal)
        evalSecond.rawApply(morph.second)(firstResult)
      }

      def present(morph: InMorph): String = s"(${evalFirst.present(morph.first)} >=> ${evalSecond.present(morph.second)})"
    }

  }

  trait Matchable[T0] {

    type T = T0
    def matchUp(l: T, r: T): T
  }

  trait FromUnit[U0, T0] {

    type U = U0
    type T = T0
    def fromUnit(u: U, e: AnyGraphObject): T
  }

  object FromUnit {

    implicit def unitToUnit[U]:
        FromUnit[U, U] =
    new FromUnit[U, U] { def fromUnit(u: U, e: AnyGraphObject): T = u }
  }

  trait TensorStructure extends AnyStructure {

    type RawTensor[L <: RawObject, R <: RawObject] //<: RawObject
    type RawUnit

    def construct[L <: RawObject, R <: RawObject](l: L, r: R): RawTensor[L, R]
    def leftProjRaw[L <: RawObject, R <: RawObject](t: RawTensor[L, R]): L
    def rightProjRaw[L <: RawObject, R <: RawObject](t: RawTensor[L, R]): R

    def matchUpRaw[X <: RawObject](t: RawTensor[X, X])
      (implicit m: Matchable[X]): X =
        m.matchUp(leftProjRaw(t), rightProjRaw(t))

    def fromUnitRaw[X <: RawObject](u: RawUnit, e: AnyGraphObject)
      (implicit fu: FromUnit[RawUnit, X]): X =
        fu.fromUnit(u, e)

    def toUnitRaw[X <: RawObject](x: X): RawUnit


    // IL ⊗ IR → OL ⊗ OR
    implicit final def eval_tensor[
      IL <: RawObject, IR <: RawObject,
      OL <: RawObject, OR <: RawObject,
      L <: AnyGraphMorphism, R <: AnyGraphMorphism
    ](implicit
      evalLeft:  Eval[IL, L, OL],
      evalRight: Eval[IR, R, OR]
    ):  Eval[RawTensor[IL, IR], TensorMorph[L, R], RawTensor[OL, OR]] =
    new Eval[RawTensor[IL, IR], TensorMorph[L, R], RawTensor[OL, OR]] {

      def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal =>
        construct[OL, OR](
          evalLeft.rawApply(morph.left)  ( leftProjRaw[IL, IR](inVal) ),
          evalRight.rawApply(morph.right)( rightProjRaw[IL, IR](inVal) )
        )
      }

      def present(morph: InMorph): String = s"(${evalLeft.present(morph.left)} ⊗ ${evalRight.present(morph.right)})"
    }

    // △: X → X ⊗ X
    implicit final def eval_duplicate[
      I <: RawObject, T <: AnyGraphObject
    ]:  Eval[I, duplicate[T], RawTensor[I, I]] =
    new Eval[I, duplicate[T], RawTensor[I, I]] {

      def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal =>
        construct[I, I](inVal, inVal)
      }

      def present(morph: InMorph): String = morph.label
    }

    // ▽: X ⊗ X → X
    implicit final def eval_matchUp[
      O <: RawObject, T <: AnyGraphObject
    ](implicit
      matchable: Matchable[O]
    ):  Eval[RawTensor[O, O], matchUp[T], O] =
    new Eval[RawTensor[O, O], matchUp[T], O] {

      def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal =>
        matchUpRaw(inVal)
      }

      def present(morph: InMorph): String = morph.label
    }


    // I → X
    implicit final def eval_fromUnit[
      T <: AnyGraphObject, O <: RawObject
    ](implicit
      fu: FromUnit[RawUnit, O]
    ):  Eval[RawUnit, fromUnit[T], O] =
    new Eval[RawUnit, fromUnit[T], O] {

      def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal =>
        fromUnitRaw(inVal, morph.obj)
      }

      def present(morph: InMorph): String = morph.label
    }

    // X → I
    implicit final def eval_toUnit[
      T <: AnyGraphObject, I <: RawObject
    ]:  Eval[I, toUnit[T], RawUnit] =
    new Eval[I, toUnit[T], RawUnit] {

      def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal =>
        toUnitRaw(inVal)
      }

      def present(morph: InMorph): String = morph.label
    }

  }

  trait GraphStructure extends AnyStructure {

    type RawEdge
    type RawSource
    type RawTarget

    def outVRaw(edge: AnyEdge)(v: RawSource): RawTarget
    def inVRaw(edge: AnyEdge)(v: RawTarget): RawSource


    implicit final def eval_outV[
      E <: AnyEdge
    ]:  Eval[RawSource, outV[E], RawTarget] =
    new Eval[RawSource, outV[E], RawTarget] {

      def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal =>
        outVRaw(morph.edge)(inVal)
      }

      def present(morph: InMorph): String = morph.label
    }

    implicit final def eval_inV[
      E <: AnyEdge
    ]:  Eval[RawTarget, inV[E], RawSource] =
    new Eval[RawTarget, inV[E], RawSource] {

      def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal =>
        inVRaw(morph.edge)(inVal)
      }

      def present(morph: InMorph): String = morph.label
    }


    /*
    implicit final def eval_outE[
      I, E <: AnyEdge, OE, OV
    ](implicit
      vImpl:  VertexOutImpl[E, I, OE, OV]
    ):  Eval[I, outE[E], OE] =
    new Eval[I, outE[E], OE] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := vImpl.outE(input.value, morph.edge)
      }

      def present(morph: InMorph): String = morph.label
    }

    implicit final def eval_source[
      E <: AnyEdge, I, S, T
    ](implicit
      eImpl: EdgeImpl[I, S, T]
    ):  Eval[I, source[E], S] =
    new Eval[I, source[E], S] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        (morph.out: InMorph#Out) := eImpl.source(input.value)
      }

      def present(morph: InMorph): String = morph.label
    }


    implicit final def eval_inE[
      I, E <: AnyEdge, IE, IV
    ](implicit
      vImpl:  VertexInImpl[E, I, IE, IV]
    ):  Eval[I, inE[E], IE] =
    new Eval[I, inE[E], IE] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := vImpl.inE(input.value, morph.edge)
      }

      def present(morph: InMorph): String = morph.label
    }

    implicit final def eval_target[
      E <: AnyEdge, I, S, T
    ](implicit
      eImpl: EdgeImpl[I, S, T]
    ):  Eval[I, target[E], T] =
    new Eval[I, target[E], T] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        (morph.out: InMorph#Out) := eImpl.target(input.value)
      }

      def present(morph: InMorph): String = morph.label
    }
    */

  }

/*
  trait BiproductStructure extends AnyStructure {

    // IL ⊕ IR → OL ⊕ OR
    implicit final def eval_biproduct[
      IL, IR, I,
      L <: AnyGraphMorphism, R <: AnyGraphMorphism,
      OL, OR, O
    ](implicit
      inBip:  BiproductImpl[I, IL, IR],
      outBip: BiproductImpl[O, OL, OR],
      evalLeft:  Eval[IL, L, OL],
      evalRight: Eval[IR, R, OR]
    ):  Eval[I, BiproductMorph[L, R], O] =
    new Eval[I, BiproductMorph[L, R], O] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := outBip(
          evalLeft(morph.left)  ( (morph.left.in:  L#In) := inBip.leftProj(input.value) ).value,
          evalRight(morph.right)( (morph.right.in: R#In) := inBip.rightProj(input.value) ).value
        )
      }

      def present(morph: InMorph): String = s"(${evalLeft.present(morph.left)} ⊕ ${evalRight.present(morph.right)})"
    }

    // X → X ⊕ X
    implicit final def eval_fork[
      I, T <: AnyGraphObject, O
    ](implicit
      outBip: BiproductImpl[O, I, I]
    ):  Eval[I, fork[T], O] =
    new Eval[I, fork[T], O] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := outBip(input.value, input.value)
      }

      def present(morph: InMorph): String = morph.label
    }

    // X ⊕ X → X
    implicit final def eval_merge[
      I, T <: AnyGraphObject, O
    ](implicit
      bipImpl: BiproductImpl[I, O, O],
      mergeImpl: MergeImpl[O]
    ):  Eval[I, merge[T], O] =
    new Eval[I, merge[T], O] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := mergeImpl.merge(bipImpl.leftProj(input.value), bipImpl.rightProj(input.value))
      }

      def present(morph: InMorph): String = morph.label
    }

    // L → L ⊕ R
    implicit final def eval_leftInj[
      L <: AnyGraphObject, R <: AnyGraphObject,
      I, OR, O
    ](implicit
      outBip: BiproductImpl[O, I, OR]
    ):  Eval[I, leftInj[L ⊕ R], O] =
    new Eval[I, leftInj[L ⊕ R], O] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := outBip.leftInj(input.value)
      }

      def present(morph: InMorph): String = morph.label
    }

    // R → L ⊕ R
    implicit final def eval_rightInj[
      L <: AnyGraphObject, R <: AnyGraphObject,
      OL, OR, O
    ](implicit
      outBip: BiproductImpl[O, OL, OR]
    ):  Eval[OR, rightInj[L ⊕ R], O] =
    new Eval[OR, rightInj[L ⊕ R], O] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := outBip.rightInj(input.value)
      }

      def present(morph: InMorph): String = morph.label
    }

    // L ⊕ R → L
    implicit final def eval_leftProj[
      IL, IR, I,
      L <: AnyGraphObject, R <: AnyGraphObject
    ](implicit
      outBip: BiproductImpl[I, IL, IR]
    ):  Eval[I, leftProj[L ⊕ R], IL] =
    new Eval[I, leftProj[L ⊕ R], IL] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := outBip.leftProj(input.value)
      }

      def present(morph: InMorph): String = morph.label
    }

    // L ⊕ R → R
    implicit final def eval_rightProj[
      IL, IR, I,
      L <: AnyGraphObject, R <: AnyGraphObject
    ](implicit
      outBip: BiproductImpl[I, IL, IR]
    ):  Eval[I, rightProj[L ⊕ R], IR] =
    new Eval[I, rightProj[L ⊕ R], IR] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := outBip.rightProj(input.value)
      }

      def present(morph: InMorph): String = morph.label
    }

    // 0 → X
    implicit final def eval_fromZero[
      I, X <: AnyGraphObject, O
    ](implicit
      inZero:  ZeroImpl[I],
      outZero: ZeroImpl[O]
    ):  Eval[I, fromZero[X], O] =
    new Eval[I, fromZero[X], O] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := outZero()
      }

      def present(morph: InMorph): String = morph.label
    }

    // X → 0
    implicit final def eval_toZero[
      I, T, X <: AnyGraphObject, O
    ](implicit
      inZero:  ZeroImpl[I],
      outZero: ZeroImpl[O]
    ):  Eval[I, toZero[X], O] =
    new Eval[I, toZero[X], O] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := outZero()
      }

      def present(morph: InMorph): String = morph.label
    }


    implicit final def eval_get[
      P <: AnyProperty, RawElem, RawValue
    ](implicit
      propImpl: PropertyImpl[P, RawElem, RawValue]
    ):  Eval[RawElem, get[P], RawValue] =
    new Eval[RawElem, get[P], RawValue] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        (morph.out: InMorph#Out) := propImpl.get(input.value, morph.property)
      }

      def present(morph: InMorph): String = morph.label
    }

    implicit final def eval_lookup[
      P <: AnyProperty, RawElem, RawValue
    ](implicit
      propImpl: PropertyImpl[P, RawElem, RawValue]
    ):  Eval[RawValue, lookup[P], RawElem] =
    new Eval[RawValue, lookup[P], RawElem] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        (morph.out: InMorph#Out) := propImpl.lookup(input.value, morph.property)
      }

      def present(morph: InMorph): String = morph.label
    }


    implicit final def eval_quantify[
      P <: AnyPredicate, RawPred, RawElem
    ](implicit
      predImpl: PredicateImpl[P, RawPred, RawElem]
    ):  Eval[RawElem, quantify[P], RawPred] =
    new Eval[RawElem, quantify[P], RawPred] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        (morph.out: InMorph#Out) := predImpl.quantify(input.value, morph.predicate)
      }

      def present(morph: InMorph): String = morph.label
    }


    implicit final def eval_coerce[
      P <: AnyPredicate, RawPred, RawElem
    ](implicit
      predImpl: PredicateImpl[P, RawPred, RawElem]
    ):  Eval[RawPred, coerce[P], RawElem] =
    new Eval[RawPred, coerce[P], RawElem] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        (morph.out: InMorph#Out) := predImpl.coerce(input.value)
      }

      def present(morph: InMorph): String = morph.label
    }

  }
*/

}
