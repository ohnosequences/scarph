package ohnosequences.scarph

import ohnosequences.cosas._

trait AnyPath extends HasOutArity {

  type InT <: AnyLabelType
  val  inT: InT

  type OutT <: AnyLabelType
  val  outT: OutT
}

trait AnyWrappedPath extends AnyPath {

  type OutT <: AnyContainer

  type WrappedByOut = OutT#Of
}

abstract class Path[I <: AnyLabelType, O <: AnyLabelType](val inT: I, val outT: O) extends AnyPath {

  type InT = I
  type OutT = O
}


/* A step is a minimal unit of path */
trait AnyStep extends AnyPath

abstract class Step[I <: AnyLabelType, O <: AnyLabelType](i: I, o: O) 
  extends Path[I, O](i, o) with AnyStep

/* Path is a composition of other paths */
trait AnyComposition extends AnyPath {

  type First <: AnyPath
  val  first: First

  type Second <: AnyPath //{ type InT = First#OutT }
  val  second: Second

  type InT = First#InT
  val  inT = first.inT

  type OutT = Second#OutT
  val  outT = second.outT

  // should be provided implicitly:
  val canCompose: Composable[First, Second]
}

class Compose[F <: AnyPath, S <: AnyPath, A <: AnyArity]
  (val first: F, val second: S)
  (implicit val canCompose: Composable[F, S] { type Out = A })
  extends AnyComposition {

  type First = F
  type Second = S

  type OutArity = A
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
case class MapExactlyOne[
  X0 <: AnyLabelType,
  M <: AnyPath { type InT = X0 }
](val inT: exactlyOne[X0], val mapped: M) extends AnyMapExactlyOne {

  type X = X0
  type Mapped = M
  type OutT = exactlyOne[M#OutT]

  val outT: exactlyOne[M#OutT] = inT(mapped.outT)
}

object AnyPath {

  implicit def pathOps[T <: AnyPath](t: T): PathOps[T] = new PathOps(t)

  implicit def mapExactlyOneOps[X <: AnyLabelType, P <: AnyWrappedPath { type OutT = exactlyOne[X] }](p: P): exactlyOneMapOps[P#WrappedByOut,P] = exactlyOneMapOps[P#WrappedByOut,P](p)

  implicit def mapOps[
    R <: AnyEdgeType { type InC[X <: AnyLabelType] = exactlyOne[X] }
  ](in:In[R] { type OutT = exactlyOne[R] }): exactlyOneMapOps[R, In[R]] = 
    exactlyOneMapOps[R, In[R]](in)
}

/* This checks that the paths are composable and multiplies their out-arities */
@annotation.implicitNotFound(msg = "Can't compose ${F} with ${S}")
trait Composable[F <: AnyPath, S <: AnyPath] extends AnyFn with OutBound[AnyArity] {
  val mutlipliedArities: (F#OutArity x S#OutArity)
}

object Composable {

  implicit def can[F <: AnyPath, S <: AnyPath { type InT = F#OutT }, A <: AnyArity]
    (implicit m: (F#OutArity x S#OutArity) { type Out = A }): 
        Composable[F, S] with Out[A] =
    new Composable[F, S] with Out[A] { val mutlipliedArities = m }
}

class PathOps[F <: AnyPath](val t: F) {

  /* Just a synonym for composition */
  def >=>[S <: AnyPath, A <: AnyArity](s: S)
    (implicit c: Composable[F, S] { type Out = A }): 
        Compose[F, S, A] = 
    new Compose[F, S, A](t, s)(c)

  def evalOn[I, O, PackedOut](i: I LabeledBy F#InT)
    (implicit 
      ev: EvalPath[I, F, O],
      pack: Pack[O LabeledBy F#OutT, F#OutArity] { type Out = PackedOut }
    ): PackedOut = pack(ev(i, t))
}

case class exactlyOneMapOps[X <: AnyLabelType, P <: AnyPath { type OutT = exactlyOne[X] }](val p: P) {

  def map[F <: AnyPath { type InT = X }](f: F): MapExactlyOne[X, F] = MapExactlyOne(p.outT, f)
}