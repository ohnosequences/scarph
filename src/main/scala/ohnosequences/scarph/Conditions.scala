package ohnosequences.scarph

import ohnosequences.cosas._

/* Condition is some restriction on the property values */
trait AnyCondition { 

  type Property <: AnyProp
  val  property: Property

  type ElementType = Property#Owner
  val  elementType = property.owner
}

object AnyCondition {

  type OnProperty[P <: AnyProp] = AnyCondition { type Property = P }
  type OnElementType[E <: AnyElementType] = AnyCondition { type ElementType = E }
}


/* Comparison conditions with **One** property value */
trait AnyCompareCondition extends AnyCondition { val value: Property#Raw }
trait CompareCondition[A <: AnyProp] extends AnyCompareCondition { type Property = A }


trait AnyEqual extends AnyCompareCondition
case class Equal[A <: AnyProp](
  val property: A,
  val value: A#Raw
) extends AnyEqual with CompareCondition[A]

trait AnyNotEqual extends AnyCompareCondition
case class NotEqual[A <: AnyProp](
  val property: A,
  val value: A#Raw
) extends AnyNotEqual with CompareCondition[A]


trait AnyLess extends AnyCompareCondition
case class Less[A <: AnyProp](
  val property: A,
  val value: A#Raw
) extends AnyLess with CompareCondition[A]

trait AnyLessOrEqual extends AnyCompareCondition
case class LessOrEqual[A <: AnyProp](
  val property: A,
  val value: A#Raw
) extends AnyLessOrEqual with CompareCondition[A]


trait AnyGreater extends AnyCompareCondition
case class Greater[A <: AnyProp](
  val property: A,
  val value: A#Raw
) extends AnyGreater with CompareCondition[A]

trait AnyGreaterOrEqual extends AnyCompareCondition
case class GreaterOrEqual[A <: AnyProp](
  val property: A,
  val value: A#Raw
) extends AnyGreaterOrEqual with CompareCondition[A]
