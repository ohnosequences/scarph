package ohnosequences.scarph

import ohnosequences.cosas._

import AnyEvalPath._

trait AnyPath { path =>

  /* the wrapped type */
  type InT <: AnyLabelType
  val  inT: InT
  /* the container used */
  type InC <: AnyConstructor
  val  inC: InC

  type In <: AnyLabelType//<: InC#C[InT]
  val  in: In

  type OutT <: AnyLabelType
  val  outT: OutT
  type OutC <: AnyConstructor
  val  outC: OutC

  type Out <: AnyLabelType//<: OutC#C[OutT]
  val  out: Out

  // we will need to forget about these bounds at some point
  type Rev <: AnyPath { type In <: path.Out; type Out <: path.In }
}

/* a composition of other paths */
trait AnyComposition extends AnyPath { comp =>

  type First <: AnyPath
  val  first: First

  type InT = First#InT
  lazy val inT = first.inT
  type InC = First#InC
  lazy val inC = first.inC
  type In = First#In
  lazy val in = first.in

  type Second <: AnyPath
  val  second: Second

  type OutT = Second#OutT
  lazy val outT = second.outT 
  type OutC = Second#OutC
  lazy val outC = second.outC
  type Out = Second#Out
  lazy val out = second.out
}

case class Composition[
  F <: AnyPath,
  G <: AnyPath //{ type In = F#Out }
](val first: F, val second: G) extends AnyComposition {

  type First = F
  type Second = G

  type Rev = Composition[G#Rev, F#Rev]

  // def evalOn[I,X,O](input: I LabeledBy In)(implicit
  //   evalComp: EvalComposition[I,First,Second,X,O]
  // ): O LabeledBy Out = {

  //   evalComp(this)(input)
  // }
}
object Composition {

  implicit def compOps[F <: AnyPath, G <: AnyPath {type In = F#Out}](comp: Composition[F,G]): CompositionOps[F,G] = 
    CompositionOps(comp)
}
case class CompositionOps[F <: AnyPath, G <: AnyPath { type In = F#Out}](comp: Composition[F,G]) {

  def evalOn[I,X,O](input: I LabeledBy Composition[F,G]#In)(implicit
    evalComp: EvalComposition[I,F,G,X,O]
  ): O LabeledBy Composition[F,G]#Out = {

    evalComp(comp)(input)
  }
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
  type In = PrevPath#In
  lazy val in: In = prevPath.in


  // the path being mapped should have as In the wrapped type
  type MappedPath <: AnyPath //{ type In = PrevPath#OutT }
  val  mappedPath: MappedPath

  type OutT = MappedPath#Out
  lazy val outT: OutT = mappedPath.out
  type OutC = PrevPath#OutC
  lazy val outC = prevPath.outC
  type Out = PrevPath#OutC#C[MappedPath#Out]
  lazy val out: Out = prevPath.outC(mappedPath.out)
}

case class Map[P <: AnyPath, M <: AnyPath { type In = P#OutT }]
  (val prevPath: P, val mappedPath: M) extends AnyMapPath {

  type PrevPath = P
  type MappedPath = M
}


object AnyPath {

  implicit def pathOps[T <: AnyPath](t: T) = PathOps(t)
}

case class PathOps[P <: AnyPath](val p: P) {

  def >=>[S <: AnyPath { type In = P#Out }](s: S): Composition[P,S] = Composition(p,s)
  // TODO: add witnesses for composition to workaround P <:!< P { type In = P#In }
  // def ∘[F <: AnyPath { type Out = P#In }](f: F): Composition[F,P] 
  def map[G <: AnyPath { type In = P#OutT }](g: G): Map[P,G] = ohnosequences.scarph.Map[P,G](p,g)

  import combinators._

  def ⨁[G <: AnyPath](g: G): (P ⨁ G) = Or(p,g)
  def ⨂[G <: AnyPath](g: G): (P ⨂ G) = Par(p,g)
  def rev: rev[P] = Rev(p)
}