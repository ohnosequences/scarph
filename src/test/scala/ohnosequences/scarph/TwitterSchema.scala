package ohnosequences.scarph.test

import ohnosequences.scarph._, steps._, combinators._
import ohnosequences.cosas._, AnyTypeSet._

object Twitter {

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

  // case object posted extends EdgeType(user, tweet) with InArity[ExactlyOne] with OutArity[ManyOrNone]
  // case object time extends PropertyOf(posted) { type Raw = String }
  // case object url  extends PropertyOf(posted) { type Raw = String }

  // case object follows extends EdgeType(user, user) with InArity[ManyOrNone] with OutArity[ManyOrNone]

  // case object liked extends EdgeType(user, tweet) with InArity[ManyOrNone] with OutArity[ManyOrNone]

  // simple indexes
  case object userByName extends SimpleIndex(user, name)
  case object tweetByText extends SimpleIndex(tweet, text)
  case object postedByTime extends SimpleIndex(posted, time)

  // composite indexes
  case object userByNameAndAge extends CompositeIndex(user, name :~: age :~: ∅)

  // vertex-centric indexes
  case object postedByTimeAndUrlLocal extends LocalEdgeIndex(posted, OnlySourceCentric, time :~: url :~: ∅)

  val schema = Schema(label = "twitter",
    properties = name :~: age :~: text :~: time :~: url :~: ∅,
    vertexTypes =  user :~: tweet :~: ∅,
    edgeTypes = posted :~: follows :~: liked :~: ∅,
    indexes = 
      userByName :~: userByNameAndAge :~:
      tweetByText :~: 
      postedByTime :~: 
      postedByTimeAndUrlLocal :~: 
      ∅
  )

}

object StupidQueries {
  import Twitter._

  val uh = InV(follows) map Get(name)
  val zz = Target(follows) >=> InE(follows)
  val altSyntax = Target(follows) >=> InE(follows)
  val ups = InE(posted)
  // this is clunky right now, but it works
  val uuuuh = InE(posted) map Target(posted)

  val asdfadf = InV(follows) map InV(follows)
  val asdfadf2 = InV(follows) map InV(follows)

  val ohno = Par(
    InV(follows) map InV(follows),
    Target(follows) >=> InE(follows)
  )

  val sfdsd = ((InV(follows) map InV(follows)) ⨂ (Target(follows) >=> InE(follows))) ⨁ (InV(follows) map OutV(posted))

  val yurj = rev( Target(follows) >=> InE(follows) )
}
