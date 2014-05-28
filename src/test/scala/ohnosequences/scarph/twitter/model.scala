package ohnosequences.scarph.twitter.test

import ohnosequences.scarph._

object twitterModel {

  // vertices
  case object User extends VertexType("User")
  case object Tweet extends VertexType("Tweet")

  // edges
  case object Follows extends ManyToMany(User, "Follows", User)
  case object Tweets extends ManyToOne(User, "Tweets", Tweet)

  // properties
  case object id extends Property[String]
  case object name extends Property[String]
  case object date extends Property[String]

  implicit val User_id      = User has id
  implicit val Tweet_id     = Tweet has id
  implicit val Tweet_date   = Tweet has date
  implicit val Follows_date = Follows has date

  trait Impl {
    
    type user <: Vertex[User.type]
    val user: user
    type tweet <: Vertex[Tweet.type]
    val tweet: tweet

    type follows <: Edge[Follows.type]
    val follows: follows
    type tweets <: Edge[Tweets.type]
    val tweets: tweets
  }
}

