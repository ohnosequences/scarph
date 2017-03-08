package ohnosequences.scarph.impl

import ohnosequences.scarph._

trait Matchable[T0] {

  type T = T0
  def matchUp(l: T, r: T): T
}

trait FromUnit[U0, T0] {

  type U = U0
  type T = T0
  def fromUnit(u: U, o: AnyGraphObject): T
}

case object FromUnit {

  implicit def unitToUnit[U]:
      FromUnit[U, U] =
  new FromUnit[U, U] { def fromUnit(u: U, o: AnyGraphObject): T = u }
}

trait Tensors {

  type TensorBound
  type RawTensor[L <: TensorBound, R <: TensorBound] <: TensorBound
  type RawUnit <: TensorBound

  def raw_tensor[L <: TensorBound, R <: TensorBound](l: L, r: R): RawTensor[L, R]
  def raw_left  [L <: TensorBound, R <: TensorBound](t: RawTensor[L, R]): L
  def raw_right [L <: TensorBound, R <: TensorBound](t: RawTensor[L, R]): R
  def raw_toUnit[X <: TensorBound](x: X): RawUnit


  // IL ⊗ IR → OL ⊗ OR
  implicit final def eval_tensor[
    IL <: TensorBound, IR <: TensorBound,
    OL <: TensorBound, OR <: TensorBound,
    L <: AnyGraphMorphism,
    R <: AnyGraphMorphism
  ](implicit
    evalLeft:  Eval[L, IL, OL],
    evalRight: Eval[R, IR, OR]
  ):  Eval[TensorMorph[L, R], RawTensor[IL, IR], RawTensor[OL, OR]] =
  new Eval[TensorMorph[L, R], RawTensor[IL, IR], RawTensor[OL, OR]]( morph => raw_input =>

    raw_tensor[OL, OR](
      evalLeft.raw_apply(morph.left)  ( raw_left[IL, IR](raw_input) ),
      evalRight.raw_apply(morph.right)( raw_right[IL, IR](raw_input) )
    )
  ) {

    override def present(morph: InMorph): Seq[String] =
      ("(" +: evalLeft.present(morph.left)) ++
      (" ⊗ " +: evalRight.present(morph.right) :+ ")")
  }

  // A ⊗ B → B ⊗ A
  implicit final def eval_symmetry[
    A <: TensorBound, B <: TensorBound,
    L <: AnyGraphObject, R <: AnyGraphObject
  ]:  Eval[symmetry[L, R], RawTensor[A, B], RawTensor[B, A]] =
  new Eval( morph => raw_input =>

    raw_tensor[B, A](raw_right(raw_input), raw_left(raw_input))
  )


  // △: X → X ⊗ X
  implicit final def eval_duplicate[
    I <: TensorBound, T <: AnyGraphObject
  ]:  Eval[duplicate[T], I, RawTensor[I, I]] =
  new Eval( morph => raw_input =>

    raw_tensor[I, I](raw_input, raw_input)
  )

  // ▽: X ⊗ X → X
  implicit final def eval_matchUp[
    O <: TensorBound, T <: AnyGraphObject
  ](implicit
    matchable: Matchable[O]
  ):  Eval[matchUp[T], RawTensor[O, O], O] =
  new Eval( morph => raw_input =>

    matchable.matchUp(raw_left(raw_input), raw_right(raw_input))
  )


  // I → X
  implicit final def eval_fromUnit[
    O, T <: AnyGraphObject
  ](implicit
    fu: FromUnit[RawUnit, O]
  ):  Eval[fromUnit[T], RawUnit, O] =
  new Eval( morph => raw_input =>

    fu.fromUnit(raw_input, morph.obj)
  )

  // X → I
  implicit final def eval_toUnit[
    I <: TensorBound, T <: AnyGraphObject
  ]:  Eval[toUnit[T], I, RawUnit] =
  new Eval[toUnit[T], I, RawUnit]( morph => raw_toUnit )

  implicit def fromUnitTensor[U, L <: TensorBound, R <: TensorBound]
  (implicit
    l: FromUnit[U, L],
    r: FromUnit[U, R]
  ):  FromUnit[U, RawTensor[L, R]] =
  new FromUnit[U, RawTensor[L, R]] {

    def fromUnit(u: U, o: AnyGraphObject): T = raw_tensor(l.fromUnit(u, o), r.fromUnit(u, o))
  }

  implicit final def eval_associateTensorLeft[
    A <: AnyGraphObject, B <: AnyGraphObject, C <: AnyGraphObject,
    X <: TensorBound, Y <: TensorBound, Z <: TensorBound
  ]: Eval[
    associateTensorLeft[A, B, C],
    RawTensor[X, RawTensor[Y, Z]],
    RawTensor[RawTensor[X, Y], Z]
  ] = new Eval( morph => raw_input => {

    val x: X = raw_left(raw_input)
    val y: Y = raw_left(raw_right(raw_input))
    val z: Z = raw_right(raw_right(raw_input))

    raw_tensor(raw_tensor(x, y), z)
  })

  implicit final def eval_associateTensorRight[
    A <: AnyGraphObject, B <: AnyGraphObject, C <: AnyGraphObject,
    X <: TensorBound, Y <: TensorBound, Z <: TensorBound
  ]: Eval[
    associateTensorRight[A, B, C],
    RawTensor[RawTensor[X, Y], Z],
    RawTensor[X, RawTensor[Y, Z]]
  ] = new Eval( morph => raw_input => {

    val x: X = raw_left(raw_left(raw_input))
    val y: Y = raw_right(raw_left(raw_input))
    val z: Z = raw_right(raw_input)

    raw_tensor(x, raw_tensor(y, z))
  })

  implicit final def eval_leftUnit[
    X <: TensorBound, T <: AnyGraphObject
  ]:  Eval[leftUnit[T], RawTensor[RawUnit, X], X] =
  new Eval( morph => raw_right )

  implicit final def eval_leftCounit[
    X <: TensorBound, T <: AnyGraphObject
  ]:  Eval[leftCounit[T], X, RawTensor[RawUnit, X]] =
  new Eval( morph => raw_input =>

    raw_tensor(raw_toUnit[X](raw_input), raw_input)
  )

  implicit final def eval_rightUnit[
    X <: TensorBound, T <: AnyGraphObject
  ]:  Eval[rightUnit[T], RawTensor[X, RawUnit], X] =
  new Eval( morph => raw_left )

  implicit final def eval_rightCounit[
    X <: TensorBound, T <: AnyGraphObject
  ]:  Eval[rightCounit[T], X, RawTensor[X, RawUnit]] =
  new Eval( morph => raw_input =>

    raw_tensor(raw_input, raw_toUnit[X](raw_input))
  )

}
