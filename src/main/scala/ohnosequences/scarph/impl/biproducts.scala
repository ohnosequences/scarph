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

  // // IL ⊕ IR → OL ⊕ OR
  // implicit final def eval_biproduct[
  //   IL <: BiproductBound, IR <: BiproductBound,
  //   OL <: BiproductBound, OR <: BiproductBound,
  //   L <: AnyGraphMorphism {
  //     type In  <: AnyGraphObject { type Raw >: IL }
  //     type Out <: AnyGraphObject { type Raw >: OL }
  //   },
  //   R <: AnyGraphMorphism {
  //     type In  <: AnyGraphObject { type Raw >: IR }
  //     type Out <: AnyGraphObject { type Raw >: OR }
  //   }
  // ](implicit
  //   evalLeft:  Eval[IL, L, OL],
  //   evalRight: Eval[IR, R, OR]
  // ):  Eval[RawBiproduct[IL, IR], BiproductMorph[L, R], RawBiproduct[OL, OR]] =
  // new Eval[RawBiproduct[IL, IR], BiproductMorph[L, R], RawBiproduct[OL, OR]]( morph => raw_input =>
  //
  //   raw_biproduct[OL, OR](
  //     evalLeft.raw_apply(morph.left)  ( raw_leftProj[IL, IR](raw_input) ),
  //     evalRight.raw_apply(morph.right)( raw_rightProj[IL, IR](raw_input) )
  //   )
  // ) {
  //
  //   override def present(morph: InMorph): Seq[String] =
  //     ("(" +: evalLeft.present(morph.left)) ++
  //     (" ⊕ " +: evalRight.present(morph.right) :+ ")")
  // }

  // X → X ⊕ X
  implicit final def eval_fork[
    X <: BiproductBound, T <: AnyGraphObject
  ]:  Eval[fork[T], X, RawBiproduct[X, X]] =
  new Eval( morph => raw_input =>

    raw_biproduct[X, X](raw_input, raw_input)
  )

  // X ⊕ X → X
  implicit final def eval_merge[
    X <: BiproductBound, T <: AnyGraphObject
  ](implicit
    raw_merge: RawMerge[X]
  ):  Eval[merge[T], RawBiproduct[X, X], X] =
  new Eval( morph => raw_input =>

    raw_merge(
       raw_leftProj(raw_input),
      raw_rightProj(raw_input)
    )
  )


  // 0 → T0
  implicit final def eval_fromZero[
    T <: AnyGraphObject, T0 <: BiproductBound
  ](implicit
    z: ZeroFor[T, T0]
  ):  Eval[fromZero[T], RawZero, T0] =
  new Eval( morph => _ => z.zero(morph.obj) )

  // T0 → 0
  implicit final def eval_toZero[
    T0 <: BiproductBound, T <: AnyGraphObject
  ]:  Eval[toZero[T], T0, RawZero] =
  new Eval( morph => raw_toZero )


  // L ⊕ R → L
  implicit final def eval_leftProj[
    A <: BiproductBound, B <: BiproductBound,
    L <: AnyGraphObject, R <: AnyGraphObject
  ]:  Eval[leftProj[L ⊕ R], RawBiproduct[A, B], A] =
  new Eval( morph => raw_leftProj )

  // L ⊕ R → R
  implicit final def eval_rightProj[
    A <: BiproductBound, B <: BiproductBound,
    L <: AnyGraphObject, R <: AnyGraphObject
  ]:  Eval[rightProj[L ⊕ R], RawBiproduct[A, B], B] =
  new Eval( morph => raw_rightProj )


  // L → L ⊕ R
  implicit final def eval_leftInj[
    A <: BiproductBound, B <: BiproductBound,
    L <: AnyGraphObject, R <: AnyGraphObject
  ](implicit
    b: ZeroFor[R, B]
  ):  Eval[leftInj[L ⊕ R], A, RawBiproduct[A, B]] =
  new Eval( morph => raw_input =>

    raw_biproduct(raw_input, b.zero(morph.biproduct.right))
  )

  // R → L ⊕ R
  implicit final def eval_rightInj[
    A <: BiproductBound, B <: BiproductBound,
    L <: AnyGraphObject, R <: AnyGraphObject
  ](implicit
    a: ZeroFor[L, A]
  ):  Eval[rightInj[L ⊕ R], B, RawBiproduct[A, B]] =
  new Eval( morph => raw_input =>

    raw_biproduct(a.zero(morph.biproduct.left), raw_input)
  )

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
  ]:  Eval[
    associateBiproductLeft[A, B, C],
    RawBiproduct[X, RawBiproduct[Y, Z]],
    RawBiproduct[RawBiproduct[X, Y], Z]
  ] = new Eval( morph => raw_input => {

    val x: X = raw_leftProj(raw_input)
    val y: Y = raw_leftProj(raw_rightProj(raw_input))
    val z: Z = raw_rightProj(raw_rightProj(raw_input))

    raw_biproduct(raw_biproduct(x, y), z)
  })

  implicit final def eval_associateBiproductRight[
    A <: AnyGraphObject, B <: AnyGraphObject, C <: AnyGraphObject,
    X <: BiproductBound, Y <: BiproductBound, Z <: BiproductBound
  ]:  Eval[
    associateBiproductRight[A, B, C],
    RawBiproduct[RawBiproduct[X, Y], Z],
    RawBiproduct[X, RawBiproduct[Y, Z]]
  ] = new Eval( morph => raw_input => {

    val x: X = raw_leftProj(raw_leftProj(raw_input))
    val y: Y = raw_rightProj(raw_leftProj(raw_input))
    val z: Z = raw_rightProj(raw_input)

    raw_biproduct(x, raw_biproduct(y, z))
  })

  implicit final def eval_leftZero[
    X <: BiproductBound, T <: AnyGraphObject
  ]:  Eval[leftZero[T], RawBiproduct[RawZero, X], X] =
  new Eval( morph => raw_rightProj )

  implicit final def eval_leftCozero[
    X <: BiproductBound, T <: AnyGraphObject
  ]:  Eval[leftCozero[T], X, RawBiproduct[RawZero, X]] =
  new Eval( morph => raw_input =>

    raw_biproduct(raw_toZero[X](raw_input), raw_input)
  )

  implicit final def eval_rightZero[
    X <: BiproductBound, T <: AnyGraphObject
  ]:  Eval[rightZero[T], RawBiproduct[X, RawZero], X] =
  new Eval( morph => raw_leftProj )

  implicit final def eval_rightCozero[
    X <: BiproductBound, T <: AnyGraphObject
  ]:  Eval[rightCozero[T], X, RawBiproduct[X, RawZero]] =
  new Eval( morph => raw_input =>

    raw_biproduct(raw_input, raw_toZero[X](raw_input))
  )

}
