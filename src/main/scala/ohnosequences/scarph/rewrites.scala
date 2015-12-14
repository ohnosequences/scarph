package ohnosequences.scarph

import objects._, morphisms._, axioms._
import ohnosequences.cosas._, fns._

case object rewrites {

  trait AnyRewritingRule extends AnyDepFn1 {

    type In1 = AnyGraphMorphism
    type Out = AnyGraphMorphism
  }

  trait AnyRewrite[
    R <: AnyRewritingRule,
    IM <: AnyGraphMorphism
  ] extends AnyApp1At[R, IM] {

    // type OutMorph <: IM#In --> IM#Out
    type OutMorph <: AnyGraphMorphism

    type Y = OutMorph
  }

  // NOTE: could add here an axiom witness
  @annotation.implicitNotFound("""Cannot rewrite morphism
    ${IM}
  to
    ${OM}
  using ${R}
  """)
  case class Rewrite[
    R <: AnyRewritingRule,
    IM <: AnyGraphMorphism,
    OM <: AnyGraphMorphism
  ](val rewr: IM => OM
  // )(implicit
    // NOTE: this makes imporsible to contruct rewriting
    // i: OM#In =:= IM#In,
    // o: OM#Out =:= IM#Out
  ) extends AnyRewrite[R, IM] {

    type OutMorph = OM

    final def apply(in: X1): Y = rewr(in)
  }


  trait RuleWithDefault extends AnyRewritingRule

  case object RuleWithDefault {
    // this is a fallback case for any strategy:
    implicit def default[
      R <: AnyRewritingRule,
      F <: AnyGraphMorphism
    ]: AnyRewrite[R, F] { type OutMorph = F } =
      Rewrite[R, F, F](Predef.identity[F])
  }

  // trait AnyComposedRewriting extends AnyRewritingRule {
  //   type First <: AnyRewritingRule
  //   type Second <: AnyRewritingRule
  // }
  //
  // class ComposedRewriting[
  //   F <: AnyRewritingRule,
  //   S <: AnyRewritingRule
  // ] extends AnyComposedRewriting {
  //
  //   type First = F
  //   type Second = S
  // }
  //
  // case object ComposedRewriting {
  //
  //   implicit def appForComposition[
  //     SF <: AnyComposedRewriting,
  //     O <: AnyGraphMorphism
  //   ](implicit
  //     appF: AnyApp1At[SF#First, FO] { type Y = M0 },
  //     appS: AnyApp1At[SF#Second, M0] { type Y = O }
  //   )
  //   : AnyApp1At[SF,X10] { type Y = O } =
  //     App1 { x1: X10 => appS(appF(x1)) }
  // }

  // case class RewritingSyntax[R <: AnyRewritingRule](val r: R) extends AnyVal {
  //
  //   final def >>[S <: AnyRewritingRule](s: S): ComposedRewriting[R, S] =
  //     new ComposedRewriting[R, S]
  // }

  trait AnyRecursiveRewriting extends RuleWithDefault {
    type Rewriting <: AnyRewritingRule
    val  rewriting: Rewriting
  }

  case class RecursiveRewriting[R <: AnyRewritingRule](val rewriting: R)
    extends AnyRecursiveRewriting { type Rewriting = R }

  case object AnyRecursiveRewriting {

    // rewrites composed morphisms
    implicit def goDeeper[
      R <: AnyRecursiveRewriting,
      F <: FO#In ==> FO#Out,
      S <: SO#In ==> SO#Out,
      FO <: AnyGraphMorphism,
      SO <: AnyGraphMorphism
    ](implicit
      rewriteF: AnyRewrite[R#Rewriting, F] { type OutMorph = FO },
      rewriteS: AnyRewrite[R#Rewriting, S] { type OutMorph = SO }
    ): AnyRewrite[R, morphisms.Composition[F, S]] { type OutMorph = morphisms.Composition[FO, SO] } =
       Rewrite { f_s: morphisms.Composition[F, S] =>

         val fo = rewriteF(f_s.first)
         val so = rewriteS(f_s.second)

         println(s"Going deeper! ${f_s.label}")
         println(s"  ${f_s.first.label} ~> ${fo.label}")
         println(s"  ${f_s.second.label} ~> ${so.label}")

        //  fo >=> so
        morphisms.Composition(fo, so)
       }
  }

  def rec[R <: AnyRewritingRule](r: R): fns.Composition[R, RecursiveRewriting[R]] =
    new fns.Composition[R, RecursiveRewriting[R]]

  // trait ReduceIsoDagger extends AnyRewritingRule
  // case object ReduceIsoDagger {
  //   // TODO: once we are using axioms this should be redundant using dagger monos/epis
  //
  //   // TODO:  this should be recursive
  //   // U - iso, U ∘ U† ~> id
  //   implicit def unitaryIsoLeft[
  //     R <: ReduceIsoDagger,
  //     U <: AnyNaturalIsomorphism,
  //     UD <: DaggerOf[U]
  //   ]: AnyRewrite[R, U >=>> UD] { type OutMorph = id[U#In] } =
  //      Rewrite { u_ud: U >=>> UD => id[U#In](u_ud.first.in) }
  //
  //   // NOTE: this is likely not needed (for iso it brings ambiguance)
  //   // U - iso, U† ∘ U ~> id
  //   // implicit def unitaryIsoRight[
  //   //   R <: AnyRewritingRule,
  //   //   U <: AnyNaturalIsomorphism,
  //   //   UD <: DaggerOf[U]
  //   // ]: Rewrite[R, U#Out, U#Out, UD >>=> U, id[U#Out]] =
  //   //    Rewrite { u_ud: UD >>=> U => id[U#Out](u_ud.second.out) }
  // }

  type reduceRightIdentities = reduceRightIdentities.type
  case object reduceRightIdentities extends RuleWithDefault {

    // F ∘ id ~> F
    implicit def rightIdentity[
      FOut <: AnyGraphObject,
      F <: AnyGraphMorphism.To[FOut],
      O <: AnyGraphMorphism
    ](implicit
      rewriteF: AnyRewrite[reduceRightIdentities, F] { type OutMorph = O}
    ): AnyRewrite[reduceRightIdentities, F >=>> id[FOut]] { type OutMorph = O } =
       Rewrite { f_id: F >=>> id[FOut] =>
         rewriteF(f_id.first)
       }
  }

  // NOTE: one of these traits has to have higher priority, otherwise (id ∘ id) is ambiguous
  // NOTE: we cannot build this hierarchy with the companion object extending LowPrioritySmth, because then it's on the same level as AnyRewritingRule
  type reduceLeftIdentities = reduceLeftIdentities.type
  case object reduceLeftIdentities extends RuleWithDefault {

    // id ∘ F ~> F
    implicit def leftIdentity[
      FIn <: AnyGraphObject,
      F <: AnyGraphMorphism.From[FIn],
      O <: AnyGraphMorphism
    ](implicit
      rewriteF: AnyRewrite[reduceLeftIdentities, F] { type OutMorph = O }
      // i: O#In =:= F#In,
      // o: O#Out =:= F#Out
    ): AnyRewrite[reduceLeftIdentities, id[FIn] >>=> F] { type OutMorph = O } =
       Rewrite { id_f: id[FIn] >>=> F =>
         rewriteF(id_f.second)
       }
  }

  // // This is just for convenience
  // case object reduceIdentities extends ComposedRewriting[reduceLeftIdentities, reduceRightIdentities]

}
