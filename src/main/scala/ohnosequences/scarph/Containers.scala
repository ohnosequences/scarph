package ohnosequences.scarph


/* This is a label type containing another label type */
// TODO: this name is pretty unintuitive
sealed trait AnyContainerType extends AnyLabelType {

  type Of <: AnyLabelType
  val  of: Of
}

abstract class ContainerTypeOf[T <: AnyLabelType](val of: T)(val lbl: String) extends AnyContainerType { 
  type Of = T
  lazy val label = s"${lbl}(${of.label})"
}

case class ExactlyOneOf[T <: AnyLabelType](t: T) extends ContainerTypeOf[T](t)("ExactlyOne")
case class  OneOrNoneOf[T <: AnyLabelType](t: T) extends ContainerTypeOf[T](t)("OneOrNone")
case class ManyOrNoneOf[T <: AnyLabelType](t: T) extends ContainerTypeOf[T](t)("ManyOrNone")
case class AtLeastOneOf[T <: AnyLabelType](t: T) extends ContainerTypeOf[T](t)("AtLeastOne")


/* These are 4 types of arity containers that we can use for wrapping label types */
sealed trait AnyContainer {

  type Of[T <: AnyLabelType] <: AnyLabelType
  def apply[T <: AnyLabelType](t: T): Of[T]
}


object ExactlyOne extends AnyContainer { 

  type Of[T <: AnyLabelType] = T
  def apply[T <: AnyLabelType](t: T): Of[T] = t
}

object OneOrNone extends AnyContainer  { 

  type Of[T <: AnyLabelType] = OneOrNoneOf[T]  
  def apply[T <: AnyLabelType](t: T): Of[T] = OneOrNoneOf[T](t)
}

object ManyOrNone extends AnyContainer { 

  type Of[T <: AnyLabelType] = ManyOrNoneOf[T] 
  def apply[T <: AnyLabelType](t: T): Of[T] = ManyOrNoneOf[T](t)
}

object AtLeastOne extends AnyContainer { 

  type Of[T <: AnyLabelType] = AtLeastOneOf[T] 
  def apply[T <: AnyLabelType](t: T): Of[T] = AtLeastOneOf[T](t)
}
