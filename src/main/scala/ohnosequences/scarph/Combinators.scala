package ohnosequences.scarph

trait AnyPathCombinator {


}


trait AnyParPath {

  type First <: AnyPath
  val first: First
  type Second <: AnyPath
  val second: Second

  type InT = ParV[first.InT, second.InT]
  val inT = ParV(first.inT, second.inT)

  type In = ParV[first.In, second.In]
  val In = ParV(first.in, second.in)

  type OutT = ParV[first.OutT, second.OutT]
  val outT = ParV(first.outT, second.outT)

  type Out = ParV[first.Out, second.Out]
  val Out = ParV(first.out, second.out)
}

case class Par[F <: AnyPath, S <: AnyPath](val first: F, val second: S) extends AnyParPath {

  type First = F
  type Second = S
}