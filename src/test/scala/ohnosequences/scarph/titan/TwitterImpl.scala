package ohnosequences.scarph.test.titan

import ohnosequences.pointless._, AnyTypeSet._
import ohnosequences.scarph._, impl.titan._
import ohnosequences.scarph.test._, TwitterSchema._
import com.thinkaurelius.titan.core.{ TitanGraph => TGraph }

case object TitanTwitter extends Schema(TwitterSchema.schemaType) {

  implicit val user = this.implements(TitanVertex(User))
  implicit val tweet = this.implements(TitanVertex(Tweet))

  implicit val posted = this.implements(TitanEdge(Posted))
  implicit val follows = this.implements(TitanEdge(Follows))

}
