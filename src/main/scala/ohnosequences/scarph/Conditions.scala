package ohnosequences.scarph

object conditions {

  import ohnosequences.cosas._, properties._
  import graphTypes._
  import java.lang.Comparable


  /* A condition is a restriction on the property values */
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
  trait AnyCompareCondition extends AnyCondition { 
    type Property <: AnyGraphProperty { type Raw <: Comparable[_] }

    val value: Property#Raw 
  }

  trait CompareCondition[P <: AnyGraphProperty { type Raw <: Comparable[_] }] 
    extends AnyCompareCondition { type Property = P }


  trait AnyEqual extends AnyCompareCondition
  case class Equal[P <: AnyGraphProperty { type Raw <: Comparable[_] }](
    val property: P,
    val value: P#Raw
  ) extends AnyEqual with CompareCondition[P]

  trait AnyNotEqual extends AnyCompareCondition
  case class NotEqual[P <: AnyGraphProperty { type Raw <: Comparable[_] }](
    val property: P,
    val value: P#Raw
  ) extends AnyNotEqual with CompareCondition[P]


  trait AnyLess extends AnyCompareCondition
  case class Less[P <: AnyGraphProperty { type Raw <: Comparable[_] }](
    val property: P,
    val value: P#Raw
  ) extends AnyLess with CompareCondition[P]

  trait AnyLessOrEqual extends AnyCompareCondition
  case class LessOrEqual[P <: AnyGraphProperty { type Raw <: Comparable[_] }](
    val property: P,
    val value: P#Raw
  ) extends AnyLessOrEqual with CompareCondition[P]


  trait AnyGreater extends AnyCompareCondition
  case class Greater[P <: AnyGraphProperty { type Raw <: Comparable[_] }](
    val property: P,
    val value: P#Raw
  ) extends AnyGreater with CompareCondition[P]

  trait AnyGreaterOrEqual extends AnyCompareCondition
  case class GreaterOrEqual[P <: AnyGraphProperty { type Raw <: Comparable[_] }](
    val property: P,
    val value: P#Raw
  ) extends AnyGreaterOrEqual with CompareCondition[P]


  trait AnyInterval extends AnyCondition {
    type Property <: AnyGraphProperty { type Raw <: Comparable[_] }

    val start: Property#Raw
    val end: Property#Raw
  }

  case class Interval[P <: AnyGraphProperty { type Raw <: Comparable[_] }](
    val property: P,
    val start: P#Raw,
    val end: P#Raw
  ) extends AnyInterval { type Property = P }

}
