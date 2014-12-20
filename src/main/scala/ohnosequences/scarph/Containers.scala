package ohnosequences.scarph

object containers {

  import graphTypes._


  /* This is a label type containing another label type */
  // TODO: this name is pretty unintuitive
  sealed trait AnyNestedGraphType extends AnyGraphType {

    type Container <: AnyContainer
    val  container: Container

    type Inside <: AnyGraphType
    val  inside: Inside
  }

  /* These classes are only for convenience of definitions inside this object */
  abstract sealed private class NestedGraphType[C <: AnyContainer, T <: AnyGraphType]
    (val container: C, val inside: T) extends AnyNestedGraphType {

    type Container = C
    type Inside = T

    val label = this.toString
  }

  private[containers] case class ExactlyOneOf[T <: AnyGraphType](t: T) extends NestedGraphType(ExactlyOne, t)
  private[containers] case class  OneOrNoneOf[T <: AnyGraphType](t: T) extends NestedGraphType(OneOrNone, t)
  private[containers] case class ManyOrNoneOf[T <: AnyGraphType](t: T) extends NestedGraphType(ManyOrNone, t)
  private[containers] case class AtLeastOneOf[T <: AnyGraphType](t: T) extends NestedGraphType(AtLeastOne, t)


  /* These are 4 types of arity containers that we can use for wrapping label types */
  sealed trait AnyContainer {

    type Of[T <: AnyGraphType] <: AnyNestedGraphType
    def  of[T <: AnyGraphType](t: T): Of[T]
  }


  object ExactlyOne extends AnyContainer { 
    type Of[T <: AnyGraphType] = ExactlyOneOf[T]
    def  of[T <: AnyGraphType](t: T): Of[T] = ExactlyOneOf[T](t)
  }
  type ExactlyOne = ExactlyOne.type
  // NOTE: not sure that such synonyms are needed:
  // def  exactlyOne[T <: AnyGraphType](t: T): ExactlyOne.Of[T] = ExactlyOne.of(t)

  object OneOrNone extends AnyContainer { 
    type Of[T <: AnyGraphType] = OneOrNoneOf[T]  
    def  of[T <: AnyGraphType](t: T): Of[T] = OneOrNoneOf[T](t)
  }
  type OneOrNone = OneOrNone.type

  object AtLeastOne extends AnyContainer { 
    type Of[T <: AnyGraphType] = AtLeastOneOf[T] 
    def  of[T <: AnyGraphType](t: T): Of[T] = AtLeastOneOf[T](t)
  }
  type AtLeastOne = AtLeastOne.type

  object ManyOrNone extends AnyContainer { 
    type Of[T <: AnyGraphType] = ManyOrNoneOf[T] 
    def  of[T <: AnyGraphType](t: T): Of[T] = ManyOrNoneOf[T](t)
  }
  type ManyOrNone = ManyOrNone.type


  import ohnosequences.cosas._, fns._

  /* This is for mapping (in implementation) ManyOrNone to List, OneOrNone to Option, etc. */
  trait ValueContainer[C <: AnyContainer, X] extends Fn1[X]


  /* Containers multiplication */
  trait ×[A <: AnyContainer, B <: AnyContainer] extends Fn2[A, B] with OutBound[AnyContainer]

  object × extends x_2 {
    implicit def idemp[A <: AnyContainer]: 
        (A × A) with Out[A] = 
    new (A × A) with Out[A] { def apply(a: In1, b: In2): Out = a }
  }

  trait x_2 extends x_3 {
    implicit def unitL[A <: AnyContainer]: 
        (ExactlyOne × A) with Out[A] = 
    new (ExactlyOne × A) with Out[A] { def apply(a: In1, b: In2): Out = b }

    implicit def unitR[A <: AnyContainer]: 
        (A × ExactlyOne) with Out[A] = 
    new (A × ExactlyOne) with Out[A] { def apply(a: In1, b: In2): Out = a }
  }

  trait x_3 extends x_4 {
    implicit def oneornoneL[A <: AnyContainer]: 
        (OneOrNone × A) with Out[ManyOrNone] = 
    new (OneOrNone × A) with Out[ManyOrNone] { def apply(a: In1, b: In2): Out = ManyOrNone }

    implicit def oneornoneR[A <: AnyContainer]: 
        (A × OneOrNone) with Out[ManyOrNone] = 
    new (A × OneOrNone) with Out[ManyOrNone] { def apply(a: In1, b: In2): Out = ManyOrNone }
  }

  trait x_4 {
    implicit def atleastoneL[A <: AnyContainer]: 
        (AtLeastOne × A) with Out[AtLeastOne] = 
    new (AtLeastOne × A) with Out[AtLeastOne] { def apply(a: In1, b: In2): Out = AtLeastOne }

    implicit def atleastoneR[A <: AnyContainer]: 
        (A × AtLeastOne) with Out[AtLeastOne] = 
    new (A × AtLeastOne) with Out[AtLeastOne] { def apply(a: In1, b: In2): Out = AtLeastOne }
  }


  // TODO: HList-like with bound on vertices, another for paths etc

  trait AnyParV extends AnyGraphType {

    type First <: AnyGraphType
    val  first: First

    type Second <: AnyGraphType
    val  second: Second
  }

  case class ParV[F <: AnyGraphType, S <: AnyGraphType](val first: F, val second: S) extends AnyParV {

    type First = F
    type Second = S

    val label = this.toString
  }


  trait AnyOrV extends AnyGraphType {

    type First <: AnyGraphType
    val  first: First

    type Second <: AnyGraphType
    val  second: Second
  }

  case class OrV[F <: AnyGraphType, S <: AnyGraphType](val first: F, val second: S) extends AnyOrV {

    type First = F
    type Second = S

    val label = this.toString
  }

}
