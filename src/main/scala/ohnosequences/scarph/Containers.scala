package ohnosequences.scarph


/* This is a label type containing another label type */
// TODO: this name is pretty unintuitive
sealed trait AnyContainerType extends AnyLabelType {

  type Container <: AnyContainer
  val  container: Container

  type Of <: AnyLabelType
  val  of: Of
}

abstract class ContainerTypeOf[T <: AnyLabelType, C <: AnyContainer](val of: T)
  (val container: C, val lbl: String) extends AnyContainerType { 

  type Container = C
  type Of = T
  lazy val label = s"${lbl}(${of.label})"
}

// case class ExactlyOneOf[T <: AnyLabelType](t: T) extends ContainerTypeOf[T](t)("ExactlyOne")
case class  OneOrNoneOf[T <: AnyLabelType](t: T) extends ContainerTypeOf(t)(OneOrNone, "OneOrNone")
case class ManyOrNoneOf[T <: AnyLabelType](t: T) extends ContainerTypeOf(t)(ManyOrNone, "ManyOrNone")
case class AtLeastOneOf[T <: AnyLabelType](t: T) extends ContainerTypeOf(t)(AtLeastOne, "AtLeastOne")


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


import ohnosequences.cosas._

/* Arities multiplication */
// not the best name, but it may look cool: A x B
trait x[A <: AnyContainer, B <: AnyContainer] extends Fn2[A, B] with OutBound[AnyContainer]

object x extends x_2 {
  implicit def idemp[A <: AnyContainer]: 
      (A x A) with Out[A] = 
  new (A x A) with Out[A] { def apply(a: In1, b: In2): Out = a }
}

trait x_2 extends x_3 {
  implicit def unitL[A <: AnyContainer]: 
      (ExactlyOne.type x A) with Out[A] = 
  new (ExactlyOne.type x A) with Out[A] { def apply(a: In1, b: In2): Out = b }

  implicit def unitR[A <: AnyContainer]: 
      (A x ExactlyOne.type) with Out[A] = 
  new (A x ExactlyOne.type) with Out[A] { def apply(a: In1, b: In2): Out = a }
}

trait x_3 extends x_4 {
  implicit def oneornoneL[A <: AnyContainer]: 
      (OneOrNone.type x A) with Out[ManyOrNone.type] = 
  new (OneOrNone.type x A) with Out[ManyOrNone.type] { def apply(a: In1, b: In2): Out = ManyOrNone }

  implicit def oneornoneR[A <: AnyContainer]: 
      (A x OneOrNone.type) with Out[ManyOrNone.type] = 
  new (A x OneOrNone.type) with Out[ManyOrNone.type] { def apply(a: In1, b: In2): Out = ManyOrNone }
}

trait x_4 {
  implicit def atleastoneL[A <: AnyContainer]: 
      (AtLeastOne.type x A) with Out[AtLeastOne.type] = 
  new (AtLeastOne.type x A) with Out[AtLeastOne.type] { def apply(a: In1, b: In2): Out = AtLeastOne }

  implicit def atleastoneR[A <: AnyContainer]: 
      (A x AtLeastOne.type) with Out[AtLeastOne.type] = 
  new (A x AtLeastOne.type) with Out[AtLeastOne.type] { def apply(a: In1, b: In2): Out = AtLeastOne }
}
