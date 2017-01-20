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

  def tensorRaw[L <: TensorBound, R <: TensorBound](l: L, r: R): RawTensor[L, R]
  def leftRaw[L <: TensorBound, R <: TensorBound](t: RawTensor[L, R]): L
  def rightRaw[L <: TensorBound, R <: TensorBound](t: RawTensor[L, R]): R

  // def matchUpRaw[X <: TensorBound](t: RawTensor[X, X])
  //   (implicit m: Matchable[X]): X =
  //     m.matchUp(leftRaw(t), rightRaw(t))
  //
  // def fromUnitRaw[X <: TensorBound](o: AnyGraphObject)(u: RawUnit)
  //   (implicit fu: FromUnit[RawUnit, X]): X =
  //     fu.fromUnit(u, o)

  def toUnitRaw[X <: TensorBound](x: X): RawUnit

  // IL ⊗ IR → OL ⊗ OR
  implicit final def eval_tensor[
    IL <: TensorBound, IR <: TensorBound,
    OL <: TensorBound, OR <: TensorBound,
    L <: AnyGraphMorphism,
    R <: AnyGraphMorphism
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

  // A ⊗ B → B ⊗ A
  implicit final def eval_symmetry[
    A <: TensorBound, B <: TensorBound,
    L <: AnyGraphObject, R <: AnyGraphObject
  ]:  Eval[RawTensor[A, B], symmetry[L, R], RawTensor[B, A]] =
  new Eval[RawTensor[A, B], symmetry[L, R], RawTensor[B, A]] {

    def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal =>
      tensorRaw[B, A](rightRaw(inVal), leftRaw(inVal))
    }

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
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

    def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal =>
      matchable.matchUp(leftRaw(inVal), rightRaw(inVal))
    }

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }


  // I → X
  implicit final def eval_fromUnit[
    O, T <: AnyGraphObject
  ](implicit
    fu: FromUnit[RawUnit, O]
  ):  Eval[RawUnit, fromUnit[T], O] =
  new Eval[RawUnit, fromUnit[T], O] {

    def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal =>
      fu.fromUnit(inVal, morph.obj)
    }

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }

  // X → I
  implicit final def eval_toUnit[
    I <: TensorBound, T <: AnyGraphObject
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

  // implicit final def eval_tensorTrace[
  //   M <: AnyGraphMorphism {
  //     type In <: AnyTensorObj
  //     type Out <: AnyTensorObj { type Right = In#Right }
  //   },
  //   I, O
  // ]:  Eval[I, tensorTrace[M], O] =
  // new Eval[I, tensorTrace[M], O] {
  //
  //   def rawApply(morph: InMorph): InVal => OutVal = ???
  //   // ({
  //   //   lazy val a: A = m.in.left
  //   //   lazy val x: X = m.in.right
  //   //   lazy val b: B = m.out.left
  //   //
  //   //   rightCounit(a) >=>
  //   //   (id(a) ⊗ fromUnit(x)) >=>
  //   //   (id(a) ⊗ duplicate(x)) >=>
  //   //   associateLeft(a, x, x) >=>
  //   //   (m ⊗ id(x)) >=>
  //   //   associateRight(b, x, x) >=>
  //   //   (id(b) ⊗ matchUp(x)) >=>
  //   //   (id(b) ⊗ toUnit(x)) >=>
  //   //   rightUnit(b)
  //   // }) {
  //
  //   def present(morph: InMorph): Seq[String] = Seq(morph.label)
  // }

  implicit final def eval_rightUnit[
    I <: TensorBound, T <: AnyGraphObject
  ]:  Eval[RawTensor[I, RawUnit], rightUnit[T], I] =
  new Eval[RawTensor[I, RawUnit], rightUnit[T], I] {

    // FIXME: this is wrong!
    def rawApply(morph: InMorph): InVal => OutVal = leftRaw

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }

  implicit final def eval_rightCounit[
    I <: TensorBound, T <: AnyGraphObject
  ]:  Eval[I, rightCounit[T], RawTensor[I, RawUnit]] =
  new Eval[I, rightCounit[T], RawTensor[I, RawUnit]] {

    def rawApply(morph: InMorph): InVal => OutVal = { inVal: InVal =>
      tensorRaw(inVal, toUnitRaw[I](inVal))
    }

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }
}
