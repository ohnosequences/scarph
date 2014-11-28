package ohnosequences.scarph.test

import ohnosequences.scarph._, combinators._
import ohnosequences.cosas._, AnyTypeSet._

object TwitterSchema {

  case object user extends VertexType
  case object name extends PropertyOf(user) { type Raw = String }
  case object age  extends PropertyOf(user) { type Raw = Integer }

  case object tweet extends VertexType
  case object text extends PropertyOf(tweet) { type Raw = String }

  case object posted extends EdgeType(ExactlyOne, user, ManyOrNone, tweet)

  case object time extends PropertyOf(posted) { type Raw = String }
  case object url  extends PropertyOf(posted) { type Raw = String }

  case object follows extends EdgeType(ManyOrNone, user, ManyOrNone, user)
  case object liked extends EdgeType(ManyOrNone, user, ManyOrNone, tweet) 

  // stupid queries
  val uh = inV(follows) map get(name)
  val zz = target(follows) >=> in(follows)
  val altSyntax = target(follows) >=> in(follows)
  val ups = in(posted)
  // this is clunky right now, but it works
  val uuuuh = in(posted) map target(posted)

  val asdfadf = inV(follows) map inV(follows)
  val asdfadf2 = inV(follows) map inV(follows)

  val ohno = Par(
                  inV(follows) map inV(follows),
                  target(follows) >=> in(follows)
                )

  val sfdsd = ((inV(follows) map inV(follows)) ⨂ (target(follows) >=> in(follows))) ⨁ (inV(follows) map outV(posted))
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
