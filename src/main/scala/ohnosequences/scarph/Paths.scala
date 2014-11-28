package ohnosequences.scarph

import ohnosequences.cosas._

import AnyEvalPath._

trait AnyPath { path =>

  /* the wrapped type */
  type InT <: AnyLabelType
  val  inT: InT
  /* the container used */
  type InC <: AnyConstructor
  val inC: InC

  type In <: AnyLabelType//<: InC#C[InT]
  val in: In

  type OutT <: AnyLabelType
  val  outT: OutT
  type OutC <: AnyConstructor
  val outC: OutC

  type Out <: AnyLabelType//<: OutC#C[OutT]
  val out: Out

  // def map[F <: AnyPath { type In = path.OutT }](f: F): Map[] = new AnyMap {

  //   type PrevPath = path.type
  //   val prevPath = path: path.type

  //   type MappedPath = F
  //   val mappedPath = f
  // }
}

abstract class Path[
  I <: AnyLabelType,
  InC0 <: AnyConstructor, 
  O <: AnyLabelType,
  OutC0 <: AnyConstructor
]
(
  val inC: InC0,
  val inT: I,
  val outC: OutC0,
  val outT: O
)
extends AnyPath {

  type InT = I
  type InC = InC0

  type OutT = O
  type OutC = OutC0
}

/* a composition of other paths */
trait AnyComposition extends AnyPath { comp =>

  type First <: AnyPath //{ type Out = Middle }
  val  first: First

  type Second <: AnyPath //{ type In = Middle }
  val  second: Second
}

case class Composition[
  F <: AnyPath, 
  G <: AnyPath { type In = F#Out }
](val first: F, val second: G) extends AnyComposition {

  type First = F
  type Second = G

  type InT = First#InT
  val inT = first.inT
  type InC = First#InC
  val inC = first.inC

  type In = First#In
  val in = first.in

  type OutT = Second#OutT
  val outT = second.outT 
  type OutC = Second#OutC
  val outC = second.outC//: second.outC.type
  type Out = Second#Out
  val out = second.out

  def evalOn[I,X,O](input: I LabeledBy In)(implicit
    evalComp: EvalComposition[I,First,Second,X,O]
  ): O LabeledBy Out = {

    evalComp(this)(input)
  }
}

/*
this represents mapping a Path over a container; the path should have InT/OutT matching what the container wraps.
*/
trait AnyMap extends AnyPath {

  // TODO add stuff from map
  type PrevPath <: AnyPath
  val prevPath: PrevPath
  // the path being mapped should have as In the wrapped type
  type MappedPath <: AnyPath //{ type In = PrevPath#OutT }
  val mappedPath: MappedPath
}

case class map[P <: AnyPath, M <: AnyPath { type In = P#OutT }](val prevPath: P, val mappedPath: M) extends AnyMap {

  type PrevPath = P
  type MappedPath = M

  type InT = PrevPath#InT
  val inT: InT = prevPath.inT
  type InC = PrevPath#InC
  val inC: InC = prevPath.inC
  type In = PrevPath#In
  val in: In = prevPath.in

  type OutT = MappedPath#Out
  val outT: OutT = mappedPath.out
  type OutC = PrevPath#OutC
  val outC = prevPath.outC

  type Out = PrevPath#OutC#C[MappedPath#Out]
  val out: Out = prevPath.outC(mappedPath.out)
}

object AnyPath {

  implicit def pathOps[T <: AnyPath](t: T) = PathOps(t)
}

case class PathOps[P <: AnyPath](val p: P) {

  /* Just a synonym for composition */
  def >=>[S <: AnyPath { type In = P#Out }](s: S): Composition[P,S] = Composition(p,s)

  def map[G <: AnyPath { type In = P#OutT }](g: G): map[P,G] = ohnosequences.scarph.map[P,G](p,g)
}