
```scala
package ohnosequences.scarph.syntax

object predicates {

  import ohnosequences.cosas._, types._

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.containers._, s.conditions._, s.predicates._
```

When you don't want to restrict the query anyhow (let's imagine it makes sence),
you can just say: `query(user).out(..).blah.evalOn(any(user))`

```scala
  implicit def any[E <: AnyGraphElement](e: E): EmptyPredicate[E] = new EmptyPredicate[E](e)
```

A way of building a predicate from an element

```scala
  implicit def elementPredicateOps[E <: AnyGraphElement](elem: E):
      ElementPredicateOps[E] = 
      ElementPredicateOps[E](elem)

  case class ElementPredicateOps[E <: AnyGraphElement](elem: E) {
```

For example: `user ? (name === "bob")` - this operator can be read as "such that"

```scala
    def ?[C <: AnyCondition.OnElement[E]](c: C): 
      AndPredicate[EmptyPredicate[E], C] = AndPredicate(new EmptyPredicate(elem), c)
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
          + [ContainersTest.scala][test/scala/ohnosequences/scarph/ContainersTest.scala]
          + [ScalazEquality.scala][test/scala/ohnosequences/scarph/ScalazEquality.scala]
          + titan
            + [TwitterTitanTest.scala][test/scala/ohnosequences/scarph/titan/TwitterTitanTest.scala]
          + [TwitterSchema.scala][test/scala/ohnosequences/scarph/TwitterSchema.scala]
    + resources
  + main
    + scala
      + ohnosequences
        + scarph
          + [GraphTypes.scala][main/scala/ohnosequences/scarph/GraphTypes.scala]
          + [Containers.scala][main/scala/ohnosequences/scarph/Containers.scala]
          + impl
            + titan
              + [Schema.scala][main/scala/ohnosequences/scarph/impl/titan/Schema.scala]
              + [Evals.scala][main/scala/ohnosequences/scarph/impl/titan/Evals.scala]
              + [Predicates.scala][main/scala/ohnosequences/scarph/impl/titan/Predicates.scala]
          + [Paths.scala][main/scala/ohnosequences/scarph/Paths.scala]
          + [Indexes.scala][main/scala/ohnosequences/scarph/Indexes.scala]
          + [Evals.scala][main/scala/ohnosequences/scarph/Evals.scala]
          + [Conditions.scala][main/scala/ohnosequences/scarph/Conditions.scala]
          + [Steps.scala][main/scala/ohnosequences/scarph/Steps.scala]
          + [Predicates.scala][main/scala/ohnosequences/scarph/Predicates.scala]
          + [Schemas.scala][main/scala/ohnosequences/scarph/Schemas.scala]
          + [Combinators.scala][main/scala/ohnosequences/scarph/Combinators.scala]
          + syntax
            + [GraphTypes.scala][main/scala/ohnosequences/scarph/syntax/GraphTypes.scala]
            + [Paths.scala][main/scala/ohnosequences/scarph/syntax/Paths.scala]
            + [Conditions.scala][main/scala/ohnosequences/scarph/syntax/Conditions.scala]
            + [Predicates.scala][main/scala/ohnosequences/scarph/syntax/Predicates.scala]

[test/scala/ohnosequences/scarph/ContainersTest.scala]: ../../../../../test/scala/ohnosequences/scarph/ContainersTest.scala.md
[test/scala/ohnosequences/scarph/ScalazEquality.scala]: ../../../../../test/scala/ohnosequences/scarph/ScalazEquality.scala.md
[test/scala/ohnosequences/scarph/titan/TwitterTitanTest.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/TwitterTitanTest.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[main/scala/ohnosequences/scarph/GraphTypes.scala]: ../GraphTypes.scala.md
[main/scala/ohnosequences/scarph/Containers.scala]: ../Containers.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Schema.scala]: ../impl/titan/Schema.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Evals.scala]: ../impl/titan/Evals.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Predicates.scala]: ../impl/titan/Predicates.scala.md
[main/scala/ohnosequences/scarph/Paths.scala]: ../Paths.scala.md
[main/scala/ohnosequences/scarph/Indexes.scala]: ../Indexes.scala.md
[main/scala/ohnosequences/scarph/Evals.scala]: ../Evals.scala.md
[main/scala/ohnosequences/scarph/Conditions.scala]: ../Conditions.scala.md
[main/scala/ohnosequences/scarph/Steps.scala]: ../Steps.scala.md
[main/scala/ohnosequences/scarph/Predicates.scala]: ../Predicates.scala.md
[main/scala/ohnosequences/scarph/Schemas.scala]: ../Schemas.scala.md
[main/scala/ohnosequences/scarph/Combinators.scala]: ../Combinators.scala.md
[main/scala/ohnosequences/scarph/syntax/GraphTypes.scala]: GraphTypes.scala.md
[main/scala/ohnosequences/scarph/syntax/Paths.scala]: Paths.scala.md
[main/scala/ohnosequences/scarph/syntax/Conditions.scala]: Conditions.scala.md
[main/scala/ohnosequences/scarph/syntax/Predicates.scala]: Predicates.scala.md