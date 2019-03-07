
```scala
package ohnosequences.scarph.test

import ohnosequences.cosas._
import ohnosequences.scarph._


case object twitter extends GraphSchema {

  lazy val label = this.toString

  lazy val vertices: Set[AnyVertex] = Set(user,tweet)
  lazy val edges: Set[AnyEdge] = Set(follows, liked, posted, reposted)
  lazy val properties: Set[AnyProperty] = user.properties ++ tweet.properties ++ liked.properties ++ posted.properties ++ reposted.properties
  lazy val valueTypes: Set[AnyValueType] = Set(name,age,text,time,url)
```

Property value types

```scala
  case object name extends valueOfType[String]
  case object age  extends valueOfType[Int]
  case object text extends valueOfType[String]
  case object time extends valueOfType[String] // should have some better raw type
  case object url  extends valueOfType[String]
```

Vertices with their properties

```scala
  case object user extends vertex {
    case object name    extends property(manyOrNone(user) -> exactlyOne(twitter.name))
    case object age     extends property(manyOrNone(user) -> exactlyOne(twitter.age))
    // example of shared value types:
    case object bio     extends property(manyOrNone(user) -> oneOrNone(twitter.text))
    case object webpage extends property(manyOrNone(user) -> oneOrNone(twitter.url))

    lazy val properties: Set[AnyProperty] = Set(name,age,bio,webpage)
  }

  case object tweet extends vertex {
    case object text extends property(manyOrNone(tweet) -> exactlyOne(twitter.text))
    case object url  extends property(manyOrNone(tweet) -> exactlyOne(twitter.url))

    lazy val properties: Set[AnyProperty] = Set(text,url)
  }

  case object follows extends edge(manyOrNone(user) -> manyOrNone(user))

  case object liked extends edge(manyOrNone(user) -> manyOrNone(tweet)) {
    case object time extends property(manyOrNone(liked) -> exactlyOne(twitter.time))

    lazy val properties: Set[AnyProperty] = Set(time)
  }
```

Edges with their properties

```scala
  case object posted extends edge(exactlyOne(user) -> manyOrNone(tweet)) {
    case object time extends property(manyOrNone(posted) -> exactlyOne(twitter.time))

    lazy val properties: Set[AnyProperty] = Set(time)
  }

  case object reposted extends edge(manyOrNone(user) -> manyOrNone(tweet)) {
    case object time extends property(manyOrNone(reposted) -> exactlyOne(twitter.time))

    lazy val properties: Set[AnyProperty] = Set(time)
  }
}

```




[main/scala/ohnosequences/scarph/axioms.scala]: ../../../../main/scala/ohnosequences/scarph/axioms.scala.md
[main/scala/ohnosequences/scarph/tensor.scala]: ../../../../main/scala/ohnosequences/scarph/tensor.scala.md
[main/scala/ohnosequences/scarph/predicates.scala]: ../../../../main/scala/ohnosequences/scarph/predicates.scala.md
[main/scala/ohnosequences/scarph/impl/biproducts.scala]: ../../../../main/scala/ohnosequences/scarph/impl/biproducts.scala.md
[main/scala/ohnosequences/scarph/impl/tensors.scala]: ../../../../main/scala/ohnosequences/scarph/impl/tensors.scala.md
[main/scala/ohnosequences/scarph/impl/evals.scala]: ../../../../main/scala/ohnosequences/scarph/impl/evals.scala.md
[main/scala/ohnosequences/scarph/impl/distributivity.scala]: ../../../../main/scala/ohnosequences/scarph/impl/distributivity.scala.md
[main/scala/ohnosequences/scarph/impl/relations.scala]: ../../../../main/scala/ohnosequences/scarph/impl/relations.scala.md
[main/scala/ohnosequences/scarph/impl/category.scala]: ../../../../main/scala/ohnosequences/scarph/impl/category.scala.md
[main/scala/ohnosequences/scarph/rewrites.scala]: ../../../../main/scala/ohnosequences/scarph/rewrites.scala.md
[main/scala/ohnosequences/scarph/package.scala]: ../../../../main/scala/ohnosequences/scarph/package.scala.md
[main/scala/ohnosequences/scarph/arities.scala]: ../../../../main/scala/ohnosequences/scarph/arities.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: ../../../../main/scala/ohnosequences/scarph/objects.scala.md
[main/scala/ohnosequences/scarph/writes.scala]: ../../../../main/scala/ohnosequences/scarph/writes.scala.md
[main/scala/ohnosequences/scarph/biproduct.scala]: ../../../../main/scala/ohnosequences/scarph/biproduct.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: ../../../../main/scala/ohnosequences/scarph/schemas.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: ../../../../main/scala/ohnosequences/scarph/morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/package.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/package.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/objects.scala.md
[main/scala/ohnosequences/scarph/syntax/writes.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/writes.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/isomorphisms.scala]: ../../../../main/scala/ohnosequences/scarph/isomorphisms.scala.md
[test/scala/ohnosequences/scarph/TwitterQueries.scala]: TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: impl/dummy.scala.md
[test/scala/ohnosequences/scarph/impl/writes.scala]: impl/writes.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: TwitterSchema.scala.md
[test/scala/ohnosequences/scarph/asserts.scala]: asserts.scala.md
[test/scala/ohnosequences/scarph/SchemaCreation.scala]: SchemaCreation.scala.md
[test/scala/ohnosequences/scarph/implicitSearch.scala]: implicitSearch.scala.md