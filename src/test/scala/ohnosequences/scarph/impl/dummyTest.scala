package ohnosequences.scarph.test

import ohnosequences.scarph._, objects._, morphisms._, evals._
import syntax.morphisms._
import asserts._, twitter._, dummy._, dummy.syntax._

class DummyTests extends org.scalatest.FunSuite {

  val I = unit := DummyUnit
  val du = user := DummyVertex
  val dt = tweet := DummyVertex
  val dp = posted := DummyEdge

  val dages = age := new Integer(2)
  val dnames = name := "Paco"
  val dtexts = text := "Hola!"
  val dtimes = time := "24:00"


  test("dummy evals for the basic structure") {
    import dummy.categoryStructure._
    import queries.categoryStructure._

    assertTaggedEq( eval(q_id)(du), du )
    assertTaggedEq(
      eval(q_comp1)(du),
      du
    )
  }

  test("dummy evals for the tensor structure") {
    import dummy.categoryStructure._
    import dummy.tensorStructure._
    import queries.tensorStructure._

    assertTaggedEq( eval(q_symmetry)(du ⊗ dt), dt ⊗ du )
    assertTaggedEq( eval(q_fromUnit)(I), du )
    assertTaggedEq( eval(q_toUnit)(du), I )
    assertTaggedEq( eval(q_tensor)(du ⊗ du ⊗ du), du ⊗ du ⊗ du )
    assertTaggedEq( eval(q_dupl)(du ⊗ du), du ⊗ du ⊗ du )
    assertTaggedEq( eval(q_match)(du ⊗ du), du )
    assertTaggedEq( eval(q_comp)(du ⊗ du), du )

    // assertTaggedEq( eval(q_trace)(du), du )
  }

  test("dummy evals for the biproduct structure") {
    import dummy.categoryStructure._
    import dummy.biproductStructure._
    import queries.biproductStructure._

    assertTaggedEq( eval(q_inj)(dt), du ⊕ du ⊕ dt )
    assertTaggedEq( eval(q_bip)(du ⊕ du ⊕ dt), du ⊕ du ⊕ dt )
    assertTaggedEq( eval(q_fork)(du ⊕ dt), du ⊕ du ⊕ dt )
    assertTaggedEq( eval(q_merge)(du ⊕ du), du )
    assertTaggedEq( eval(q_comp)(du ⊕ dt), dt )
  }

  test("dummy evals for the graph structure") {
    import dummy.categoryStructure._
    import dummy.graphStructure._
    import queries.graphStructure._

    assertTaggedEq( eval(q_outV)(du), dt )
    assertTaggedEq( eval(q_inV)(dt), du )
    assertTaggedEq( eval(q_compV)(du), du )
    //
    assertTaggedEq( eval(q_outE)(du), dt )
    assertTaggedEq( eval(q_inE)(dt), du )
    assertTaggedEq( eval(q_compE)(du), du )
  }

  test("dummy evals for the property structure") {
    import dummy.categoryStructure._
    import dummy.propertyStructure._
    import queries.propertyStructure._

    // TODO these are methods because of lacking implementations
    def p1 = eval(q_getV)(du)
    def p2 = eval(q_lookupV)(dnames)(eval_lookupV)
    def p3 = eval(q_compV)(dnames)

    def p4 = eval(q_getE)(dp)
    def p5 = eval(q_lookupE)(dtimes)
    def p6 = eval(q_compE)(dp)
    //
    // assertTaggedEq( eval(q_getV)(du), dages )
    // assertTaggedEq( eval(q_lookupV)(dnames), du )
    // assertTaggedEq( eval(q_compV)(dnames), dages )
    //
    // assertTaggedEq( eval(q_getE)(dp), dtimes )
    // assertTaggedEq( eval(q_lookupE)(dtimes), dp )
    // assertTaggedEq( eval(q_compE)(dp), dp )
  }

  test("dummy evals for the predicate structure") {
    import dummy.categoryStructure._
    import dummy.predicateStructure._
    import queries.predicateStructure._

    assertTaggedEq( eval(q_quant)(du), pred := du.value )
    assertTaggedEq( eval(q_coerce)(pred := du.value), du )
    assertTaggedEq( eval(q_comp)(du), du )
  }

  import rewrites._

