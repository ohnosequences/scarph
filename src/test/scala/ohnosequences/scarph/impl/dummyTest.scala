package ohnosequences.scarph.test

import ohnosequences.scarph._, graphTypes._, morphisms._, evals._
import syntax.morphisms._, syntax.conditions._, syntax.predicates._
import twitter._, dummy._

class DummyTests extends org.scalatest.FunSuite {

  test("record property bound works") {

    val query2 = duplicate(user)
    val query3 = inE(posted)
    val query4 = inE(posted) >=> source(posted)
    val query5 = fromZero(user) >=> toZero(user) >=> fromZero(tweet)
    val query6 = duplicate(user) >=> outV(posted) ⊗ outV(posted)
    val query7 = inV(follows) ⊕ outV(follows)
    val query8 = get(user.name)
    val query9 = lookup(user.name) >=> query8
    val query10 = fromUnit(user) >=> toUnit(user)
    val query11 = query6 >=> matchUp(tweet)
    val query12 = query7 >=> merge(user)
    val query13 = quantify(tweet ? (tweet.text === "foo") and (tweet.url === "www"))
    val query14 = coerce(query13.predicate)

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
    println("------------")
    println(query8.present)
    println("------------")
    println(query9.present)
    println("------------")
    println(query10.present)
    println("------------")
    println(query11.present)
    println("------------")
    println(query12.present)
    println("------------")
    println(query13.present)
    println("------------")
    println(query14.present)

  }

}
