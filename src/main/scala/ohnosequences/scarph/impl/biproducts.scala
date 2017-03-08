package ohnosequences.scarph.impl

import ohnosequences.scarph._

trait RawMerge[T0] {

  type T = T0
  def apply(l: T, r: T): T
}

// TODO: I think object type is not needed here, this should be just FromZero[X]
trait ZeroFor[O <: AnyGraphObject, T0] {

  type Obj = O
  type T = T0
  def zero(o: Obj): T
}

trait Biproducts {

  type BiproductBound
  type RawBiproduct[L <: BiproductBound, R <: BiproductBound] <: BiproductBound
  type RawZero <: BiproductBound

  def raw_biproduct[L <: BiproductBound, R <: BiproductBound](l: L, r: R): RawBiproduct[L, R]
  def raw_leftProj [L <: BiproductBound, R <: BiproductBound](t: RawBiproduct[L, R]): L
  def raw_rightProj[L <: BiproductBound, R <: BiproductBound](t: RawBiproduct[L, R]): R

  def raw_toZero[X <: BiproductBound](x: X): RawZero

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

    def raw_apply(morph: InMorph): RawInput => RawOutput = { raw_input: RawInput =>
      raw_biproduct[OL, OR](
        evalLeft.raw_apply(morph.left)  ( raw_leftProj[IL, IR](raw_input) ),
        evalRight.raw_apply(morph.right)( raw_rightProj[IL, IR](raw_input) )
      )
    }