  test("basic rewriting") {

    case object basic extends ReduceIsoDagger

    assertResult(id(user ⊗ tweet))(
      basic(
        symmetry(user, tweet) >=> symmetry(tweet, user)
      )
    )
  }

  test("reducing identities") {
    val i = id(user)
    val x = outV(follows)

    case object reduceL extends ReduceLeftIdentities
    case object reduceR extends ReduceRightIdentities
    val reduceIds = reduceR ∘ reduceL

    // info(reduceL(i >=> x).label)
    // info(reduceR(x >=> i).label)
    // info((reduceR ∘ reduceL)((i >=> (x >=> (i >=> (i >=> i)))) >=> i).label)

    assertResult(i)(
      reduceIds(i >=> i)
    )

    assertResult(i)(
      reduceIds(i >=> (i >=> i))
    )

    assertResult(i)(
      reduceIds(i >=> ((i >=> (i >=> i)) >=> i))
    )

    assertResult(x)(
      reduceIds(i >=> x)
    )

    assertResult(x)(
      reduceIds(x >=> i)
    )

    // a case for RecurseOverComposition:
    assertResult(x >=> x)(
      reduceIds((x >=> i) >=> (i >=> x))
    )
  }


  case object compositionToLeft extends compositionToLeft_2 {

    implicit final def left_bias_assoc[
      F <: AnyGraphMorphism { type Out = G#In },
      G <: AnyGraphMorphism { type In = FO#Out; type Out = H#In },
      H <: AnyGraphMorphism { type In = FGO#Out },
      FO <: AnyGraphMorphism,
      FGO <: AnyGraphMorphism,
      O <: AnyGraphMorphism
    ](implicit
      rewriteF: AnyRewrite[compositionToLeft.type, F] { type OutMorph = FO },
      rewriteG: AnyRewrite[compositionToLeft.type, FO >=>> G] { type OutMorph = FGO },
      rewriteH: AnyRewrite[compositionToLeft.type, FGO >=>> H] { type OutMorph = O }
    )
    : AnyRewrite[compositionToLeft.type, F >>=> (G >>=> H)] { type OutMorph = O }
    = Rewrite { f_gh: F >>=> (G >>=> H) =>

      val f = f_gh.first
      val g = f_gh.second.first
      val h = f_gh.second.second

      rewriteH(rewriteG(rewriteF(f) >=> g) >=> h)
    }

  }

  trait compositionToLeft_2 extends RecurseOverComposition {

    implicit final def left_bias_assoc_2[
      F <: AnyGraphMorphism { type Out = G#In },
      G <: AnyGraphMorphism { type In = FO#Out; type Out = H#In },
      H <: AnyGraphMorphism { type In = O#Out },
      FO <: AnyGraphMorphism,
      O <: AnyGraphMorphism
    ](implicit
      rewriteF: AnyRewrite[compositionToLeft.type, F] { type OutMorph = FO },
      rewriteG: AnyRewrite[compositionToLeft.type, FO >=>> G] { type OutMorph = O }
    )
    : AnyRewrite[compositionToLeft.type, F >>=> G] { type OutMorph = O }
    = Rewrite { f_g: F >>=> G =>

      val f = f_g.first
      val g = f_g.second

      rewriteG(rewriteF(f) >=> g)
    }
  }

  test("rewriting composition") {

    val x = id(user)
    val morph    = x >=> ((x >=> x) >=> (x >=> x))
    val shouldBe = (((x >=> x) >=> x) >=> x) >=> x

    val rmorph = compositionToLeft(morph)

    // info("original:   " + morph.label)
    // info("rewritten:  " + rmorph.label)
    // info("should be:  " + shouldBe.label)

    assertResult(shouldBe){ rmorph }

    // composing 7 x's in any way should yield this after rewriting:
    val x7 = ((((((x >=> x) >=> x) >=> x) >=> x) >=> x) >=> x)

    assertResult(x7) {
      compositionToLeft(
        x >=> (x >=> (x >=> (x >=> (x >=> (x >=> x)))))
      )
    }

    assertResult(x7) {
      compositionToLeft(
        x >=> (x >=> (x >=> x) >=> (x >=> x) >=> x)
      )
    }

    // Checking that f gets rewritten
    val f = (x >=> (x >=> x)) >=> x
    val g = x >=> x
    val h = x

    assertResult(x7) {
      compositionToLeft( f >=> (g >=> h) )
    }
  }
}
