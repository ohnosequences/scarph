package ohnosequences.scarph

import AnyPath._

object combinators {

  type ⨁[F <: AnyPath, S <: AnyPath] = Or[F,S]
  type ⨂[F <: AnyPath, S <: AnyPath] = Par[F,S]
  type rev[P <: AnyPath] = Rev[P]

  object rev { def apply[P <: AnyPath](p: P): rev[P] = Rev(p) }
}


trait AnyParPath extends AnyPath {

  type First <: AnyPath
  val  first: First

  type Second <: AnyPath
  val  second: Second

  type InT = ParV[InOf[First], InOf[Second]]
  lazy val inT: InT = ParV(first.in, second.in)
  type InC = ExactlyOne.type
  lazy val inC: InC = ExactlyOne 

  type OutT = ParV[OutOf[First], OutOf[Second]]
  lazy val outT: OutT = ParV(first.out, second.out)
  type OutC = ExactlyOne.type
  lazy val outC: OutC = ExactlyOne
}

case class Par[F <: AnyPath, S <: AnyPath] (val first: F, val second: S) extends AnyParPath {

  type First = F
  type Second = S

  // type Rev <: Par[F#Rev, S#Rev] // ParV[First#Rev#In <: First#Out, Second#Rev#In <: Second#Out]
}


trait AnyOrPath extends AnyPath {

  type First <: AnyPath
  val  first: First

  type Second <: AnyPath
  val  second: Second

  type InT = OrV[InOf[First], InOf[Second]]
  lazy val inT: InT = OrV(first.in, second.in)
  type InC = ExactlyOne.type
  lazy val inC: InC = ExactlyOne 

  type OutT = OrV[OutOf[First], OutOf[Second]]
  lazy val outT: OutT = OrV(first.out, second.out)
  type OutC = ExactlyOne.type
  lazy val outC: OutC = ExactlyOne
}

case class Or[F <: AnyPath, S <: AnyPath] (val first: F, val second: S) extends AnyOrPath {

  type First = F
  type Second = S
}


trait AnyRevPath extends AnyPath {

  type Original <: AnyPath
  val  original: Original

  type InT = Original#OutT
  lazy val inT: InT = original.outT
  type InC = Original#OutC
  lazy val inC: InC = original.outC

  type OutT = Original#InT
  lazy val outT: OutT = original.inT
  type OutC = Original#InC
  lazy val outC: OutC = original.inC
}

trait RevPath[P <: AnyPath] extends AnyRevPath { type Original = P }

case class Rev[P <: AnyPath] (val original: P) extends RevPath[P]
