package ohnosequences.scarph

object evals {

  import ohnosequences.cosas.types._
  import objects._, morphisms._

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

  def eval[I, IM <: AnyGraphMorphism, O](m: IM)(i: IM#In := I)(implicit
      eval: Eval[I, IM, O]
    ): IM#Out := O =
    new evaluate[I, IM, O](m, eval).on(i)

  class evalWithIn[I] {

    def apply[IM <: AnyGraphMorphism, O](m: IM)(implicit
      eval: Eval[I, IM, O]
    ):  evaluate[I, IM, O] =
    new evaluate[I, IM, O](m, eval)
  }

  def evalOn[I]: evalWithIn[I] = new evalWithIn[I] {}

  class evalWithInOut[I, O] {

    def apply[IM <: AnyGraphMorphism](m: IM)(implicit
      eval: Eval[I, IM, O]
    ):  evaluate[I, IM, O] =
    new evaluate[I, IM, O](m, eval)
  }

  def evalInOut[I, O]: evalWithInOut[I, O] = new evalWithInOut[I, O] {}


  trait CategoryStructure extends CategoryStructure2 {

    implicit final def eval_id[I, X <: AnyGraphObject]:
        Eval[I, id[X], I] =
    new Eval[I, id[X], I] {

      def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal => inVal }

      final def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }


