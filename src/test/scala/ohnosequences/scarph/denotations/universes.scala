package ohnosequences.scarph.universes.test

import ohnosequences.scarph._

object SimpleUniverse {
    
  sealed trait Types

    object INT extends Types

    trait LIST extends Types {

      type T <: Types
      val t: T
    }
    case class L[T0 <: Types](val t: T0) extends LIST {

      type T = T0
    }
  trait Interpretation extends Denotation[Types] {}
  class Interpret[T <: Types](val t: T) extends Interpretation {

    type Tpe = T
    val tpe = t
  }
}

object simpleInterpretation {

  import SimpleUniverse._

  sealed trait interpretation extends Interpretation

    implicit case object int extends Interpret[INT.type](INT) with interpretation { type Raw = Int }
    
    case class list[
      L <: LIST,
      I <: interpretation { type Tpe = L#T }
    ](val l: L)(implicit val it: I) 
    extends Interpret(l) with interpretation {

      type Raw = List[it.Raw]
    }

  implicit def asdf[
    T0 <: Singleton with LIST
  ](implicit il0: Singleton with interpretation { type Tpe = T0 }): list[L[T0],il0.type] = list(L(il0.tpe))(il0)

  implicit def co(implicit it: Singleton with Interpret[INT.type] with interpretation): list[L[INT.type],it.type] = list(L(INT))(it)

  // implicit def asfdl[
  //   L0 <: LIST,
  //   L <: LIST { type T = L0 },
  //   I0 <: interpretation { type Tpe = L0#T },
  //   I <: list[L0,I0] with interpretation { type Tpe = L#T }
  // ](implicit
  //   il: I 
  // ): list[L,I] = list(il.l)(il)
}

class testUniv extends org.scalatest.FunSuite {

  import simpleInterpretation._
  import SimpleUniverse._

  val buh = int ->> 343

  val l = list(L(INT)) ->> List(12,232,3)
  val ll = list(L(L(INT))) ->> List(List(12), List(12,874))

  // val wrongll = list(L(L(INT))) ->> 2323

  abstract class EqualImpl[UI <: SimpleUniverse.Interpretation](val ui: UI) {

    def apply(x: ui.Rep, y: ui.Rep): Boolean
  }

  implicit def toEqOps[UI <: SimpleUniverse.Interpretation](ui: UI) = equalOps(ui)
  case class equalOps[UI <: SimpleUniverse.Interpretation](ui: UI) {

    def equal(x: ui.Rep, y: ui.Rep)(implicit eqImpl: EqualImpl[ui.type]): Boolean = {

      eqImpl(x,y)
    }   
  }

  implicit val int_eq: EqualImpl[int.type] = new EqualImpl[int.type](int) {

    def apply(x: int.Rep, y: int.Rep): Boolean = (x == y)
  }

  test("equality") {

    val a = int ->> 12
    val b = int ->> 12

    assert(int.equal(a, b))
  }

}

