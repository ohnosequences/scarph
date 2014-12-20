package ohnosequences.scarph

object containers {

  import graphTypes._


  /* This is a label type containing another label type */
  // TODO: this name is pretty unintuitive
  sealed trait AnyContainerType extends AnyGraphType {

    type Container <: AnyContainer
    val  container: Container

    type Of <: AnyGraphType
    val  of: Of
  }

  abstract class ContainerTypeOf[T <: AnyGraphType, C <: AnyContainer](val of: T)
    (val container: C) extends AnyContainerType { 

    type Container = C
    type Of = T
    val label = this.toString
  }

  case class ExactlyOneOf[T <: AnyGraphType](t: T) extends ContainerTypeOf(t)(ExactlyOne)
  case class  OneOrNoneOf[T <: AnyGraphType](t: T) extends ContainerTypeOf(t)(OneOrNone)
  case class ManyOrNoneOf[T <: AnyGraphType](t: T) extends ContainerTypeOf(t)(ManyOrNone)
  case class AtLeastOneOf[T <: AnyGraphType](t: T) extends ContainerTypeOf(t)(AtLeastOne)


  /* These are 4 types of arity containers that we can use for wrapping label types */
  sealed trait AnyContainer {

    type Of[T <: AnyGraphType] <: AnyContainerType
    def apply[T <: AnyGraphType](t: T): Of[T]
  }


  object ExactlyOne extends AnyContainer { 

    type Of[T <: AnyGraphType] = ExactlyOneOf[T]
    def apply[T <: AnyGraphType](t: T): Of[T] = ExactlyOneOf[T](t)
  }

  object OneOrNone extends AnyContainer  { 

    type Of[T <: AnyGraphType] = OneOrNoneOf[T]  
    def apply[T <: AnyGraphType](t: T): Of[T] = OneOrNoneOf[T](t)
  }

  object ManyOrNone extends AnyContainer { 

    type Of[T <: AnyGraphType] = ManyOrNoneOf[T] 
    def apply[T <: AnyGraphType](t: T): Of[T] = ManyOrNoneOf[T](t)
  }

  object AtLeastOne extends AnyContainer { 

    type Of[T <: AnyGraphType] = AtLeastOneOf[T] 
    def apply[T <: AnyGraphType](t: T): Of[T] = AtLeastOneOf[T](t)
  }


  import ohnosequences.cosas._, fns._

  /* This is for mapping (in implementation) ManyOrNone to List, OneOrNone to Option, etc. */
  trait ContainerVal[C <: AnyContainer, X] extends Fn1[X]

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

}
