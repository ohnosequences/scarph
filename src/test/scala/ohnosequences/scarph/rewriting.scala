package ohnosequences.scarph.test

import ohnosequences.scarph._, objects._, morphisms._, evals._, rewrites._
// import syntax.morphisms._
import asserts._, twitter._

class RewritingTests extends org.scalatest.FunSuite {

  // test("recursive rewriting") {
  //
  //   case object v extends Vertex("v"); type v = v.type
  //   case object foo extends AnyRewriting
  //   // implicitly[AnyRewrite[foo.type, v.type, v.type, id[v.type]]]
  //   foo(id(v)) //(AnyRewriting.default)
  //
  //   case object recFoo extends AnyRecursiveRewriting
  //
  //   info(
  //     recFoo( id(v) >=> id(v) ).label
  //   )
  //
  // }

  // test("basic rewriting") {
  //
  //   case object basic extends ReduceIsoDagger
  //
  //   assertResult(id(user ⊗ tweet))(
  //     basic(
  //       symmetry(user, tweet) >=> symmetry(tweet, user)
  //     )
  //   )
  // }

  test("reducing identities") {
    val i = id(user)
    val x = outV(follows)

    assertResult(i)(
      reduceLeftIdentities(i >=> i)
    )

    assertResult(i)(
      reduceRightIdentities(i >=> i)
    )


    // val reduceIds = RecursiveRewriting(RecursiveRewriting(reduceLeftIdentities) ∘ RecursiveRewriting(reduceRightIdentities))
    // val reduceIds = RecursiveRewriting(reduceLeftIdentities) ∘ RecursiveRewriting(reduceRightIdentities)
    val reduceIds = rec(reduceLeftIdentities) ∘ rec(reduceRightIdentities)

    assertResult(i)(
      reduceIds(i >=> (i >=> i))
    )

    assertResult(i)(
      reduceIds(i >=> ((i >=> (i >=> i)) >=> i))
    )

    assertResult(i)(
      reduceIds(i >=> ((i >=> ((i >=> i) >=> i)) >=> i))
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


  // case object compositionToLeft extends compositionToLeft_2 {
  //
  //   implicit final def left_bias_assoc[
  //     F <: AnyGraphMorphism { type Out = G#In },
  //     G <: AnyGraphMorphism { type In = FO#Out; type Out = H#In },
  //     H <: AnyGraphMorphism { type In = FGO#Out },
  //     FO <: AnyGraphMorphism,
  //     FGO <: AnyGraphMorphism,
  //     O <: AnyGraphMorphism
  //   ](implicit
  //     rewriteF: AnyRewrite[compositionToLeft.type, F] { type OutMorph = FO },
  //     rewriteG: AnyRewrite[compositionToLeft.type, FO >=>> G] { type OutMorph = FGO },
  //     rewriteH: AnyRewrite[compositionToLeft.type, FGO >=>> H] { type OutMorph = O }
  //   )
  //   : AnyRewrite[compositionToLeft.type, F >>=> (G >>=> H)] { type OutMorph = O }
  //   = Rewrite { f_gh: F >>=> (G >>=> H) =>
  //
  //     val f = f_gh.first
  //     val g = f_gh.second.first
  //     val h = f_gh.second.second
  //
  //     rewriteH(rewriteG(rewriteF(f) >=> g) >=> h)
  //   }
  //
  // }
  //
  // trait compositionToLeft_2 extends RecurseOverComposition {
  //
  //   implicit final def left_bias_assoc_2[
  //     F <: AnyGraphMorphism { type Out = G#In },
  //     G <: AnyGraphMorphism { type In = FO#Out; type Out = H#In },
  //     H <: AnyGraphMorphism { type In = O#Out },
  //     FO <: AnyGraphMorphism,
  //     O <: AnyGraphMorphism
  //   ](implicit
  //     rewriteF: AnyRewrite[compositionToLeft.type, F] { type OutMorph = FO },
  //     rewriteG: AnyRewrite[compositionToLeft.type, FO >=>> G] { type OutMorph = O }
  //   )
  //   : AnyRewrite[compositionToLeft.type, F >>=> G] { type OutMorph = O }
  //   = Rewrite { f_g: F >>=> G =>
  //
  //     val f = f_g.first
  //     val g = f_g.second
  //
  //     rewriteG(rewriteF(f) >=> g)
  //   }
  // }
  //
  // test("rewriting composition") {
  //
  //   val x = id(user)
  //   val morph    = x >=> ((x >=> x) >=> (x >=> x))
  //   val shouldBe = (((x >=> x) >=> x) >=> x) >=> x
  //
  //   val rmorph = compositionToLeft(morph)
  //
  //   // info("original:   " + morph.label)
  //   // info("rewritten:  " + rmorph.label)
  //   // info("should be:  " + shouldBe.label)
  //
  //   assertResult(shouldBe){ rmorph }
  //
  //   // composing 7 x's in any way should yield this after rewriting:
  //   val x7 = ((((((x >=> x) >=> x) >=> x) >=> x) >=> x) >=> x)
  //
  //   assertResult(x7) {
  //     compositionToLeft(
  //       x >=> (x >=> (x >=> (x >=> (x >=> (x >=> x)))))
  //     )
  //   }
  //
  //   assertResult(x7) {
  //     compositionToLeft(
  //       x >=> (x >=> (x >=> x) >=> (x >=> x) >=> x)
  //     )
  //   }
  //
  //   // Checking that f gets rewritten
  //   val f = (x >=> (x >=> x)) >=> x
  //   val g = x >=> x
  //   val h = x
  //
  //   assertResult(x7) {
  //     compositionToLeft( f >=> (g >=> h) )
  //   }
  // }
}
