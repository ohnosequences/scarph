package ohnosequences.scarph.syntax

object conditions {

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.conditions._


  /* Method aliases for predicate constructors */
  implicit def conditionOps[P <: AnyGraphProperty { type Raw <: Comparable[_] }](property: P):
      ConditionOps[P] = 
      ConditionOps[P](property)

  case class ConditionOps[P <: AnyGraphProperty { type Raw <: Comparable[_] }](property: P) {

    def ===(value: P#Raw): Equal[P] = Equal(property, value)
    def =/=(value: P#Raw): NotEqual[P] = NotEqual(property, value)

    def <(value: P#Raw): Less[P] = Less(property, value)
    def ≤(value: P#Raw): LessOrEqual[P] = LessOrEqual(property, value)

    def >(value: P#Raw): Greater[P] = Greater(property, value)
    def ≥(value: P#Raw): GreaterOrEqual[P] = GreaterOrEqual(property, value)

    def between(s: P#Raw, e: P#Raw): Interval[P] = Interval(property, s, e)
  }
}