    override def present(morph: InMorph): Seq[String] =
      ("(" +: evalLeft.present(morph.left)) ++
      (" ⊕ " +: evalRight.present(morph.right) :+ ")")
  }

  // X → X ⊕ X
  implicit final def eval_fork[
    I <: BiproductBound, T <: AnyGraphObject
  ]:  Eval[I, fork[T], RawBiproduct[I, I]] =
  new Eval[I, fork[T], RawBiproduct[I, I]] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = { raw_input: RawInput =>
      raw_biproduct[I, I](raw_input, raw_input)
    }
  }

  // X ⊕ X → X
  implicit final def eval_merge[
    O <: BiproductBound, T <: AnyGraphObject
  ](implicit
    raw_merge: RawMerge[O]
  ):  Eval[RawBiproduct[O, O], merge[T], O] =
  new Eval[RawBiproduct[O, O], merge[T], O] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = { raw_input =>
      raw_merge(
        raw_leftProj(raw_input),
        raw_rightProj(raw_input)
      )
    }
  }


  // 0 → T0
  implicit final def eval_fromZero[
    T <: AnyGraphObject, T0 <: BiproductBound
  ](implicit
    z: ZeroFor[T, T0]
  ):  Eval[RawZero, fromZero[T], T0] =
  new Eval[RawZero, fromZero[T], T0] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = _ => z.zero(morph.obj)
  }

  // T0 → 0
  implicit final def eval_toZero[
    T0 <: BiproductBound, T <: AnyGraphObject
  ]:  Eval[T0, toZero[T], RawZero] =
  new Eval[T0, toZero[T], RawZero] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = raw_toZero
  }

  // L ⊕ R → L
  implicit final def eval_leftProj[
    A <: BiproductBound, B <: BiproductBound,
    L <: AnyGraphObject, R <: AnyGraphObject
  ]:  Eval[RawBiproduct[A, B], leftProj[L ⊕ R], A] =
  new Eval[RawBiproduct[A, B], leftProj[L ⊕ R], A] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = raw_leftProj
  }

  // L ⊕ R → R
  implicit final def eval_rightProj[
    A <: BiproductBound, B <: BiproductBound,
    L <: AnyGraphObject, R <: AnyGraphObject
  ]:  Eval[RawBiproduct[A, B], rightProj[L ⊕ R], B] =
  new Eval[RawBiproduct[A, B], rightProj[L ⊕ R], B] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = raw_rightProj
  }


  // L → L ⊕ R
  implicit final def eval_leftInj[
    A <: BiproductBound, B <: BiproductBound,
    L <: AnyGraphObject, R <: AnyGraphObject
  ](implicit
    b: ZeroFor[R, B]
  ):  Eval[A, leftInj[L ⊕ R], RawBiproduct[A, B]] =
  new Eval[A, leftInj[L ⊕ R], RawBiproduct[A, B]] {

    def raw_apply(morph: InMorph): RawInput => RawOutput =
      raw_biproduct(_, b.zero(morph.biproduct.right))
  }

  // R → L ⊕ R
  implicit final def eval_rightInj[
    A <: BiproductBound, B <: BiproductBound,
    L <: AnyGraphObject, R <: AnyGraphObject
  ](implicit
    a: ZeroFor[L, A]
  ):  Eval[B, rightInj[L ⊕ R], RawBiproduct[A, B]] =
  new Eval[B, rightInj[L ⊕ R], RawBiproduct[A, B]] {

    def raw_apply(morph: InMorph): RawInput => RawOutput =
      raw_biproduct(a.zero(morph.biproduct.left), _)
  }

  implicit def zeroForBiproduct[
    LO <: AnyGraphObject, L <: BiproductBound,
    RO <: AnyGraphObject, R <: BiproductBound
  ](implicit
    l: ZeroFor[LO, L],
    r: ZeroFor[RO, R]
  ):  ZeroFor[BiproductObj[LO, RO], RawBiproduct[L, R]] =
  new ZeroFor[BiproductObj[LO, RO], RawBiproduct[L, R]] {

    def zero(o: Obj): T = raw_biproduct(l.zero(o.left), r.zero(o.right))
  }

  implicit def fromUnitBiproduct[U, L <: BiproductBound, R <: BiproductBound]
  (implicit
    l: FromUnit[U, L],
    r: FromUnit[U, R]
  ):  FromUnit[U, RawBiproduct[L, R]] =
  new FromUnit[U, RawBiproduct[L, R]] {

    def fromUnit(u: U, o: AnyGraphObject): T = raw_biproduct(l.fromUnit(u, o), r.fromUnit(u, o))
  }

  // Isomorphisms

  implicit final def eval_associateBiproductLeft[
    A <: AnyGraphObject, B <: AnyGraphObject, C <: AnyGraphObject,
    X <: BiproductBound, Y <: BiproductBound, Z <: BiproductBound
  ]:  Eval[RawBiproduct[X, RawBiproduct[Y, Z]], associateBiproductLeft[A, B, C], RawBiproduct[RawBiproduct[X, Y], Z]] =
  new Eval[RawBiproduct[X, RawBiproduct[Y, Z]], associateBiproductLeft[A, B, C], RawBiproduct[RawBiproduct[X, Y], Z]] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = { raw_input: RawInput =>
      val x: X = raw_leftProj(raw_input)
      val y: Y = raw_leftProj(raw_rightProj(raw_input))
      val z: Z = raw_rightProj(raw_rightProj(raw_input))

      raw_biproduct(raw_biproduct(x, y), z)
    }
  }

  implicit final def eval_associateBiproductRight[
    A <: AnyGraphObject, B <: AnyGraphObject, C <: AnyGraphObject,
    X <: BiproductBound, Y <: BiproductBound, Z <: BiproductBound
  ]:  Eval[RawBiproduct[RawBiproduct[X, Y], Z], associateBiproductRight[A, B, C], RawBiproduct[X, RawBiproduct[Y, Z]]] =
  new Eval[RawBiproduct[RawBiproduct[X, Y], Z], associateBiproductRight[A, B, C], RawBiproduct[X, RawBiproduct[Y, Z]]] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = { raw_input: RawInput =>
      val x: X = raw_leftProj(raw_leftProj(raw_input))
      val y: Y = raw_rightProj(raw_leftProj(raw_input))
      val z: Z = raw_rightProj(raw_input)

      raw_biproduct(x, raw_biproduct(y, z))
    }
  }

  implicit final def eval_leftZero[
    X <: BiproductBound, T <: AnyGraphObject
  ]:  Eval[RawBiproduct[RawZero, X], leftZero[T], X] =
  new Eval[RawBiproduct[RawZero, X], leftZero[T], X] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = raw_rightProj
  }

  implicit final def eval_leftCozero[
    X <: BiproductBound, T <: AnyGraphObject
  ]:  Eval[X, leftCozero[T], RawBiproduct[RawZero, X]] =
  new Eval[X, leftCozero[T], RawBiproduct[RawZero, X]] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = { raw_input: RawInput =>
      raw_biproduct(raw_toZero[X](raw_input), raw_input)
    }
  }

  implicit final def eval_rightZero[
    X <: BiproductBound, T <: AnyGraphObject
  ]:  Eval[RawBiproduct[X, RawZero], rightZero[T], X] =
  new Eval[RawBiproduct[X, RawZero], rightZero[T], X] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = raw_leftProj
  }

  implicit final def eval_rightCozero[
    X <: BiproductBound, T <: AnyGraphObject
  ]:  Eval[X, rightCozero[T], RawBiproduct[X, RawZero]] =
  new Eval[X, rightCozero[T], RawBiproduct[X, RawZero]] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = { raw_input: RawInput =>
      raw_biproduct(raw_input, raw_toZero[X](raw_input))
    }
  }
}
