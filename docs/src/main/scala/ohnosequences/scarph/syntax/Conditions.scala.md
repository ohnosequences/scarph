
```scala
package ohnosequences.scarph.syntax

object conditions {

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.conditions._
```

Method aliases for predicate constructors

```scala
  implicit def conditionOps[P <: AnyGraphProperty { type Raw <: Comparable[_] }](property: P):
      ConditionOps[P] = 
      ConditionOps[P](property)

  case class ConditionOps[P <: AnyGraphProperty { type Raw <: Comparable[_] }](property: P) {

    def ===(value: P#Raw): Equal[P] = Equal(property, value)
    def =/=(value: P#Raw): NotEqual[P] = NotEqual(property, value)

    def <(value: P#Raw): Less[P] = Less(property, value)
    def ≤(value: P#Raw): LessOrEqual[P] = LessOrEqual(property, value)

    def >(value: P#Raw): Greater[P] = Greater(property, value)
    def ≥(value: P#Raw): GreaterOrEqual[P] = GreaterOrEqual(property, value)

    def between(s: P#Raw, e: P#Raw): Interval[P] = Interval(property, s, e)
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