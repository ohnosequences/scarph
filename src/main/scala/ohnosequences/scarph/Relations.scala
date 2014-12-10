package ohnosequences.scarph

// for saying that two paths are equal
trait AnyRelation {

  type First <: AnyPath
  val  first: First
  type Second <: AnyPath
  val  second: Second
}

// TODO: proofs whatever for same In and Out
class Eq[F <: AnyPath, S <: AnyPath](val first: F, val second: S) extends AnyRelation {

  type First = F
  type Second = S
}

object Eq {

  // for example
  // case class inVEqinCompSrc[E <: AnyEdgeType](val edge: E) 
  //   extends Eq(
  //               inV(edge),
  //               in(edge) map src(edge)
  //             )
}
