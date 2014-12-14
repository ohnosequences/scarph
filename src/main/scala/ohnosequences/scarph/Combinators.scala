package ohnosequences.scarph

import paths._

object combinators {

  type ⨁[F <: AnyPath, S <: AnyPath] = Or[F,S]
  type ⨂[F <: AnyPath, S <: AnyPath] = Par[F,S]

  // type rev[P <: AnyPath] = Rev[P]
  // object rev { def apply[P <: AnyPath](p: P): rev[P] = Rev(p) }
}


/* Sequential composition of two paths */
trait AnyComposition extends AnyCombinator {

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
trait AnyMap extends AnyCombinator {

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

  type OutT = OutOf[MappedPath]
  lazy val outT: OutT = mappedPath.outC(mappedPath.outT)
  type OutC = PrevPath#OutC
  lazy val outC = prevPath.outC
}

case class Map[P <: AnyPath, M <: AnyPath { type In = P#OutT }]
  (val prevPath: P, val mappedPath: M) extends AnyMap {

  type PrevPath = P
  type MappedPath = M
}


/* Parallel composition of paths */
trait AnyPar extends AnyCombinator {

  type First <: AnyPath
  val  first: First

  type Second <: AnyPath
  val  second: Second

  type InT = ParV[InOf[First], InOf[Second]]
  lazy val inT: InT = ParV(inOf(first), inOf(second))
  type InC = ExactlyOne.type
  lazy val inC: InC = ExactlyOne 

  type OutT = ParV[OutOf[First], OutOf[Second]]
  lazy val outT: OutT = ParV(outOf(first), outOf(second))
  type OutC = ExactlyOne.type
  lazy val outC: OutC = ExactlyOne
}

case class Par[F <: AnyPath, S <: AnyPath] (val first: F, val second: S) extends AnyPar {

  type First = F
  type Second = S

  // type Rev <: Par[F#Rev, S#Rev] // ParV[First#Rev#In <: First#Out, Second#Rev#In <: Second#Out]
}


/* Choice */
trait AnyOr extends AnyCombinator {

  type First <: AnyPath
  val  first: First

  type Second <: AnyPath
  val  second: Second

  type InT = OrV[InOf[First], InOf[Second]]
  lazy val inT: InT = OrV(inOf(first), inOf(second))
  type InC = ExactlyOne.type
  lazy val inC: InC = ExactlyOne 

  type OutT = OrV[OutOf[First], OutOf[Second]]
  lazy val outT: OutT = OrV(outOf(first), outOf(second))
  type OutC = ExactlyOne.type
  lazy val outC: OutC = ExactlyOne
}

case class Or[F <: AnyPath, S <: AnyPath] (val first: F, val second: S) extends AnyOr {

  type First = F
  type Second = S
}
