package ohnosequences.scarph

object containers {

  import graphTypes._


  /* This is a graph type containing another graph type */
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


  /* Here are 4 types of arity containers that we can use for wrapping graph types */
  sealed trait AnyContainer {

    type Of[T <: AnyGraphType] <: AnyNestedGraphType
    def  of[T <: AnyGraphType](t: T): Of[T]
  }


  object ExactlyOne extends AnyContainer { 
    type Of[T <: AnyGraphType] = ExactlyOneOf[T]
    def  of[T <: AnyGraphType](t: T): Of[T] = ExactlyOneOf[T](t)
  }
  type ExactlyOne = ExactlyOne.type

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


  /* Containers multiplication (`\times` symbol)*/
  trait ×[A <: AnyContainer, B <: AnyContainer] extends Fn2[A, B] with OutBound[AnyContainer]

  object × extends x_2 {
    implicit def idemp[A <: AnyContainer]: 
        (A × A) with Out[A] = 
    new (A × A) with Out[A] { def apply(a: In1, b: In2): Out = a }
  }

  trait x_2 extends x_3 {
    implicit def unitL[B <: AnyContainer]: 
        (ExactlyOne × B) with Out[B] = 
    new (ExactlyOne × B) with Out[B] { def apply(a: In1, b: In2): Out = b }

    implicit def unitR[A <: AnyContainer]: 
        (A × ExactlyOne) with Out[A] = 
    new (A × ExactlyOne) with Out[A] { def apply(a: In1, b: In2): Out = a }
  }

  trait x_3 {
    implicit def rest[A <: AnyContainer, B <: AnyContainer]: 
        (A × B) with Out[ManyOrNone] = 
    new (A × B) with Out[ManyOrNone] { def apply(a: In1, b: In2): Out = ManyOrNone }
  }


  // TODO: HList-like with bound on vertices, another for paths etc

  trait AnyParType extends AnyGraphType {

    type First <: AnyGraphType
    val  first: First

    type Second <: AnyGraphType
    val  second: Second
  }

  case class ParType[F <: AnyGraphType, S <: AnyGraphType](val first: F, val second: S) extends AnyParType {

    type First = F
    type Second = S

    val label = this.toString
  }


  trait AnyOrType extends AnyGraphType {

    type First <: AnyGraphType
    val  first: First

    type Second <: AnyGraphType
    val  second: Second
  }

  case class OrType[F <: AnyGraphType, S <: AnyGraphType](val first: F, val second: S) extends AnyOrType {

    type First = F
    type Second = S

    val label = this.toString
  }

}
