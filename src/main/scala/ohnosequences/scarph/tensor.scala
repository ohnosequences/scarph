package ohnosequences.scarph

/* ## Tensor product */

/* ### Objects */
sealed trait AnyTensorObj extends AnyGraphObject {

  type Left <: AnyGraphObject
  val  left: Left

  type Right <: AnyGraphObject
  val  right: Right
}

case class TensorObj[L <: AnyGraphObject, R <: AnyGraphObject]
  (val left: L, val right: R) extends AnyTensorObj {

  type Left = L
  type Right = R

  lazy val label: String = s"(${left.label} ⊗ ${right.label})"
}

case object TensorUnit extends AnyGraphObject {

  lazy val label: String = "unit"
}


/* ### Morphisms */
sealed trait AnyTensorMorph extends AnyGraphMorphism { tensor =>

  type Left <: AnyGraphMorphism
  val  left: Left

  type Right <: AnyGraphMorphism
  val  right: Right

  type In  <: AnyTensorObj { type Left = tensor.Left#In; type Right = tensor.Right#In }
  type Out <: AnyTensorObj { type Left = tensor.Left#Out; type Right = tensor.Right#Out }

  type Dagger <: AnyTensorMorph {
    type Left = tensor.Left#Dagger;
    type Right = tensor.Right#Dagger
  }
}

case class TensorMorph[L <: AnyGraphMorphism, R <: AnyGraphMorphism]
  (val left: L, val right: R) extends AnyTensorMorph { tensor =>

  type Left = L
  type Right = R

  type     In = TensorObj[Left#In, Right#In]
  lazy val in: In = TensorObj(left.in, right.in): In

  type     Out = TensorObj[Left#Out, Right#Out]
  lazy val out: Out = TensorObj(left.out, right.out): Out

  type     Dagger = TensorMorph[Left#Dagger, Right#Dagger]
  lazy val dagger: Dagger = TensorMorph(left.dagger: Left#Dagger, right.dagger: Right#Dagger)

  lazy val label: String = s"(${left.label} ⊗ ${right.label})"
}


/* #### `I → X` */
case class fromUnit[X <: AnyGraphObject](val obj: X) extends AnyPrimitiveMorph {
  type Obj = X

  type     In = unit
  lazy val in: In = unit

  type     Out = Obj
  lazy val out: Out = obj

  type     Dagger = toUnit[Obj]
  lazy val dagger: Dagger = toUnit(obj)

  lazy val label: String = s"fromUnit(${obj.label})"
}

/* #### `X → I` */
case class toUnit[X <: AnyGraphObject](obj: X) extends AnyPrimitiveMorph {
  type Obj = X

  type     Out = unit
  lazy val out: Out = unit

  type     In = Obj
  lazy val in: In = obj

  type     Dagger = fromUnit[Obj]
  lazy val dagger: Dagger = fromUnit(obj)

  lazy val label: String = s"toUnit(${obj.label})"
}

/* #### `△: X → X ⊗ X` */
case class duplicate[X <: AnyGraphObject](obj: X) extends AnyPrimitiveMorph {
  type Obj = X

  type     In = Obj
  lazy val in: In = obj

  type     Out = Obj ⊗ Obj
  lazy val out: Out = obj ⊗ obj

  type     Dagger = matchUp[Obj]
  lazy val dagger: Dagger = matchUp(obj)

  lazy val label: String = s"duplicate(${obj.label})"
}

/* #### `▽: X ⊗ X → X` */
case class matchUp[X <: AnyGraphObject](obj: X) extends AnyPrimitiveMorph {
  type Obj = X

  type     Out = Obj
  lazy val out: Out = obj

  type     In = Obj ⊗ Obj
  lazy val in: In = obj ⊗ obj

  type     Dagger = duplicate[Obj]
  lazy val dagger: Dagger = duplicate(obj)

  lazy val label: String = s"matchUp(${obj.label} ⊗ ${obj.label})"
}

/* #### Tensor trace */
trait AnyTensorTrace extends AnyGraphMorphism {

  type Morph <: AnyGraphMorphism {
    type In <: AnyTensorObj
    // TODO this or the non-symmetric version?
    type Out <: AnyTensorObj { type Right = In#Right }
  }
  val morph: Morph

  type     In = Morph#In#Left
  lazy val in: In = morph.in.left

  type     Out = Morph#Out#Left
  lazy val out: Out = morph.out.left

  type Dagger = daggerTensorTrace[Morph]
  lazy val dagger: Dagger = daggerTensorTrace[Morph](morph)

// tensor Trace
  lazy val label: String = s"tensorTrace(${morph.label})"
}

case class tensorTrace[
  M <: AnyGraphMorphism {
    type In <: AnyTensorObj
    type Out <: AnyTensorObj { type Right = In#Right }
  }
](val morph: M) extends AnyTensorTrace {

  type Morph = M
}

case class daggerTensorTrace[
  M <: AnyGraphMorphism {
    type In <: AnyTensorObj
    type Out <: AnyTensorObj { type Right = In#Right }
  }
](val morph: M) extends AnyGraphMorphism {

  type Morph = M

  type     In = Morph#Out#Left
  lazy val in: In = morph.out.left

  type     Out = Morph#In#Left
  lazy val out: Out = morph.in.left

  type Dagger = tensorTrace[Morph]
  lazy val dagger: Dagger = tensorTrace[Morph](morph)

  lazy val label: String = s"tensorTrace(${morph.label})"
}
