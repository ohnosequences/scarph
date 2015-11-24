package ohnosequences.scarph

import objects._, morphisms._, axioms._
import ohnosequences.cosas._, fns._

case object rewrites {

  trait AnyRewriting extends AnyDepFn1 {

    type In1 = AnyGraphMorphism
    type Out = AnyGraphMorphism // is a bound needed here?
  }


  // NOTE: could add here an axiom witness
  @annotation.implicitNotFound("""Cannot rewrite morphism
    ${IM}
  to
    ${OM}
  using ${S}
  """)
  case class Rewrite[
    S <: AnyRewriting {
      type In1 >: IM
      type Out >: OM
    },
    I <: AnyGraphObject, O <: AnyGraphObject,
    IM <: I --> O,
    OM <: I --> O
    // IM <: AnyGraphMorphism, // { type In = OM#In; type Out = OM#Out },
    // OM <: IM#In --> IM#Out
  ](val rewr: IM => OM) extends AnyApp1At[S, IM] {

    type Y = OM

    final def apply(in: X1): Y = rewr(in)
  }

  // case object AnyRewrite {
  //   type Of[M <: AnyGraphMorphism] = Rewrite[]
  // }

  case object AnyRewriting {

    // this is a fallback case for any strategy:
    implicit def default[
      R <: AnyRewriting,
      F <: AnyGraphMorphism
    ]: Rewrite[R, F#In, F#Out, F, F] =
       Rewrite(Predef.identity[F])
  }


  trait ReduceIsoDagger extends AnyRewriting
  case object ReduceIsoDagger {
    // TODO: once we are using axioms this should be redundant using dagger monos/epis

    // U - iso, U ∘ U† ~> id
    implicit def unitaryIsoLeft[
      R <: ReduceIsoDagger,
      U <: AnyNaturalIsomorphism,
      UD <: DaggerOf[U]
    ]: Rewrite[R, U#In, U#In, U >=>> UD, id[U#In]] =
       Rewrite { u_ud: U >=>> UD => id[U#In](u_ud.first.in) }

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
    ]: Rewrite[R, F#In, F#Out, F >=>> id[F#Out], F] =
       Rewrite { f_id: F >=>> id[F#Out] => f_id.first }

    // id ∘ F ~> F
    implicit def rightIdentities[
      R <: AnyRewriting,
      F <: AnyGraphMorphism
    ]: Rewrite[R, F#In, F#Out, id[F#In] >>=> F, F] =
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
      rewriteF: Rewrite[R, F#In, F#Out, F, F1],
      rewriteS: Rewrite[R, S#In, S#Out, S, S1]
    ): Rewrite[R, F#In, S#Out,
         F  >=>> S,
         F1 >=>> S1
       ] =
       Rewrite { f_s: F >=>> S =>
         rewriteF(f_s.first) >=> rewriteS(f_s.second)
       }
  }

}
