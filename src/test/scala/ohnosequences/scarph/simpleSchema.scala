package ohnosequences.scarph.test

object simpleSchema {

  import ohnosequences.scarph._

  // vertices
  case object User extends VertexType("user")
  implicit val userId    = User has id
  implicit val userName  = User has name
  case object Org extends VertexType("org")
  implicit val orgName   = Org has name

  // edges
  case object MemberOf extends ManyToMany  (User, "memberOf", Org)
  implicit val memberOfHasValidUntil = MemberOf has validUntil
  implicit val memberOfHasSince      = MemberOf has since
  case object Owns extends ManyToOne(User, "owns", Org)
  implicit val ownsHasSince      = Owns has since
  implicit val ownsHasValidUntil = Owns has validUntil

  // properties
  case object since extends Property[Int]
  case object validUntil extends Property[Int]
  case object name extends Property[String]
  case object isPublic extends Property[Boolean]
  case object id extends Property[String]
}