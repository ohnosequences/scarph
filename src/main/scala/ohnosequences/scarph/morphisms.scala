package ohnosequences.scarph

object morphisms {

  import ohnosequences.cosas.types._
  import objects._


  /* Morphisms are spans */
  trait AnyGraphMorphism extends AnyGraphType { morphism =>

    type Raw = Any

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
    lazy val in = first.in: In

    type     Out = Second#Out
    lazy val out = second.out: Out

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
    lazy val in = obj

    type     Out = Obj
    lazy val out = obj

    type     Dagger = id[Obj]
    lazy val dagger = id(obj)

    lazy val label = s"id(${obj.label})"
  }


  // I → X
  case class fromUnit[X <: AnyGraphObject](val obj: X) extends AnyPrimitiveMorph {
    type Obj = X

    type     In = unit
    lazy val in = unit

    type     Out = Obj
    lazy val out = obj

    type     Dagger = toUnit[Obj]
    lazy val dagger = toUnit(obj)

    lazy val label = s"fromUnit(${obj.label})"
  }

  // X → I
  case class toUnit[X <: AnyGraphObject](obj: X) extends AnyPrimitiveMorph {
    type Obj = X

    type     Out = unit
    lazy val out = unit

    type     In = Obj
    lazy val in = obj

    type     Dagger = fromUnit[Obj]
    lazy val dagger = fromUnit(obj)

    lazy val label = s"toUnit(${obj.label})"
  }

  // △: X → X ⊗ X
  case class duplicate[X <: AnyGraphObject](obj: X) extends AnyPrimitiveMorph {
    type Obj = X

    type     In = Obj
    lazy val in = obj

    type     Out = Obj ⊗ Obj
    lazy val out = obj ⊗ obj

    type     Dagger = matchUp[Obj]
    lazy val dagger = matchUp(obj)

    lazy val label = s"duplicate(${obj.label})"
  }

  // ▽: X ⊗ X → X
  case class matchUp[X <: AnyGraphObject](obj: X) extends AnyPrimitiveMorph {
    type Obj = X

    type     Out = Obj
    lazy val out = obj

    type     In = Obj ⊗ Obj
    lazy val in = obj ⊗ obj

    type     Dagger = duplicate[Obj]
    lazy val dagger = duplicate(obj)

    lazy val label = s"matchUp(${obj.label} ⊗ ${obj.label})"
  }


  // 0 → X
  case class fromZero[X <: AnyGraphObject](obj: X) extends AnyPrimitiveMorph {
    type Obj = X

    type     In = zero
    lazy val in = zero

    type     Out = Obj
    lazy val out = obj

    type     Dagger = toZero[Obj]
    lazy val dagger = toZero(obj)

    lazy val label = s"fromZero(${obj.label})"
  }

  // X → 0
  case class toZero[X <: AnyGraphObject](obj: X) extends AnyPrimitiveMorph {
    type Obj = X

    type     Out = zero
    lazy val out = zero

    type     In = Obj
    lazy val in = obj

    type     Dagger = fromZero[Obj]
    lazy val dagger = fromZero(obj)

    lazy val label = s"toZero(${obj.label})"
  }

  // X -> X ⊕ X
  case class fork[X <: AnyGraphObject](obj: X) extends AnyPrimitiveMorph {
    type Obj = X

    type     In = Obj
    lazy val in = obj

    type     Out = BiproductObj[Obj, Obj]
    lazy val out = BiproductObj(obj, obj)

    type     Dagger = merge[Obj]
    lazy val dagger = merge(obj)

    lazy val label = s"fork(${obj.label})"
  }

  // X ⊕ X -> X
  case class merge[X <: AnyGraphObject](obj: X) extends AnyPrimitiveMorph {
    type Obj = X

    type     Out = Obj
    lazy val out = obj

    type     In = BiproductObj[Obj, Obj]
    lazy val in = BiproductObj(obj, obj)

    type     Dagger = fork[Obj]
    lazy val dagger = fork(obj)

    lazy val label = s"merge(${obj.label} ⊕ ${obj.label})"
  }


