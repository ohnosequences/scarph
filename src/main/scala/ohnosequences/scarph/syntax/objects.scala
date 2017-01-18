package ohnosequences.scarph.syntax

import ohnosequences.cosas.types._
import ohnosequences.scarph._


/* A way of building a predicate from an element */
case class ElementPredicateOps[E <: AnyGraphElement](e: E) extends AnyVal {

  /* For example: `user ? (user.name === "bob")` */
  def ?[C <: AnyCondition.OnElement[E]](c: C):
    AndPredicate[EmptyPredicate[E], C] =
    AndPredicate(EmptyPredicate(e), c)
}

/* Adding more conditions to a predicate */
case class PredicateOps[P <: AnyPredicate](pred: P) extends AnyVal {

  /* It's basically cons for the internal conditions type-set,
     but with a restriction on the condtion's element type */
  def and[C <: AnyCondition.OnElement[P#Element]](c: C):
    AndPredicate[P, C] = AndPredicate(pred, c)
}

/* Method aliases for predicate constructors */
case class ConditionOps[P <: AnyProperty](property: P) extends AnyVal {

  def ===(value: P#Target#Raw): Equal[P] = Equal(property, value)
  def =/=(value: P#Target#Raw): NotEqual[P] = NotEqual(property, value)

  def <(value: P#Target#Raw): Less[P] = Less(property, value)
  def ≤(value: P#Target#Raw): LessOrEqual[P] = LessOrEqual(property, value)

  def >(value: P#Target#Raw): Greater[P] = Greater(property, value)
  def ≥(value: P#Target#Raw): GreaterOrEqual[P] = GreaterOrEqual(property, value)

  def between(s: P#Target#Raw, e: P#Target#Raw): Interval[P] = Interval(property, s, e)
}
