package ohnosequences.scarph.test
  
import ohnosequences.typesets._
import ohnosequences.scarph._, properties._

object vertexTypes {

  // just labels and witnesses for properties
  case object User extends VertexType("user") {
    
    implicit val userId     = this has id
    implicit val userName   = this has name
  }

  case object Org extends VertexType("org", name :~: ∅)
}

class VertexTypeSuite extends org.scalatest.FunSuite {

  import vertexTypes._  

  test("filter vertex type properties") {
    assert(User.filterMyProps(allProperties) === name :~: id :~: ∅)
    // assert( Org.filterMyProps(allProperties) === name :~: ∅)
  }

}
