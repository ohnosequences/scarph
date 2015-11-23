package ohnosequences.scarph

import morphisms._, axioms._
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
    IM <: AnyGraphMorphism, // { type In = OM#In; type Out = OM#Out },
    OM <: AnyGraphMorphism
  ](val rewr: IM => OM) extends AnyApp1At[S, IM] {

    type Y = OM

    final def apply(in: X1): Y = rewr(in)
  }

  case object AnyRewriting extends ReduceCompositionWithIdentities {

    // TODO: once we are using axioms this should be redundant using dagger monos/epis

    // U - iso, U ∘ U† ~> id
    implicit def unitaryIsoLeft[
      S <: AnyRewriting,
      U <: AnyNaturalIsomorphism
    ]: Rewrite[S, morphisms.Composition[U, U#Dagger], id[U#In]] =
       Rewrite { u_ud: morphisms.Composition[U, U#Dagger] => id[U#In](u_ud.first.in) }

    // U - iso, U† ∘ U ~> id
    implicit def unitaryIsoRight[
      S <: AnyRewriting,
      U <: AnyNaturalIsomorphism
    ]: Rewrite[S, morphisms.Composition[U#Dagger, U], id[U#Out]] =
       Rewrite { u_ud: morphisms.Composition[U#Dagger, U] => id[U#Out](u_ud.second.out) }
  }

  trait ReduceCompositionWithIdentities extends DoNothing {

    // F ∘ id ~> F
    implicit def leftIdentities[
      S <: AnyRewriting,
      F <: AnyGraphMorphism
    ]: Rewrite[S, F >=> id[F#Out], F] =
       Rewrite { f_id: F >=> id[F#Out] => f_id.first }

    // id ∘ F ~> F
    implicit def rightIdentities[
      S <: AnyRewriting,
      F <: AnyGraphMorphism
    ]: Rewrite[S, morphisms.Composition[id[F#In], F], F] =
       Rewrite { id_f: morphisms.Composition[id[F#In], F] => id_f.second }
  }

  trait DoNothing {

    // this is a fallback case for any strategy:
    implicit def default[
      S <: AnyRewriting,
      F <: AnyGraphMorphism
    ]: Rewrite[S, F, F] =
       Rewrite(Predef.identity[F])
  }






  /////////////////////////////////////////////////////////////////////////////

  /* Transforms a morphism to another morphism with same domain/codomain */
  // trait AnyRewrite extends AnyMorphismTransform {
  //
  //   type OutMorph <: InMorph#In --> InMorph#Out
  // }
  //
  // @annotation.implicitNotFound(msg = "Cannot rewrite morphism ${M} to ${OM}")
  // trait Rewrite[M <: AnyGraphMorphism, OM <: M#In --> M#Out] extends AnyRewrite {
  //
  //   type InMorph = M
  //   type OutMorph = OM
  // }
  //
  // case class apply[RS <: AnyRewriteStrategy](val rewriteStrategy: RS) extends AnyVal {
  //
  //   def to[M <: AnyGraphMorphism, OM <: M#In --> M#Out]
  //     (m: M)(implicit rewrite: RewriteFrom[RS,M,OM])
  //     : OM = rewrite(m)
  // }
  //
  // case object rewrite {
  //
  //   def apply[M <: AnyGraphMorphism, OM <: M#In --> M#Out]
  //     (m: M)(implicit rewr: Rewrite[M, OM])
  //     : OM = rewr(m)
  // }
  //
  // @annotation.implicitNotFound(
  //   msg = "Cannot rewrite morphism ${M} to ${OM} using rewrite strategy ${RS}"
  // )
  // case class RewriteFrom[
  //   RS <: AnyRewriteStrategy,
  //   M <: AnyGraphMorphism,
  //   OM <: M#In --> M#Out
  // ](val rwrt: M => OM) extends Rewrite[M,OM] {
  //
  //   @inline final def apply(morph: InMorph): OutMorph = rwrt(morph)
  // }
  //
  // trait AnyRewriteStrategy { rs =>
  //
  //   implicit final def id_rewrite[M <: AnyGraphMorphism]:
  //       RewriteFrom[rs.type, M, M] =
  //   new RewriteFrom[rs.type, M, M]( identity[M] )
  //
  //   type rewriteTo[M0 <: AnyGraphMorphism, OM0 <: M0#In --> M0#Out] = RewriteFrom[rs.type, M0, OM0]
  //   final def rewriteTo[M0 <: AnyGraphMorphism, OM0 <: M0#In --> M0#Out]
  //     (rwrt: M0 => OM0) =
  //     new rewriteTo(rwrt)
  // }
  //
  // trait AnyRecursiveRewriteStrategy extends AnyRewriteStrategy { rs =>
  //
  //   implicit final def composition_rewrite[
  //     F1 <: AnyGraphMorphism,
  //     S1 <: AnyGraphMorphism { type In = F1#Out },
  //     F2 <: AnyGraphMorphism { type In = F1#In; type Out = S1#In },
  //     S2 <: AnyGraphMorphism { type In = F2#Out; type Out = S1#Out },
  //     M  <: AnyGraphMorphism { type In = F2#In; type Out = S2#Out }
  //   ](implicit
  //     rewrF: Rewrite[F1, F2],
  //     rewrS: Rewrite[S1, S2],
  //     rewrFS: Rewrite[F2 >=> S2, M]
  //   ):  RewriteFrom[rs.type, F1 >=> S1, M] =
  //   new RewriteFrom[rs.type, F1 >=> S1, M]({ f_s =>
  //
  //     val f = f_s.first
  //     val s = f_s.second
  //
  //     rewrFS(rewrF(f) >=> rewrS(s))
  //   })
  // }
  //
  // trait AnyRecursiveRightAssocRewriteStrategy extends AnyRecursiveRewriteStrategy { rs =>
  //
  //   implicit final def right_bias_assoc[
  //     F <: AnyGraphMorphism,
  //     G <: AnyGraphMorphism { type In = F#Out },
  //     H <: AnyGraphMorphism { type In = G#Out },
  //     A <: AnyGraphMorphism { type In = F#In; type Out = F#Out },
  //     B <: AnyGraphMorphism { type In = F#Out; type Out = H#Out }
  //   ](implicit
  //     rewrF: RewriteFrom[rs.type, F, A],
  //     rewrGH: RewriteFrom[rs.type, G >=> H, B]
  //   ):  RewriteFrom[rs.type, (F >=> G) >=> H, A >=> B] =
  //   new RewriteFrom[rs.type, (F >=> G) >=> H, A >=> B]({ fg_h =>
  //
  //     val fg = fg_h.first
  //     val f = fg.first
  //     val g = fg.second
  //     val h  = fg_h.second
  //
  //     rewrF(f) >=> rewrGH(g >=> h)
  //   })
  // }
}
