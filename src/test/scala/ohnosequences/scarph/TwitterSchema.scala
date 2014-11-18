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

  case object nameIx extends SimpleIndex(name)
  case object textIx extends SimpleIndex(text)
  case object timeIx extends SimpleIndex(time)

  case object postedByTimeAndUrl extends LocalEdgeIndex(posted, OnlySourceCentric, time :~: url :~: ∅)

  val schema = Schema(label = "twitter",
    properties = name :~: age :~: text :~: time :~: url :~: ∅,
    vertexTypes =  user :~: tweet :~: ∅,
    edgeTypes = posted :~: follows :~: liked :~: ∅,
    indexes = nameIx :~: textIx :~: timeIx :~: postedByTimeAndUrl :~: ∅
  )

}
