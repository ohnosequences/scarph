package ohnosequences.scarph

object paths {

  import ohnosequences.cosas._, types._
  import graphTypes._, containers._


  /* 
    _Path_ describes some graph traversal. It contains of steps that are combined in various ways.

    Note that `AnyPath` hierarchy is sealed, meaning that a path is either a step or a combinator.
    You can create steps and combinators extending `AnyStep` and `AnyCombinator` correspondingly.

    In the following code there is a naming convention:
    - `C` suffix means (arity) _Container_
    - `T` suffix means (label) _Type_
  */
  trait AnyPath {

    /* Input */
    type InC <: AnyContainer
    val  inC: InC
    type InT <: AnyGraphType
    val  inT: InT

    /* Output */
    type OutC <: AnyContainer
    val  outC: OutC
    type OutT <: AnyGraphType
    val  outT: OutT

    // TODO: add Reverse member
  }

  /* Important aliases which combine input/output arity container with its label type */
  type InOf[P <: AnyPath] = P#InC#Of[P#InT]
  type OutOf[P <: AnyPath] = P#OutC#Of[P#OutT]

  def inOf[P <: AnyPath](p: P): InOf[P] = p.inC(p.inT)
  def outOf[P <: AnyPath](p: P): OutOf[P] = p.outC(p.outT)


  /* A _step_ is a simple atomic _path_ which can be evaluated directly.
     Note that it always has form "ExactlyOne to something". */
  trait AnyStep extends AnyPath {

    type InC = ExactlyOne.type
    val  inC = ExactlyOne
  }

  abstract class Step[
    IT <: AnyGraphType,
    OC <: AnyContainer,
    OT <: AnyGraphType
  ](val inT: IT,
    val outC: OC,
    val outT: OT
  ) extends AnyStep {

    type InT = IT
    type OutC = OC
    type OutT = OT
  }


  /* See available combinators in [Combinators.scala] */
  trait AnyCombinator extends AnyPath


  /* Adding useful methods */
  object AnyPath {

    implicit def pathOps[T <: AnyPath](t: T) = PathOps(t)
  }

  case class PathOps[P <: AnyPath](val p: P) {

    val in: InOf[P] = inOf(p)
    val out: OutOf[P] = outOf(p)

    import combinators._
    // it's left here and not moved to syntax, because using syntax you shouldn't need it
    def >=>[S <: AnyPath { type InC = P#OutC; type InT = P#OutT }](s: S): Composition[P, S] = Composition(p, s)

    import evals._
    def evalOn[I, O](input: I Denotes InOf[P])
      (implicit eval: EvalPathOn[I, P, O]): O Denotes OutOf[P] = eval(p)(input)
  }

}
