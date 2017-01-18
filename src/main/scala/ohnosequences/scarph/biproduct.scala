package ohnosequences.scarph

/* ## Biproduct */

/* ### Objects */
sealed trait AnyBiproductObj extends AnyGraphObject {

  type Left <: AnyGraphObject
  val  left: Left

  type Right <: AnyGraphObject
  val  right: Right
}

case class BiproductObj[L <: AnyGraphObject, R <: AnyGraphObject]
  (val left: L, val right: R) extends AnyBiproductObj {

  type Left = L
  type Right = R

  lazy val label: String = s"(${left.label} ⊕ ${right.label})"
}

case object BiproductZero extends AnyGraphObject {

  lazy val label: String = "zero"
}


/* ### Morphisms */
sealed trait AnyBiproductMorph extends AnyGraphMorphism { biprod =>

  type Left <: AnyGraphMorphism
  val  left: Left

  type Right <: AnyGraphMorphism
  val  right: Right

  type In  <: BiproductObj[Left#In, Right#In]
  type Out <: BiproductObj[Left#Out, Right#Out]

  type Dagger <: AnyBiproductMorph {
    type Left = biprod.Left#Dagger
    type Right = biprod.Right#Dagger
  }
}

case class BiproductMorph[L <: AnyGraphMorphism, R <: AnyGraphMorphism]
  (val left: L, val right: R) extends AnyBiproductMorph { biprod =>

  type Left = L
  type Right = R

  type     In = BiproductObj[Left#In, Right#In]
  lazy val in: In = BiproductObj(left.in, right.in): In

  type     Out = BiproductObj[Left#Out, Right#Out]
  lazy val out: Out = BiproductObj(left.out, right.out): Out

  type     Dagger = BiproductMorph[Left#Dagger, Right#Dagger]
  lazy val dagger: Dagger = BiproductMorph(left.dagger: Left#Dagger, right.dagger: Right#Dagger)

  lazy val label: String = s"(${left.label} ⊕ ${right.label})"
}


/* #### `0 → X` */
case class fromZero[X <: AnyGraphObject](obj: X) extends AnyPrimitiveMorph {
  type Obj = X

  type     In = zero
  lazy val in: In = zero

  type     Out = Obj
  lazy val out: Out = obj

  type     Dagger = toZero[Obj]
  lazy val dagger: Dagger = toZero(obj)

  lazy val label: String = s"fromZero(${obj.label})"
}

/* #### `X → 0` */
case class toZero[X <: AnyGraphObject](obj: X) extends AnyPrimitiveMorph {
  type Obj = X

  type     Out = zero
  lazy val out: Out = zero

  type     In = Obj
  lazy val in: In = obj

  type     Dagger = fromZero[Obj]
  lazy val dagger: Dagger = fromZero(obj)

  lazy val label: String = s"toZero(${obj.label})"
}

/* #### `X → X ⊕ X` */
case class fork[X <: AnyGraphObject](obj: X) extends AnyPrimitiveMorph {
  type Obj = X

  type     In = Obj
  lazy val in: In = obj

  type     Out = BiproductObj[Obj, Obj]
  lazy val out: Out = BiproductObj(obj, obj)

  type     Dagger = merge[Obj]
  lazy val dagger: Dagger = merge(obj)

  lazy val label: String = s"fork(${obj.label})"
}

/* #### `X ⊕ X → X` */
case class merge[X <: AnyGraphObject](obj: X) extends AnyPrimitiveMorph {
  type Obj = X

  type     Out = Obj
  lazy val out: Out = obj

  type     In = BiproductObj[Obj, Obj]
  lazy val in: In = BiproductObj(obj, obj)

  type     Dagger = fork[Obj]
  lazy val dagger: Dagger = fork(obj)

  lazy val label: String = s"merge(${obj.label} ⊕ ${obj.label})"
}

/* #### Left injection `L → L ⊕ R` */
case class leftInj[B <: AnyBiproductObj](val biproduct: B) extends AnyPrimitiveMorph {

  type Biproduct = B

  type     In = Biproduct#Left
  lazy val in: In = biproduct.left

  type     Out = Biproduct
  lazy val out: Out = biproduct

  type     Dagger = leftProj[Biproduct]
  lazy val dagger: Dagger = leftProj(biproduct)

  lazy val label: String = s"(${biproduct.left.label} leftInj ${biproduct.label})"
}

/* #### Left projection `L ⊕ R → L` */
case class leftProj[B <: AnyBiproductObj](val biproduct: B) extends AnyPrimitiveMorph {

  type Biproduct = B

  type     Out = Biproduct#Left
  lazy val out: Out = biproduct.left

  type     In = Biproduct
  lazy val in: In = biproduct

  type     Dagger = leftInj[Biproduct]
  lazy val dagger: Dagger = leftInj(biproduct)

  lazy val label: String = s"leftProj(${biproduct.label})"
}

/* #### Right injection `R → L ⊕ R` */
case class rightInj[B <: AnyBiproductObj](val biproduct: B) extends AnyPrimitiveMorph {
  type Biproduct = B

  type     In = Biproduct#Right
  lazy val in: In = biproduct.right

  type     Out = Biproduct
  lazy val out: Out = biproduct

  type     Dagger = rightProj[Biproduct]
  lazy val dagger: Dagger = rightProj(biproduct)

  lazy val label: String = s"(${biproduct.right.label} rightInj ${biproduct.label})"
}

/* #### Right projection `L ⊕ R → R` */
case class rightProj[B <: AnyBiproductObj](val biproduct: B) extends AnyPrimitiveMorph {

  type Biproduct = B

  type     Out = Biproduct#Right
  lazy val out: Out = biproduct.right

  type     In = Biproduct
  lazy val in: In = biproduct

  type     Dagger = rightInj[Biproduct]
  lazy val dagger: Dagger = rightInj(biproduct)

  lazy val label: String = s"leftProj(${biproduct.label})"
}


/* #### Biproduct trace */
trait AnyBiproductTrace extends AnyGraphMorphism {

  type Morph <: AnyGraphMorphism {
    type In <: AnyBiproductObj
    type Out <: AnyBiproductObj { type Right = In#Right }
  }
  val morph: Morph

  type      In = Morph#In#Left
  lazy val  in: In = morph.in.left

  type      Out = Morph#Out#Left
  lazy val  out: Out = morph.out.left

  type      Dagger = daggerBiproductTrace[Morph]
  lazy val  dagger: Dagger = daggerBiproductTrace[Morph](morph)

  lazy val  label: String = s"biproductTrace(${morph.label})"
}

case class biproductTrace[
  M <: AnyGraphMorphism {
    type In <: AnyBiproductObj
    type Out <: AnyBiproductObj { type Right = In#Right }
  }
](val morph: M) extends AnyBiproductTrace {

  type Morph = M
}

case class daggerBiproductTrace[
  M <: AnyGraphMorphism {
    type In <: AnyBiproductObj
    type Out <: AnyBiproductObj { type Right = In#Right }
  }
](val morph: M) extends AnyGraphMorphism {

  type Morph = M

  type     In = Morph#Out#Left
  lazy val in: In = morph.out.left

  type     Out = Morph#In#Left
  lazy val out: Out = morph.in.left

  type Dagger = biproductTrace[Morph]
  lazy val dagger: Dagger = biproductTrace[Morph](morph)

  lazy val label: String = s"biproductTrace(${morph.label})"
}
