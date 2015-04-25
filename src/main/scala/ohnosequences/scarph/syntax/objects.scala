package ohnosequences.scarph.syntax

import ohnosequences.cosas.types._
import ohnosequences._

case object objects extends module(scarph.objects) {

  import scarph.objects._

  implicit final def graphObjectValOps[F <: AnyGraphObject, VF](vt: F := VF):
    GraphObjectValOps[F, VF] =
    GraphObjectValOps[F, VF](vt.value)

  case class GraphObjectValOps[F <: AnyGraphObject, VF](vf: VF) extends AnyVal {

    // (F := t) ⊗ (S := s) : (F ⊗ S) := (t, s)
    def ⊗[S <: AnyGraphObject, VS](vs: S := VS): (F ⊗ S) := (VF, VS) =
      new Denotes( (vf, vs.value) )

    // (F := t) ⊕ (S := s) : (F ⊕ S) := (t, s)
    def ⊕[S <: AnyGraphObject, VS](vs: S := VS): (F ⊕ S) := (VF, VS) =
      new Denotes( (vf, vs.value) )
  }

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

    def ===(value: P#Value#Raw): Equal[P] = Equal(property, value)
    def =/=(value: P#Value#Raw): NotEqual[P] = NotEqual(property, value)

    def <(value: P#Value#Raw): Less[P] = Less(property, value)
    def ≤(value: P#Value#Raw): LessOrEqual[P] = LessOrEqual(property, value)

    def >(value: P#Value#Raw): Greater[P] = Greater(property, value)
    def ≥(value: P#Value#Raw): GreaterOrEqual[P] = GreaterOrEqual(property, value)

    def between(s: P#Value#Raw, e: P#Value#Raw): Interval[P] = Interval(property, s, e)
  }
}
