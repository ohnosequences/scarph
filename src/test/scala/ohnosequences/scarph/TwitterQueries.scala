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
