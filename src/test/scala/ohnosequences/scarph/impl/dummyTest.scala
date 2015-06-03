package ohnosequences.scarph.test

import ohnosequences.scarph._, objects._, morphisms._, evals._
import syntax.morphisms._
import twitter._, dummy._, dummy.syntax._

class DummyTests extends org.scalatest.FunSuite {

  val du = user := DummyVertex
  val dt = tweet := DummyVertex
  val dp = posted := DummyEdge

  val dages = age := Seq[Integer]()
  val dnames = name := Seq[String]()
  val dtexts = text := Seq[String]()
  val dtimes = time := Seq[String]()


  test("dummy evals for the basic structure") {
    import dummy.categoryStructure._
    import queries.categoryStructure._

    assert{ eval(q_id)(du) == du }
    assert{ eval(q_comp1)(du) == du }
    assert{ eval(q_comp2)(du) == du }
  }

  test("dummy evals for the tensor structure") {
    import dummy.categoryStructure._
    import dummy.tensorStructure._
    import queries.tensorStructure._

    assert{ eval(q_tensor)(du ⊗ du ⊗ du) == du ⊗ du ⊗ du }
    assert{ eval(q_dupl)(du ⊗ du) == du ⊗ du ⊗ du }
    assert{ eval(q_match)(du ⊗ du) == du }
    assert{ eval(q_comp)(du ⊗ du) == du }
  }

  test("dummy evals for the biproduct structure") {
    import dummy.categoryStructure._
    import dummy.biproductStructure._
    import queries.biproductStructure._

    assert{ eval(q_inj)(dt) == du ⊕ du ⊕ dt }
    assert{ eval(q_bip)(du ⊕ du ⊕ dt) == du ⊕ du ⊕ dt }
    assert{ eval(q_fork)(du ⊕ dt) == du ⊕ du ⊕ dt }
    assert{ eval(q_merge)(du ⊕ du) == du }
    assert{ eval(q_comp)(du ⊕ dt) == dt }
  }

  test("dummy evals for the graph structure") {
    import dummy.categoryStructure._
    import dummy.graphStructure._
    import queries.graphStructure._

    assert{ eval(q_outV)(du) == dt }
    assert{ eval(q_inV)(dt) == du }
    assert{ eval(q_compV)(du) == du }

    assert{ eval(q_outE)(du) == dt }
    assert{ eval(q_inE)(dt) == du }
    assert{ eval(q_compE)(du) == du }
  }

  test("dummy evals for the property structure") {
    import dummy.categoryStructure._
    import dummy.propertyStructure._
    import queries.propertyStructure._

    // FIXME: this works if you put dnames on the right (no tag/type parameter check)
    assert{ eval(q_getV)(du) == dages }
    assert{ eval(q_lookupV)(dnames) == du }
    assert{ eval(q_compV)(dnames) == dages }

    assert{ eval(q_getE)(dp) == dtimes }
    assert{ eval(q_lookupE)(dtimes) == dp }
    assert{ eval(q_compE)(dp) == dp }
  }

  // TODO: predicates test

  import rewrites._

  object compositionToRight extends AnyRewriteStrategy {

    /*
    implicit final def right_bias_assoc[
      F <: AnyGraphMorphism,
      G <: AnyGraphMorphism { type In = F#Out },
      H <: AnyGraphMorphism { type In = G#Out }
    ]: ( (F >=> G) >=> H ) rewriteTo ( F >=> (G >=> H) )
    = rewriteTo( fg_h => {

        val fg  = fg_h.first
        val h   = fg_h.second

        val f = fg.first
        val g = fg.second

        f >=> (g >=> h)
      })
    */
  }

  ignore("rewriting composition") {
    import compositionToRight._

    val morph = (outV(follows) >=> inV(follows)) >=> outV(follows)
    val rmorph = apply(compositionToRight) to morph

    info(morph.label)
    info(rmorph.label)

    // FIXME
    assert{ rmorph == (outV(follows) >=> (inV(follows) >=> outV(follows)))}
  }
}
