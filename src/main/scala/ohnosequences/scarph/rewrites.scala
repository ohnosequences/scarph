package ohnosequences.scarph

import objects._, morphisms._, axioms._
import ohnosequences.cosas._, fns._

case object rewrites {

  trait AnyRewriting extends AnyDepFn1 {

    type In1 = AnyGraphMorphism
    type Out = AnyGraphMorphism
  }

  case object AnyRewriting {
    implicit def rewritingSyntax[R <: AnyRewriting](r: R):
      RewritingSyntax[R] =
      RewritingSyntax[R](r)

    // this is a fallback case for any strategy:
    implicit def default[
      R <: AnyRewriting,
      F <: AnyGraphMorphism
    ]: AnyRewrite[R, F] { type OutMorph = F } =
       Rewrite[R, F, F](Predef.identity[F])
  }

  case class RewritingSyntax[R <: AnyRewriting](r: R) extends AnyVal {

    // def rewrite[
    //   IM <: AnyGraphMorphism
    //   OM <: IM#In --> IM#Out
    // ](im: IM)(implicit
    //   rewr: AnyRewrite[DeepRewriting[R], IM] { type OutMorph = OM }
    // ): OM = rewr(im)
  }

  trait AnyRewrite[
    R <: AnyRewriting,
    IM <: AnyGraphMorphism
  ] extends AnyApp1At[R, IM] {

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
    R <: AnyRewriting,
    IM <: AnyGraphMorphism,
    OM <: AnyGraphMorphism
  ](val rewr: IM => OM) extends AnyRewrite[R, IM] {

    type OutMorph = OM

    final def apply(in: X1): Y = rewr(in)
  }



  trait ReduceIsoDagger extends AnyRewriting
  case object ReduceIsoDagger {
    // TODO: once we are using axioms this should be redundant using dagger monos/epis

    // TODO:  this should be recursive
    // U - iso, U ∘ U† ~> id
    implicit def unitaryIsoLeft[
      R <: ReduceIsoDagger,
      U <: AnyNaturalIsomorphism,
      UD <: DaggerOf[U]
    ]: AnyRewrite[R, U >=>> UD] { type OutMorph = id[U#In] } =
       Rewrite { u_ud: U >=>> UD => id[U#In](u_ud.first.in) }

    // NOTE: this is likely not needed (for iso it brings ambiguance)
    // U - iso, U† ∘ U ~> id
    // implicit def unitaryIsoRight[
    //   R <: AnyRewriting,
    //   U <: AnyNaturalIsomorphism,
    //   UD <: DaggerOf[U]
    // ]: Rewrite[R, U#Out, U#Out, UD >>=> U, id[U#Out]] =
    //    Rewrite { u_ud: UD >>=> U => id[U#Out](u_ud.second.out) }
  }

  trait ReduceCompositionWithIdentities extends ReduceIsoDagger
  case object ReduceCompositionWithIdentities {
    // TODO:  this should be recursive

    // F ∘ id ~> F
    implicit def leftIdentities[
      R <: ReduceCompositionWithIdentities,
      F <: AnyGraphMorphism
    ]: AnyRewrite[R, F >=>> id[F#Out]] { type OutMorph = F } =
       Rewrite { f_id: F >=>> id[F#Out] => f_id.first }

    // id ∘ F ~> F
    implicit def rightIdentities[
      R <: ReduceCompositionWithIdentities,
      F <: AnyGraphMorphism
    ]: AnyRewrite[R, id[F#In] >>=> F] { type OutMorph = F } =
       Rewrite { id_f: id[F#In] >>=> F => id_f.second }
  }


  trait RecurseOverComposition extends AnyRewriting
  case object RecurseOverComposition {

    // rewrites composed moprhisms
    implicit def goInside[
      R <: RecurseOverComposition,
      F <: AnyGraphMorphism,
      S <: AnyGraphMorphism { type In = F#Out },
      F1 <: AnyGraphMorphism,
      S1 <: AnyGraphMorphism { type In = F1#Out }
    ](implicit
      rewriteF: AnyRewrite[R, F] { type OutMorph = F1 },
      rewriteS: AnyRewrite[R, S] { type OutMorph = S1 }
    ): AnyRewrite[R, F >=>> S] { type OutMorph = F1 >=>> S1 } =
       Rewrite { f_s: F >=>> S =>

         val f1: F1 = rewriteF(f_s.first)
         val s1: S1 = rewriteS(f_s.second)

         f1 >=> s1
       }
  }

}
