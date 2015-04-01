package ohnosequences.scarph


/* Basic set of morphisms: */
object isomorphisms {

  import graphTypes._, predicates._, monoidalStructures._, morphisms._


  trait AnyIsomorphism extends AnyPrimitive


  // id: X → X
  case class identity[X <: AnyGraphObject](x: X) extends AnyIsomorphism with AnyDaggerPrimitive {

    type     In = X
    lazy val in = x

    type     Out = X
    lazy val out = x

    type     Dagger = identity[X]
    lazy val dagger = identity(x)

    lazy val label = s"identity(${x.label})"
  }


  // σ: L ⊗ R → R ⊗ L
  case class symmetry[L <: AnyGraphObject, R <: AnyGraphObject](l: L, r: R)
    extends AnyIsomorphism with AnyDaggerPrimitive {

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
  case class leftUnit[X <: AnyGraphObject](x: X) extends AnyIsomorphism {

    type     In = unit ⊗ X
    lazy val in = unit ⊗ x

    type     Out = X
    lazy val out = x

    type     Dagger = leftCounit[X]
    lazy val dagger = leftCounit(x)

    lazy val label = s"leftUnit(I ⊗ ${x.label})"
  }

  // λ^{-1}: X → I ⊗ X
  case class leftCounit[X <: AnyGraphObject](x: X)
    extends DaggerOf(leftUnit(x)) { lazy val label = s"leftCounit(${x.label})"}


  // ρ: X ⊗ I → X
  case class rightUnit[X <: AnyGraphObject](x: X) extends AnyIsomorphism {

    type     In = X ⊗ unit
    lazy val in = x ⊗ unit

    type     Out = X
    lazy val out = x

    type     Dagger = rightCounit[X]
    lazy val dagger = rightCounit(x)

    lazy val label = s"rightUnit(${x.label} ⊗ I)"
  }

  // ρ^{-1}: X → I ⊗ X
  case class rightCounit[X <: AnyGraphObject](x: X)
    extends DaggerOf(rightUnit(x)) { lazy val label = s"rightCounit(${x.label})" }

}
