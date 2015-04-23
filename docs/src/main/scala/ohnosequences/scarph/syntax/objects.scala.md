
```scala
package ohnosequences.scarph.syntax

object objects {

  import ohnosequences.cosas.types._

  import ohnosequences.{ scarph => s }
  import s.objects._

  implicit final def graphObjectValOps[F <: AnyGraphObject, VF](vt: F := VF):
    GraphObjectValOps[F, VF] =
    GraphObjectValOps[F, VF](vt.value)

  case class GraphObjectValOps[F <: AnyGraphObject, VF](vf: VF) extends AnyVal {

    // (F := t) ⊗ (S := s) : (F ⊗ S) := (t, s)
    def ⊗[S <: AnyGraphObject, VS](vs: S := VS): (F ⊗ S) := (VF, VS) =
      new Denotes( (vf, vs.value) )

    // (F := t) ⊕ (S := s) : (F ⊕ S) := (t, s)
    def ⊕[S <: AnyGraphObject, VS](vs: S := VS): (F ⊕ S) := (VF, VS) =
      new Denotes( (vf, vs.value) )
  }
```

A way of building a predicate from an element

```scala
  implicit def elementPredicateOps[E <: AnyGraphElement](e: E):
      ElementPredicateOps[E] =
      ElementPredicateOps[E](e)

  case class ElementPredicateOps[E <: AnyGraphElement](e: E) extends AnyVal {
```

For example: `user ? (user.name === "bob")`

```scala
    def ?[C <: AnyCondition.OnElement[E]](c: C):
      AndPredicate[EmptyPredicate[E], C] =
      AndPredicate(EmptyPredicate(e), c)
  }
```

Adding more conditions to a predicate

```scala
  implicit def predicateOps[P <: AnyPredicate](p: P):
      PredicateOps[P] =
      PredicateOps[P](p)

  case class PredicateOps[P <: AnyPredicate](pred: P) extends AnyVal {
```

It's basically cons for the internal conditions type-set,
but with a restriction on the condtion's element type

```scala
    def and[C <: AnyCondition.OnElement[P#Element]](c: C):
      AndPredicate[P, C] = AndPredicate(pred, c)
  }
```

Method aliases for predicate constructors

```scala
  implicit final def conditionOps[P <: AnyGraphProperty](property: P):
    ConditionOps[P] =
    ConditionOps[P](property)

  case class ConditionOps[P <: AnyGraphProperty](property: P) extends AnyVal {

    def ===(value: P#Value#Raw): Equal[P] = Equal(property, value)
    def =/=(value: P#Value#Raw): NotEqual[P] = NotEqual(property, value)

    def <(value: P#Value#Raw): Less[P] = Less(property, value)
    def ≤(value: P#Value#Raw): LessOrEqual[P] = LessOrEqual(property, value)

    def >(value: P#Value#Raw): Greater[P] = Greater(property, value)
    def ≥(value: P#Value#Raw): GreaterOrEqual[P] = GreaterOrEqual(property, value)

    def between(s: P#Value#Raw, e: P#Value#Raw): Interval[P] = Interval(property, s, e)
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
          + [objects.scala][main/scala/ohnosequences/scarph/objects.scala]
          + [evals.scala][main/scala/ohnosequences/scarph/evals.scala]
          + [implementations.scala][main/scala/ohnosequences/scarph/implementations.scala]
          + [schemas.scala][main/scala/ohnosequences/scarph/schemas.scala]
          + [naturalIsomorphisms.scala][main/scala/ohnosequences/scarph/naturalIsomorphisms.scala]
          + syntax
            + [morphisms.scala][main/scala/ohnosequences/scarph/syntax/morphisms.scala]
            + [objects.scala][main/scala/ohnosequences/scarph/syntax/objects.scala]

[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummy.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: ../morphisms.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: ../objects.scala.md
[main/scala/ohnosequences/scarph/evals.scala]: ../evals.scala.md
[main/scala/ohnosequences/scarph/implementations.scala]: ../implementations.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: ../schemas.scala.md
[main/scala/ohnosequences/scarph/naturalIsomorphisms.scala]: ../naturalIsomorphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: objects.scala.md