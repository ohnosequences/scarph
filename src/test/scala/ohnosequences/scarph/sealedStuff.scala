package ohnosequences.scarph.test

import ohnosequences.scarph._, ops.typelevel._
import ohnosequences.typesets._

object StupidSealedStuff {
  
  case object name extends Property[String]
  case object age extends Property[Integer]
  case object happiness extends Property[Long]
  case object id extends Property[Int]

  // records
  case object record extends Record(name :~: age :~: ∅)
  case object anotherRecord extends Record(age :~: happiness :~: ∅)
  case object myFavRecord extends Record(id :~: happiness :~: name :~: ∅)

  // types
  case object Yuhu extends SealedVertexType("yuhu", record)
  case object OhNo extends SealedVertexType("ohno", anotherRecord)  
  case object SayItTo extends SealedEdgeType(Yuhu, "sayitto", myFavRecord, OhNo) with ManyIn with OneOut

  case object yuhu extends SealedVertex(Yuhu) {

    type Other = String
  }

  case object ohNo extends SealedVertex(OhNo) {

    // why not?
    type Other = Integer
  }

  case object sayItTo extends SealedEdge(yuhu, SayItTo, ohNo) { sayItTo =>

    type Other = (source.Rep, target.Rep)

    implicit val s: GetSource = new GetSource { def apply(edge: sayItTo.Rep) = edge.other._1 }
    implicit val t: GetTarget = new GetTarget { def apply(edge: sayItTo.Rep) = edge.other._2 } 
  }

  val y1 = yuhu ->> (

    yuhu.raw (

      yuhu fields (
        (name is "lalala") :~:
        (age is 12) :~: ∅
      ),
      "rubbish"
    )
  )

  val o1 = ohNo ->> {

    ohNo.raw (

      ohNo fields (
        (age is 1231231)      :~:
        (happiness is 111111) :~: ∅
      ),
      234234
    )
  }

  val s1 = sayItTo ->> {

    sayItTo.raw (

      sayItTo fields (

        (id is 12312)       :~: 
        (happiness is 23423) :~: 
        (name is "dfadfww")  :~: ∅
      ),
      (y1, o1)
    )
  }


}

class CheckSealedTypes extends org.scalatest.FunSuite {

  import StupidSealedStuff._

  test("getting properties from sealed vertex instances") {

    assert (

      (y1 get name) === "lalala"
    )

    assert (

      (o1 get happiness) === 111111
    )
  }

  test("getting properties from sealed edge instances") {

    assert (

      (s1 get happiness) === 23423
    )
  }

  test ("naive source and target from sealed edge instances") {

    assert (

      (s1 src) === y1
    )

    assert (

      (s1 tgt) === o1
    )
  }
}