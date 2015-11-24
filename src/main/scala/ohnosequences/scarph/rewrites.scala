package ohnosequences.scarph

import objects._, morphisms._, axioms._
import ohnosequences.cosas._, fns._

case object rewrites {

  trait AnyRewriting extends AnyDepFn1 {

    type In1 = AnyGraphMorphism
    type Out = AnyGraphMorphism
  }

  case object AnyRewriting {

    // this is a fallback case for any strategy:
    implicit def default[
      R <: AnyRewriting,
      F <: AnyGraphMorphism
    ]: AnyRewrite[R, F] { type OutMorph = F } =
       Rewrite(Predef.identity[F])
  }


  trait AnyRewrite[
    R <: AnyRewriting {
      type In1 >: IM
    },
    IM <: AnyGraphMorphism
  ] extends AnyApp1At[R, IM] {

    type OutMorph <: IM#In --> IM#Out

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
    R <: AnyRewriting {
      type In1 >: IM
      type Out >: OM
    },
    IM <: AnyGraphMorphism,
    OM <: IM#In --> IM#Out
  ](val rewr: IM => OM) extends AnyRewrite[R, IM] {

    type OutMorph = OM

    final def apply(in: X1): Y = rewr(in)
  }



  trait ReduceIsoDagger extends AnyRewriting
  case object ReduceIsoDagger {
    // TODO: once we are using axioms this should be redundant using dagger monos/epis

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

    // F ∘ id ~> F
    implicit def leftIdentities[
      R <: ReduceCompositionWithIdentities,
      F <: AnyGraphMorphism
    ]: AnyRewrite[R, F >=>> id[F#Out]] { type OutMorph = F } =
       Rewrite { f_id: F >=>> id[F#Out] => f_id.first }

    // id ∘ F ~> F
    implicit def rightIdentities[
      R <: AnyRewriting,
      F <: AnyGraphMorphism
    ]: AnyRewrite[R, id[F#In] >>=> F] { type OutMorph = F } =
       Rewrite { id_f: id[F#In] >>=> F => id_f.second }
  }


  trait RecurseOverComposition extends AnyRewriting //ReduceCompositionWithIdentities
  case object RecurseOverComposition {

    // rewrites composed moprhisms
    implicit def goInside[
      R <: RecurseOverComposition,
      F <: AnyGraphMorphism,
      S <: AnyGraphMorphism { type In = F#Out },
      F1 <: F#In ==> F#Out,
      S1 <: S#In ==> S#Out
    ](implicit
      rewriteF: AnyRewrite[R, F] { type OutMorph = F1 },
      rewriteS: AnyRewrite[R, S] { type OutMorph = S1 }
    ): AnyRewrite[R, F  >=>> S] { type OutMorph = F1 >=>> S1 } =
       Rewrite { f_s: F >=>> S =>
         rewriteF(f_s.first) >=> rewriteS(f_s.second)
       }
  }

}
