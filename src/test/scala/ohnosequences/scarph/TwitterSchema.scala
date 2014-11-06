package ohnosequences.scarph.test

import ohnosequences.scarph._
import ohnosequences.pointless._, AnyTypeSet._

object TwitterSchema {

  case object user extends VertexType
  case object name extends PropertyOf(user) { type Raw = String }
  case object age  extends PropertyOf(user) { type Raw = Integer }

  case object tweet extends VertexType
  case object text extends PropertyOf(tweet) { type Raw = String }

  case object posted extends EdgeType(user, Many(tweet))
  case object time extends PropertyOf(posted) { type Raw = String }
  case object url  extends PropertyOf(posted) { type Raw = String }

  case object follows extends EdgeType(Many(user), Many(user))

  case object liked extends EdgeType(Many(user), tweet)
}

//   case object UserNameIx extends CompositeIndex(User, name)
//   case object TweetTextIx extends CompositeIndex(Tweet, text)
//   case object PostedTimeIx extends CompositeIndex(Posted, time)

//   val schemaType = SchemaType("twitter",
//     vertexTypes = User :~: Tweet :~: ∅,
//     edgeTypes = Posted :~: Follows :~: ∅,
//     indexes = UserNameIx :~: TweetTextIx :~: PostedTimeIx :~: ∅
//   )

// }
