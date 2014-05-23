package ohnosequences.scarph.test

object vertexTypes {
  
  import ohnosequences.typesets._
  import ohnosequences.scarph._, properties._

  // just labels and witnesses for properties
  case object User extends VertexType("user") {
    
    implicit val userId     = this has id
    implicit val userName   = this has name
  }

  case object Org extends FinalVertexType("org", name :~: ∅)
}
