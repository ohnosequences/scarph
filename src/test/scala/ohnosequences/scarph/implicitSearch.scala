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
