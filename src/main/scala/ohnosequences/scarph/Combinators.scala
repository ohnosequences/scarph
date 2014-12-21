package ohnosequences.scarph

import paths._

object combinators {

  type >=>[F <: AnyPath, S <: AnyPath { type InC = F#OutC; type InT = F#OutT }] = Composition[F, S]
  type ⨁[F <: AnyPath, S <: AnyPath] = Or[F, S]
  type ⨂[F <: AnyPath, S <: AnyPath] = Par[F, S]
}

trait CombinatorOf1 extends AnyCombinator {

  type Path <: AnyPath
  val  path: Path
}

trait CombinatorOf2 extends AnyCombinator {

  type First <: AnyPath
  val  first: First

  type Second <: AnyPath
  val  second: Second
}


/* Sequential composition of two paths */
trait AnyComposition extends CombinatorOf2 {

  type InT = First#InT
  val  inT = first.inT
  type InC = First#InC
  val  inC = first.inC

  type OutT = Second#OutT
  val  outT = second.outT 
  type OutC = Second#OutC
  val  outC = second.outC
}

case class Composition[F <: AnyPath, S <: AnyPath { type InC = F#OutC; type InT = F#OutT }]
  (val first: F, val second: S) extends AnyComposition {

  type First = F
  type Second = S
}

/* Mapping a Path over a container */
trait AnyMapOver extends CombinatorOf1 {

  type Path <: AnyPath { type InC = ExactlyOne.type }

  type Container <: AnyContainer
  val  container: Container

  type InT = Path#InT
  val  inT = path.inT
  type InC = Container
  val  inC = container

  type OutT = OutOf[Path]
  val  outT = outOf(path)
  type OutC = Container
  val  outC = container
}

case class MapOver[P <: AnyPath { type InC = ExactlyOne.type }, C <: AnyContainer]
  (val path: P, val container: C) extends AnyMapOver {

  type Path = P
  type Container = C
}


/* Mapping a Path over a container */
trait AnyFlatten extends CombinatorOf1 {

  type Path <: AnyPath { type OutT <: AnyContainerType }

  type InT = Path#InT
  val  inT = path.inT
  type InC = Path#InC
  val  inC = path.inC

  type OutT = Path#OutT#Of
  val  outT = path.outT.of

  // we will get OutC through this implicit on construction:
  val mul: Path#OutC x Path#OutT#Container
}

case class Flatten[P <: AnyPath { type OutT <: AnyContainerType }, C <: AnyContainer]
  (val path: P)
  (implicit val mul: (P#OutC x P#OutT#Container) { type Out = C })
  extends AnyFlatten {

    type Path = P 

    type OutC = C
    val  outC = mul(path.outC, path.outT.container)
  }


/* Parallel composition of paths */
trait AnyPar extends CombinatorOf2 {

  type InT = ParV[InOf[First], InOf[Second]]
  val  inT = ParV(inOf(first), inOf(second))
  type InC = ExactlyOne.type
  val  inC = ExactlyOne 

  type OutT = ParV[OutOf[First], OutOf[Second]]
  val  outT = ParV(outOf(first), outOf(second))
  type OutC = ExactlyOne.type
  val  outC = ExactlyOne
}

case class Par[F <: AnyPath, S <: AnyPath]
  (val first: F, val second: S) extends AnyPar {

  type First = F
  type Second = S
}


/* Choice */
trait AnyOr extends CombinatorOf2 {

  type InT = OrV[InOf[First], InOf[Second]]
  val  inT = OrV(inOf(first), inOf(second))
  type InC = ExactlyOne.type
  val  inC = ExactlyOne 

  type OutT = OrV[OutOf[First], OutOf[Second]]
  val  outT = OrV(outOf(first), outOf(second))
  type OutC = ExactlyOne.type
  val  outC = ExactlyOne
}

case class Or[F <: AnyPath, S <: AnyPath]
  (val first: F, val second: S) extends AnyOr {

  type First = F
  type Second = S
}
