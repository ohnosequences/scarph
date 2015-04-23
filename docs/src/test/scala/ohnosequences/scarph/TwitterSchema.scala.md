
```scala
package ohnosequences.scarph.test

import ohnosequences.cosas._, typeSets._

import ohnosequences.{ scarph => s }
import s.objects._, s.schemas._


object twitter extends AnyGraphSchema {

  lazy val label = this.toString

  lazy val vertices: Set[AnyVertex] = Set(user, tweet)

  lazy val edges: Set[AnyEdge] = Set(posted, follows, liked)

  lazy val valueTypes: Set[AnyValueType] = Set(name, age, text, time, url)

  lazy val properties: Set[AnyGraphProperty] = Set(
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
    case object name    extends Property(user -> twitter.name)("name")
    case object age     extends Property(user -> twitter.age)("age")
    // example of shared value types:
    case object bio     extends Property(user -> twitter.text)("bio")
    case object webpage extends Property(user -> twitter.url)("webpage")
  }

  case object tweet extends Vertex("tweet") {
    case object text extends Property(tweet -> twitter.text)("text")
    case object url  extends Property(tweet -> twitter.url)("url")
  }
```

Edges with their properties

```scala
  case object posted extends Edge(ExactlyOne(user) -> ManyOrNone(tweet))("posted") {
    case object time  extends Property(posted -> twitter.time)("time")
  }

  case object follows extends Edge(ManyOrNone(user) -> ManyOrNone(user))("follows")

  case object liked extends Edge(ManyOrNone(user) -> ManyOrNone(tweet))("liked") {
    case object time extends Property(liked -> twitter.time)("time")
  }

  case object reposted extends Edge(ManyOrNone(user) -> ManyOrNone(tweet))("reposted") {
    case object time extends Property(reposted -> twitter.time)("time")
  }

}

```


------

### Index

+ src
  + test
    + scala
      + ohnosequences
        + scarph
          + [TwitterQueries.scala][test/scala/ohnosequences/scarph/TwitterQueries.scala]
          + impl
            + [dummyTest.scala][test/scala/ohnosequences/scarph/impl/dummyTest.scala]
            + [dummy.scala][test/scala/ohnosequences/scarph/impl/dummy.scala]
          + [TwitterSchema.scala][test/scala/ohnosequences/scarph/TwitterSchema.scala]
  + main
    + scala
      + ohnosequences
        + scarph
          + [morphisms.scala][main/scala/ohnosequences/scarph/morphisms.scala]
          + [objects.scala][main/scala/ohnosequences/scarph/objects.scala]
          + [evals.scala][main/scala/ohnosequences/scarph/evals.scala]
          + [implementations.scala][main/scala/ohnosequences/scarph/implementations.scala]
          + [schemas.scala][main/scala/ohnosequences/scarph/schemas.scala]
          + [naturalIsomorphisms.scala][main/scala/ohnosequences/scarph/naturalIsomorphisms.scala]
          + syntax
            + [morphisms.scala][main/scala/ohnosequences/scarph/syntax/morphisms.scala]
            + [predicates.scala][main/scala/ohnosequences/scarph/syntax/predicates.scala]
            + [graphTypes.scala][main/scala/ohnosequences/scarph/syntax/graphTypes.scala]
            + [conditions.scala][main/scala/ohnosequences/scarph/syntax/conditions.scala]

[test/scala/ohnosequences/scarph/TwitterQueries.scala]: TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: impl/dummy.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: TwitterSchema.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: ../../../../main/scala/ohnosequences/scarph/morphisms.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: ../../../../main/scala/ohnosequences/scarph/objects.scala.md
[main/scala/ohnosequences/scarph/evals.scala]: ../../../../main/scala/ohnosequences/scarph/evals.scala.md
[main/scala/ohnosequences/scarph/implementations.scala]: ../../../../main/scala/ohnosequences/scarph/implementations.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: ../../../../main/scala/ohnosequences/scarph/schemas.scala.md
[main/scala/ohnosequences/scarph/naturalIsomorphisms.scala]: ../../../../main/scala/ohnosequences/scarph/naturalIsomorphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/predicates.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/predicates.scala.md
[main/scala/ohnosequences/scarph/syntax/graphTypes.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/graphTypes.scala.md
[main/scala/ohnosequences/scarph/syntax/conditions.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/conditions.scala.md