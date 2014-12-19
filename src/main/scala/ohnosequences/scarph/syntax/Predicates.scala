package ohnosequences.scarph.syntax

import ohnosequences.cosas._, types._
import ohnosequences.scarph._

object predicates {

  /* When you don't want to restrict the query anyhow (let's imagine it makes sence),
     you can just say: `query(user).out(..).blah.evalOn(any(user))` */
  def any[E <: AnyElementType](e: E): EmptyPredicate[E] = new EmptyPredicate[E](e)


  /* Every predicate already knows it's own label type, so let's make this labeling implicit */
  implicit def labeledPredicate[E <: AnyElementType, P <: AnyPredicate { type ElementType = E }](p: P):
      (P Denotes PredicateType[E]) =
  new (P Denotes PredicateType[E])(p)

  // implicit def elementLabeledPredicate[E <: AnyElementType](e: E):
  //     (EmptyPredicate[E] Denotes PredicateType[E]) =
  // new (EmptyPredicate[E] Denotes PredicateType[E])(new EmptyPredicate[E](e))


  /* A way of building a predicate from an element */
  implicit def elementPredicateOps[E <: AnyElementType](elem: E):
      ElementPredicateOps[E] = 
      ElementPredicateOps[E](elem)

  case class ElementPredicateOps[E <: AnyElementType](elem: E) {

    /* For example: `user ? (name === "bob")` - this operator can be read as "such that" */
    def ?[C <: AnyCondition.OnElementType[E]](c: C): 
      AndPredicate[EmptyPredicate[E], C] = AndPredicate(new EmptyPredicate(elem), c)
  }


  /* Adding more conditions to a predicate */
  implicit def predicateOps[P <: AnyPredicate](p: P):
      PredicateOps[P] = 
      PredicateOps[P](p)

  case class PredicateOps[P <: AnyPredicate](pred: P) {

    /* It's basically cons for the internal conditions type-set, 
       but with a restriction on the condtion's element type */
    def and[C <: AnyCondition.OnElementType[P#ElementType]](c: C): 
      AndPredicate[P, C] = AndPredicate(pred, c)
  }
}

