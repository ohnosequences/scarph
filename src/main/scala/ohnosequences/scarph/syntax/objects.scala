package ohnosequences.scarph.syntax

object objects {

  import ohnosequences.cosas.types._
  import ohnosequences.scarph.objects._


  /* A way of building a predicate from an element */
  implicit def elementPredicateOps[E <: AnyGraphElement](e: E):
      ElementPredicateOps[E] =
      ElementPredicateOps[E](e)

  case class ElementPredicateOps[E <: AnyGraphElement](e: E) extends AnyVal {

    /* For example: `user ? (user.name === "bob")` */
    def ?[C <: AnyCondition.OnElement[E]](c: C):
      AndPredicate[EmptyPredicate[E], C] =
      AndPredicate(EmptyPredicate(e), c)
  }

  /* Adding more conditions to a predicate */
  implicit def predicateOps[P <: AnyPredicate](p: P):
      PredicateOps[P] =
      PredicateOps[P](p)

  case class PredicateOps[P <: AnyPredicate](pred: P) extends AnyVal {

    /* It's basically cons for the internal conditions type-set,
       but with a restriction on the condtion's element type */
    def and[C <: AnyCondition.OnElement[P#Element]](c: C):
      AndPredicate[P, C] = AndPredicate(pred, c)
  }

  /* Method aliases for predicate constructors */
  implicit final def conditionOps[P <: AnyProperty](property: P):
    ConditionOps[P] =
    ConditionOps[P](property)

  case class ConditionOps[P <: AnyProperty](property: P) extends AnyVal {

    def ===(value: P#TargetVertex#Raw): Equal[P] = Equal(property, value)
    def =/=(value: P#TargetVertex#Raw): NotEqual[P] = NotEqual(property, value)

    def <(value: P#TargetVertex#Raw): Less[P] = Less(property, value)
    def ≤(value: P#TargetVertex#Raw): LessOrEqual[P] = LessOrEqual(property, value)

    def >(value: P#TargetVertex#Raw): Greater[P] = Greater(property, value)
    def ≥(value: P#TargetVertex#Raw): GreaterOrEqual[P] = GreaterOrEqual(property, value)

    def between(s: P#TargetVertex#Raw, e: P#TargetVertex#Raw): Interval[P] = Interval(property, s, e)
  }
}
