package ohnosequences.scarph.syntax


/* This is an example gremlin-like syntax for paths construction */
object graphTypes {

  import scalaz.\/

  import ohnosequences.cosas._, types._

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.combinators._


  implicit def graphTypeValOps[F <: AnyGraphType, VF](vt: F := VF):
        GraphTypeValOps[F, VF] =
    new GraphTypeValOps[F, VF](vt)

  class GraphTypeValOps[F <: AnyGraphType, VF](vt: F := VF) {

    // (F := t) ⊕ (S := s) : (F ⊕ S) := (t, s)
    def ⊕[S <: AnyGraphType, VS](vs: S := VS): (F ⊕ S) := (VF, VS) = 
      new Denotes( (vt.value, vs.value) )
  }

}
