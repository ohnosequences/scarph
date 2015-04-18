
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

    type     Element = Property#Owner
    lazy val element = property.owner

    val label: String
    override final def toString = label
  }

  object AnyCondition {

    type OnProperty[P <: AnyGraphProperty] = AnyCondition { type Property = P }
    type OnElement[E <: AnyGraphElement] = AnyCondition { type Element = E }
  }
```

Comparison conditions with **One** property value

```scala
  trait AnyCompareCondition extends AnyCondition {
    type Property <: AnyGraphProperty //{ type Raw <: Comparable[_] }

    val value: Property#Value#Raw
  }

  trait CompareCondition[P <: AnyGraphProperty]
    extends AnyCompareCondition { type Property = P }


  trait AnyEqual extends AnyCompareCondition
  case class Equal[P <: AnyGraphProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyEqual with CompareCondition[P] {
    lazy val label = s"${property.label} = ${value.toString}"
  }

  trait AnyNotEqual extends AnyCompareCondition
  case class NotEqual[P <: AnyGraphProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyNotEqual with CompareCondition[P] {
    lazy val label = s"${property.label} ≠ ${value.toString}"
  }


  trait AnyLess extends AnyCompareCondition
  case class Less[P <: AnyGraphProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyLess with CompareCondition[P] {
    lazy val label = s"${property.label} < ${value.toString}"
  }

  trait AnyLessOrEqual extends AnyCompareCondition
  case class LessOrEqual[P <: AnyGraphProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyLessOrEqual with CompareCondition[P] {
    lazy val label = s"${property.label} ≤ ${value.toString}"
  }


  trait AnyGreater extends AnyCompareCondition
  case class Greater[P <: AnyGraphProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyGreater with CompareCondition[P] {
    lazy val label = s"${property.label} > ${value.toString}"
  }

  trait AnyGreaterOrEqual extends AnyCompareCondition
  case class GreaterOrEqual[P <: AnyGraphProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyGreaterOrEqual with CompareCondition[P] {
    lazy val label = s"${property.label} ≥ ${value.toString}"
  }


  trait AnyInterval extends AnyCondition {
    type Property <: AnyGraphProperty

    val start: Property#Value#Raw
    val end: Property#Value#Raw
  }

  case class Interval[P <: AnyGraphProperty](
    val property: P,
    val start: P#Value#Raw,
    val end: P#Value#Raw
  ) extends AnyInterval {
    type Property = P
    lazy val label = s"${start.toString} ≤ ${property.label} ≤ ${end.toString}"
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