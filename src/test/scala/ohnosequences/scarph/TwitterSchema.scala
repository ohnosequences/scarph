package ohnosequences.scarph.test

import ohnosequences.cosas._
import ohnosequences.scarph._


case object twitter extends GraphSchema {

  lazy val label = this.toString

  /* Property value types */
  case object name extends valueOfType[String]
  case object age  extends valueOfType[Int]
  case object text extends valueOfType[String]
  case object time extends valueOfType[String] // should have some better raw type
  case object url  extends valueOfType[String]


  /* Vertices with their properties */
  case object user extends vertex {
    case object name    extends property(manyOrNone(user) -> exactlyOne(twitter.name))
    case object age     extends property(manyOrNone(user) -> exactlyOne(twitter.age))
    // example of shared value types:
    case object bio     extends property(manyOrNone(user) -> oneOrNone(twitter.text))
    case object webpage extends property(manyOrNone(user) -> oneOrNone(twitter.url))
  }

  case object tweet extends vertex {
    case object text extends property(manyOrNone(tweet) -> exactlyOne(twitter.text))
    case object url  extends property(manyOrNone(tweet) -> exactlyOne(twitter.url))
  }


  /* Edges with their properties */
  case object posted extends edge(exactlyOne(user) -> manyOrNone(tweet)) {
    case object time extends property(manyOrNone(posted) -> exactlyOne(twitter.time))
  }

  case object follows extends edge(manyOrNone(user) -> manyOrNone(user))

  case object liked extends edge(manyOrNone(user) -> manyOrNone(tweet)) {
    case object time extends property(manyOrNone(liked) -> exactlyOne(twitter.time))
  }

  case object reposted extends edge(manyOrNone(user) -> manyOrNone(tweet)) {
    case object time extends property(manyOrNone(reposted) -> exactlyOne(twitter.time))
  }

}
