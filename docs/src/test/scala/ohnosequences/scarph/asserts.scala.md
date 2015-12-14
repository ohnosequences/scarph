
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
[main/scala/ohnosequences/scarph/evals.scala]: ../../../../main/scala/ohnosequences/scarph/evals.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: ../../../../main/scala/ohnosequences/scarph/morphisms.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: ../../../../main/scala/ohnosequences/scarph/objects.scala.md
[main/scala/ohnosequences/scarph/rewrites.scala]: ../../../../main/scala/ohnosequences/scarph/rewrites.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: ../../../../main/scala/ohnosequences/scarph/schemas.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/objects.scala.md
[test/scala/ohnosequences/scarph/asserts.scala]: asserts.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: impl/dummy.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/implicitSearch.scala]: implicitSearch.scala.md
[test/scala/ohnosequences/scarph/TwitterQueries.scala]: TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: TwitterSchema.scala.md