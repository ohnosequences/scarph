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

    def present(morph: InMorph): Seq[String]
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
    final def evalPlan: String = eval.present(f).mkString("")
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

      final def present(morph: InMorph): Seq[String] = Seq(morph.label)
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

      def present(morph: InMorph): Seq[String] =
        ("(" +: evalFirst.present(morph.first)) ++
        (" >=> " +: evalSecond.present(morph.second) :+ ")")
    }

  }

  trait Matchable[T0] {

    type T = T0
    def matchUp(l: T, r: T): T
  }

  trait FromUnit[U0, T0] {

    type U = U0
    type T = T0
    def fromUnit(u: U, o: AnyGraphObject): T
  }

  object FromUnit {

    implicit def unitToUnit[U]:
        FromUnit[U, U] =
    new FromUnit[U, U] { def fromUnit(u: U, o: AnyGraphObject): T = u }
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

    def fromUnitRaw[X <: RawObject](o: AnyGraphObject)(u: RawUnit)
      (implicit fu: FromUnit[RawUnit, X]): X =
        fu.fromUnit(u, o)

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

      def present(morph: InMorph): Seq[String] =
        ("(" +: evalLeft.present(morph.left)) ++
        (" ⊗ " +: evalRight.present(morph.right) :+ ")")
    }

    // △: X → X ⊗ X
    implicit final def eval_duplicate[
      I <: RawObject, T <: AnyGraphObject
    ]:  Eval[I, duplicate[T], RawTensor[I, I]] =
    new Eval[I, duplicate[T], RawTensor[I, I]] {

      def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal =>
        construct[I, I](inVal, inVal)
      }

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

    // ▽: X ⊗ X → X
    implicit final def eval_matchUp[
      O <: RawObject, T <: AnyGraphObject
    ](implicit
      matchable: Matchable[O]
    ):  Eval[RawTensor[O, O], matchUp[T], O] =
    new Eval[RawTensor[O, O], matchUp[T], O] {

      def rawApply(morph: InMorph): InVal => OutVal = matchUpRaw

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }


    // I → X
    implicit final def eval_fromUnit[
      T <: AnyGraphObject, O <: RawObject
    ](implicit
      fu: FromUnit[RawUnit, O]
    ):  Eval[RawUnit, fromUnit[T], O] =
    new Eval[RawUnit, fromUnit[T], O] {

      def rawApply(morph: InMorph): InVal => OutVal = fromUnitRaw[O](morph.obj)

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

    // X → I
    implicit final def eval_toUnit[
      T <: AnyGraphObject, I <: RawObject
    ]:  Eval[I, toUnit[T], RawUnit] =
    new Eval[I, toUnit[T], RawUnit] {

      def rawApply(morph: InMorph): InVal => OutVal = toUnitRaw

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

  }

  trait GraphStructure extends AnyStructure {

    type RawEdge
    type RawSource
    type RawTarget

    def outVRaw(edge: AnyEdge)(v: RawSource): RawTarget
    def inVRaw(edge: AnyEdge)(v: RawTarget): RawSource

    def outERaw(edge: AnyEdge)(v: RawSource): RawEdge
    def sourceRaw(edge: AnyEdge)(e: RawEdge): RawSource

    def inERaw(edge: AnyEdge)(v: RawTarget): RawEdge
    def targetRaw(edge: AnyEdge)(e: RawEdge): RawTarget


    implicit final def eval_outV[
      E <: AnyEdge
    ]:  Eval[RawSource, outV[E], RawTarget] =
    new Eval[RawSource, outV[E], RawTarget] {

      def rawApply(morph: InMorph): InVal => OutVal = outVRaw(morph.edge)

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

    implicit final def eval_inV[
      E <: AnyEdge
    ]:  Eval[RawTarget, inV[E], RawSource] =
    new Eval[RawTarget, inV[E], RawSource] {

      def rawApply(morph: InMorph): InVal => OutVal = inVRaw(morph.edge)

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }


    implicit final def eval_outE[
      E <: AnyEdge
    ]:  Eval[RawSource, outE[E], RawEdge] =
    new Eval[RawSource, outE[E], RawEdge] {

      def rawApply(morph: InMorph): InVal => OutVal = outERaw(morph.edge)

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

    implicit final def eval_source[
      E <: AnyEdge
    ]:  Eval[RawEdge, source[E], RawSource] =
    new Eval[RawEdge, source[E], RawSource] {

      def rawApply(morph: InMorph): InVal => OutVal = sourceRaw(morph.edge)

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }


    implicit final def eval_inE[
      E <: AnyEdge
    ]:  Eval[RawTarget, inE[E], RawEdge] =
    new Eval[RawTarget, inE[E], RawEdge] {

      def rawApply(morph: InMorph): InVal => OutVal = inERaw(morph.edge)

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

    implicit final def eval_target[
      E <: AnyEdge
    ]:  Eval[RawEdge, target[E], RawTarget] =
    new Eval[RawEdge, target[E], RawTarget] {

      def rawApply(morph: InMorph): InVal => OutVal = targetRaw(morph.edge)

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

  }


  trait Mergeable[T0] {

    type T = T0
    def merge(l: T, r: T): T
  }

  trait ZeroFor[T0] {

    type T = T0
    def zero(o: AnyGraphObject): T
  }

  trait BiproductStructure extends AnyStructure {

    type RawBiproduct[L <: RawObject, R <: RawObject]
    type RawZero

    def construct[L <: RawObject, R <: RawObject](l: L, r: R): RawBiproduct[L, R]
    def leftProjRaw[L <: RawObject, R <: RawObject](t: RawBiproduct[L, R]): L
    def rightProjRaw[L <: RawObject, R <: RawObject](t: RawBiproduct[L, R]): R

    def mergeRaw[X <: RawObject](t: RawBiproduct[X, X])
      (implicit m: Mergeable[X]): X =
        m.merge(leftProjRaw(t), rightProjRaw(t))

    def fromZeroRaw[X <: RawObject](o: AnyGraphObject)(u: RawZero)
      (implicit z: ZeroFor[X]): X =
        z.zero(o)

    def toZeroRaw[X <: RawObject](x: X): RawZero

    // IL ⊕ IR → OL ⊕ OR
    implicit final def eval_biproduct[
      IL <: RawObject, IR <: RawObject,
      OL <: RawObject, OR <: RawObject,
      L <: AnyGraphMorphism, R <: AnyGraphMorphism
    ](implicit
      evalLeft:  Eval[IL, L, OL],
      evalRight: Eval[IR, R, OR]
    ):  Eval[RawBiproduct[IL, IR], BiproductMorph[L, R], RawBiproduct[OL, OR]] =
    new Eval[RawBiproduct[IL, IR], BiproductMorph[L, R], RawBiproduct[OL, OR]] {

      def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal =>
        construct[OL, OR](
          evalLeft.rawApply(morph.left)  ( leftProjRaw[IL, IR](inVal) ),
          evalRight.rawApply(morph.right)( rightProjRaw[IL, IR](inVal) )
        )
      }

      def present(morph: InMorph): Seq[String] =
        ("(" +: evalLeft.present(morph.left)) ++
        (" ⊕ " +: evalRight.present(morph.right) :+ ")")
    }

    // △: X → X ⊗ X
    implicit final def eval_fork[
      I <: RawObject, T <: AnyGraphObject
    ]:  Eval[I, fork[T], RawBiproduct[I, I]] =
    new Eval[I, fork[T], RawBiproduct[I, I]] {

      def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal =>
        construct[I, I](inVal, inVal)
      }

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

    // ▽: X ⊗ X → X
    implicit final def eval_matchUp[
      O <: RawObject, T <: AnyGraphObject
    ](implicit
      mergeable: Mergeable[O]
    ):  Eval[RawBiproduct[O, O], matchUp[T], O] =
    new Eval[RawBiproduct[O, O], matchUp[T], O] {

      def rawApply(morph: InMorph): InVal => OutVal = mergeRaw

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }


    // I → X
    implicit final def eval_fromZero[
      T <: AnyGraphObject, O <: RawObject
    ](implicit
      z: ZeroFor[O]
    ):  Eval[RawZero, fromZero[T], O] =
    new Eval[RawZero, fromZero[T], O] {

      def rawApply(morph: InMorph): InVal => OutVal = fromZeroRaw[O](morph.obj)

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

    // X → I
    implicit final def eval_toZero[
      T <: AnyGraphObject, I <: RawObject
    ]:  Eval[I, toZero[T], RawZero] =
    new Eval[I, toZero[T], RawZero] {

      def rawApply(morph: InMorph): InVal => OutVal = toZeroRaw

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }


    // L ⊕ R → L
    implicit final def eval_leftProj[
      A <: RawObject, B <: RawObject,
      L <: AnyGraphObject, R <: AnyGraphObject
    ]:  Eval[RawBiproduct[A, B], leftProj[L ⊕ R], A] =
    new Eval[RawBiproduct[A, B], leftProj[L ⊕ R], A] {

      def rawApply(morph: InMorph): InVal => OutVal = leftProjRaw

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

    // L ⊕ R → R
    implicit final def eval_rightProj[
      A <: RawObject, B <: RawObject,
      L <: AnyGraphObject, R <: AnyGraphObject
    ]:  Eval[RawBiproduct[A, B], rightProj[L ⊕ R], B] =
    new Eval[RawBiproduct[A, B], rightProj[L ⊕ R], B] {

      def rawApply(morph: InMorph): InVal => OutVal = rightProjRaw

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }


    // L → L ⊕ R
    implicit final def eval_leftInj[
      A <: RawObject, B <: RawObject,
      L <: AnyGraphObject, R <: AnyGraphObject
    ](implicit
      b: ZeroFor[B]
    ):  Eval[A, leftInj[L ⊕ R], RawBiproduct[A, B]] =
    new Eval[A, leftInj[L ⊕ R], RawBiproduct[A, B]] {

      def rawApply(morph: InMorph): InVal => OutVal =
        construct(_, b.zero(morph.biproduct.right))

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

    // R → L ⊕ R
    implicit final def eval_rightInj[
      A <: RawObject, B <: RawObject,
      L <: AnyGraphObject, R <: AnyGraphObject
    ](implicit
      a: ZeroFor[A]
    ):  Eval[B, rightInj[L ⊕ R], RawBiproduct[A, B]] =
    new Eval[B, rightInj[L ⊕ R], RawBiproduct[A, B]] {

      def rawApply(morph: InMorph): InVal => OutVal =
        construct(a.zero(morph.biproduct.right), _)

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

  }

/*
    implicit final def eval_get[
      P <: AnyProperty, RawElem, RawValue
    ](implicit
      propImpl: PropertyImpl[P, RawElem, RawValue]
    ):  Eval[RawElem, get[P], RawValue] =
    new Eval[RawElem, get[P], RawValue] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        (morph.out: InMorph#Out) := propImpl.get(input.value, morph.property)
      }

      def present(morph: InMorph): Seq[String] = morph.label
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

      def present(morph: InMorph): Seq[String] = morph.label
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

      def present(morph: InMorph): Seq[String] = morph.label
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

      def present(morph: InMorph): Seq[String] = morph.label
    }

  }
*/

}
