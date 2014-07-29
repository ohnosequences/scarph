package ohnosequences.scarph.test

import ohnosequences.scarph._
import ohnosequences.typesets._

object StupidSealedVertices {
  
  case object name extends Property[String]
  case object age extends Property[Integer]

  case object record extends Record(name :~: age :~: ∅)

  case object Yuhu extends SealedVertexType("yuhu", record)

  case object yuhu extends SealedVertex(Yuhu) {

    type Other = String
  }

  val uhoh = yuhu ->> (
    yuhu.raw (

      yuhu fields (
        (name is "lalala") :~:
        (age is 12) :~: ∅
      ),
      "rubbish"
    )
  )
}

class CheckSealedVertices extends org.scalatest.FunSuite {

  import StupidSealedVertices._

  test("getting properties from sealed vertex instances") {

    assert (

      (uhoh get name) === "lalala"
    )
  }
}