package ohnosequences.scarph.test.titan

import ohnosequences.pointless._, AnyTypeSet._
import ohnosequences.scarph._, impl.titan._
import ohnosequences.scarph.test._, TwitterSchema._

object TwitterImpl {

  case object user extends TitanVertex(User)
  case object tweet extends TitanVertex(Tweet)

  case object posted extends TitanEdge(user, Posted, tweet)
  case object follows extends TitanEdge(user, Follows, user)

}
