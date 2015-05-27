package ohnosequences.scarph.test

import ohnosequences.scarph._, objects._, morphisms._, evals._
import syntax.morphisms._, syntax.objects._
import twitter._, dummy._

class DummyTests extends org.scalatest.FunSuite {

  test("dummy evals for the basic structure") {
    import dummy.categoryStructure._

    val q_id = id(user)
    val q_comp = q_id >=> q_id

    info(evalOn[Dummy](q_id).evalPlan)
    info(evalOn[Dummy](q_comp).evalPlan)
  }

  test("dummy evals for the tensor structure") {
    import dummy.categoryStructure._
    import dummy.tensorStructure._

    val q_tensor = id(user) ⊗ id(user) ⊗ id(user)
    val q_dupl = duplicate(user) ⊗ id(user)
    val q_match = matchUp(user)
    val q_comp = q_dupl >=> q_tensor >=> (id(user ⊗ user) ⊗ duplicate(user)) >=> (q_match ⊗ q_match) >=> q_match

    info(evalOn[
      DummyTensor[
        DummyTensor[Dummy, Dummy],
        Dummy
      ]
    ](q_tensor).evalPlan)
    info(evalOn[DummyTensor[Dummy, Dummy]](q_dupl).evalPlan)
    info(evalOn[DummyTensor[Dummy, Dummy]](q_match).evalPlan)
    info(evalOn[DummyTensor[Dummy, Dummy]](q_comp).evalPlan)
  }

  test("dummy evals for the graph structure") {
    import dummy.categoryStructure._
    import dummy.graphStructure._

    val q_outV = outV(posted)
    val q_inV = inV(liked)
    val q_compV = q_outV >=> q_inV

    info(evalOn[DummyVertex](q_outV).evalPlan)
    info(evalOn[DummyVertex](q_inV).evalPlan)
    info(evalOn[DummyVertex](q_compV).evalPlan)

    val q_outE = outE(posted) >=> target(posted)
    val q_inE = inE(liked) >=> source(liked)
    val q_compE = q_outE >=> q_inE

    info(evalOn[DummyVertex](q_outE).evalPlan)
    info(evalOn[DummyVertex](q_inE).evalPlan)
    info(evalOn[DummyVertex](q_compE).evalPlan)
  }

  test("dummy evals for the biproduct structure") {
    import dummy.categoryStructure._
    import dummy.biproductStructure._

    val q_inj = rightInj((user ⊕ user) ⊕ tweet)
    val q_biproduct = id(user) ⊕ id(user) ⊕ id(tweet)
    val q_fork = fork(user) ⊕ id(tweet)
    val q_merge = merge(user)
    val q_comp =
      q_fork >=>
      q_biproduct >=>
      (id(user ⊕ user) ⊕ fork(tweet)) >=>
      (merge(user) ⊕ merge(tweet)) >=>
      rightProj(user ⊕ tweet)

    info(evalOn[
      DummyBiproduct[
        DummyBiproduct[Dummy, Dummy],
        Dummy
      ]
    ](q_biproduct).evalPlan)
    info(evalOn[DummyBiproduct[Dummy, Dummy]](q_fork).evalPlan)
    info(evalOn[DummyBiproduct[Dummy, Dummy]](q_merge).evalPlan)
    info(evalOn[DummyBiproduct[Dummy, Dummy]](q_comp).evalPlan)
  }

  test("dummy evals for the property structure") {
    import dummy.categoryStructure._

    val vertexInteger = dummy.vertexPropertyStructure[Integer](0); import vertexInteger._
    //val vertexString = dummy.vertexPropertyStructure[String](""); import vertexString._*/

    //val edgeInteger = dummy.edgePropertyStructure[Integer](0); import edgeInteger._
    val edgeString = dummy.edgePropertyStructure[String](""); import edgeString._

    val q_get = get(user.age)
    val q_lookup = lookup(user.age)
    val q_comp = q_lookup >=> q_get

    info(evalOn[DummyEdge](q_get).evalPlan)
    //info(evalOn[Integer](q_lookup).evalPlan)*/
    //info(evalOn[DummyVertex](q_comp).evalPlan)*/
  }

/*
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
    //println(evaluate(query5).evalPlan)
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
    // println(evaluate(query15).evalPlan)

    val uh2 = evaluate(query2) on (user := Dummy)

    val uh1 = evaluate(query1) on (name := Dummy)

    assert(
      query6.dagger.dagger === query6
    )
  }

*/

  import rewrites._

  object compositionToRight extends AnyRewriteStrategy {

    implicit final def right_bias_assoc[
      F <: AnyGraphMorphism,
      G <: AnyGraphMorphism { type In = F#Out },
      H <: AnyGraphMorphism { type In = G#Out }
    ]
    : ( (F >=> G) >=> H ) rewriteTo ( F >=> (G >=> H) )
    = rewriteTo( fg_h => {

        val fg  = fg_h.first
        val h   = fg_h.second

        val f = fg.first
        val g = fg.second

        f >=> (g >=> h)
      })
  }

  test("rewriting composition") {

    val morph = outV(follows) >=> inV(follows) >=> outV(follows)

    val rmorph = apply(compositionToRight) to morph

    info(morph.label)
    info(rmorph.label)
  }
}
