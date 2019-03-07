
```scala
package ohnosequences.scarph.syntax

import ohnosequences.cosas.types._
import ohnosequences.scarph._
```

A way of building a predicate from an element

```scala
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
case class ConditionOps[P <: AnyProperty](property: P) extends AnyVal {

  def ===(value: P#Target#Raw): Equal[P] = Equal(property, value)
  def =/=(value: P#Target#Raw): NotEqual[P] = NotEqual(property, value)

  def <(value: P#Target#Raw): Less[P] = Less(property, value)
  def ≤(value: P#Target#Raw): LessOrEqual[P] = LessOrEqual(property, value)

  def >(value: P#Target#Raw): Greater[P] = Greater(property, value)
  def ≥(value: P#Target#Raw): GreaterOrEqual[P] = GreaterOrEqual(property, value)

  def between(s: P#Target#Raw, e: P#Target#Raw): Interval[P] = Interval(property, s, e)
}

```




[main/scala/ohnosequences/scarph/axioms.scala]: ../axioms.scala.md
[main/scala/ohnosequences/scarph/tensor.scala]: ../tensor.scala.md
[main/scala/ohnosequences/scarph/predicates.scala]: ../predicates.scala.md
[main/scala/ohnosequences/scarph/impl/biproducts.scala]: ../impl/biproducts.scala.md
[main/scala/ohnosequences/scarph/impl/tensors.scala]: ../impl/tensors.scala.md
[main/scala/ohnosequences/scarph/impl/evals.scala]: ../impl/evals.scala.md
[main/scala/ohnosequences/scarph/impl/distributivity.scala]: ../impl/distributivity.scala.md
[main/scala/ohnosequences/scarph/impl/relations.scala]: ../impl/relations.scala.md
[main/scala/ohnosequences/scarph/impl/category.scala]: ../impl/category.scala.md
[main/scala/ohnosequences/scarph/rewrites.scala]: ../rewrites.scala.md
[main/scala/ohnosequences/scarph/package.scala]: ../package.scala.md
[main/scala/ohnosequences/scarph/arities.scala]: ../arities.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: ../objects.scala.md
[main/scala/ohnosequences/scarph/writes.scala]: ../writes.scala.md
[main/scala/ohnosequences/scarph/biproduct.scala]: ../biproduct.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: ../schemas.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: ../morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/package.scala]: package.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: objects.scala.md
[main/scala/ohnosequences/scarph/syntax/writes.scala]: writes.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: morphisms.scala.md
[main/scala/ohnosequences/scarph/isomorphisms.scala]: ../isomorphisms.scala.md
[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummy.scala.md
[test/scala/ohnosequences/scarph/impl/writes.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/writes.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[test/scala/ohnosequences/scarph/asserts.scala]: ../../../../../test/scala/ohnosequences/scarph/asserts.scala.md
[test/scala/ohnosequences/scarph/SchemaCreation.scala]: ../../../../../test/scala/ohnosequences/scarph/SchemaCreation.scala.md
[test/scala/ohnosequences/scarph/implicitSearch.scala]: ../../../../../test/scala/ohnosequences/scarph/implicitSearch.scala.md