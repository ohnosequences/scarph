package ohnosequences.scarph.syntax

import ohnosequences.scarph._

object conditions {

  /* Method aliases for predicate constructors */
  implicit def compareConditionOps[A <: AnyProp](property: A):
      CompareConditionOps[A] = 
      CompareConditionOps[A](property)

  case class CompareConditionOps[A <: AnyProp](property: A) {

    final def ===(value: A#Raw): Equal[A] = Equal(property, value)
    final def =/=(value: A#Raw): NotEqual[A] = NotEqual(property, value)

    final def <(value: A#Raw): Less[A] = Less(property, value)
    final def ≤(value: A#Raw): LessOrEqual[A] = LessOrEqual(property, value)

    final def >(value: A#Raw): Greater[A] = Greater(property, value)
    final def ≥(value: A#Raw): GreaterOrEqual[A] = GreaterOrEqual(property, value)
  }
}