  // L → L ⊕ R
  case class leftInj[B <: AnyBiproductObj](val biproduct: B) extends AnyPrimitiveMorph {

    type Biproduct = B

    type     In = Biproduct#Left
    lazy val in = biproduct.left

    type     Out = Biproduct
    lazy val out = biproduct

    type     Dagger = leftProj[Biproduct]
    lazy val dagger = leftProj(biproduct)

    lazy val label = s"(${biproduct.left.label} leftInj ${biproduct.label})"
  }

  // L ⊕ R → L
  case class leftProj[B <: AnyBiproductObj](val biproduct: B) extends AnyPrimitiveMorph {

    type Biproduct = B

    type     Out = Biproduct#Left
    lazy val out = biproduct.left

    type     In = Biproduct
    lazy val in = biproduct

    type     Dagger = leftInj[Biproduct]
    lazy val dagger = leftInj(biproduct)

    lazy val label = s"leftProj(${biproduct.label})"
  }


  // R → L ⊕ R
  case class rightInj[B <: AnyBiproductObj](val biproduct: B) extends AnyPrimitiveMorph {
    type Biproduct = B

    type     In = Biproduct#Right
    lazy val in = biproduct.right

    type     Out = Biproduct
    lazy val out = biproduct

    type     Dagger = rightProj[Biproduct]
    lazy val dagger = rightProj(biproduct)

    lazy val label = s"(${biproduct.right.label} rightInj ${biproduct.label})"
  }

  // L ⊕ R → R
  case class rightProj[B <: AnyBiproductObj](val biproduct: B) extends AnyPrimitiveMorph {

    type Biproduct = B

    type     Out = Biproduct#Right
    lazy val out = biproduct.right

    type     In = Biproduct
    lazy val in = biproduct

    type     Dagger = rightInj[Biproduct]
    lazy val dagger = rightInj(biproduct)

    lazy val label = s"leftProj(${biproduct.label})"
  }


  case class target[E <: AnyEdge](val edge: E) extends AnyPrimitiveMorph {

    type Edge = E

    type     In = Edge
    lazy val in = edge

    type     Out = Edge#TargetVertex
    lazy val out = edge.targetVertex

    type     Dagger = inE[Edge]
    lazy val dagger = inE(edge)

    lazy val label: String = s"target(${edge.label})"
  }

  case class inE[E <: AnyEdge](val edge: E) extends AnyPrimitiveMorph {

    type Edge = E

    type     Out = Edge
    lazy val out = edge

    type     In = Edge#TargetVertex
    lazy val in = edge.targetVertex

    type     Dagger = target[Edge]
    lazy val dagger = target(edge)


    lazy val label = s"inE(${edge.label})"
  }


  case class source[E <: AnyEdge](val edge: E) extends AnyPrimitiveMorph {

    type Edge = E

    type     In = Edge
    lazy val in = edge

    type     Out = Edge#SourceVertex
    lazy val out = edge.sourceVertex

    type     Dagger = outE[Edge]
    lazy val dagger = outE(edge)

    lazy val label: String = s"source(${edge.label})"
  }

  case class outE[E <: AnyEdge](val edge: E) extends AnyPrimitiveMorph {

    type Edge = E

    type     Out = Edge
    lazy val out = edge

    type     In = Edge#SourceVertex
    lazy val in = edge.sourceVertex

    type     Dagger = source[Edge]
    lazy val dagger = source(edge)

    lazy val label = s"outE(${edge.label})"
  }


  case class outV[E <: AnyEdge](val edge: E) extends AnyPrimitiveMorph {

    type Edge = E

    type     In = Edge#SourceVertex
    lazy val in = edge.sourceVertex

    type     Out = Edge#TargetVertex
    lazy val out = edge.targetVertex

    type     Dagger = inV[Edge]
    lazy val dagger = inV(edge)

    lazy val label: String = s"outV(${edge.label})"
  }

  case class inV[E <: AnyEdge](val edge: E) extends AnyPrimitiveMorph {

    type Edge = E

    type     Out = Edge#SourceVertex
    lazy val out = edge.sourceVertex

    type     In = Edge#TargetVertex
    lazy val in = edge.targetVertex

    type     Dagger = outV[Edge]
    lazy val dagger = outV(edge)

    lazy val label = s"inV(${edge.label})"
  }


