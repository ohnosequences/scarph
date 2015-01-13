package ohnosequences.scarph

object containers {

  import graphTypes._


  /* These classes are only for convenience of definitions inside this object */
  abstract sealed private class NestedGraphType[C <: AnyContainer, T <: AnyGraphType]
    (val container: C, val inside: T) extends AnyGraphType {

    type Container = C
    type Inside = T

    lazy val label = this.toString
  }

  private[containers] case class ExactlyOneOf[T <: AnyGraphType](t: T) extends NestedGraphType(ExactlyOne, t)
  private[containers] case class  OneOrNoneOf[T <: AnyGraphType](t: T) extends NestedGraphType(OneOrNone, t)
  private[containers] case class ManyOrNoneOf[T <: AnyGraphType](t: T) extends NestedGraphType(ManyOrNone, t)
  private[containers] case class AtLeastOneOf[T <: AnyGraphType](t: T) extends NestedGraphType(AtLeastOne, t)


  /* Here are 4 types of arity containers that we can use for wrapping graph types */
  sealed trait AnyContainer {

    val label: String

    type Of[T <: AnyGraphType] <: AnyGraphType
    def  of[T <: AnyGraphType](t: T): Of[T]
  }


  case object ExactlyOne extends AnyContainer {

    lazy val label: String = ""
    type Of[T <: AnyGraphType] = T
    def  of[T <: AnyGraphType](t: T): T = t
  }
  type ExactlyOne = ExactlyOne.type

  case object OneOrNone extends AnyContainer {

    lazy val label: String = "Opt"
    type Of[T <: AnyGraphType] = OneOrNoneOf[T]  
    def  of[T <: AnyGraphType](t: T): Of[T] = OneOrNoneOf[T](t)
  }
  type OneOrNone = OneOrNone.type

  case object AtLeastOne extends AnyContainer {

    lazy val label: String = "NEList"
    type Of[T <: AnyGraphType] = AtLeastOneOf[T] 
    def  of[T <: AnyGraphType](t: T): Of[T] = AtLeastOneOf[T](t)
  }
  type AtLeastOne = AtLeastOne.type

  case object ManyOrNone extends AnyContainer {

    lazy val label: String = "List"
    type Of[T <: AnyGraphType] = ManyOrNoneOf[T] 
    def  of[T <: AnyGraphType](t: T): Of[T] = ManyOrNoneOf[T](t)
  }
  type ManyOrNone = ManyOrNone.type


  import ohnosequences.cosas._, fns._

  /* This is for mapping (in implementation) ManyOrNone to List, OneOrNone to Option, etc. */
  trait ValueContainer[C <: AnyContainer, X] extends Fn1[X]


  /* Containers multiplication (`\times` symbol)*/
  trait ×[A <: AnyContainer, B <: AnyContainer] extends Fn2[A, B] with OutBound[AnyContainer]

  object × extends times_2 {
    implicit def idemp[A <: AnyContainer]: 
        (A × A) with Out[A] = 
    new (A × A) with Out[A] { def apply(a: In1, b: In2): Out = a }
  }

  trait times_2 extends times_3 {
    implicit def unitL[B <: AnyContainer]: 
        (ExactlyOne × B) with Out[B] = 
    new (ExactlyOne × B) with Out[B] { def apply(a: In1, b: In2): Out = b }

    implicit def unitR[A <: AnyContainer]: 
        (A × ExactlyOne) with Out[A] = 
    new (A × ExactlyOne) with Out[A] { def apply(a: In1, b: In2): Out = a }
  }

  trait times_3 {
    implicit def rest[A <: AnyContainer, B <: AnyContainer]: 
        (A × B) with Out[ManyOrNone] = 
    new (A × B) with Out[ManyOrNone] { def apply(a: In1, b: In2): Out = ManyOrNone }
  }


  /* Containers multiplication (`\times` symbol)*/
  trait +[A <: AnyContainer, B <: AnyContainer] extends Fn2[A, B] with OutBound[AnyContainer]

  object + extends plus_2 {

    implicit def exactlyOnePlus[B <: AnyContainer]: 
        (ExactlyOne + B) with Out[AtLeastOne] = 
    new (ExactlyOne + B) with Out[AtLeastOne] { def apply(a: In1, b: In2): Out = AtLeastOne }

    implicit def atLeastOnePlus[B <: AnyContainer]: 
        (AtLeastOne + B) with Out[AtLeastOne] = 
    new (AtLeastOne + B) with Out[AtLeastOne] { def apply(a: In1, b: In2): Out = AtLeastOne }
  }

  trait plus_2 extends plus_3 {

    implicit def plusExactlyOne[A <: AnyContainer]: 
        (A + ExactlyOne) with Out[AtLeastOne] = 
    new (A + ExactlyOne) with Out[AtLeastOne] { def apply(a: In1, b: In2): Out = AtLeastOne }

    implicit def plusAtLeastOne[A <: AnyContainer]: 
        (A + AtLeastOne) with Out[AtLeastOne] = 
    new (A + AtLeastOne) with Out[AtLeastOne] { def apply(a: In1, b: In2): Out = AtLeastOne }
  }

  trait plus_3 {

    implicit def rest[A <: AnyContainer, B <: AnyContainer]: 
        (A + B) with Out[ManyOrNone] = 
    new (A + B) with Out[ManyOrNone] { def apply(a: In1, b: In2): Out = ManyOrNone }
  }

}
