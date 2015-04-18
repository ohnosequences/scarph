
```scala
package ohnosequences.scarph.syntax

object predicates {

  import ohnosequences.cosas._, types._

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.conditions._, s.predicates._, s.morphisms._
```

When you don't want to restrict the query anyhow (let's imagine it makes sence),
you can just say: `query(user).out(..).blah.evalOn(any(user))`

```scala
  //implicit def any[E <: AnyGraphElement](e: E): EmptyPredicate[E] = EmptyPredicate[E](e)

```

A way of building a predicate from an element

```scala
  implicit def elementPredicateOps[E <: AnyGraphElement](e: E):
      ElementPredicateOps[E] =
      ElementPredicateOps[E](e)

  case class ElementPredicateOps[E <: AnyGraphElement](e: E) {
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

  case class PredicateOps[P <: AnyPredicate](pred: P) {
```

It's basically cons for the internal conditions type-set,
but with a restriction on the condtion's element type

```scala
    def and[C <: AnyCondition.OnElement[P#Element]](c: C):
      AndPredicate[P, C] = AndPredicate(pred, c)
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

[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummy.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: ../morphisms.scala.md
[main/scala/ohnosequences/scarph/predicates.scala]: ../predicates.scala.md
[main/scala/ohnosequences/scarph/monoidalStructures.scala]: ../monoidalStructures.scala.md
[main/scala/ohnosequences/scarph/evals.scala]: ../evals.scala.md
[main/scala/ohnosequences/scarph/implementations.scala]: ../implementations.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: ../schemas.scala.md
[main/scala/ohnosequences/scarph/naturalIsomorphisms.scala]: ../naturalIsomorphisms.scala.md
[main/scala/ohnosequences/scarph/graphTypes.scala]: ../graphTypes.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/predicates.scala]: predicates.scala.md
[main/scala/ohnosequences/scarph/syntax/graphTypes.scala]: graphTypes.scala.md
[main/scala/ohnosequences/scarph/syntax/conditions.scala]: conditions.scala.md
[main/scala/ohnosequences/scarph/conditions.scala]: ../conditions.scala.md