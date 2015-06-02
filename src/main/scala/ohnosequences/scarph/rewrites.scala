package ohnosequences.scarph

object rewrites {

  import morphisms._

  /* Transforms a morphism to another morphism with same domain/codomain */
  trait AnyRewrite extends AnyMorphismTransform {

    type OutMorph <: InMorph#In --> InMorph#Out
  }

  @annotation.implicitNotFound(msg = "Cannot rewrite morphism ${M} to ${OM}")
  trait Rewrite[M <: AnyGraphMorphism, OM <: M#In --> M#Out] extends AnyRewrite {

    type InMorph = M
    type OutMorph = OM
  }

  case class apply[RS <: AnyRewriteStrategy](val rewriteStrategy: RS) extends AnyVal {

    def to[M <: AnyGraphMorphism, OM <: M#In --> M#Out]
      (m: M)(implicit rewrite: RewriteFrom[RS,M,OM])
      : OM = rewrite(m)
  }

  object rewrite {

    def apply[M <: AnyGraphMorphism, OM <: M#In --> M#Out]
      (m: M)(implicit rewr: Rewrite[M, OM])
      : OM = rewr(m)
  }

  @annotation.implicitNotFound(
    msg = "Cannot rewrite morphism ${M} to ${OM} using rewrite strategy ${RS}"
  )
  case class RewriteFrom[
    RS <: AnyRewriteStrategy,
    M <: AnyGraphMorphism,
    OM <: M#In --> M#Out
  ](val rwrt: M => OM) extends Rewrite[M,OM] {

    @inline final def apply(morph: InMorph): OutMorph = rwrt(morph)
  }

  trait AnyRewriteStrategy { rs =>

    implicit final def id_rewrite[M <: AnyGraphMorphism]:
        RewriteFrom[rs.type, M, M] =
    new RewriteFrom[rs.type, M, M]( identity[M] )

    type rewriteTo[M0 <: AnyGraphMorphism, OM0 <: M0#In --> M0#Out] = RewriteFrom[rs.type, M0, OM0]
    final def rewriteTo[M0 <: AnyGraphMorphism, OM0 <: M0#In --> M0#Out]
      (rwrt: M0 => OM0) =
      new rewriteTo(rwrt)
  }

  trait AnyRecursiveRewriteStrategy extends AnyRewriteStrategy { rs =>

    implicit final def composition_rewrite[
      F1 <: AnyGraphMorphism,
      S1 <: AnyGraphMorphism { type In = F1#Out },
      F2 <: AnyGraphMorphism { type In = F1#In; type Out = S1#In },
      S2 <: AnyGraphMorphism { type In = F2#Out; type Out = S1#Out },
      M  <: AnyGraphMorphism { type In = F2#In; type Out = S2#Out }
    ](implicit
      rewrF: Rewrite[F1, F2],
      rewrS: Rewrite[S1, S2],
      rewrFS: Rewrite[F2 >=> S2, M]
    ):  RewriteFrom[rs.type, F1 >=> S1, M] =
    new RewriteFrom[rs.type, F1 >=> S1, M]({ f_s =>

      val f = f_s.first
      val s = f_s.second

      rewrFS(rewrF(f) >=> rewrS(s))
    })
  }

  trait AnyRecursiveRightAssocRewriteStrategy extends AnyRecursiveRewriteStrategy { rs =>

    implicit final def right_bias_assoc[
      F <: AnyGraphMorphism,
      G <: AnyGraphMorphism { type In = F#Out },
      H <: AnyGraphMorphism { type In = G#Out },
      S <: AnyGraphMorphism { type In = F#Out; type Out = H#Out },
      M <: AnyGraphMorphism { type In = F#In; type Out = H#Out }
    ](implicit
      rewrGH: Rewrite[G >=> H, S],
      rewrFS: Rewrite[F >=> S, M]
    ):  RewriteFrom[rs.type, (F >=> G) >=> H, M] =
    new RewriteFrom[rs.type, (F >=> G) >=> H, M]({ fg_h =>

      val fg = fg_h.first
      val f = fg.first
      val g = fg.second
      val h  = fg_h.second

      rewrFS(f >=> (rewrGH(g >=> h)))
    })
  }
}
