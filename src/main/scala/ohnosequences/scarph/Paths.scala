package ohnosequences.scarph

import ohnosequences.cosas._

trait AnyPath {

  type InT <: AnyLabelType
  val  inT: InT
  type InC[X <: AnyLabelType] <: Container[InC] with Of[X]
  type In = InC[InT]
  val in: In

  type OutT <: AnyLabelType
  val  outT: OutT
  type OutC[X <: AnyLabelType] <: Container[OutC] with Of[X]
  type Out = OutC[OutT]
  val out: Out
}

trait AnyWrappedPath extends AnyPath {

  type OutT <: AnyContainer

  type WrappedByOut = OutT#Of
}

class Path[
  I <: AnyLabelType,
  InC0[X <: AnyLabelType] <: Container[InC0] with Of[X], 
  O <: AnyLabelType,
  OutC0[Z <: AnyLabelType] <: Container[OutC0] with Of[Z]
]
(
  val in: InC0[I],
  val out: OutC0[O]
)
extends AnyPath {

  type InT = I
  val inT = in.of
  type InC[X <: AnyLabelType] = InC0[X]

  type OutT = O
  val outT = out.of
  type OutC[X <: AnyLabelType] = OutC0[X]
}


/* A step is a minimal unit of path */
// trait AnyStep extends AnyPath

// abstract class Step[I <: AnyLabelType, O <: AnyLabelType](i: I, o: O) 
//   extends Path[I, O](i, o) with AnyStep

/* Path is a composition of other paths */
trait AnyComposition extends AnyPath {

  type First <: AnyPath
  val  first: First

  type Second <: AnyPath //{ type InT = First#OutT }
  val  second: Second

  type InT <: First#InT
  
  type OutT <: Second#OutT

  // should be provided implicitly:
  // val canCompose: Composable[First, Second]
}

case class Compose[F <: AnyPath, S <: AnyPath]
  (val first: F, val second: S)
  // (implicit val canCompose: Composable[F, S])
  extends AnyComposition {

  type First = F
  type Second = S

  type InT = first.InT
  val inT = first.inT
  type InC[X <: AnyLabelType] = first.InC[X]
  // type In = first.In
  val in = first.in

  type OutT = second.OutT
  val outT = second.outT
  type OutC[X <: AnyLabelType] = second.OutC[X]
  // type Out = second.Out
  val out = second.out
}

/*
this represents mapping a Path over a container; the path should have InT/OutT matching what the container wraps
*/
trait AnyMapExactlyOne extends AnyPath { map =>

  type X <: AnyLabelType
  type InT = exactlyOne[X]

  type Mapped <: AnyPath { type InT = X }
  val mapped: Mapped

  type OutT <: exactlyOne[Mapped#OutT]
}

/*
  here we need

  1. the container value
  2. the path that is being mapped

  most likely it would be better to specialize for each container. This is just a hack.
*/
// case class MapExactlyOne[
//   X0 <: AnyLabelType,
//   M <: AnyPath { type InT = X0 }
// ](val inT: exactlyOne[X0], val mapped: M) extends AnyMapExactlyOne {

//   type X = X0
//   type Mapped = M
//   type OutT = exactlyOne[M#OutT]

//   val outT: exactlyOne[M#OutT] = inT(mapped.outT)
// }

object AnyPath {

  implicit def pathOps[T <: AnyPath](t: T): PathOps[T] = new PathOps(t)

  // implicit def mapExactlyOneOps[X <: AnyLabelType, P <: AnyWrappedPath { type OutT = exactlyOne[X] }](p: P): exactlyOneMapOps[P#WrappedByOut,P] = exactlyOneMapOps[P#WrappedByOut,P](p)

  // implicit def mapOps[
  //   R <: AnyEdgeType { type InC[X <: AnyLabelType] = exactlyOne[X] }
  // ](in:In[R] { type OutT = exactlyOne[R] }): exactlyOneMapOps[R, In[R]] = 
  //   exactlyOneMapOps[R, In[R]](in)
}

/* This checks that the paths are composable and multiplies their out-arities */
@annotation.implicitNotFound(msg = "Can't compose ${F} with ${S}")
trait Composable[F <: AnyPath, S <: AnyPath]

object Composable {

  implicit def can[F <: AnyPath, S <: AnyPath { type In <: F#Out }]:
        Composable[F, S] =
    new Composable[F, S] {}
}

class PathOps[F <: AnyPath](val t: F) {

  /* Just a synonym for composition */
  def >=>[S <: AnyPath { type In = t.Out }](s: S): Compose[F, S] = Compose[F, S](t, s)

  // def evalOn[I, O](i: I LabeledBy F#InT)
  //   (implicit 
  //     ev: EvalPath[I, F, O]
  //   ): O LabeledBy F#Out = ev(i, t)
}

// case class exactlyOneMapOps[X <: AnyLabelType, P <: AnyPath { type OutT = exactlyOne[X] }](val p: P) {

//   def map[F <: AnyPath { type InT = X }](f: F): MapExactlyOne[X, F] = MapExactlyOne(p.outT, f)
// }
