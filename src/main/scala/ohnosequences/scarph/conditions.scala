package ohnosequences.scarph

object conditions {

  import ohnosequences.cosas._, properties._
  import graphTypes._
  import java.lang.Comparable


  /* A condition is a restriction on the property values */
  trait AnyCondition {

    type Property <: AnyGraphProperty
    val  property: Property

    type     Element = Property#Owner
    lazy val element = property.owner

    val label: String
    override final def toString = label
  }

  object AnyCondition {

    type OnProperty[P <: AnyGraphProperty] = AnyCondition { type Property = P }
    type OnElement[E <: AnyGraphElement] = AnyCondition { type Element = E }
  }


  /* Comparison conditions with **One** property value */
  trait AnyCompareCondition extends AnyCondition {
    type Property <: AnyGraphProperty //{ type Raw <: Comparable[_] }

    val value: Property#Value#Raw
  }

  trait CompareCondition[P <: AnyGraphProperty]
    extends AnyCompareCondition { type Property = P }


  trait AnyEqual extends AnyCompareCondition
  case class Equal[P <: AnyGraphProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyEqual with CompareCondition[P] {
    lazy val label = s"${property.label} = ${value.toString}"
  }

  trait AnyNotEqual extends AnyCompareCondition
  case class NotEqual[P <: AnyGraphProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyNotEqual with CompareCondition[P] {
    lazy val label = s"${property.label} ≠ ${value.toString}"
  }


  trait AnyLess extends AnyCompareCondition
  case class Less[P <: AnyGraphProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyLess with CompareCondition[P] {
    lazy val label = s"${property.label} < ${value.toString}"
  }

  trait AnyLessOrEqual extends AnyCompareCondition
  case class LessOrEqual[P <: AnyGraphProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyLessOrEqual with CompareCondition[P] {
    lazy val label = s"${property.label} ≤ ${value.toString}"
  }


  trait AnyGreater extends AnyCompareCondition
  case class Greater[P <: AnyGraphProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyGreater with CompareCondition[P] {
    lazy val label = s"${property.label} > ${value.toString}"
  }

  trait AnyGreaterOrEqual extends AnyCompareCondition
  case class GreaterOrEqual[P <: AnyGraphProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyGreaterOrEqual with CompareCondition[P] {
    lazy val label = s"${property.label} ≥ ${value.toString}"
  }


  trait AnyInterval extends AnyCondition {
    type Property <: AnyGraphProperty

    val start: Property#Value#Raw
    val end: Property#Value#Raw
  }

  case class Interval[P <: AnyGraphProperty](
    val property: P,
    val start: P#Value#Raw,
    val end: P#Value#Raw
  ) extends AnyInterval {
    type Property = P
    lazy val label = s"${start.toString} ≤ ${property.label} ≤ ${end.toString}"
  }

}
