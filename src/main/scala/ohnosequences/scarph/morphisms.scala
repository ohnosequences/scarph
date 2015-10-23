package ohnosequences.scarph

object morphisms {

  import ohnosequences.cosas.types._
  import objects._

  /* Morphisms are spans */
  trait AnyGraphMorphism extends AnyGraphType {

    type In <: AnyGraphObject
    val  in: In

    type Out <: AnyGraphObject
    val  out: Out

    type Dagger <: AnyGraphMorphism
    val  dagger: Dagger
  }

  // strict:
  type ==>[A <: AnyGraphObject, B <: AnyGraphObject] = AnyGraphMorphism { type In = A; type Out = B }

  // non-strict:
  type -->[A <: AnyGraphObject, B <: AnyGraphObject] = AnyGraphMorphism { type In <: A; type Out <: B }

  type DaggerOf[M <: AnyGraphMorphism] = M#Out --> M#In


  trait AnyMorphismTransform {

    type InMorph <: AnyGraphMorphism
    type OutMorph

    def apply(morph: InMorph): OutMorph
  }

  /* Sequential sition of two morphisms */
  sealed trait AnyComposition extends AnyGraphMorphism { composition =>

    type First <: AnyGraphMorphism
    type Second <: AnyGraphMorphism //{ type In = First#Out }

    type In  <: First#In
    type Out <: Second#Out
  }

