package ohnosequences.scarph

import ohnosequences.cosas._

import AnyEvalPath._

trait AnyPath { path =>

  /* The container used */
  type InC <: AnyConstructor
  val  inC: InC
  /* The wrapped type */
  type InT <: AnyLabelType
  val  inT: InT

  /* Same for out: */
  type OutC <: AnyConstructor
  val  outC: OutC
  type OutT <: AnyLabelType
  val  outT: OutT

  // NOTE: we will need to forget about these bounds at some point
  // type Rev <: AnyPath { type In <: path.Out; type Out <: path.In }
}

abstract class Path[
  IC <: AnyConstructor,
  IT <: AnyLabelType,
  OC <: AnyConstructor,
  OT <: AnyLabelType
](val inC: IC,
  val inT: IT,
  val outC: OC,
  val outT: OT
) extends AnyPath {

  type InC = IC
  type InT = IT

  type OutC = OC
  type OutT = OT
}

/* A composition of two paths */
trait AnyComposition extends AnyPath {

  type First <: AnyPath
  val  first: First

  type InT = First#InT
  lazy val inT = first.inT
  type InC = First#InC
  lazy val inC = first.inC

  type Second <: AnyPath
  val  second: Second

  type OutT = Second#OutT
  lazy val outT = second.outT 
  type OutC = Second#OutC
  lazy val outC = second.outC
}

case class Composition[
  F <: AnyPath,
  S <: AnyPath //{ type In = F#Out }
](val first: F, val second: S) extends AnyComposition {

  type First = F
  type Second = S

  // type Rev = Composition[S#Rev, F#Rev]
}

/*
this represents mapping a Path over a container; the path should have InT/OutT matching what the container wraps.
*/
trait AnyMapPath extends AnyPath {

  // TODO add stuff from map
  type PrevPath <: AnyPath
  val  prevPath: PrevPath

  type InT = PrevPath#InT
  lazy val inT: InT = prevPath.inT
  type InC = PrevPath#InC
  lazy val inC: InC = prevPath.inC

  // the path being mapped should have as In the wrapped type
  type MappedPath <: AnyPath //{ type In = PrevPath#OutT }
  val  mappedPath: MappedPath

  type OutT = AnyPath.OutOf[MappedPath]
  lazy val outT: OutT = mappedPath.outC(mappedPath.outT)
  type OutC = PrevPath#OutC
  lazy val outC = prevPath.outC
}

case class Map[P <: AnyPath, M <: AnyPath { type In = P#OutT }]
  (val prevPath: P, val mappedPath: M) extends AnyMapPath {

  type PrevPath = P
  type MappedPath = M
}


object AnyPath {
  type InOf[P <: AnyPath] = P#InC#C[P#InT]
  type OutOf[P <: AnyPath] = P#OutC#C[P#OutT]

  implicit def pathOps[T <: AnyPath](t: T) = PathOps(t)
}

case class PathOps[P <: AnyPath](val p: P) {

  val in: AnyPath.InOf[P] = p.inC(p.inT)
  val out: AnyPath.OutOf[P] = p.outC(p.outT)

  def >=>[S <: AnyPath { type InC = P#OutC; type InT = P#OutT }](s: S): Composition[P,S] = Composition(p,s)
  // TODO: add witnesses for composition to workaround P <:!< P { type In = P#In }
  // def ∘[F <: AnyPath { type Out = P#In }](f: F): Composition[F,P] 
  def map[G <: AnyPath { type In = P#OutT }](g: G): Map[P,G] = ohnosequences.scarph.Map[P,G](p,g)

  import combinators._

  def ⨁[G <: AnyPath](g: G): (P ⨁ G) = Or(p,g)
  def ⨂[G <: AnyPath](g: G): (P ⨂ G) = Par(p,g)
  def rev: rev[P] = Rev(p)

  def evalOn[I, O](input: I LabeledBy P#InC#C[P#InT])
    (implicit eval: EvalPathOn[I, P, O]): O LabeledBy P#OutC#C[P#OutT] = eval(p)(input)
}
