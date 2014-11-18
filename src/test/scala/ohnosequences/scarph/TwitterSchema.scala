package ohnosequences.scarph.test

import ohnosequences.scarph._
import ohnosequences.cosas._, AnyTypeSet._

object Twitter {

  case object user extends VertexType
  case object name extends PropertyOf(user) { type Raw = String }
  case object age  extends PropertyOf(user) { type Raw = Integer }

  case object tweet extends VertexType
  case object text extends PropertyOf(tweet) { type Raw = String }

  case object posted extends EdgeType(user, tweet) with InArity[ExactlyOne] with OutArity[ManyOrNone]
  case object time extends PropertyOf(posted) { type Raw = String }
  case object url  extends PropertyOf(posted) { type Raw = String }

  case object follows extends EdgeType(user, user) with InArity[ManyOrNone] with OutArity[ManyOrNone]

  case object liked extends EdgeType(user, tweet) with InArity[ManyOrNone] with OutArity[ManyOrNone]

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
