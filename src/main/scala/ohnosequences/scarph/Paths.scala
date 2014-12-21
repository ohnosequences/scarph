package ohnosequences.scarph

object paths {

  import ohnosequences.cosas._, types._
  import graphTypes._, containers._


  /* 
    _Path_ describes some graph traversal. It contains of steps that are combined in various ways.

    Note that `AnyPath` hierarchy is sealed, meaning that a path is either a step or a combinator.
    You can create steps and combinators extending `AnyStep` and `AnyPathCombinator` correspondingly.

    In the following code there is a naming convention:
    - `C` suffix means (arity) _Container_
    - `T` suffix means (label) _Type_
  */
  sealed trait AnyPath {

    /* Input */
    type In <: AnyGraphType
    val  in: In

    /* Output */
    type Out <: AnyGraphType
    val  out: Out

    // TODO: add Reverse member
  }

  def inOf[P <: AnyPath](p: P): P#In = p.in
  def outOf[P <: AnyPath](p: P): P#Out = p.out

  /* A _step_ is a simple atomic _path_ which can be evaluated directly.
     Note that it always has form "ExactlyOne to something". */
  trait AnyStep extends AnyPath {

    type In <: AnyPlainGraphType
  }

  abstract class Step[
    I <: AnyPlainGraphType,
    O <: AnyGraphType
  ](i: I, o: O) extends AnyEdge {

    type In = I
    val  in = i

    type Out = O
    val  out = o
  }


  /* See available combinators in [Combinators.scala] */
  trait AnyPathCombinator extends AnyPath


  /* Adding useful methods */
  object AnyPath {

    implicit def pathOps[T <: AnyPath](t: T) = PathOps(t)
  }

  case class PathOps[P <: AnyPath](val p: P) {

    import combinators._
    // it's left here and not moved to syntax, because using syntax you shouldn't need it
    def >=>[S <: AnyPath { type In = P#Out }](s: S): Composition[P, S] = Composition(p, s)

    import evals._
    def evalOn[I, O](input: I Denotes P#In)
      (implicit eval: EvalPathOn[I, P, O]): O Denotes P#Out = eval(p)(input)
  }

}
