package ohnosequences.scarph.test

import ohnosequences.scarph._, graphTypes._, morphisms._, evals._, syntax.morphisms._
import twitter._, dummy._, dummyEvals._

class DummyTests extends org.scalatest.FunSuite {

  test("record property bound works") {

    //val query = id(user) >=> outV(posted).duplicate*/
    //val query = inV(posted) >=> outV(posted).duplicate*/
    //val query = inV(posted)*/
    val query = id(user) >=> id(user)
    //val query = fromZero(user) >=> toZero(user) >=> fromZero(tweet)*/
    //val query = duplicate(user) >=> outV(posted) ⊗ outV(posted) >=> matchUp(tweet)*/
    //val query = id(posted) ⊕ id(posted)*/
    println(query.present(eval_composition(eval_primitive, eval_primitive)))
  }

}
