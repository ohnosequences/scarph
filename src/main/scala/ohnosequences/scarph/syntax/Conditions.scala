package ohnosequences.scarph.syntax

object conditions {

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.conditions._


  /* Method aliases for predicate constructors */
  implicit def compareConditionOps[A <: AnyGraphProperty](property: A):
      CompareConditionOps[A] = 
      CompareConditionOps[A](property)

  case class CompareConditionOps[A <: AnyGraphProperty](property: A) {

    final def ===(value: A#Raw): Equal[A] = Equal(property, value)
    final def =/=(value: A#Raw): NotEqual[A] = NotEqual(property, value)

    final def <(value: A#Raw): Less[A] = Less(property, value)
    final def ≤(value: A#Raw): LessOrEqual[A] = LessOrEqual(property, value)

    final def >(value: A#Raw): Greater[A] = Greater(property, value)
    final def ≥(value: A#Raw): GreaterOrEqual[A] = GreaterOrEqual(property, value)
  }
}