    // F >=> S
    implicit final def eval_composition[
      F <: AnyGraphMorphism,
      S <: AnyGraphMorphism { type In = F#Out },
      I, X, O
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

  trait CategoryStructure2 {

    implicit final def eval_derived[
      I, M <: AnyGraphMorphism, O,
      D <: AnyDerivedMorphism { type Morph <: M }
      // D <: DerivedMorphism[M]
    ](implicit
      inner: Eval[I, M, O]
    ):  Eval[I, D, O] =
    new Eval[I, D, O] {

      def rawApply(morph: InMorph): InVal => OutVal = inner.rawApply(morph.morph)

      def present(morph: InMorph): Seq[String] = inner.present(morph.morph)
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

  trait TensorStructure {

    type TensorBound
    type RawTensor[L <: TensorBound, R <: TensorBound] <: TensorBound
    type RawUnit

    def tensorRaw[L <: TensorBound, R <: TensorBound](l: L, r: R): RawTensor[L, R]
    def leftRaw[L <: TensorBound, R <: TensorBound](t: RawTensor[L, R]): L
    def rightRaw[L <: TensorBound, R <: TensorBound](t: RawTensor[L, R]): R

    def matchUpRaw[X <: TensorBound](t: RawTensor[X, X])
      (implicit m: Matchable[X]): X =
        m.matchUp(leftRaw(t), rightRaw(t))

    def fromUnitRaw[X <: TensorBound](o: AnyGraphObject)(u: RawUnit)
      (implicit fu: FromUnit[RawUnit, X]): X =
        fu.fromUnit(u, o)

    def toUnitRaw[X <: TensorBound](x: X): RawUnit


    // IL ⊗ IR → OL ⊗ OR
    implicit final def eval_tensor[
      IL <: TensorBound, IR <: TensorBound,
      OL <: TensorBound, OR <: TensorBound,
      L <: AnyGraphMorphism, R <: AnyGraphMorphism
    ](implicit
      evalLeft:  Eval[IL, L, OL],
      evalRight: Eval[IR, R, OR]
    ):  Eval[RawTensor[IL, IR], TensorMorph[L, R], RawTensor[OL, OR]] =
    new Eval[RawTensor[IL, IR], TensorMorph[L, R], RawTensor[OL, OR]] {

      def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal =>
        tensorRaw[OL, OR](
          evalLeft.rawApply(morph.left)  ( leftRaw[IL, IR](inVal) ),
          evalRight.rawApply(morph.right)( rightRaw[IL, IR](inVal) )
        )
      }

      def present(morph: InMorph): Seq[String] =
        ("(" +: evalLeft.present(morph.left)) ++
        (" ⊗ " +: evalRight.present(morph.right) :+ ")")
    }

    // △: X → X ⊗ X
    implicit final def eval_duplicate[
      I <: TensorBound, T <: AnyGraphObject
    ]:  Eval[I, duplicate[T], RawTensor[I, I]] =
    new Eval[I, duplicate[T], RawTensor[I, I]] {

      def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal =>
        tensorRaw[I, I](inVal, inVal)
      }

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

    // ▽: X ⊗ X → X
    implicit final def eval_matchUp[
      O <: TensorBound, T <: AnyGraphObject
    ](implicit
      matchable: Matchable[O]
    ):  Eval[RawTensor[O, O], matchUp[T], O] =
    new Eval[RawTensor[O, O], matchUp[T], O] {

      def rawApply(morph: InMorph): InVal => OutVal = matchUpRaw

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }


    // I → X
    implicit final def eval_fromUnit[
      T <: AnyGraphObject, O <: TensorBound
    ](implicit
      fu: FromUnit[RawUnit, O]
    ):  Eval[RawUnit, fromUnit[T], O] =
    new Eval[RawUnit, fromUnit[T], O] {

      def rawApply(morph: InMorph): InVal => OutVal = fromUnitRaw[O](morph.obj)

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

    // X → I
    implicit final def eval_toUnit[
      T <: AnyGraphObject, I <: TensorBound
    ]:  Eval[I, toUnit[T], RawUnit] =
    new Eval[I, toUnit[T], RawUnit] {

      def rawApply(morph: InMorph): InVal => OutVal = toUnitRaw

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

    implicit def fromUnitTensor[U, L <: TensorBound, R <: TensorBound]
    (implicit
      l: FromUnit[U, L],
      r: FromUnit[U, R]
    ):  FromUnit[U, RawTensor[L, R]] =
    new FromUnit[U, RawTensor[L, R]] {

      def fromUnit(u: U, o: AnyGraphObject): T = tensorRaw(l.fromUnit(u, o), r.fromUnit(u, o))
    }

    implicit final def eval_associateLeft[
      A <: AnyGraphObject, B <: AnyGraphObject, C <: AnyGraphObject,
      X <: TensorBound, Y <: TensorBound, Z <: TensorBound
    ]:  Eval[RawTensor[X, RawTensor[Y, Z]], associateLeft[A, B, C], RawTensor[RawTensor[X, Y], Z]] =
    new Eval[RawTensor[X, RawTensor[Y, Z]], associateLeft[A, B, C], RawTensor[RawTensor[X, Y], Z]] {

      def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal =>
        val x: X = leftRaw(inVal)
        val y: Y = leftRaw(rightRaw(inVal))
        val z: Z = rightRaw(rightRaw(inVal))

        tensorRaw(tensorRaw(x, y), z)
      }

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

    implicit final def eval_associateRight[
      A <: AnyGraphObject, B <: AnyGraphObject, C <: AnyGraphObject,
      X <: TensorBound, Y <: TensorBound, Z <: TensorBound
    ]:  Eval[RawTensor[RawTensor[X, Y], Z], associateRight[A, B, C], RawTensor[X, RawTensor[Y, Z]]] =
    new Eval[RawTensor[RawTensor[X, Y], Z], associateRight[A, B, C], RawTensor[X, RawTensor[Y, Z]]] {

      def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal =>
        val x: X = leftRaw(leftRaw(inVal))
        val y: Y = rightRaw(leftRaw(inVal))
        val z: Z = rightRaw(inVal)

        tensorRaw(x, tensorRaw(y, z))
      }

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

  }

  trait GraphStructure {

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

  trait ZeroFor[O <: AnyGraphObject, T0] {

    type Obj = O
    type T = T0
    def zero(o: Obj): T
  }

  trait BiproductStructure {

    type BiproductBound
    type RawBiproduct[L <: BiproductBound, R <: BiproductBound]
    type RawZero

    def biproductRaw[L <: BiproductBound, R <: BiproductBound](l: L, r: R): RawBiproduct[L, R]
    def leftProjRaw[L <: BiproductBound, R <: BiproductBound](t: RawBiproduct[L, R]): L
    def rightProjRaw[L <: BiproductBound, R <: BiproductBound](t: RawBiproduct[L, R]): R

    def mergeRaw[X <: BiproductBound](t: RawBiproduct[X, X])
      (implicit m: Mergeable[X]): X =
        m.merge(leftProjRaw(t), rightProjRaw(t))

    def toZeroRaw[X <: BiproductBound](x: X): RawZero

    // IL ⊕ IR → OL ⊕ OR
    implicit final def eval_biproduct[
      IL <: BiproductBound, IR <: BiproductBound,
      OL <: BiproductBound, OR <: BiproductBound,
      L <: AnyGraphMorphism, R <: AnyGraphMorphism
    ](implicit
      evalLeft:  Eval[IL, L, OL],
      evalRight: Eval[IR, R, OR]
    ):  Eval[RawBiproduct[IL, IR], BiproductMorph[L, R], RawBiproduct[OL, OR]] =
    new Eval[RawBiproduct[IL, IR], BiproductMorph[L, R], RawBiproduct[OL, OR]] {

      def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal =>
        biproductRaw[OL, OR](
          evalLeft.rawApply(morph.left)  ( leftProjRaw[IL, IR](inVal) ),
          evalRight.rawApply(morph.right)( rightProjRaw[IL, IR](inVal) )
        )
      }

      def present(morph: InMorph): Seq[String] =
        ("(" +: evalLeft.present(morph.left)) ++
        (" ⊕ " +: evalRight.present(morph.right) :+ ")")
    }

    // X → X ⊕ X
    implicit final def eval_fork[
      I <: BiproductBound, T <: AnyGraphObject
    ]:  Eval[I, fork[T], RawBiproduct[I, I]] =
    new Eval[I, fork[T], RawBiproduct[I, I]] {

      def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal =>
        biproductRaw[I, I](inVal, inVal)
      }

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

    // X ⊕ X → X
    implicit final def eval_merge[
      O <: BiproductBound, T <: AnyGraphObject
    ](implicit
      mergeable: Mergeable[O]
    ):  Eval[RawBiproduct[O, O], merge[T], O] =
    new Eval[RawBiproduct[O, O], merge[T], O] {

      def rawApply(morph: InMorph): InVal => OutVal = mergeRaw

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }


    // I → X
    implicit final def eval_fromZero[
      T <: AnyGraphObject, O <: BiproductBound
    ](implicit
      z: ZeroFor[T, O]
    ):  Eval[RawZero, fromZero[T], O] =
    new Eval[RawZero, fromZero[T], O] {

      def rawApply(morph: InMorph): InVal => OutVal = _ => z.zero(morph.obj)

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

    // X → I
    implicit final def eval_toZero[
      T <: AnyGraphObject, I <: BiproductBound
    ]:  Eval[I, toZero[T], RawZero] =
    new Eval[I, toZero[T], RawZero] {

      def rawApply(morph: InMorph): InVal => OutVal = toZeroRaw

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }


    // L ⊕ R → L
    implicit final def eval_leftProj[
      A <: BiproductBound, B <: BiproductBound,
      L <: AnyGraphObject, R <: AnyGraphObject
    ]:  Eval[RawBiproduct[A, B], leftProj[L ⊕ R], A] =
    new Eval[RawBiproduct[A, B], leftProj[L ⊕ R], A] {

      def rawApply(morph: InMorph): InVal => OutVal = leftProjRaw

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

    // L ⊕ R → R
    implicit final def eval_rightProj[
      A <: BiproductBound, B <: BiproductBound,
      L <: AnyGraphObject, R <: AnyGraphObject
    ]:  Eval[RawBiproduct[A, B], rightProj[L ⊕ R], B] =
    new Eval[RawBiproduct[A, B], rightProj[L ⊕ R], B] {

      def rawApply(morph: InMorph): InVal => OutVal = rightProjRaw

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }


    // L → L ⊕ R
    implicit final def eval_leftInj[
      A <: BiproductBound, B <: BiproductBound,
      L <: AnyGraphObject, R <: AnyGraphObject
    ](implicit
      b: ZeroFor[R, B]
    ):  Eval[A, leftInj[L ⊕ R], RawBiproduct[A, B]] =
    new Eval[A, leftInj[L ⊕ R], RawBiproduct[A, B]] {

      def rawApply(morph: InMorph): InVal => OutVal =
        biproductRaw(_, b.zero(morph.biproduct.right))

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

    // R → L ⊕ R
    implicit final def eval_rightInj[
      A <: BiproductBound, B <: BiproductBound,
      L <: AnyGraphObject, R <: AnyGraphObject
    ](implicit
      a: ZeroFor[L, A]
    ):  Eval[B, rightInj[L ⊕ R], RawBiproduct[A, B]] =
    new Eval[B, rightInj[L ⊕ R], RawBiproduct[A, B]] {

      def rawApply(morph: InMorph): InVal => OutVal =
        biproductRaw(a.zero(morph.biproduct.left), _)

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

    implicit def zeroForBiproduct[
      LO <: AnyGraphObject, L <: BiproductBound,
      RO <: AnyGraphObject, R <: BiproductBound
    ](implicit
      l: ZeroFor[LO, L],
      r: ZeroFor[RO, R]
    ):  ZeroFor[BiproductObj[LO, RO], RawBiproduct[L, R]] =
    new ZeroFor[BiproductObj[LO, RO], RawBiproduct[L, R]] {

      def zero(o: Obj): T = biproductRaw(l.zero(o.left), r.zero(o.right))
    }

    implicit def fromUnitBiproduct[U, L <: BiproductBound, R <: BiproductBound]
    (implicit
      l: FromUnit[U, L],
      r: FromUnit[U, R]
    ):  FromUnit[U, RawBiproduct[L, R]] =
    new FromUnit[U, RawBiproduct[L, R]] {

      def fromUnit(u: U, o: AnyGraphObject): T = biproductRaw(l.fromUnit(u, o), r.fromUnit(u, o))
    }

  }

}
