package ohnosequences.scarph

import ohnosequences.cosas._

// NOTE: maybe arity is not a proper name for what we are defining here...
// sealed trait AnyArity {}

/* 4 things: one/many x non-empty/any */
// NOTE: don't know if the values of them are needed for anything
// TODO objects and aliases for their .type
// trait OneOrNone extends AnyArity // Option
// trait ExactlyOne extends AnyArity // Id
// trait ManyOrNone extends AnyArity // List
// trait AtLeastOne extends AnyArity // NEList

// trait HasInArity  { type InArity  <: AnyArity }
// trait HasOutArity { type OutArity <: AnyArity }

// trait InArity[A <: AnyArity] extends HasInArity { type InArity = A }
// trait OutArity[A <: AnyArity] extends HasOutArity { type OutArity = A }


// TODO Fn
/*
Containers are type constructors
*/
trait AnyContainer extends AnyLabelType {

  type Of <: AnyLabelType
  val of: Of
}

sealed trait Container[C[_ <: AnyLabelType] <: Container[C]] extends AnyContainer with AnyLabelType {

  def apply[Y <: AnyLabelType](y: Y): C[Y] with ohnosequences.scarph.Of[Y]
}

trait Of[T <: AnyLabelType] extends AnyContainer {

  type Of = T
}

final case class oneOrNone[T <: AnyLabelType](val of: T) extends Container[oneOrNone] with Of[T] {

  def apply[Y <: AnyLabelType](y: Y): oneOrNone[Y] = oneOrNone[Y](y)
  lazy val label = s"oneOrNone(${of.label})"

}
  
trait AnyExactlyOne extends AnyContainer
final case class exactlyOne[T <: AnyLabelType](val of: T) extends Container[exactlyOne] with Of[T] {

  def apply[X <: AnyLabelType](x: X): exactlyOne[X] = exactlyOne[X](x)
  lazy val label = s"exactlyOne(${of.label})"
}
final case class manyOrNone[T <: AnyLabelType](val of: T) extends Container[manyOrNone] with Of[T] {

  def apply[X <: AnyLabelType](x: X): manyOrNone[X] = manyOrNone[X](x)
  lazy val label = s"manyOrNone(${of.label})"
}
final case class atLeastOne[T <: AnyLabelType](val of: T) extends Container[atLeastOne] with Of[T] {
  
  def apply[X <: AnyLabelType](x: X): atLeastOne[X] = atLeastOne[X](x)
  lazy val label = s"atLeastOne(${of.label})"
}

object AnyContainer {

  implicit def oneOrNoneV[X <: AnyElementType](x: X): oneOrNone[X] = oneOrNone[X](x)
  implicit def exactlyOneV[X <: AnyElementType](x: X): exactlyOne[X] = exactlyOne[X](x)
}
