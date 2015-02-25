package ohnosequences.scarph.test

object Twitter {

  import ohnosequences.cosas._, typeSets._

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.schemas._

  /* Property value types */
  case object name extends ValueType[String]("name")
  case object age  extends ValueType[Integer]("age")
  case object text extends ValueType[String]("text")
  case object time extends ValueType[String]("time") // should have some better raw type
  case object url  extends ValueType[String]("url")

  /* Vertices with their properties */
  case object user extends Vertex
  case object userName extends Property(user, name)
  case object userAge  extends Property(user, age)
  // example of shared value types:
  case object userBio extends Property(user, text)
  case object userWebpage extends Property(user, url)

  case object tweet extends Vertex
  case object tweetText extends Property(tweet, text)
  case object tweetUrl  extends Property(tweet, url)

  /* Edges with their properties */
  case object posted extends Edge(user -> tweet)
  case object postedTime extends Property(posted, time)

  case object follows extends Edge(user -> user)

  case object liked extends Edge(user -> tweet)
  case object likedTime extends Property(liked, time)

  case object reposted extends Edge(user -> tweet)
  case object repostedTime extends Property(reposted, time)

  /* Schema */
  case object twitter extends GraphSchema(
    label = "twitter",
    vertices =  user :~: tweet :~: ∅,
    edges = posted :~: follows :~: liked :~: ∅,
    valueTypes = name :~: age :~: text :~: time :~: url :~: ∅,
    properties = 
      userName :~: userAge :~: userBio :~: userWebpage :~:
      tweetText :~: tweetUrl :~:
      postedTime :~:
      likedTime :~:
      repostedTime :~:
      ∅
  )
}
