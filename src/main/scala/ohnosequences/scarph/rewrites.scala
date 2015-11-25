package ohnosequences.scarph

import objects._, morphisms._, axioms._
import ohnosequences.cosas._, fns._

case object rewrites {

  trait AnyRewriting extends AnyDepFn1 {

    type In1 = AnyGraphMorphism
    type Out = AnyGraphMorphism
  }

  case object AnyRewriting {
    // implicit def rewritingSyntax[R <: AnyRewriting](r: R):
    //   RewritingSyntax[R] =
    //   RewritingSyntax[R](r)

    // this is a fallback case for any strategy:
    implicit def default[
      R <: AnyRewriting,
      F <: AnyGraphMorphism
    ]: AnyRewrite[R, F] { type OutMorph = F } =
       Rewrite[R, F, F](Predef.identity[F])
  }

  // case class RewritingSyntax[R <: AnyRewriting](r: R) extends AnyVal {
  //   def rewrite[
  //     IM <: AnyGraphMorphism
  //     OM <: IM#In --> IM#Out
  //   ](im: IM)(implicit
  //     rewr: AnyRewrite[DeepRewriting[R], IM] { type OutMorph = OM }
  //   ): OM = rewr(im)
  // }

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


  trait RecurseOverComposition extends AnyRewriting
  case object RecurseOverComposition {

    // rewrites composed morphisms
    implicit def goDeeper[
      R <: RecurseOverComposition,
      F <: AnyGraphMorphism,
      S <: AnyGraphMorphism.From[F#Out],
      FO <: AnyGraphMorphism
      // SO <: AnyGraphMorphism //.From[FO#Out]
    ](implicit
      rewriteF: AnyRewrite[R, F] { type OutMorph = FO },
      rewriteS: AnyRewrite[R, S] { type OutMorph <: AnyGraphMorphism.From[FO#Out] }
    ): AnyRewrite[R, F >=>> S] { type OutMorph = FO >=>> rewriteS.OutMorph } =
       Rewrite { f_s: F >=>> S =>

         val fo = rewriteF(f_s.first)
         val so = rewriteS(f_s.second)

         println(s"Going deeper! (${f_s.label})")
         println(s"fo: ${fo.label}")
         println(s"so: ${so.label}")

         fo >=> so
        // morphisms.Composition(fo, so)
       }
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

  trait ReduceRightIdentities extends RecurseOverComposition //ReduceIsoDagger
  case object ReduceRightIdentities {

    // F ∘ id ~> F
    implicit def rightIdentity[
      R <: ReduceRightIdentities,
      FOut <: AnyGraphObject,
      F <: AnyGraphMorphism.To[FOut],
      O <: AnyGraphMorphism
    ](implicit
      rewriteF: AnyRewrite[R, F] { type OutMorph = O}
    ): AnyRewrite[R, F >=>> id[FOut]] { type OutMorph = O } =
       Rewrite { f_id: F >=>> id[FOut] =>
         rewriteF(f_id.first)
       }
  }

  // NOTE: one of these traits has to have higher priority, otherwise (id ∘ id) is ambiguous
  // NOTE: we cannot build this hierarchy with the companion object extending LowPrioritySmth, because then it's on the same level as AnyRewriting
  trait ReduceLeftIdentities extends RecurseOverComposition //ReduceIsoDagger
  case object ReduceLeftIdentities {

    // id ∘ F ~> F
    implicit def leftIdentity[
      R <: ReduceLeftIdentities,
      FIn <: AnyGraphObject,
      F <: AnyGraphMorphism.From[FIn],
      O <: AnyGraphMorphism
    ](implicit
      rewriteF: AnyRewrite[R, F] { type OutMorph = O}
    ): AnyRewrite[R, id[FIn] >>=> F] { type OutMorph = O } =
       Rewrite { id_f: id[FIn] >>=> F =>
         rewriteF(id_f.second)
       }
  }

  // This is just for convenience
  trait ReduceIdentities extends ReduceLeftIdentities with ReduceRightIdentities

}
