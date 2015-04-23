
```scala
package ohnosequences.scarph.syntax

object conditions {

  import ohnosequences.{ scarph => s }
  import s.objects._
```

Method aliases for predicate constructors

```scala
  implicit def conditionOps[P <: AnyGraphProperty](property: P):
      ConditionOps[P] =
      ConditionOps[P](property)

  case class ConditionOps[P <: AnyGraphProperty](property: P) {

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
            + [predicates.scala][main/scala/ohnosequences/scarph/syntax/predicates.scala]
            + [graphTypes.scala][main/scala/ohnosequences/scarph/syntax/graphTypes.scala]
            + [conditions.scala][main/scala/ohnosequences/scarph/syntax/conditions.scala]

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
[main/scala/ohnosequences/scarph/syntax/predicates.scala]: predicates.scala.md
[main/scala/ohnosequences/scarph/syntax/graphTypes.scala]: graphTypes.scala.md
[main/scala/ohnosequences/scarph/syntax/conditions.scala]: conditions.scala.md