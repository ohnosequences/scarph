package ohnosequences.scarph.test

object vertexTypes {
  
  import ohnosequences.scarph.VertexType

  import ohnosequences.scarph._
  import properties._

  // just labels and witnesses for properties
  case object User extends VertexType("user") {
    
    implicit val userId     = this has id
    implicit val userName   = this has name
  }

  case object Org extends VertexType("org") {
    
    implicit val orgName   = this has name
  }
}
