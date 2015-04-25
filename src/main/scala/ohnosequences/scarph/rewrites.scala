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
}