package bio4j.model.test

import bio4j.model._
import vertexTypes._
import properties._

object edgeTypes {
  // specify a source and target
  case object MemberOf extends ManyToMany(User, Org)
  // add some more props externally  
  // directly
  implicit val withSince = EdgeTypeHasProperty(MemberOf, since)
  // through ops
  implicit val withValidUntil = MemberOf has validUntil

  // explicit witness
  case object Owns extends ManyToOne(User, Org){
    implicit val x = this has since
    implicit val y = this has validUntil
  }

  // pretty cool DSL
  implicit val thisIsSoCoolItScaresMe = many(User) -- "owns" --> one(Org)
  implicit val manymanymany = many(User) -- "member of" --> many(Org)

}
