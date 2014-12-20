package ohnosequences.scarph

import paths._

object combinators {

  type >=>[F <: AnyPath, S <: AnyPath { type In = F#Out }] = Composition[F, S]
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

  type In = First#In
  val  in = first.in
  // type InT = First#InT
  // val  inT = first.inT
  // type InC = First#InC
  // val  inC = first.inC

  type Out = Second#Out
  val  out = second.out 
  // type OutT = Second#OutT
  // val  outT = second.outT 
  // type OutC = Second#OutC
  // val  outC = second.outC
}

case class Composition[F <: AnyPath, S <: AnyPath { type In = F#Out }]
  (val first: F, val second: S) extends AnyComposition {

  type First = F
  type Second = S
}

/* Mapping a Path over a container */
trait AnyMapOver extends CombinatorOf1 {

  type Path <: AnyPath { type InC = ExactlyOne.type }

  type Container <: AnyContainer
  val  container: Container

  type In = Container#Of[Path#In]
  val  in = container(path.in): In
  // type InT = Path#InT
  // val  inT = path.inT
  // type InC = Container
  // val  inC = container

  type Out = Container#Of[Path#Out]
  val  out = container(path.out)
  // type OutT = OutOf[Path]
  // val  outT = outOf(path)
  // type OutC = Container
  // val  outC = container
}

case class MapOver[P <: AnyPath { type InC = ExactlyOne.type }, C <: AnyContainer]
  (val path: P, val container: C) extends AnyMapOver {

  type Path = P
  type Container = C
}


/* Mapping a Path over a container */
trait AnyFlatten extends CombinatorOf1 {

  type Path <: AnyPath { type Out <: AnyContainerType { type Of <: AnyContainerType } }

  type In = Path#In
  val  in = path.in
  // type InT = Path#InT
  // val  inT = path.inT
  // type InC = Path#InC
  // val  inC = path.inC

  // type OutT = Path#OutT#Of
  // val  outT = path.outT.of

  // we will get OutC through this implicit on construction:
  val mul: Path#OutC x Path#OutT#Container
}

case class Flatten[P <: AnyPath { type Out <: AnyContainerType { type Of <: AnyContainerType } }, C <: AnyContainer]
  (val path: P)
  (implicit val mul: (P#OutC x P#OutT#Container) { type Out = C })
  extends AnyFlatten {

    type Path = P 

    type Out = C#Of[Path#OutT#Of]
    val  out = mul(path.outC, path.outT.container)(path.outT.of)

    // type OutC = C
    // val  outC = mul(path.outC, path.outT.container)
  }


/* Parallel composition of paths */
trait AnyPar extends CombinatorOf2 {

  type In = ExactlyOne.Of[ParV[First#In, Second#In]]
  val  in = ExactlyOne(ParV(first.in, second.in))
  // type InT = ParV[InOf[First], InOf[Second]]
  // val  inT = ParV(inOf(first), inOf(second))
  // type InC = ExactlyOne.type
  // val  inC = ExactlyOne 

  type OutT = ExactlyOne.Of[ParV[First#Out, Second#Out]]
  val  outT = ExactlyOne(ParV(first.out, second.out))
  // type OutT = ParV[OutOf[First], OutOf[Second]]
  // val  outT = ParV(outOf(first), outOf(second))
  // type OutC = ExactlyOne.type
  // val  outC = ExactlyOne
}

case class Par[F <: AnyPath, S <: AnyPath]
  (val first: F, val second: S) extends AnyPar {

  type First = F
  type Second = S
}


/* Choice */
trait AnyOr extends CombinatorOf2 {

  type In = ExactlyOne.Of[OrV[First#In, Second#In]]
  val  in = ExactlyOne(OrV(first.in, second.in))
  // type InT = OrV[InOf[First], InOf[Second]]
  // val  inT = OrV(inOf(first), inOf(second))
  // type InC = ExactlyOne.type
  // val  inC = ExactlyOne 

  type OutT = ExactlyOne.Of[OrV[First#Out, Second#Out]]
  val  outT = ExactlyOne(OrV(first.out, second.out))
  // type OutT = OrV[OutOf[First], OutOf[Second]]
  // val  outT = OrV(outOf(first), outOf(second))
  // type OutC = ExactlyOne.type
  // val  outC = ExactlyOne
}

case class Or[F <: AnyPath, S <: AnyPath]
  (val first: F, val second: S) extends AnyOr {

  type First = F
  type Second = S
}
