package ohnosequences.scarph.test

object Twitter {

  import ohnosequences.cosas._, typeSets._

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.steps._, s.indexes._, s.schemas._

  case object user extends Vertex
  case object name extends PropertyOf(user) { type Raw = String }
  case object age  extends PropertyOf(user) { type Raw = Integer }

  case object tweet extends Vertex
  case object text  extends PropertyOf(tweet) { type Raw = String }

  case object posted extends Edge(user -> tweet)
  case object time extends PropertyOf(posted) { type Raw = String }
  case object url  extends PropertyOf(posted) { type Raw = String }

  case object follows extends Edge(user -> user)
  // case object liked   extends Edge(user -> tweet)

  // case object reposted extends Edge(user -> tweet)
  // case object time extends PropertyOf(posted) { type Raw = String }

  // simple indexes
  case object userByName extends KeyIndex(user, name, Unique)
  case object tweetByText extends KeyIndex(tweet, text, NonUnique)
  case object postedByTime extends KeyIndex(posted, time, NonUnique)

  // composite indexes
  case object userByNameAndAge extends CompositeIndex(user, name :~: age :~: ∅, Unique)

  // vertex-centric indexes
  case object postedByTimeAndUrlLocal extends LocalEdgeIndex(posted, OnlySourceCentric, time :~: url :~: ∅)

  case object twitter extends GraphSchema(
    label = "twitter",
    properties = name :~: age :~: text :~: time :~: url :~: ∅,
    vertices =  user :~: tweet :~: ∅,
    edges = posted :~: follows :~: ∅,
    indexes = 
      userByName :~: userByNameAndAge :~:
      tweetByText :~: 
      postedByTime :~: 
      postedByTimeAndUrlLocal :~: 
      ∅
  )
}
