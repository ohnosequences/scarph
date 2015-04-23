package ohnosequences.scarph.syntax


/* This is an example gremlin-like syntax for paths construction */
object graphTypes {

  import scalaz.\/

  import ohnosequences.cosas._, types._

  import ohnosequences.{ scarph => s }
  import s.objects._

  implicit def graphObjectValOps[F <: AnyGraphObject, VF](vt: F := VF):
        GraphObjectValOps[F, VF] =
    new GraphObjectValOps[F, VF](vt)

  class GraphObjectValOps[F <: AnyGraphObject, VF](vt: F := VF) {

    // (F := t) ⊗ (S := s) : (F ⊗ S) := (t, s)
    def ⊗[S <: AnyGraphObject, VS](vs: S := VS): (F ⊗ S) := (VF, VS) =
      new Denotes( (vt.value, vs.value) )

    // (F := t) ⊕ (S := s) : (F ⊕ S) := (t, s)
    def ⊕[S <: AnyGraphObject, VS](vs: S := VS): (F ⊕ S) := (VF, VS) =
      new Denotes( (vt.value, vs.value) )
  }
}
