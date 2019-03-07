
```scala
package ohnosequences.scarph.test

case object asserts {

  import ohnosequences.cosas._, types._
  import org.scalatest.Assertions._

  // not only compares the values, but also check the types equality (essential for tagged values)
  def checkTypedEq[A, B](a: A, b: B)(implicit typesEq: A ≃ B): Boolean = a == b

  implicit def taggedOps[T <: AnyType, V <: T#Raw](v: T := V):
    TaggedOps[T, V] =
    TaggedOps[T, V](v)

  case class TaggedOps[T <: AnyType, V <: T#Raw](v: T := V) {

    def =~=[W <: T#Raw](w: T := W)(implicit typesEq: V ≃ W): Boolean = v.value == w.value
  }

  def assertTypedEq[A, B](a: A, b: B)(implicit typesEq: A ≃ B): Unit =
    assert(a == b)

  def assertTaggedEq[TA <: AnyType, A <: TA#Raw, TB <: AnyType, B <: TB#Raw](a: TA := A, b: TB := B)
    (implicit
      tagsEq: TA ≃ TB,
      valsEq: A ≃ B
    ): Unit = assert(a.value == b.value)
}

```




[main/scala/ohnosequences/scarph/axioms.scala]: ../../../../main/scala/ohnosequences/scarph/axioms.scala.md
[main/scala/ohnosequences/scarph/tensor.scala]: ../../../../main/scala/ohnosequences/scarph/tensor.scala.md
[main/scala/ohnosequences/scarph/predicates.scala]: ../../../../main/scala/ohnosequences/scarph/predicates.scala.md
[main/scala/ohnosequences/scarph/impl/biproducts.scala]: ../../../../main/scala/ohnosequences/scarph/impl/biproducts.scala.md
[main/scala/ohnosequences/scarph/impl/tensors.scala]: ../../../../main/scala/ohnosequences/scarph/impl/tensors.scala.md
[main/scala/ohnosequences/scarph/impl/evals.scala]: ../../../../main/scala/ohnosequences/scarph/impl/evals.scala.md
[main/scala/ohnosequences/scarph/impl/distributivity.scala]: ../../../../main/scala/ohnosequences/scarph/impl/distributivity.scala.md
[main/scala/ohnosequences/scarph/impl/relations.scala]: ../../../../main/scala/ohnosequences/scarph/impl/relations.scala.md
[main/scala/ohnosequences/scarph/impl/category.scala]: ../../../../main/scala/ohnosequences/scarph/impl/category.scala.md
[main/scala/ohnosequences/scarph/rewrites.scala]: ../../../../main/scala/ohnosequences/scarph/rewrites.scala.md
[main/scala/ohnosequences/scarph/package.scala]: ../../../../main/scala/ohnosequences/scarph/package.scala.md
[main/scala/ohnosequences/scarph/arities.scala]: ../../../../main/scala/ohnosequences/scarph/arities.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: ../../../../main/scala/ohnosequences/scarph/objects.scala.md
[main/scala/ohnosequences/scarph/writes.scala]: ../../../../main/scala/ohnosequences/scarph/writes.scala.md
[main/scala/ohnosequences/scarph/biproduct.scala]: ../../../../main/scala/ohnosequences/scarph/biproduct.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: ../../../../main/scala/ohnosequences/scarph/schemas.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: ../../../../main/scala/ohnosequences/scarph/morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/package.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/package.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/objects.scala.md
[main/scala/ohnosequences/scarph/syntax/writes.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/writes.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/isomorphisms.scala]: ../../../../main/scala/ohnosequences/scarph/isomorphisms.scala.md
[test/scala/ohnosequences/scarph/TwitterQueries.scala]: TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: impl/dummy.scala.md
[test/scala/ohnosequences/scarph/impl/writes.scala]: impl/writes.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: TwitterSchema.scala.md
[test/scala/ohnosequences/scarph/asserts.scala]: asserts.scala.md
[test/scala/ohnosequences/scarph/SchemaCreation.scala]: SchemaCreation.scala.md
[test/scala/ohnosequences/scarph/implicitSearch.scala]: implicitSearch.scala.md