  case class Composition[
    F <: AnyGraphMorphism,
    S <: AnyGraphMorphism //{ type In = F#Out }
  ] (val first: F, val second: S) extends AnyComposition { cc =>

    type First = F
    type Second = S

    type     In = First#In
    lazy val in: In = first.in: In

    type     Out = Second#Out
    lazy val out: Out = second.out: Out

    type     Dagger = Composition[Second#Dagger, First#Dagger]
    lazy val dagger: Dagger = Composition(second.dagger, first.dagger)

    lazy val label: String = s"(${first.label} >=> ${second.label})"
  }


  /* Basic aliases */
  type >=>[F <: AnyGraphMorphism, S <: AnyGraphMorphism { type In = F#Out }] = Composition[F, S]

  implicit def graphMorphismOps[F <: AnyGraphMorphism](f: F):
        GraphMorphismOps[F] =
    new GraphMorphismOps[F](f)

  case class GraphMorphismOps[F <: AnyGraphMorphism](val f: F) extends AnyVal {

    def >=>[S <: AnyGraphMorphism { type In = F#Out }](s: S): F >=> S = Composition(f, s)

    def ⊗[S <: AnyGraphMorphism](q: S): TensorMorph[F, S] = TensorMorph(f, q)
    def ⊕[S <: AnyGraphMorphism](q: S): BiproductMorph[F, S] = BiproductMorph(f, q)
  }

  trait AnyPrimitiveMorph extends AnyGraphMorphism { morph =>

    // type Raw = Any

    type Dagger <: AnyPrimitiveMorph {
      type Dagger >: morph.type <: AnyPrimitiveMorph
    }
  }

  // id: X → X
  case class id[X <: AnyGraphObject](val obj: X) extends AnyPrimitiveMorph {

    type Obj = X

    type     In = Obj
    lazy val in: In = obj

    type     Out = Obj
    lazy val out: Out = obj

    type     Dagger = id[Obj]
    lazy val dagger: Dagger = id(obj)

    lazy val label: String = s"id(${obj.label})"
  }


  // I → X
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

  // X → I
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

  // △: X → X ⊗ X
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

  // ▽: X ⊗ X → X
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


  // 0 → X
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

  // X → 0
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

  // X -> X ⊕ X
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

  // X ⊕ X -> X
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


  // L → L ⊕ R
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

  // L ⊕ R → L
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


  // R → L ⊕ R
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

  // L ⊕ R → R
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


  case class target[E <: AnyRelation](val relation: E) extends AnyPrimitiveMorph {

    type Relation = E

    type     In = Relation
    lazy val in: In = relation

    type     Out = Relation#Target
    lazy val out: Out = relation.target

    type     Dagger = inE[Relation]
    lazy val dagger: Dagger = inE(relation)

    lazy val label: String = s"target(${relation.label})"
  }

  case class inE[E <: AnyRelation](val relation: E) extends AnyPrimitiveMorph {

    type Relation = E

    type     Out = Relation
    lazy val out: Out = relation

    type     In = Relation#Target
    lazy val in: In = relation.target

    type     Dagger = target[Relation]
    lazy val dagger: Dagger = target(relation)


    lazy val label: String = s"inE(${relation.label})"
  }


  case class source[E <: AnyRelation](val relation: E) extends AnyPrimitiveMorph {

    type Relation = E

    type     In = Relation
    lazy val in: In = relation

    type     Out = Relation#Source
    lazy val out: Out = relation.source

    type     Dagger = outE[Relation]
    lazy val dagger: Dagger = outE(relation)

    lazy val label: String = s"source(${relation.label})"
  }

  case class outE[E <: AnyRelation](val relation: E) extends AnyPrimitiveMorph {

    type Relation = E

    type     Out = Relation
    lazy val out: Out = relation

    type     In = Relation#Source
    lazy val in: In = relation.source

    type     Dagger = source[Relation]
    lazy val dagger: Dagger = source(relation)

    lazy val label: String = s"outE(${relation.label})"
  }


  case class outV[E <: AnyRelation](val relation: E) extends AnyPrimitiveMorph {

    type Relation = E

    type     In = Relation#Source
    lazy val in: In = relation.source

    type     Out = Relation#Target
    lazy val out: Out = relation.target

    type     Dagger = inV[Relation]
    lazy val dagger: Dagger = inV(relation)

    lazy val label: String = s"outV(${relation.label})"
  }

  case class inV[E <: AnyRelation](val relation: E) extends AnyPrimitiveMorph {

    type Relation = E

    type     Out = Relation#Source
    lazy val out: Out = relation.source

    type     In = Relation#Target
    lazy val in: In = relation.target

    type     Dagger = outV[Relation]
    lazy val dagger: Dagger = outV(relation)

    lazy val label: String = s"inV(${relation.label})"
  }


  type get[P <: AnyProperty] = outV[P]
  def get[P <: AnyProperty](p: P): get[P] = outV[P](p)

  type lookup[P <: AnyProperty] = inV[P]
  def lookup[P <: AnyProperty](p: P): lookup[P] = inV[P](p)

  case class quantify[P <: AnyPredicate](val predicate: P) extends AnyPrimitiveMorph {

    type Predicate = P

    type     In = Predicate#Element
    lazy val in: In = predicate.element

    type     Out = Predicate
    lazy val out: Out = predicate

    type     Dagger = coerce[Predicate]
    lazy val dagger: Dagger = coerce(predicate)

    lazy val label: String = s"quantify(${predicate.label})"
  }


  case class coerce[P <: AnyPredicate](val predicate: P) extends AnyPrimitiveMorph {

    type Predicate = P

    type     Out = Predicate#Element
    lazy val out: Out = predicate.element

    type     In = Predicate
    lazy val in: In = predicate

    type     Dagger = quantify[Predicate]
    lazy val dagger: Dagger = quantify(predicate)

    lazy val label: String = s"coerce(${predicate.label})"
  }

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





  trait AnyNaturalIsomorphism extends AnyPrimitiveMorph { iso =>

    type Dagger <: AnyNaturalIsomorphism {
      type Dagger >: iso.type <: AnyNaturalIsomorphism
    }
  }


  // σ: L ⊗ R → R ⊗ L
  case class symmetry[L <: AnyGraphObject, R <: AnyGraphObject](l: L, r: R)
    extends AnyNaturalIsomorphism {

    type     In = L ⊗ R
    lazy val in: In = l ⊗ r

    type     Out = R ⊗ L
    lazy val out: Out = r ⊗ l

    type     Dagger = symmetry[R, L]
    lazy val dagger: Dagger = symmetry(r, l)

    lazy val label: String = s"symmetry(${l.label}, ${r.label})"
  }

  case class associateLeft[A <: AnyGraphObject, B <: AnyGraphObject, C <: AnyGraphObject]
    (a: A, b: B, c: C) extends AnyNaturalIsomorphism {

    type     In = A ⊗ (B ⊗ C)
    lazy val in: In = a ⊗ (b ⊗ c)

    type     Out = (A ⊗ B) ⊗ C
    lazy val out: Out = (a ⊗ b) ⊗ c

    type     Dagger = associateRight[A, B, C]
    lazy val dagger: Dagger = associateRight(a, b, c)

    lazy val label: String = s"associateLeft(${a.label} ⊗ (${b.label} ⊕ ${c.label}))"
  }

  case class associateRight[A <: AnyGraphObject, B <: AnyGraphObject, C <: AnyGraphObject]
    (a: A, b: B, c: C) extends AnyNaturalIsomorphism {

    type     In = (A ⊗ B) ⊗ C
    lazy val in: In = (a ⊗ b) ⊗ c

    type     Out = A ⊗ (B ⊗ C)
    lazy val out: Out = a ⊗ (b ⊗ c)

    type     Dagger = associateLeft[A, B, C]
    lazy val dagger: Dagger = associateLeft(a, b, c)

    lazy val label: String = s"associateRight((${a.label} ⊗ ${b.label}) ⊗ ${c.label})"
  }


  case class distribute[U <: AnyGraphObject, A <: AnyGraphObject, B <: AnyGraphObject]
    (u: U, a: A, b: B) extends AnyNaturalIsomorphism {

    type     In = U ⊗ (A ⊕ B)
    lazy val in: In = u ⊗ (a ⊕ b)

    type     Out = (U ⊗ A) ⊕ (U ⊗ B)
    lazy val out: Out = (u ⊗ a) ⊕ (u ⊗ b)

    type     Dagger = undistribute[U, A, B]
    lazy val dagger: Dagger = undistribute(u, a, b)

    lazy val label: String = s"distribute(${u.label} ⊗ (${a.label} ⊕ ${b.label}))"
  }

  case class undistribute[U <: AnyGraphObject, A <: AnyGraphObject, B <: AnyGraphObject]
    (u: U, a: A, b: B) extends AnyNaturalIsomorphism {

    type     Out = U ⊗ (A ⊕ B)
    lazy val out: Out = u ⊗ (a ⊕ b)

    type     In = (U ⊗ A) ⊕ (U ⊗ B)
    lazy val in: In = (u ⊗ a) ⊕ (u ⊗ b)

    type     Dagger = distribute[U, A, B]
    lazy val dagger: Dagger = distribute(u, a, b)

    lazy val label: String = s"undistribute((${u.label} ⊗ ${a.label}) ⊕ (${u.label} ⊗ ${b.label}))"
  }


  // I ⊗ X → X
  case class leftUnit[X <: AnyGraphObject](x: X) extends AnyNaturalIsomorphism {

    type     In = unit ⊗ X
    lazy val in: In = unit ⊗ x

    type     Out = X
    lazy val out: Out = x

    type     Dagger = leftCounit[X]
    lazy val dagger: Dagger = leftCounit(x)

    lazy val label: String = s"leftUnit(I ⊗ ${x.label})"
  }

  // X → I ⊗ X
  case class leftCounit[X <: AnyGraphObject](x: X) extends AnyNaturalIsomorphism {

    type     Out = unit ⊗ X
    lazy val out: Out = unit ⊗ x

    type     In = X
    lazy val in: In = x

    type     Dagger = leftUnit[X]
    lazy val dagger: Dagger = leftUnit(x)

    lazy val label: String = s"leftCounit(${x.label})"

  }


  // X ⊗ I → X
  case class rightUnit[X <: AnyGraphObject](x: X) extends AnyNaturalIsomorphism {

    type     In = X ⊗ unit
    lazy val in: In = x ⊗ unit

    type     Out = X
    lazy val out: Out = x

    type     Dagger = rightCounit[X]
    lazy val dagger: Dagger = rightCounit(x)

    lazy val label: String = s"rightUnit(${x.label} ⊗ I)"
  }

  // X → I ⊗ X
  case class rightCounit[X <: AnyGraphObject](x: X) extends AnyNaturalIsomorphism {

    type     In = X
    lazy val in: In = x

    type     Out = X ⊗ unit
    lazy val out: Out = x ⊗ unit

    type     Dagger = rightUnit[X]
    lazy val dagger: Dagger = rightUnit(x)

    lazy val label: String = s"rightCounit(${x.label})"
  }


  // 0 ⊕ X → X
  case class leftZero[X <: AnyGraphObject](x: X) extends AnyNaturalIsomorphism {

    type     In = zero ⊕ X
    lazy val in: In = zero ⊕ x

    type     Out = X
    lazy val out: Out = x

    type     Dagger = leftCozero[X]
    lazy val dagger: Dagger = leftCozero(x)

    lazy val label: String = s"leftZero(0 ⊕ ${x.label})"
  }

  // X → 0 ⊕ X
  case class leftCozero[X <: AnyGraphObject](x: X) extends AnyNaturalIsomorphism {

    type     Out = zero ⊕ X
    lazy val out: Out = zero ⊕ x

    type     In = X
    lazy val in: In = x

    type     Dagger = leftZero[X]
    lazy val dagger: Dagger = leftZero(x)

    lazy val label: String = s"leftCozero(${x.label})"

  }


  // X ⊕ 0 → X
  case class rightZero[X <: AnyGraphObject](x: X) extends AnyNaturalIsomorphism {

    type     In = X ⊕ zero
    lazy val in: In = x ⊕ zero

    type     Out = X
    lazy val out: Out = x

    type     Dagger = rightCozero[X]
    lazy val dagger: Dagger = rightCozero(x)

    lazy val label: String = s"rightZero(${x.label} ⊕ 0)"
  }

  // X → 0 ⊕ X
  case class rightCozero[X <: AnyGraphObject](x: X) extends AnyNaturalIsomorphism {

    type     In = X
    lazy val in: In = x

    type     Out = X ⊕ zero
    lazy val out: Out = x ⊕ zero

    type     Dagger = rightZero[X]
    lazy val dagger: Dagger = rightZero(x)

    lazy val label: String = s"rightCozero(${x.label})"
  }

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

    lazy val label: String = s"tensorTrace(${morph.label})"
  }

  // tensor Trace
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
}
