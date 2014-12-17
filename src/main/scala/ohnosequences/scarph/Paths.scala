package ohnosequences.scarph

import ohnosequences.cosas._

import AnyEvalPath._

/* 
  _Path_ describes some graph traversal. It contains of steps that are combined in various ways.

  Note that `AnyPath` hierarchy is sealed, meaning that a path is either a step or a combinator.
  You can create steps and combinators extending `AnyStep` and `AnyCombinator` correspondingly.

  In the following code there is a naming convention:
  - `C` suffix means (arity) _Container_
  - `T` suffix means (label) _Type_
*/
sealed trait AnyPath {

  /* Input */
  type InC <: AnyContainer
  val  inC: InC
  type InT <: AnyLabelType
  val  inT: InT

  /* Output */
  type OutC <: AnyContainer
  val  outC: OutC
  type OutT <: AnyLabelType
  val  outT: OutT

  // NOTE: we will need to forget about these bounds at some point
  // type Rev <: AnyPath { type In <: path.Out; type Out <: path.In }
}

/* Important aliases which combine input/output arity container with its label type */
object paths {

  type InOf[P <: AnyPath] = P#InC#Of[P#InT]
  type OutOf[P <: AnyPath] = P#OutC#Of[P#OutT]

  def inOf[P <: AnyPath](p: P): InOf[P] = p.inC(p.inT)
  def outOf[P <: AnyPath](p: P): OutOf[P] = p.outC(p.outT)
}

/* A _step_ is a simple atomic _path_ which can be evaluated directly.
   Note that it always has form "ExactlyOne to something". */
trait AnyStep extends AnyPath {

  type InC = ExactlyOne.type
  val  inC = ExactlyOne
}

abstract class Step[
  IT <: AnyLabelType,
  OC <: AnyContainer,
  OT <: AnyLabelType
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

// ok, this is not a step, but it's similar
case class Flatten[IC <: AnyContainer, T <: AnyContainerType, OC <: AnyContainer](val ic: IC, val t: T)
  (implicit val mul: (IC x T#Container) { type Out = OC })
  extends AnyPath {

  type InC = IC
  val  inC = ic
  type InT = T
  val  inT = t

  type OutC = OC
  val  outC = mul(ic, t.container)
  type OutT = T#Of
  val  outT = t.of

}


/* Adding useful methods */
object AnyPath {

  implicit def pathOps[T <: AnyPath](t: T) = PathOps(t)
}

case class PathOps[P <: AnyPath](val p: P) {
  import paths._

  // val in: InOf[P] = inOf(p)
  // val out: OutOf[P] = outOf(p)

  // it's left here and not moved to syntax, because using syntax you shouldn't need it
  def >=>[S <: AnyPath { type InC = P#OutC; type InT = P#OutT }](s: S): Composition[P, S] = Composition(p, s)

  def evalOn[I, O](input: I LabeledBy InOf[P])
    (implicit eval: EvalPathOn[I, P, O]): O LabeledBy OutOf[P] = eval(p)(input)
}
