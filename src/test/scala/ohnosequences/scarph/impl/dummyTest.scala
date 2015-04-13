package ohnosequences.scarph.test

import ohnosequences.scarph._, graphTypes._, morphisms._, evals._, syntax.morphisms._
import twitter._, dummy._, dummyEvals._, DefaultEvals._

class DummyTests extends org.scalatest.FunSuite {

  test("record property bound works") {

    val query1 = id(user) >=> outV(posted).duplicate
    val query2 = inV(posted) >=> outV(posted).duplicate
    val query3 = inV(posted)
    val query4 = id(user) >=> id(user)
    val query5 = fromZero(user) >=> toZero(user) >=> fromZero(tweet)
    val query6 = duplicate(user) >=> outV(posted) ⊗ outV(posted) >=> matchUp(tweet)
    //val query7 = id(posted) ⊕ id(posted)*/
    //println(query2.present)*/
    //println(query3.present)*/
    println(query4.present(eval_composition))
    query4.evalOn[Dummy, Dummy](query4.in := Dummy)(eval_composition)
    //println(query5.present)*/
    //println(query6.present)
  }

}
