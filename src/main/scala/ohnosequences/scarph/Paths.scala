package ohnosequences.scarph

import ohnosequences.cosas._, types._

import AnyEvalPath._

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
  type In <: AnyContainerType
  val  in: In

  type InC = In#Container
  val  inC = in.container
  type InT = In#Of
  val  inT = in.of

  /* Output */
  type Out <: AnyContainerType
  val  out: Out

  type OutC = Out#Container
  val  outC = out.container
  type OutT = Out#Of
  val  outT = out.of

  // NOTE: we will need to forget about these bounds at some point
  // type Rev <: AnyPath { type In <: path.Out; type Out <: path.In }
}

/* Important aliases which combine input/output arity container with its label type */
object paths {

  // type InOf[P <: AnyPath] = P#In //C#Of[P#InT]
  // type OutOf[P <: AnyPath] = P#Out //C#Of[P#OutT]

  def inOf[P <: AnyPath](p: P): P#In = p.in //C(p.inT)
  def outOf[P <: AnyPath](p: P): P#Out = p.out //C(p.outT)
}

/* A _step_ is a simple atomic _path_ which can be evaluated directly.
   Note that it always has form "ExactlyOne to something". */
trait AnyStep extends AnyPath {

  type In <: ExactlyOneOf[_]
  // type InC <: ExactlyOne.type
  // val  inC <: ExactlyOne
}

abstract class Step[
  IT <: AnyGraphType,
  OC <: AnyContainer,
  OT <: AnyGraphType
](iT: IT,
  oC: OC,
  oT: OT
) extends AnyStep {

  type In = ExactlyOne.Of[IT]
  val  in = ExactlyOne(iT)

  type Out = OC#Of[OT]
  val  out = oC(oT): Out
}

/* See available combinators in [Combinators.scala] */
trait AnyCombinator extends AnyPath


/* Adding useful methods */
object AnyPath {

  implicit def pathOps[T <: AnyPath](t: T) = PathOps(t)
}

case class PathOps[P <: AnyPath](val p: P) {
  import paths._

  // val in: InOf[P] = inOf(p)
  // val out: OutOf[P] = outOf(p)

  // it's left here and not moved to syntax, because using syntax you shouldn't need it
  def >=>[S <: AnyPath { type In = P#Out }](s: S): Composition[P, S] = Composition(p, s)

  def evalOn[I, O](input: I Denotes P#In)
    (implicit eval: EvalPathOn[I, P, O]): O Denotes P#Out = eval(p)(input)
}
