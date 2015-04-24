package ohnosequences.scarph.test

import ohnosequences.scarph._, objects._, morphisms._, evals._
import syntax.morphisms._, syntax.objects._
import twitter._

class DummyTests extends org.scalatest.FunSuite {

  test("dummy evaluators on sample queries") {
    import dummy._

    val query1  = lookup(user.name)
    val query2  = duplicate(user)
    val query3  = inE(posted)
    val query4  = inE(posted) >=> source(posted)
    val query5  = fromZero(user) >=> toZero(user) >=> fromZero(tweet)
    val query6  = duplicate(user) >=> outV(posted) ⊗ outV(posted)
    val query7  = inV(follows) ⊕ outV(follows)
    val query8  = get(user.name)
    val query9  = lookup(user.name) >=> query8
    val query10 = fromUnit(user) >=> toUnit(user)
    val query11 = query6 >=> matchUp(tweet)
    val query12 = query7 >=> merge(user)
    val query13 = quantify(tweet ? (tweet.text === "foo") and (tweet.url === "www"))
    val query14 = coerce(query13.predicate)
    val query15 = (query6 ⊗ query7) distribute

    println("------------")
    println(evaluate(query2).evalPlan)
    println("------------")
    println(evaluate(query3).evalPlan)
    println("------------")
    println(evaluate(query4).evalPlan)
    println("------------")
    println(evaluate(query5).evalPlan)
    println("------------")
    println(evaluate(query6).evalPlan)
    println("------------")
    println(evaluate(query7).evalPlan)
    println("------------")
    println(evaluate(query8).evalPlan)
    println("------------")
    println(evaluate(query9).evalPlan)
    println("------------")
    println(evaluate(query10).evalPlan)
    println("------------")
    println(evaluate(query11).evalPlan)
    println("------------")
    println(evaluate(query12).evalPlan)
    println("------------")
    println(evaluate(query13).evalPlan)
    println("------------")
    println(evaluate(query14).evalPlan)
    println("------------")
    // no distribute eval
    // println(evaluate(query15).evalPlan)*/

    val uh2 = evaluate(query2) on (user := Dummy)

    val uh1 = evaluate(query1) on (name := Dummy)

    assert(
      query6.dagger.dagger === query6
    )
  }

  import rewrites._

  object compositionToRight extends AnyRewriteStrategy {

    implicit def right_bias_assoc[
      F <: AnyGraphMorphism,
      G <: AnyGraphMorphism { type In = F#Out },
      H <: AnyGraphMorphism { type In = G#Out }
    ]: ( (F >=> G) >=> H ) rewriteTo ( F >=> (G >=> H) ) =
    rewriteTo( 
      fg_h => {

        val fg  = fg_h.first
        val h   = fg_h.second

        val f = fg.first
        val g = fg.second
        
        f >=> (g >=> h)
      }
    )
  }

  test("rewriting composition") {
    
    val morph = outV(follows) >=> inV(follows) >=> outV(follows)

    val rmorph = apply(compositionToRight) to morph


    println(morph.label)
    println(rmorph.label)
  }
}
