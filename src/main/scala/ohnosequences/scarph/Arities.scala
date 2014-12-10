package ohnosequences.scarph


trait AnyConstructor {

  type C[X <: AnyLabelType] <: AnyLabelType
  def apply[Y <: AnyLabelType](y: Y): C[Y]
}

object ExactlyOne extends AnyConstructor { 

  final type C[X <: AnyLabelType] = X
  def apply[Y <: AnyLabelType](y: Y): Y = y            
}

object OneOrNone extends AnyConstructor  { 

  type C[X <: AnyLabelType] = OneOrNoneOf[X]  
  def apply[Y <: AnyLabelType](y: Y) = OneOrNoneOf(y)
}

object ManyOrNone extends AnyConstructor { 

  type C[X <: AnyLabelType] = ManyOrNoneOf[X] 
  def apply[Y <: AnyLabelType](y: Y) = ManyOrNoneOf(y)
}

object AtLeastOne extends AnyConstructor { 

  type C[X <: AnyLabelType] = AtLeastOneOf[X] 
  def apply[Y <: AnyLabelType](y: Y) = AtLeastOneOf(y)
}


trait AnyContainer extends AnyLabelType {

  type Of <: AnyLabelType
  val  of: Of
}

trait Of[T <: AnyLabelType] extends AnyContainer {

  type Of = T
}


final case class OneOrNoneOf[T <: AnyLabelType](val of: T) extends Of[T] {

  lazy val label = s"OneOrNone(${of.label})"
}

final case class ManyOrNoneOf[T <: AnyLabelType](val of: T) extends Of[T] {

  lazy val label = s"ManyOrNone(${of.label})"
}

final case class AtLeastOneOf[T <: AnyLabelType](val of: T) extends Of[T] {
  
  lazy val label = s"AtLeastOne(${of.label})"
}
