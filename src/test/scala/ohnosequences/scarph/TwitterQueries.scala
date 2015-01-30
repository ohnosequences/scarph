package ohnosequences.scarph.test

object Queries {

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.steps._, s.indexes._
  import s.syntax._, conditions._, predicates._, paths._, graphTypes._
  import s.test.Twitter._

  val edus    = twitter.query(user ? (name === "@eparejatobes"))
  val alexeys = twitter.query(user ? (name === "@laughedelic"))
  val kims    = twitter.query(user ? (name === "@evdokim"))
  val tweets  = twitter.query(tweet ? (text === "back to twitter :)"))
  val posts   = twitter.query(posted ? (time === "13.11.2012"))

  val userName   = user.get(name)
  val tweetText  = tweet.get(text)
  val postedTime = posted.get(time)

  val tweetPosterName = tweet.inE(posted).src.get(name)

  val fffolowees = user.outV(follows).outV(follows).outV(follows)

  val sourceAndTarget = Fork(posted) >=> ( Source(posted) ⊕ Target(posted) )

}
