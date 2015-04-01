package ohnosequences.scarph.test

object Twitter {

  import ohnosequences.cosas._, typeSets._

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.schemas._

  /* Property value types */
  case object name extends ValueOfType[String]("name")
  case object age  extends ValueOfType[Integer]("age")
  case object text extends ValueOfType[String]("text")
  case object time extends ValueOfType[String]("time") // should have some better raw type
  case object url  extends ValueOfType[String]("url")

  /* Vertices with their properties */
  case object user extends Vertex(toString)

  case object userName    extends Property(user -> name)(toString)
  case object userAge     extends Property(user -> age)(toString)
  // example of shared value types:
  case object userBio     extends Property(user -> text)(toString)
  case object userWebpage extends Property(user -> url)(toString)

  case object tweet     extends Vertex(toString)
  case object tweetText extends Property(tweet -> text)(toString)
  case object tweetUrl  extends Property(tweet -> url)(toString)

  /* Edges with their properties */
  case object posted      extends Edge(user -> tweet)(toString)
  case object postedTime  extends Property(posted -> time)(toString)

  case object follows extends Edge(user -> user)(toString)

  case object liked extends Edge(user -> tweet)(toString)
  case object likedTime extends Property(liked -> time)(toString)

  case object reposted extends Edge(user -> tweet)(toString)
  case object repostedTime extends Property(reposted -> time)(toString)

  val schema = GraphSchema(
    label = "twitter",
    vertices =  Set(user, tweet),
    edges = Set(posted, follows, liked),
    valueTypes = Set(name, age, text, time, url),
    properties = Set(
      userName,
      userAge,
      userBio,
      userWebpage,
      tweetText,
      tweetUrl,
      postedTime,
      likedTime,
      repostedTime
    )
  )
}
