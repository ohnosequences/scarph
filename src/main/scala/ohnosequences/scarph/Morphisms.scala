package ohnosequences.scarph


/* Basic set of morphisms: */
object morphisms {

  import graphTypes._, predicates._, monoidalStructures._

  trait AnyPrimitive extends AnyGraphMorphism { morph =>

    type Dagger <: AnyDaggerPrimitive {
      type Dagger >: morph.type <: AnyPrimitive
    }
  }

  trait AnyDaggerPrimitive extends AnyGraphMorphism {

    type Dagger <: AnyPrimitive
    val  dagger: Dagger
  }

  abstract class DaggerOf[M <: AnyPrimitive](val m: M) extends AnyDaggerPrimitive {

    type     Dagger = M
    lazy val dagger = m

    type     In = Dagger#Out
    lazy val in = dagger.out

    type     Out = Dagger#In
    lazy val out = dagger.in
  }


  // n: I → X
  case class unitor[X <: AnyGraphObject](x: X) extends AnyPrimitive {

    type     In = unit
    lazy val in = unit

    type     Out = X
    lazy val out = x

    type     Dagger = counitor[X]
    lazy val dagger = counitor(x)

    lazy val label = s"unitor(${x.label})"
  }

  // e: X → I
  case class counitor[X <: AnyGraphObject](x: X) 
    extends DaggerOf(unitor[X](x)) { lazy val label = s"counitor(${x.label})" }


  // △: X → X ⊗ X
  case class fork[X <: AnyGraphObject](x: X) extends AnyPrimitive {

    type     In = X
    lazy val in = x

    type     Out = X ⊗ X
    lazy val out = x ⊗ x

    type     Dagger = merge[X]
    lazy val dagger = merge(x)

    lazy val label = s"fork(${x.label})"
  }

  // ▽: X ⊗ X → X
  case class merge[X <: AnyGraphObject](x: X)
    extends DaggerOf(fork[X](x)) { lazy val label = s"merge(${x.label} ⊗ ${x.label})" }


  // δ: X -> X ⊕ X
  case class either[X <: AnyGraphObject](x: X) extends AnyPrimitive {

    type     In = X
    lazy val in = x

    type     Out = BiproductObj[X, X]
    lazy val out = BiproductObj(x, x)

    type     Dagger = anyOf[X]
    lazy val dagger = anyOf(x)

    lazy val label = s"either(${x.label})"
  }

  // ε: X ⊕ X -> X
  case class anyOf[X <: AnyGraphObject](x: X)
    extends DaggerOf(either[X](x)) { lazy val label = s"anyOf(${x.label} ⊕ ${x.label})" }


  /* Projections and injections */
  case class leftInj[B <: AnyBiproductObj](val biproduct: B) extends AnyPrimitive {
    type Biproduct = B

    type     In = Biproduct#Left
    lazy val in = biproduct.left

    type     Out = Biproduct
    lazy val out = biproduct

    type     Dagger = leftProj[Biproduct]
    lazy val dagger = leftProj(biproduct)

    lazy val label = s"(${biproduct.left.label} leftInj ${biproduct.label})"
  }

  case class leftProj[B <: AnyBiproductObj](b: B) 
    extends DaggerOf(leftInj[B](b)) { lazy val label = s"leftProj(${b.label})" }


  case class rightInj[B <: AnyBiproductObj](val biproduct: B) extends AnyPrimitive {
    type Biproduct = B

    type     In = Biproduct#Right
    lazy val in = biproduct.right

    type     Out = Biproduct
    lazy val out = biproduct

    type     Dagger = rightProj[Biproduct]
    lazy val dagger = rightProj(biproduct)

    lazy val label = s"(${biproduct.right.label} rightInj ${biproduct.label})"
  }

  case class rightProj[B <: AnyBiproductObj](b: B) 
    extends DaggerOf(rightInj[B](b)) { lazy val label = s"rightProj(${b.label})" }


  case class target[E <: AnyEdge](val edge: E) extends AnyPrimitive {
    type Edge = E

    type     In = Edge
    lazy val in = edge

    type     Out = Edge#TargetVertex
    lazy val out = edge.targetVertex

    type     Dagger = inE[Edge]
    lazy val dagger = inE(edge)

    lazy val label: String = s"target(${edge.label})"
  }

  case class inE[E <: AnyEdge](e: E)
    extends DaggerOf(target[E](e)) { lazy val label = s"inE(${e.label})" }


