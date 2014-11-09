package ohnosequences.scarph

trait AnyPath extends HasOutArity {

  type InT <: AnyLabelType
  val  inT: InT

  type OutT <: AnyLabelType
  val  outT: OutT
}

abstract class Path[I <: AnyLabelType, O <: AnyLabelType](val inT: I, val outT: O) extends AnyPath {

  type InT = I
  type OutT = O
}


trait AnyStep extends AnyPath

abstract class Step[I <: AnyLabelType, O <: AnyLabelType](i: I, o: O) 
  extends Path[I, O](i, o) with AnyStep


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

abstract class Composition[F <: AnyPath, S <: AnyPath](val first: F, val second: S) extends AnyComposition {

  type First = F
  type Second = S
}

class Compose[F <: AnyPath, S <: AnyPath, A <: AnyArity] // { type InT = F#OutT }]
  (f: F, s: S)(implicit val canCompose: Composable[F, S] { type Out = A })
    extends Composition[F, S](f, s) {

  type OutArity = A
}


object AnyPath {

  implicit def traversalOps[T <: AnyPath](t: T): PathOps[T] = new PathOps(t)
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

  def >=>[S <: AnyPath, A <: AnyArity](s: S)
    (implicit c: Composable[F, S] { type Out = A }): 
        Compose[F, S, A] = 
    new Compose[F, S, A](t, s)(c)

  def evalOn[I, O, PO](i: I LabeledBy F#InT)
    (implicit 
      ev: EvalPath[I, F, O],
      // FIXME: !!! this is an important final step, it should be in some EvalEval thing
      pack: Pack[O LabeledBy F#OutT, F#OutArity] { type Out = PO }
    ): PO = pack(ev(i, t))
}
