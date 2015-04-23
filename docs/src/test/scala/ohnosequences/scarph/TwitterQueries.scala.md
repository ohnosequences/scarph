
```scala
package ohnosequences.scarph.test

object Queries {

  import ohnosequences.{ scarph => s }
  import s.objects._, s.morphisms._
  import s.syntax._, morphisms._, conditions._, predicates._
  import s.test.twitter._

   val edus    = user ? (user.name === "@eparejatobes")
  // val alexeys = twitter.query(user ? (name === "@laughedelic"))
  // val kims    = twitter.query(user ? (name === "@evdokim"))
  // val tweets  = twitter.query(tweet ? (text === "back to twitter :)"))
  // val posts   = twitter.query(posted ? (time === "13.11.2012"))

  val userName   = id(user).get(user.name)
  val tweetText  = id(tweet).get(tweet.text)
  val postedTime = id(posted).get(posted.time)
  implicitly[ userName.type <:< (user.type --> name.type) ]

  val tweetPosterName = inE(posted).source.get(user.name)

  val fffolowees = outV(follows).outV(follows).outV(follows)

  val sourceAndTarget = duplicate(posted).andThen( source(posted) ⊗ target(posted) )

  val friends = inV(follows) ⊗ outV(follows)

  val friends1 = duplicate(user) >=> ( friends )
  val friends2 = duplicate(user) >=> ( friends >=> friends )
  val friends3 = duplicate(user) >=> ( friends >=> friends >=> friends )

  implicitly[ friends1.type <:< (user.type --> TensorObj[user.type, user.type]) ]
  implicitly[ friends2.type <:< (user.type --> TensorObj[user.type, user.type]) ]

  val twist1 = friends.twist
  val twist2 = friends.duplicate.twist
  val twist3 = duplicate(user).twist
  val twist4 = duplicate(user).twist.twist

  val match1 = friends.matchUp
  val match2 = friends.twist.matchUp
  val match3 = friends.duplicate.matchUp
  val match4 = duplicate(tweet).matchUp
  //val match5 = (id(user) ⊗ id(tweet)).matchUp

  val bip = inV(follows) ⊕ outV(follows)
  val inFriends  = bip.leftProj
  val outFriends = bip.rightProj
  val allFriends = bip.merge

  val injectL = outV(liked).leftInj(tweet ⊕ user)
  val injectR = inV(posted).rightInj(tweet ⊕ user)

  // funny check / coerce
  val edusAgain = quantify(user ? (user.name === "@eparejatobes"))

  val edusTweets = edusAgain andThen edusAgain.dagger.outV(posted)

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