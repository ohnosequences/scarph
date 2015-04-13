package ohnosequences.scarph.test

import ohnosequences.scarph._, graphTypes._, morphisms._, evals._, syntax.morphisms._
import twitter._, dummy._, dummyEvals._, DefaultEvals._

class DummyTests extends org.scalatest.FunSuite {

  test("record property bound works") {

    val query2 = duplicate(user)
    val query3 = inE(posted)
    val query4 = inE(posted) >=> source(posted)
    val query5 = fromZero(user) >=> toZero(user) >=> fromZero(tweet)
    val query6 = duplicate(user) >=> outE(posted) ⊗ outV(posted) //>=> matchUp(tweet)
    val query7 = id(user) ⊕ id(user)

    println("------------")
    println(query2.present)
    println("------------")
    println(query3.present)
    println("------------")
    println(query4.present)
    println("------------")
    println(query5.present)
    println("------------")
    println(query6.present)
    println("------------")
    println(query7.present)
  }

}
