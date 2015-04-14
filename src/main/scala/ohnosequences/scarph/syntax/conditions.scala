package ohnosequences.scarph.syntax

object conditions {

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.conditions._


  /* Method aliases for predicate constructors */
  implicit def conditionOps[P <: AnyGraphProperty](property: P):
      ConditionOps[P] =
      ConditionOps[P](property)

  case class ConditionOps[P <: AnyGraphProperty](property: P) {

    def ===(value: P#Value#Raw): Equal[P] = Equal(property, value)
    def =/=(value: P#Value#Raw): NotEqual[P] = NotEqual(property, value)

    def <(value: P#Value#Raw): Less[P] = Less(property, value)
    def ≤(value: P#Value#Raw): LessOrEqual[P] = LessOrEqual(property, value)

    def >(value: P#Value#Raw): Greater[P] = Greater(property, value)
    def ≥(value: P#Value#Raw): GreaterOrEqual[P] = GreaterOrEqual(property, value)

    def between(s: P#Value#Raw, e: P#Value#Raw): Interval[P] = Interval(property, s, e)
  }
}
