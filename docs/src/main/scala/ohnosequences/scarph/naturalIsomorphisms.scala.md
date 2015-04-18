
```scala
package ohnosequences.scarph
```

Basic set of morphisms:

```scala
object naturalIsomorphisms {

  import graphTypes._, predicates._, monoidalStructures._, morphisms._


  trait AnyNaturalIsomorphism extends AnyPrimitive { iso =>

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

    type     Out = X ⊗ unit
    lazy val out = x ⊗ unit

    type     In = X
    lazy val in = x

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

    type     Out = X ⊕ zero
    lazy val out = x ⊕ zero

    type     In = X
    lazy val in = x

    type     Dagger = rightZero[X]
    lazy val dagger = rightZero(x)

    lazy val label = s"rightCozero(${x.label})" 
  }

}

```


------

### Index

+ src
  + test
    + scala
      + ohnosequences
        + scarph
          + [TwitterQueries.scala][test/scala/ohnosequences/scarph/TwitterQueries.scala]
          + impl
            + [dummyTest.scala][test/scala/ohnosequences/scarph/impl/dummyTest.scala]
            + [dummy.scala][test/scala/ohnosequences/scarph/impl/dummy.scala]
          + [TwitterSchema.scala][test/scala/ohnosequences/scarph/TwitterSchema.scala]
  + main
    + scala
      + ohnosequences
        + scarph
          + [morphisms.scala][main/scala/ohnosequences/scarph/morphisms.scala]
          + [predicates.scala][main/scala/ohnosequences/scarph/predicates.scala]
          + [monoidalStructures.scala][main/scala/ohnosequences/scarph/monoidalStructures.scala]
          + [evals.scala][main/scala/ohnosequences/scarph/evals.scala]
          + [implementations.scala][main/scala/ohnosequences/scarph/implementations.scala]
          + [schemas.scala][main/scala/ohnosequences/scarph/schemas.scala]
          + [naturalIsomorphisms.scala][main/scala/ohnosequences/scarph/naturalIsomorphisms.scala]
          + [graphTypes.scala][main/scala/ohnosequences/scarph/graphTypes.scala]
          + syntax
            + [morphisms.scala][main/scala/ohnosequences/scarph/syntax/morphisms.scala]
            + [predicates.scala][main/scala/ohnosequences/scarph/syntax/predicates.scala]
            + [graphTypes.scala][main/scala/ohnosequences/scarph/syntax/graphTypes.scala]
            + [conditions.scala][main/scala/ohnosequences/scarph/syntax/conditions.scala]
          + [conditions.scala][main/scala/ohnosequences/scarph/conditions.scala]

[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: ../../../../test/scala/ohnosequences/scarph/impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: ../../../../test/scala/ohnosequences/scarph/impl/dummy.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: morphisms.scala.md
[main/scala/ohnosequences/scarph/predicates.scala]: predicates.scala.md
[main/scala/ohnosequences/scarph/monoidalStructures.scala]: monoidalStructures.scala.md
[main/scala/ohnosequences/scarph/evals.scala]: evals.scala.md
[main/scala/ohnosequences/scarph/implementations.scala]: implementations.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: schemas.scala.md
[main/scala/ohnosequences/scarph/naturalIsomorphisms.scala]: naturalIsomorphisms.scala.md
[main/scala/ohnosequences/scarph/graphTypes.scala]: graphTypes.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/predicates.scala]: syntax/predicates.scala.md
[main/scala/ohnosequences/scarph/syntax/graphTypes.scala]: syntax/graphTypes.scala.md
[main/scala/ohnosequences/scarph/syntax/conditions.scala]: syntax/conditions.scala.md
[main/scala/ohnosequences/scarph/conditions.scala]: conditions.scala.md