  case class source[E <: AnyEdge](val edge: E) extends AnyPrimitive {
    type Edge = E

    type     In = Edge
    lazy val in = edge

    type     Out = Edge#SourceVertex
    lazy val out = edge.sourceVertex

    type     Dagger = outE[Edge]
    lazy val dagger = outE(edge)

    lazy val label: String = s"source(${edge.label})"
  }

  case class outE[E <: AnyEdge](e: E)
    extends DaggerOf(source[E](e)) { lazy val label = s"outE(${e.label})" }


  case class outV[E <: AnyEdge](val edge: E) extends AnyPrimitive {
    type Edge = E

    type     In = Edge#SourceVertex
    lazy val in = edge.sourceVertex

    type     Out = Edge#TargetVertex
    lazy val out = edge.targetVertex

    type     Dagger = inV[Edge]
    lazy val dagger = inV(edge)

    lazy val label: String = s"outV(${edge.label})"
  }

  case class inV[E <: AnyEdge](e: E)
    extends DaggerOf(outV[E](e)) { lazy val label = s"inV(${e.label})" }


  case class get[P <: AnyGraphProperty](val property: P) extends AnyPrimitive {
    type Property = P

    type     In = Property#Owner
    lazy val in = property.owner

    type     Out = Property#Value
    lazy val out = property.value

    type     Dagger = lookup[Property]
    lazy val dagger = lookup(property)

    lazy val label: String = s"get(${property.label})"
  }

  case class lookup[P <: AnyGraphProperty](p: P)
    extends DaggerOf(get[P](p)) { lazy val label = s"lookup(${p.label})" }



  trait AnyIsomorphism extends AnyPrimitive

  // σ: L ⊗ R → R ⊗ L
  case class symmetry[L <: AnyGraphObject, R <: AnyGraphObject](l: L, r: R) extends AnyIsomorphism with AnyDaggerPrimitive {

    type     In = L ⊗ R
    lazy val in = l ⊗ r

    type     Out = R ⊗ L
    lazy val out = r ⊗ l

    type     Dagger = symmetry[R, L]
    lazy val dagger = symmetry(r, l)

    lazy val label: String = s"symmetry(${l.label}, ${r.label})"
  }

  case class distribute[U <: AnyGraphObject, A <: AnyGraphObject, B <: AnyGraphObject]
    (u: U, a: A, b: B) extends AnyIsomorphism {

    type     In = U ⊗ (A ⊕ B)
    lazy val in = u ⊗ (a ⊕ b)

    type     Out = (U ⊗ A) ⊕ (U ⊗ B)
    lazy val out = (u ⊗ a) ⊕ (u ⊗ b)

    type     Dagger = undistribute[U, A, B]
    lazy val dagger = undistribute(u, a, b)

    lazy val label: String = s"distribute(${u.label} ⊗ (${a.label} ⊕ ${b.label}))"
  }

  case class undistribute[U <: AnyGraphObject, A <: AnyGraphObject, B <: AnyGraphObject]
    (u: U, a: A, b: B) extends DaggerOf(distribute[U, A, B](u, a, b)) {

    lazy val label: String = s"undistribute((${u.label} ⊗ ${a.label}) ⊕ (${u.label} ⊗ ${b.label}))"
  }


  // λ: I ⊗ X → X
  case class leftUnitor[X <: AnyGraphObject](x: X) extends AnyIsomorphism {

    type     In = unit ⊗ X
    lazy val in = unit ⊗ x

    type     Out = X
    lazy val out = x

    type     Dagger = leftCounitor[X]
    lazy val dagger = leftCounitor(x)

    lazy val label = s"leftUnitor(I ⊗ ${x.label})"
  }

  // λ^{-1}: X → I ⊗ X
  case class leftCounitor[X <: AnyGraphObject](x: X)
    extends DaggerOf(leftUnitor(x)) { lazy val label = s"leftCounitor(${x.label})"}


  // ρ: X ⊗ I → X
  case class rightUnitor[X <: AnyGraphObject](x: X) extends AnyIsomorphism {

    type     In = X ⊗ unit
    lazy val in = x ⊗ unit

    type     Out = X
    lazy val out = x

    type     Dagger = rightCounitor[X]
    lazy val dagger = rightCounitor(x)

    lazy val label = s"rightUnitor(${x.label} ⊗ I)"
  }

  // ρ^{-1}: X → I ⊗ X
  case class rightCounitor[X <: AnyGraphObject](x: X) 
    extends DaggerOf(rightUnitor(x)) { lazy val label = s"rightCounitor(${x.label})" }

}
