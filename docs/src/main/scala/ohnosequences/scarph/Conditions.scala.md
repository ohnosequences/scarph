
```scala
package ohnosequences.scarph

object conditions {

  import ohnosequences.cosas._, properties._
  import graphTypes._
  import java.lang.Comparable
```

A condition is a restriction on the property values

```scala
  trait AnyCondition { 

    type Property <: AnyGraphProperty
    val  property: Property

    type Element = Property#Owner
    val  element = property.owner
  }

  object AnyCondition {

    type OnProperty[P <: AnyGraphProperty] = AnyCondition { type Property = P }
    type OnElement[E <: AnyGraphElement] = AnyCondition { type Element = E }
  }
```

Comparison conditions with **One** property value

```scala
  trait AnyCompareCondition extends AnyCondition { 
    type Property <: AnyGraphProperty { type Raw <: Comparable[_] }

    val value: Property#Raw 
  }

  trait CompareCondition[P <: AnyGraphProperty { type Raw <: Comparable[_] }] 
    extends AnyCompareCondition { type Property = P }


  trait AnyEqual extends AnyCompareCondition
  case class Equal[P <: AnyGraphProperty { type Raw <: Comparable[_] }](
    val property: P,
    val value: P#Raw
  ) extends AnyEqual with CompareCondition[P]

  trait AnyNotEqual extends AnyCompareCondition
  case class NotEqual[P <: AnyGraphProperty { type Raw <: Comparable[_] }](
    val property: P,
    val value: P#Raw
  ) extends AnyNotEqual with CompareCondition[P]


  trait AnyLess extends AnyCompareCondition
  case class Less[P <: AnyGraphProperty { type Raw <: Comparable[_] }](
    val property: P,
    val value: P#Raw
  ) extends AnyLess with CompareCondition[P]

  trait AnyLessOrEqual extends AnyCompareCondition
  case class LessOrEqual[P <: AnyGraphProperty { type Raw <: Comparable[_] }](
    val property: P,
    val value: P#Raw
  ) extends AnyLessOrEqual with CompareCondition[P]


  trait AnyGreater extends AnyCompareCondition
  case class Greater[P <: AnyGraphProperty { type Raw <: Comparable[_] }](
    val property: P,
    val value: P#Raw
  ) extends AnyGreater with CompareCondition[P]

  trait AnyGreaterOrEqual extends AnyCompareCondition
  case class GreaterOrEqual[P <: AnyGraphProperty { type Raw <: Comparable[_] }](
    val property: P,
    val value: P#Raw
  ) extends AnyGreaterOrEqual with CompareCondition[P]


  trait AnyInterval extends AnyCondition {
    type Property <: AnyGraphProperty { type Raw <: Comparable[_] }

    val start: Property#Raw
    val end: Property#Raw
  }

  case class Interval[P <: AnyGraphProperty { type Raw <: Comparable[_] }](
    val property: P,
    val start: P#Raw,
    val end: P#Raw
  ) extends AnyInterval { type Property = P }

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

[test/scala/ohnosequences/scarph/ContainersTest.scala]: ../../../../test/scala/ohnosequences/scarph/ContainersTest.scala.md
[test/scala/ohnosequences/scarph/ScalazEquality.scala]: ../../../../test/scala/ohnosequences/scarph/ScalazEquality.scala.md
[test/scala/ohnosequences/scarph/titan/TwitterTitanTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TwitterTitanTest.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[main/scala/ohnosequences/scarph/GraphTypes.scala]: GraphTypes.scala.md
[main/scala/ohnosequences/scarph/Containers.scala]: Containers.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Schema.scala]: impl/titan/Schema.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Evals.scala]: impl/titan/Evals.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Predicates.scala]: impl/titan/Predicates.scala.md
[main/scala/ohnosequences/scarph/Paths.scala]: Paths.scala.md
[main/scala/ohnosequences/scarph/Indexes.scala]: Indexes.scala.md
[main/scala/ohnosequences/scarph/Evals.scala]: Evals.scala.md
[main/scala/ohnosequences/scarph/Conditions.scala]: Conditions.scala.md
[main/scala/ohnosequences/scarph/Steps.scala]: Steps.scala.md
[main/scala/ohnosequences/scarph/Predicates.scala]: Predicates.scala.md
[main/scala/ohnosequences/scarph/Schemas.scala]: Schemas.scala.md
[main/scala/ohnosequences/scarph/Combinators.scala]: Combinators.scala.md
[main/scala/ohnosequences/scarph/syntax/GraphTypes.scala]: syntax/GraphTypes.scala.md
[main/scala/ohnosequences/scarph/syntax/Paths.scala]: syntax/Paths.scala.md
[main/scala/ohnosequences/scarph/syntax/Conditions.scala]: syntax/Conditions.scala.md
[main/scala/ohnosequences/scarph/syntax/Predicates.scala]: syntax/Predicates.scala.md