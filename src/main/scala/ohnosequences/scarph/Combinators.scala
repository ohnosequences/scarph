package ohnosequences.scarph

trait AnyPathCombinator {}

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

case class Par[F <: AnyPath, S <: AnyPath](val first: F, val second: S) extends AnyParPath {

  type First = F
  type Second = S

  type InT = ParV[First#In, Second#In]
  lazy val inT: ParV[First#In, Second#In] = ParV(first.in, second.in)
  type In = ParV[First#In, Second#In]
  lazy val in: ParV[First#In, Second#In] = ParV(first.in, second.in)

  type OutT = ParV[First#Out, Second#Out]
  lazy val outT: OutT = ParV(first.out, second.out)
  type Out = ParV[First#Out, Second#Out]
  lazy val out: Out = ParV(first.out, second.out)
}