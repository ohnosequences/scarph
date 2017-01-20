package ohnosequences.scarph.impl

import ohnosequences.scarph._

trait Mergeable[T0] {

  type T = T0
  def merge(l: T, r: T): T
}

trait ZeroFor[O <: AnyGraphObject, T0] {

  type Obj = O
  type T = T0
  def zero(o: Obj): T
}

trait Biproducts {

  type BiproductBound
  type RawBiproduct[L <: BiproductBound, R <: BiproductBound]
  type RawZero

  def biproductRaw[L <: BiproductBound, R <: BiproductBound](l: L, r: R): RawBiproduct[L, R]
  def  leftProjRaw[L <: BiproductBound, R <: BiproductBound](t: RawBiproduct[L, R]): L
  def rightProjRaw[L <: BiproductBound, R <: BiproductBound](t: RawBiproduct[L, R]): R

  def mergeRaw[X <: BiproductBound](t: RawBiproduct[X, X])
    (implicit m: Mergeable[X]): X =
      m.merge(leftProjRaw(t), rightProjRaw(t))

  def toZeroRaw[X <: BiproductBound](x: X): RawZero

  // IL ⊕ IR → OL ⊕ OR
  implicit final def eval_biproduct[
    IL <: BiproductBound, IR <: BiproductBound,
    OL <: BiproductBound, OR <: BiproductBound,
    L <: AnyGraphMorphism {
      type In <: AnyGraphObject { type Raw >: IL }
      type Out <: AnyGraphObject { type Raw >: OL }
    },
    R <: AnyGraphMorphism {
      type In <: AnyGraphObject { type Raw >: IR }
      type Out <: AnyGraphObject { type Raw >: OR }
    }
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
    O <: BiproductBound, T <: AnyGraphObject
  ](implicit
    z: ZeroFor[T, O]
  ):  Eval[RawZero, fromZero[T], O] =
  new Eval[RawZero, fromZero[T], O] {

    def rawApply(morph: InMorph): InVal => OutVal = _ => z.zero(morph.obj)

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }

  // X → I
  implicit final def eval_toZero[
    I <: BiproductBound, T <: AnyGraphObject
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
