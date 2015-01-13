
```scala
package ohnosequences.scarph.test

object Twitter {

  import ohnosequences.cosas._, typeSets._

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.steps._, s.combinators._, s.containers._, s.indexes._, s.schemas._

  case object user extends Vertex
  case object name extends PropertyOf(user) { type Raw = String }
  case object age  extends PropertyOf(user) { type Raw = Integer }

  case object tweet extends Vertex
  case object text  extends PropertyOf(tweet) { type Raw = String }

  case object posted extends Edge(user -> ManyOrNone.of(tweet))
  case object time extends PropertyOf(posted) { type Raw = String }
  case object url  extends PropertyOf(posted) { type Raw = String }

  case object follows extends Edge(ManyOrNone.of(user) -> ManyOrNone.of(user))
  case object liked   extends Edge(ManyOrNone.of(user) -> ManyOrNone.of(tweet))

  // simple indexes
  case object userByName extends KeyIndex(user, name, Unique)
  case object tweetByText extends KeyIndex(tweet, text, NonUnique)
  case object postedByTime extends KeyIndex(posted, time, NonUnique)

  // composite indexes
  case object userByNameAndAge extends CompositeIndex(user, name :~: age :~: ∅, Unique)

  // vertex-centric indexes
  case object postedByTimeAndUrlLocal extends LocalEdgeIndex(posted, OnlySourceCentric, time :~: url :~: ∅)

  case object twitter extends Schema(
    label = "twitter",
    properties = name :~: age :~: text :~: time :~: url :~: ∅,
    vertices =  user :~: tweet :~: ∅,
    edges = posted :~: follows :~: liked :~: ∅,
    indexes = 
      userByName :~: userByNameAndAge :~:
      tweetByText :~: 
      postedByTime :~: 
      postedByTimeAndUrlLocal :~: 
      ∅
  )

}

```


------

### Index

+ src
  + test
    + scala
      + ohnosequences
        + scarph
          + [ContainersTest.scala][test/scala/ohnosequences/scarph/ContainersTest.scala]
          + [ScalazEquality.scala][test/scala/ohnosequences/scarph/ScalazEquality.scala]
          + titan
            + [TwitterTitanTest.scala][test/scala/ohnosequences/scarph/titan/TwitterTitanTest.scala]
          + [TwitterSchema.scala][test/scala/ohnosequences/scarph/TwitterSchema.scala]
    + resources
  + main
    + scala
      + ohnosequences
        + scarph
          + [GraphTypes.scala][main/scala/ohnosequences/scarph/GraphTypes.scala]
          + [Containers.scala][main/scala/ohnosequences/scarph/Containers.scala]
          + impl
            + titan
              + [Schema.scala][main/scala/ohnosequences/scarph/impl/titan/Schema.scala]
              + [Evals.scala][main/scala/ohnosequences/scarph/impl/titan/Evals.scala]
              + [Predicates.scala][main/scala/ohnosequences/scarph/impl/titan/Predicates.scala]
          + [Paths.scala][main/scala/ohnosequences/scarph/Paths.scala]
          + [Indexes.scala][main/scala/ohnosequences/scarph/Indexes.scala]
          + [Evals.scala][main/scala/ohnosequences/scarph/Evals.scala]
          + [Conditions.scala][main/scala/ohnosequences/scarph/Conditions.scala]
          + [Steps.scala][main/scala/ohnosequences/scarph/Steps.scala]
          + [Predicates.scala][main/scala/ohnosequences/scarph/Predicates.scala]
          + [Schemas.scala][main/scala/ohnosequences/scarph/Schemas.scala]
          + [Combinators.scala][main/scala/ohnosequences/scarph/Combinators.scala]
          + syntax
            + [GraphTypes.scala][main/scala/ohnosequences/scarph/syntax/GraphTypes.scala]
            + [Paths.scala][main/scala/ohnosequences/scarph/syntax/Paths.scala]
            + [Conditions.scala][main/scala/ohnosequences/scarph/syntax/Conditions.scala]
            + [Predicates.scala][main/scala/ohnosequences/scarph/syntax/Predicates.scala]

[test/scala/ohnosequences/scarph/ContainersTest.scala]: ContainersTest.scala.md
[test/scala/ohnosequences/scarph/ScalazEquality.scala]: ScalazEquality.scala.md
[test/scala/ohnosequences/scarph/titan/TwitterTitanTest.scala]: titan/TwitterTitanTest.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: TwitterSchema.scala.md
[main/scala/ohnosequences/scarph/GraphTypes.scala]: ../../../../main/scala/ohnosequences/scarph/GraphTypes.scala.md
[main/scala/ohnosequences/scarph/Containers.scala]: ../../../../main/scala/ohnosequences/scarph/Containers.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Schema.scala]: ../../../../main/scala/ohnosequences/scarph/impl/titan/Schema.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Evals.scala]: ../../../../main/scala/ohnosequences/scarph/impl/titan/Evals.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Predicates.scala]: ../../../../main/scala/ohnosequences/scarph/impl/titan/Predicates.scala.md
[main/scala/ohnosequences/scarph/Paths.scala]: ../../../../main/scala/ohnosequences/scarph/Paths.scala.md
[main/scala/ohnosequences/scarph/Indexes.scala]: ../../../../main/scala/ohnosequences/scarph/Indexes.scala.md
[main/scala/ohnosequences/scarph/Evals.scala]: ../../../../main/scala/ohnosequences/scarph/Evals.scala.md
[main/scala/ohnosequences/scarph/Conditions.scala]: ../../../../main/scala/ohnosequences/scarph/Conditions.scala.md
[main/scala/ohnosequences/scarph/Steps.scala]: ../../../../main/scala/ohnosequences/scarph/Steps.scala.md
[main/scala/ohnosequences/scarph/Predicates.scala]: ../../../../main/scala/ohnosequences/scarph/Predicates.scala.md
[main/scala/ohnosequences/scarph/Schemas.scala]: ../../../../main/scala/ohnosequences/scarph/Schemas.scala.md
[main/scala/ohnosequences/scarph/Combinators.scala]: ../../../../main/scala/ohnosequences/scarph/Combinators.scala.md
[main/scala/ohnosequences/scarph/syntax/GraphTypes.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/GraphTypes.scala.md
[main/scala/ohnosequences/scarph/syntax/Paths.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/Paths.scala.md
[main/scala/ohnosequences/scarph/syntax/Conditions.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/Conditions.scala.md
[main/scala/ohnosequences/scarph/syntax/Predicates.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/Predicates.scala.md