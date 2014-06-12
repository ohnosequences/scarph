package ohnosequences.scarph.test

import ohnosequences.typesets._
import ohnosequences.scarph._, vertexTypes._, properties._

object edgeTypes {

  case object MemberOf extends ManyToMany (User, "memberOf", Org)
  implicit val memberOfHasSince      = MemberOf has since
  implicit val memberOfHasValidUntil = MemberOf has validUntil

  case object Owns extends ManyToOne(User, "owns", Org)
  implicit val  ownsHasSince      = Owns has since
  implicit val  ownsHasValidUntil = Owns has validUntil

}

class EdgeTypeSuite extends org.scalatest.FunSuite {

  import edgeTypes._  

  test("filter edge type properties") {
    assert(MemberOf.filterMyProps(allProperties) === since :~: validUntil :~: ∅)
  }

}
