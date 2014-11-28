package ohnosequences.scarph

import ohnosequences.cosas._

trait AnyConstructor {

  type C[X <: AnyLabelType] <: AnyLabelType

  def apply[Y <: AnyLabelType](y: Y): C[Y]
}
object ExactlyOne extends AnyConstructor { 

  final type C[X <: AnyLabelType] = X
  def apply[Y <: AnyLabelType](y: Y): Y = y            
}
object OneOrNone extends AnyConstructor  { 

  type C[X <: AnyLabelType] = oneOrNone[X]  
  def apply[Y <: AnyLabelType](y: Y) = oneOrNone(y)
}
object ManyOrNone extends AnyConstructor { 

  type C[X <: AnyLabelType] = manyOrNone[X] 
  def apply[Y <: AnyLabelType](y: Y) = manyOrNone(y)
}
object AtLeastOne extends AnyConstructor { 

  type C[X <: AnyLabelType] = atLeastOne[X] 
  def apply[Y <: AnyLabelType](y: Y) = atLeastOne(y)
}

trait AnyContainer extends AnyLabelType {

  type Of <: AnyLabelType
  val of: Of
}

trait Of[T <: AnyLabelType] extends AnyContainer {

  type Of = T
}
final case class oneOrNone[T <: AnyLabelType](val of: T) extends Of[T] {

  lazy val label = s"oneOrNone(${of.label})"
}

final case class manyOrNone[T <: AnyLabelType](val of: T) extends Of[T] {

  lazy val label = s"manyOrNone(${of.label})"
}
final case class atLeastOne[T <: AnyLabelType](val of: T) extends Of[T] {
  
  lazy val label = s"atLeastOne(${of.label})"
}