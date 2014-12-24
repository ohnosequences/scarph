package ohnosequences.scarph

object conditions {

  import ohnosequences.cosas._, properties._
  import graphTypes._


  /* Condition is some restriction on the property values */
  trait AnyCondition { 

    type Property <: AnyGraphProperty
    val  property: Property

    type Element = Property#Owner
    val  element = property.owner
  }

  object AnyCondition {

    type OnProperty[P <: AnyGraphProperty] = AnyCondition { type Property = P }
    type OnElement[E <: AnyGraphElement] = AnyCondition { type Element = E }
  }


  /* Comparison conditions with **One** property value */
  trait AnyCompareCondition extends AnyCondition { val value: Property#Raw }
  trait CompareCondition[A <: AnyGraphProperty] extends AnyCompareCondition { type Property = A }


  trait AnyEqual extends AnyCompareCondition
  case class Equal[A <: AnyGraphProperty](
    val property: A,
    val value: A#Raw
  ) extends AnyEqual with CompareCondition[A]

  trait AnyNotEqual extends AnyCompareCondition
  case class NotEqual[A <: AnyGraphProperty](
    val property: A,
    val value: A#Raw
  ) extends AnyNotEqual with CompareCondition[A]


  trait AnyLess extends AnyCompareCondition
  case class Less[A <: AnyGraphProperty](
    val property: A,
    val value: A#Raw
  ) extends AnyLess with CompareCondition[A]

  trait AnyLessOrEqual extends AnyCompareCondition
  case class LessOrEqual[A <: AnyGraphProperty](
    val property: A,
    val value: A#Raw
  ) extends AnyLessOrEqual with CompareCondition[A]


  trait AnyGreater extends AnyCompareCondition
  case class Greater[A <: AnyGraphProperty](
    val property: A,
    val value: A#Raw
  ) extends AnyGreater with CompareCondition[A]

  trait AnyGreaterOrEqual extends AnyCompareCondition
  case class GreaterOrEqual[A <: AnyGraphProperty](
    val property: A,
    val value: A#Raw
  ) extends AnyGreaterOrEqual with CompareCondition[A]

}
