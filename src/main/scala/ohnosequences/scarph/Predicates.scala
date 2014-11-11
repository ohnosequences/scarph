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


/* Predicate is a combination of conditions over **one** element type */
trait AnyPredicate {
  
  type ElementType <: AnyElementType
  val  elementType: ElementType
}

object AnyPredicate {

  type On[E <: AnyElementType] = AnyPredicate { type ElementType = E }
}


/* Predicates with just one condition */
trait AnySimplePredicate extends AnyPredicate {

  type Condition <: AnyCondition
  val  condition: Condition

  type ElementType = Condition#ElementType
  val  elementType = condition.elementType
}

case class SimplePredicate[C <: AnyCondition](val condition: C) 
  extends AnySimplePredicate { type Condition = C }


/* Nice methods to build conditions and predicates */
object PredicateSyntax {

  implicit def predicateOps[E <: AnyElementType](elem: E):
      PredicateOps[E] = 
      PredicateOps[E](elem)

  implicit def compareConditionOps[A <: AnyProp](property: A):
      CompareConditionOps[A] = 
      CompareConditionOps[A](property)
}

/* ## Method aliases for predicate constructors */
case class CompareConditionOps[A <: AnyProp](property: A) {
  final def ===(value: A#Raw): Equal[A] = Equal(property, value)
  final def =/=(value: A#Raw): NotEqual[A] = NotEqual(property, value)

  final def <(value: A#Raw): Less[A] = Less(property, value)
  final def ≤(value: A#Raw): LessOrEqual[A] = LessOrEqual(property, value)

  final def >(value: A#Raw): Greater[A] = Greater(property, value)
  final def ≥(value: A#Raw): GreaterOrEqual[A] = GreaterOrEqual(property, value)
}

case class PredicateOps[E <: AnyElementType](elem: E) {

  def ?[C <: AnyCondition.OnElementType[E]](c: C): SimplePredicate[C] = SimplePredicate[C](c)
}
