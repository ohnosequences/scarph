package ohnosequences.scarph


trait AnyConstructor {

  type C[T <: AnyLabelType] <: AnyLabelType
  def apply[T <: AnyLabelType](t: T): C[T]
}

object ExactlyOne extends AnyConstructor { 

  final type C[T <: AnyLabelType] = T
  def apply[T <: AnyLabelType](t: T): C[T] = t
}

object OneOrNone extends AnyConstructor  { 

  type C[T <: AnyLabelType] = OneOrNoneOf[T]  
  def apply[T <: AnyLabelType](t: T): C[T] = OneOrNoneOf(t)
}

object ManyOrNone extends AnyConstructor { 

  type C[T <: AnyLabelType] = ManyOrNoneOf[T] 
  def apply[T <: AnyLabelType](t: T): C[T] = ManyOrNoneOf(t)
}

object AtLeastOne extends AnyConstructor { 

  type C[T <: AnyLabelType] = AtLeastOneOf[T] 
  def apply[T <: AnyLabelType](t: T): C[T] = AtLeastOneOf(t)
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
