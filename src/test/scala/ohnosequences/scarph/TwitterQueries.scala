package ohnosequences.scarph.test

case object queries {

  import ohnosequences.{ scarph => s }
  import s.objects._, s.morphisms._
  import s.syntax._, morphisms._, objects._
  import s.test.twitter._

  class TestBlock(val label: String)


  object categoryStructure extends TestBlock("Categorical structure"){

    val q_id = id(user)
    val q_comp1 = q_id >=> q_id
    val q_comp2 = q_comp1 >=> q_id >=> q_comp1
  }

  object tensorStructure extends TestBlock("Tensor structure") {

    val q_tensor = id(user) ⊗ id(user) ⊗ id(user)
    val q_dupl = duplicate(user) ⊗ id(user)
    val q_match = matchUp(user)
    val q_comp =
      q_dupl >=>
      q_tensor >=>
      (id(user ⊗ user) ⊗ duplicate(user)) >=>
      (q_match ⊗ q_match) >=>
      q_match
  }

  object biproductStructure extends TestBlock("Biproduct structure") {

    val q_inj   = rightInj((user ⊕ user) ⊕ tweet)
    val q_bip   = id(user) ⊕ id(user) ⊕ id(tweet)
    val q_fork  = fork(user) ⊕ id(tweet)
    val q_merge = merge(user)
    val q_comp  =
      q_fork >=>
      q_bip >=>
      (id(user ⊕ user) ⊕ fork(tweet)) >=>
      (merge(user) ⊕ merge(tweet)) >=>
      rightProj(user ⊕ tweet)
  }

  object graphStructure extends TestBlock("Graph structure") {

    val q_outV  = outV(posted)
    val q_inV   = inV(posted)
    val q_compV = q_outV >=> q_inV

    val q_outE  = outE(posted) >=> target(posted)
    val q_inE   = inE(posted) >=> source(posted)
    val q_compE = q_outE >=> q_inE
  }

  object propertyStructure extends TestBlock("Property structure") {

    val q_get = get(user.age)
    val q_lookup = lookup(user.name)
    val q_comp1 = q_lookup >=> q_get
    val q_comp2 = get(tweet.text) >=> lookup(tweet.text)
  }

  /*
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

  val bip = inV(follows) ⊕ outV(follows)
  val inFriends  = bip.leftProj
  val outFriends = bip.rightProj
  val allFriends = bip.merge

  val injectL = outV(liked).leftInj(tweet ⊕ user)
  val injectR = inV(posted).rightInj(tweet ⊕ user)

  val edusAgain = quantify(user ? (user.name === "@eparejatobes"))

  val edusTweets = edusAgain andThen edusAgain.dagger.outV(posted)
  */
}
