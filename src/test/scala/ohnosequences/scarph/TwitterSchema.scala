package ohnosequences.scarph.test

import ohnosequences.scarph._, types._
import ohnosequences.cosas._, AnyTypeSet._

object TwitterSchema {

  case object user extends VertexType
  case object name extends PropertyOf(user) { type Raw = String }
  case object age  extends PropertyOf(user) { type Raw = Integer }

  case object tweet extends VertexType
  case object text extends PropertyOf(tweet) { type Raw = String }

  case object posted extends EdgeType( exactlyOne(user), manyOrNone(tweet) )
  // case object posted extends ==>( exactlyOne(user), manyOrNone(tweet) )

  case object time extends PropertyOf(posted) { type Raw = String }
  case object url  extends PropertyOf(posted) { type Raw = String }

  case object follows extends EdgeType(manyOrNone(user), manyOrNone(user))

  case object liked extends EdgeType(manyOrNone(user), manyOrNone(tweet)) 

  // stupid queries
  val uh = in(follows)

  val zz = target(follows) >=> in(follows)
  // val uhoh = Compose(target(follows), in(follows))
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
