
```scala
package ohnosequences.scarph.typeSets

object implicitSearch {

  trait Ops {

    def applyImpl: String
    final def apply(): String = applyImpl
  }

  trait AnyStructuralOps extends Ops
  trait AnyShapeBasedOps extends AnyStructuralOps
  trait AnyQuerySpecificOps extends AnyShapeBasedOps

  class StructuralOps extends AnyStructuralOps {

    def applyImpl: String = "I am structural"
  }

  class ShapeBasedOps extends AnyShapeBasedOps {

    def applyImpl: String = "I am shape-based"
  }

  class QuerySpecificOps extends AnyQuerySpecificOps {

    def applyImpl: String = "I am query-specific"
  }


  trait AddsStructuralOps {

    implicit def addsStructuralOps: StructuralOps = new StructuralOps
  }

  trait AddsShapeBasedOps extends AddsStructuralOps {

    implicit def addsShapeBasedOps: ShapeBasedOps = new ShapeBasedOps
  }

  trait AddsQuerySpecificOps extends AddsShapeBasedOps {

    implicit def addsQuerySpecificOps: QuerySpecificOps = new QuerySpecificOps
  }

  object useOps {

    def hola[O <: Ops](implicit o: O): String = o()
  }

  object shouldBeQS extends AddsQuerySpecificOps
}

class testPriority extends org.scalatest.FunSuite {

  import implicitSearch._
  import shouldBeQS._

  test("priorities") {

    info(useOps.hola)
  }
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