  case class get[P <: AnyProperty](val property: P) extends AnyPrimitiveMorph {

    type Property = P

    type     In = Property#Owner
    lazy val in = property.owner

    type     Out = Property#Value
    lazy val out = property.value

    type     Dagger = lookup[Property]
    lazy val dagger = lookup(property)

    lazy val label: String = s"get(${property.label})"
  }

  case class lookup[P <: AnyProperty](val property: P) extends AnyPrimitiveMorph {

    type Property = P

    type     Out = Property#Owner
    lazy val out = property.owner

    type     In = Property#Value
    lazy val in = property.value

    type Dagger = get[Property]
    lazy val dagger = get(property)

    lazy val label = s"lookup(${property.label})"
  }


  case class quantify[P <: AnyPredicate](val predicate: P) extends AnyPrimitiveMorph {

    type Predicate = P

    type     In = Predicate#Element
    lazy val in = predicate.element

    type     Out = Predicate
    lazy val out = predicate

    type     Dagger = coerce[Predicate]
    lazy val dagger = coerce(predicate)

    lazy val label: String = s"quantify(${predicate.label})"
  }


  case class coerce[P <: AnyPredicate](val predicate: P) extends AnyPrimitiveMorph {

    type Predicate = P

    type     Out = Predicate#Element
    lazy val out = predicate.element

    type     In = Predicate
    lazy val in = predicate

    type     Dagger = quantify[Predicate]
    lazy val dagger = quantify(predicate)

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
    lazy val in = TensorObj(left.in, right.in): In

    type     Out = TensorObj[Left#Out, Right#Out]
    lazy val out = TensorObj(left.out, right.out): Out

    type     Dagger = TensorMorph[Left#Dagger, Right#Dagger]
    lazy val dagger = TensorMorph(left.dagger: Left#Dagger, right.dagger: Right#Dagger)

    lazy val label = s"(${left.label} ⊗ ${right.label})"
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
    lazy val in = BiproductObj(left.in, right.in): In

    type     Out = BiproductObj[Left#Out, Right#Out]
    lazy val out = BiproductObj(left.out, right.out): Out

    type     Dagger = BiproductMorph[Left#Dagger, Right#Dagger]
    lazy val dagger = BiproductMorph(left.dagger: Left#Dagger, right.dagger: Right#Dagger)

    lazy val label = s"(${left.label} ⊕ ${right.label})"
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
    lazy val in = l ⊗ r

    type     Out = R ⊗ L
    lazy val out = r ⊗ l

    type     Dagger = symmetry[R, L]
    lazy val dagger = symmetry(r, l)

    lazy val label: String = s"symmetry(${l.label}, ${r.label})"
  }

  case class associateLeft[A <: AnyGraphObject, B <: AnyGraphObject, C <: AnyGraphObject]
    (a: A, b: B, c: C) extends AnyNaturalIsomorphism {

    type     In = A ⊗ (B ⊗ C)
    lazy val in = a ⊗ (b ⊗ c)

    type     Out = (A ⊗ B) ⊗ C
    lazy val out = (a ⊗ b) ⊗ c

    type     Dagger = associateRight[A, B, C]
    lazy val dagger = associateRight(a, b, c)

    lazy val label: String = s"associateLeft(${a.label} ⊗ (${b.label} ⊕ ${c.label}))"
  }

  case class associateRight[A <: AnyGraphObject, B <: AnyGraphObject, C <: AnyGraphObject]
    (a: A, b: B, c: C) extends AnyNaturalIsomorphism {

    type     In = (A ⊗ B) ⊗ C
    lazy val in = (a ⊗ b) ⊗ c

    type     Out = A ⊗ (B ⊗ C)
    lazy val out = a ⊗ (b ⊗ c)

    type     Dagger = associateLeft[A, B, C]
    lazy val dagger = associateLeft(a, b, c)

    lazy val label: String = s"associateRight((${a.label} ⊗ ${b.label}) ⊗ ${c.label})"
  }


  case class distribute[U <: AnyGraphObject, A <: AnyGraphObject, B <: AnyGraphObject]
    (u: U, a: A, b: B) extends AnyNaturalIsomorphism {

    type     In = U ⊗ (A ⊕ B)
    lazy val in = u ⊗ (a ⊕ b)

    type     Out = (U ⊗ A) ⊕ (U ⊗ B)
    lazy val out = (u ⊗ a) ⊕ (u ⊗ b)

    type     Dagger = undistribute[U, A, B]
    lazy val dagger = undistribute(u, a, b)

    lazy val label: String = s"distribute(${u.label} ⊗ (${a.label} ⊕ ${b.label}))"
  }

  case class undistribute[U <: AnyGraphObject, A <: AnyGraphObject, B <: AnyGraphObject]
    (u: U, a: A, b: B) extends AnyNaturalIsomorphism {

    type     Out = U ⊗ (A ⊕ B)
    lazy val out = u ⊗ (a ⊕ b)

    type     In = (U ⊗ A) ⊕ (U ⊗ B)
    lazy val in = (u ⊗ a) ⊕ (u ⊗ b)

    type     Dagger = distribute[U, A, B]
    lazy val dagger = distribute(u, a, b)

    lazy val label: String = s"undistribute((${u.label} ⊗ ${a.label}) ⊕ (${u.label} ⊗ ${b.label}))"
  }


  // I ⊗ X → X
  case class leftUnit[X <: AnyGraphObject](x: X) extends AnyNaturalIsomorphism {

    type     In = unit ⊗ X
    lazy val in = unit ⊗ x

    type     Out = X
    lazy val out = x

    type     Dagger = leftCounit[X]
    lazy val dagger = leftCounit(x)

    lazy val label = s"leftUnit(I ⊗ ${x.label})"
  }

  // X → I ⊗ X
  case class leftCounit[X <: AnyGraphObject](x: X) extends AnyNaturalIsomorphism {

    type     Out = unit ⊗ X
    lazy val out = unit ⊗ x

    type     In = X
    lazy val in = x

    type     Dagger = leftUnit[X]
    lazy val dagger = leftUnit(x)

    lazy val label = s"leftCounit(${x.label})"

  }


  // X ⊗ I → X
  case class rightUnit[X <: AnyGraphObject](x: X) extends AnyNaturalIsomorphism {

    type     In = X ⊗ unit
    lazy val in = x ⊗ unit

    type     Out = X
    lazy val out = x

    type     Dagger = rightCounit[X]
    lazy val dagger = rightCounit(x)

    lazy val label = s"rightUnit(${x.label} ⊗ I)"
  }

  // X → I ⊗ X
  case class rightCounit[X <: AnyGraphObject](x: X) extends AnyNaturalIsomorphism {

    type     In = X
    lazy val in = x

    type     Out = X ⊗ unit
    lazy val out = x ⊗ unit

    type     Dagger = rightUnit[X]
    lazy val dagger = rightUnit(x)

    lazy val label = s"rightCounit(${x.label})"
  }


  // 0 ⊕ X → X
  case class leftZero[X <: AnyGraphObject](x: X) extends AnyNaturalIsomorphism {

    type     In = zero ⊕ X
    lazy val in = zero ⊕ x

    type     Out = X
    lazy val out = x

    type     Dagger = leftCozero[X]
    lazy val dagger = leftCozero(x)

    lazy val label = s"leftZero(0 ⊕ ${x.label})"
  }

  // X → 0 ⊕ X
  case class leftCozero[X <: AnyGraphObject](x: X) extends AnyNaturalIsomorphism {

    type     Out = zero ⊕ X
    lazy val out = zero ⊕ x

    type     In = X
    lazy val in = x

    type     Dagger = leftZero[X]
    lazy val dagger = leftZero(x)

    lazy val label = s"leftCozero(${x.label})"

  }


  // X ⊕ 0 → X
  case class rightZero[X <: AnyGraphObject](x: X) extends AnyNaturalIsomorphism {

    type     In = X ⊕ zero
    lazy val in = x ⊕ zero

    type     Out = X
    lazy val out = x

    type     Dagger = rightCozero[X]
    lazy val dagger = rightCozero(x)

    lazy val label = s"rightZero(${x.label} ⊕ 0)"
  }

  // X → 0 ⊕ X
  case class rightCozero[X <: AnyGraphObject](x: X) extends AnyNaturalIsomorphism {

    type     In = X
    lazy val in = x

    type     Out = X ⊕ zero
    lazy val out = x ⊕ zero

    type     Dagger = rightZero[X]
    lazy val dagger = rightZero(x)

    lazy val label = s"rightCozero(${x.label})"
  }


  // /* A derived morphism is just an alias for some other morphisms (we can derive eval for it)*/
  // trait AnyDerivedMorphism extends AnyGraphMorphism { self =>
  //
  //   type Morph <: AnyGraphMorphism
  //   val  morph: Morph
  //
  //   type     In = Morph#In
  //   lazy val in = morph.in
  //
  //   type     Out = Morph#Out
  //   lazy val out = morph.out
  //
  //   type     Dagger <: AnyDerivedMorphism {
  //     type Morph = self.Dagger
  //   }
  //   // lazy val dagger = morph.dagger
  // }
  //
  // abstract class DerivedMorphism[M <: AnyGraphMorphism](val morph: M)
  //   extends AnyDerivedMorphism { type Morph = M }

  trait AnyBiproductTrace extends AnyGraphMorphism {

    type Morph <: AnyGraphMorphism {
      type In <: AnyBiproductObj
      type Out <: AnyBiproductObj { type Right = In#Right }
    }
    val morph: Morph

    type     In >: Morph#In#Left <: Morph#In#Left
    lazy val in: In = morph.in.left

    type     Out >: Morph#Out#Left <: Morph#Out#Left
    lazy val out = morph.out.left

    type Dagger = daggerBiproductTrace[Morph]
    lazy val dagger = daggerBiproductTrace[Morph](morph)

    lazy val label = s"biproductTrace(${morph.label})"
  }

  case class biproductTrace[
    M <: AnyGraphMorphism {
      type In <: AnyBiproductObj
      type Out <: AnyBiproductObj { type Right = In#Right }
    }
  ](val morph: M) extends AnyBiproductTrace {

    type Morph = M
    type     In = Morph#In#Left
    type     Out = Morph#Out#Left
  }

  case class daggerBiproductTrace[
    M <: AnyGraphMorphism {
      type In <: AnyBiproductObj
      type Out <: AnyBiproductObj { type Right = In#Right }
    }
  ](val morph: M) extends AnyGraphMorphism {

    type Morph = M

    type     In = Morph#Out#Left
    lazy val in = morph.out.left

    type     Out = Morph#In#Left
    lazy val out = morph.in.left

    type Dagger = biproductTrace[Morph]
    lazy val dagger = biproductTrace[Morph](morph)

    lazy val label = s"biproductTrace(${morph.label})"
  }


  trait AnyTensorTrace extends AnyGraphMorphism {

    type Morph <: AnyGraphMorphism {
      type In <: AnyTensorObj
      // TODO this or the non-symmetric version?
      type Out <: AnyTensorObj { type Right = In#Right }
    }
    val morph: Morph

    type     In >: Morph#In#Left <: Morph#In#Left
    lazy val in: In = morph.in.left

    type     Out >: Morph#Out#Left <: Morph#Out#Left
    lazy val out = morph.out.left

    type Dagger = daggerTensorTrace[Morph]
    lazy val dagger = daggerTensorTrace[Morph](morph)

    lazy val label = s"tensorTrace(${morph.label})"
  }

  // tensor Trace
  case class tensorTrace[
    M <: AnyGraphMorphism {
      type In <: AnyTensorObj
      type Out <: AnyTensorObj { type Right = In#Right }
    }
  ](val morph: M) extends AnyTensorTrace {

    type Morph = M
    type     In = Morph#In#Left
    type     Out = Morph#Out#Left
  }

  case class daggerTensorTrace[
    M <: AnyGraphMorphism {
      type In <: AnyTensorObj
      type Out <: AnyTensorObj { type Right = In#Right }
    }
  ](val morph: M) extends AnyGraphMorphism {

    type Morph = M

    type     In = Morph#Out#Left
    lazy val in = morph.out.left

    type     Out = Morph#In#Left
    lazy val out = morph.in.left

    type Dagger = tensorTrace[Morph]
    lazy val dagger = tensorTrace[Morph](morph)

    lazy val label = s"tensorTrace(${morph.label})"
  }

}
