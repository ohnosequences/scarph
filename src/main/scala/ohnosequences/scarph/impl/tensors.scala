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

  // def raw_matchUp[X <: TensorBound](t: RawTensor[X, X])
  //   (implicit m: Matchable[X]): X =
  //     m.matchUp(raw_left(t), raw_right(t))
  //
  // def raw_fromUnit[X <: TensorBound](o: AnyGraphObject)(u: RawUnit)
  //   (implicit fu: FromUnit[RawUnit, X]): X =
  //     fu.fromUnit(u, o)

  def raw_toUnit[X <: TensorBound](x: X): RawUnit

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

    def raw_apply(morph: InMorph): RawInput => RawOutput = { raw_input: RawInput =>
      raw_tensor[OL, OR](
        evalLeft.raw_apply(morph.left)  ( raw_left[IL, IR](raw_input) ),
        evalRight.raw_apply(morph.right)( raw_right[IL, IR](raw_input) )
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

    def raw_apply(morph: InMorph): RawInput => RawOutput = { raw_input: RawInput =>
      raw_tensor[B, A](raw_right(raw_input), raw_left(raw_input))
    }

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }

  // △: X → X ⊗ X
  implicit final def eval_duplicate[
    I <: TensorBound, T <: AnyGraphObject
  ]:  Eval[I, duplicate[T], RawTensor[I, I]] =
  new Eval[I, duplicate[T], RawTensor[I, I]] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = { raw_input: RawInput =>
      raw_tensor[I, I](raw_input, raw_input)
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

    def raw_apply(morph: InMorph): RawInput => RawOutput = { raw_input: RawInput =>
      matchable.matchUp(raw_left(raw_input), raw_right(raw_input))
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

    def raw_apply(morph: InMorph): RawInput => RawOutput = { raw_input: RawInput =>
      fu.fromUnit(raw_input, morph.obj)
    }

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }

  // X → I
  implicit final def eval_toUnit[
    I <: TensorBound, T <: AnyGraphObject
  ]:  Eval[I, toUnit[T], RawUnit] =
  new Eval[I, toUnit[T], RawUnit] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = raw_toUnit

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }

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
  ]:  Eval[RawTensor[X, RawTensor[Y, Z]], associateTensorLeft[A, B, C], RawTensor[RawTensor[X, Y], Z]] =
  new Eval[RawTensor[X, RawTensor[Y, Z]], associateTensorLeft[A, B, C], RawTensor[RawTensor[X, Y], Z]] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = { raw_input: RawInput =>
      val x: X = raw_left(raw_input)
      val y: Y = raw_left(raw_right(raw_input))
      val z: Z = raw_right(raw_right(raw_input))

      raw_tensor(raw_tensor(x, y), z)
    }

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }

  implicit final def eval_associateTensorRight[
    A <: AnyGraphObject, B <: AnyGraphObject, C <: AnyGraphObject,
    X <: TensorBound, Y <: TensorBound, Z <: TensorBound
  ]:  Eval[RawTensor[RawTensor[X, Y], Z], associateTensorRight[A, B, C], RawTensor[X, RawTensor[Y, Z]]] =
  new Eval[RawTensor[RawTensor[X, Y], Z], associateTensorRight[A, B, C], RawTensor[X, RawTensor[Y, Z]]] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = { raw_input: RawInput =>
      val x: X = raw_left(raw_left(raw_input))
      val y: Y = raw_right(raw_left(raw_input))
      val z: Z = raw_right(raw_input)

      raw_tensor(x, raw_tensor(y, z))
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
  //   def raw_apply(morph: InMorph): RawInput => RawOutput = ???
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

  implicit final def eval_leftUnit[
    X <: TensorBound, T <: AnyGraphObject
  ]:  Eval[RawTensor[RawUnit, X], leftUnit[T], X] =
  new Eval[RawTensor[RawUnit, X], leftUnit[T], X] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = raw_right

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }

  implicit final def eval_leftCounit[
    X <: TensorBound, T <: AnyGraphObject
  ]:  Eval[X, leftCounit[T], RawTensor[RawUnit, X]] =
  new Eval[X, leftCounit[T], RawTensor[RawUnit, X]] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = { raw_input: RawInput =>
      raw_tensor(raw_toUnit[X](raw_input), raw_input)
    }

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }

  implicit final def eval_rightUnit[
    X <: TensorBound, T <: AnyGraphObject
  ]:  Eval[RawTensor[X, RawUnit], rightUnit[T], X] =
  new Eval[RawTensor[X, RawUnit], rightUnit[T], X] {

    // FIXME: this is wrong!
    // But why? It should be (id[X] ⊗ fromUnit[X]).matchUp, where fromUnit[X] just gives all instances of X, which is being matched with the input instance of X should be just input itself
    def raw_apply(morph: InMorph): RawInput => RawOutput = raw_left

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }

  implicit final def eval_rightCounit[
    X <: TensorBound, T <: AnyGraphObject
  ]:  Eval[X, rightCounit[T], RawTensor[X, RawUnit]] =
  new Eval[X, rightCounit[T], RawTensor[X, RawUnit]] {

    def raw_apply(morph: InMorph): RawInput => RawOutput = { raw_input: RawInput =>
      raw_tensor(raw_input, raw_toUnit[X](raw_input))
    }

    def present(morph: InMorph): Seq[String] = Seq(morph.label)
  }
}
