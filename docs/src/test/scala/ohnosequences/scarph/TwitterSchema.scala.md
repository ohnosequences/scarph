
```scala
package ohnosequences.scarph.test

import ohnosequences.cosas._

import ohnosequences.{ scarph => s }
import s.objects._, s.schemas._


case object twitter extends AnyGraphSchema {

  lazy val label = this.toString

  lazy val vertices: Set[AnyVertex] = Set(user, tweet)

  lazy val edges: Set[AnyEdge] = Set(posted, follows, liked)

  lazy val valueTypes: Set[AnyValueType] = Set(name, age, text, time, url)

  lazy val properties: Set[AnyProperty] = Set(
    user.name,
    user.age,
    user.bio,
    user.webpage,
    tweet.text,
    tweet.url,
    posted.time,
    liked.time,
    reposted.time
  )
```

Property value types

```scala
  case object name extends ValueOfType[String]("name")
  case object age  extends ValueOfType[Integer]("age")
  case object text extends ValueOfType[String]("text")
  case object time extends ValueOfType[String]("time") // should have some better raw type
  case object url  extends ValueOfType[String]("url")
```

Vertices with their properties

```scala
  case object user extends Vertex("user") {
    case object name    extends Property(ManyOrNone(user) -> ExactlyOne(twitter.name))("name")
    case object age     extends Property(ManyOrNone(user) -> ExactlyOne(ohnosequences.scarph.test.twitter.age))("age")
    // example of shared value types:
    case object bio     extends Property(ManyOrNone(user) -> OneOrNone(twitter.text))("bio")
    case object webpage extends Property(ManyOrNone(user) -> OneOrNone(twitter.url))("webpage")
  }

  case object tweet extends Vertex("tweet") {
    case object text extends Property(ManyOrNone(tweet) -> ExactlyOne(twitter.text))("text")
    case object url  extends Property(ManyOrNone(tweet) -> ExactlyOne(twitter.url))("url")
  }
```

Edges with their properties

```scala
  case object posted extends Edge(ExactlyOne(user) -> ManyOrNone(tweet))("posted") {
    case object time  extends Property(ManyOrNone(posted) -> ExactlyOne(ohnosequences.scarph.test.twitter.time))("time")
  }

  case object follows extends Edge(ManyOrNone(user) -> ManyOrNone(user))("follows")

  case object liked extends Edge(ManyOrNone(user) -> ManyOrNone(tweet))("liked") {
    case object time extends Property(ManyOrNone(liked) -> ExactlyOne(twitter.time))("time")
  }

  case object reposted extends Edge(ManyOrNone(user) -> ManyOrNone(tweet))("reposted") {
    case object time extends Property(ManyOrNone(reposted) -> ExactlyOne(twitter.time))("time")
  }

}

```




[main/scala/ohnosequences/scarph/axioms.scala]: ../../../../main/scala/ohnosequences/scarph/axioms.scala.md
[main/scala/ohnosequences/scarph/evals.scala]: ../../../../main/scala/ohnosequences/scarph/evals.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: ../../../../main/scala/ohnosequences/scarph/morphisms.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: ../../../../main/scala/ohnosequences/scarph/objects.scala.md
[main/scala/ohnosequences/scarph/rewrites.scala]: ../../../../main/scala/ohnosequences/scarph/rewrites.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: ../../../../main/scala/ohnosequences/scarph/schemas.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/objects.scala.md
[test/scala/ohnosequences/scarph/asserts.scala]: asserts.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: impl/dummy.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/implicitSearch.scala]: implicitSearch.scala.md
[test/scala/ohnosequences/scarph/TwitterQueries.scala]: TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: TwitterSchema.scala.md