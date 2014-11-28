package ohnosequences.scarph

object combinators {

  type ⨁[F <: AnyPath, S <: AnyPath] = Or[F,S]
  type ⨂[F <: AnyPath, S <: AnyPath] = Par[F,S]
  type rev[P <: AnyPath]
}

trait AnyParPath extends AnyPath {

  type First <: AnyPath
  val first: First
  type Second <: AnyPath
  val second: Second

  type InT <: ParV[First#In, Second#In]
  val inT: InT
  type InC = ExactlyOne.type
  lazy val inC: InC = ExactlyOne 
  type In <: ParV[First#In, Second#In]
  val in: In

  type OutT <: ParV[First#Out, Second#Out]
  val outT: OutT
  type OutC = ExactlyOne.type
  lazy val outC: OutC = ExactlyOne
  type Out <: ParV[First#Out, Second#Out]
  val out: Out
}

trait AnyOrPath extends AnyPath {

  type First <: AnyPath
  val first: First
  type Second <: AnyPath
  val second: Second

  type InT <: OrV[First#In, Second#In]
  val inT: InT
  type InC = ExactlyOne.type
  lazy val inC: InC = ExactlyOne 
  type In <: OrV[First#In, Second#In]
  val in: In

  type OutT <: OrV[First#Out, Second#Out]
  val outT: OutT
  type OutC = ExactlyOne.type
  lazy val outC: OutC = ExactlyOne
  type Out <: OrV[First#Out, Second#Out]
  val out: Out
}

case class Par[F <: AnyPath, S <: AnyPath](val first: F, val second: S) extends AnyParPath {

  type First = F
  type Second = S

  type InT = ParV[First#In, Second#In]
  lazy val inT: InT = ParV(first.in, second.in)
  type In = ParV[First#In, Second#In]
  lazy val in: In = inT

  type OutT = ParV[First#Out, Second#Out]
  lazy val outT: OutT = ParV(first.out, second.out)
  type Out = ParV[First#Out, Second#Out]
  lazy val out: Out = outT
}

case class Or[F <: AnyPath, S <: AnyPath](val first: F, val second: S) extends AnyOrPath {

  type First = F
  type Second = S

  type InT = OrV[First#In, Second#In]
  lazy val inT: InT = OrV(first.in, second.in)
  type In = OrV[First#In, Second#In]
  lazy val in: In = inT

  type OutT = OrV[First#Out, Second#Out]
  lazy val outT: OutT = OrV(first.out, second.out)
  type Out = OrV[First#Out, Second#Out]
  lazy val out: Out = outT
}

case class Rev[F <: AnyPath](val original: F) extends AnyPath {

  type Original = F 

  type InT = Original#OutT
  lazy val inT: InT = original.outT
  type InC = Original#OutC
  lazy val inC: InC = original.outC
  type In = Original#Out
  lazy val in: In = original.out

  type OutT = Original#InT
  lazy val outT: OutT = original.inT
  type OutC = Original#InC
  lazy val outC: OutC = original.inC

  type Out = Original#In
  lazy val out: Out = original.in
}