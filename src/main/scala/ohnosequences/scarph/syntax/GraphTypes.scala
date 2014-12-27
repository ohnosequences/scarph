package ohnosequences.scarph.syntax


/* This is an example gremlin-like syntax for paths construction */
object graphTypes {

  import scalaz.\/

  import ohnosequences.cosas._, types._

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.combinators._, s.containers._


  /* Graph/schema ops */
  implicit def graphTypeValOps[T <: AnyGraphType, VT](vt: T := VT):
        GraphTypeValOps[T, VT] =
    new GraphTypeValOps[T, VT](vt)

  class GraphTypeValOps[T <: AnyGraphType, VT](vt: T := VT) {

    def ⊗[S <: AnyGraphType, VS](vs: S := VS): ParType[T, S] := (VT, VS) = 
      new Denotes( (vt.value, vs.value) )
  }

}
