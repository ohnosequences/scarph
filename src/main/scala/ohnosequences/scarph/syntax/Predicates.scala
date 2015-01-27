package ohnosequences.scarph.syntax

object predicates {

  import ohnosequences.cosas._, types._

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.conditions._, s.predicates._


  /* When you don't want to restrict the query anyhow (let's imagine it makes sence),
     you can just say: `query(user).out(..).blah.evalOn(any(user))` */
  implicit def any[E <: AnyGraphElement](e: E): EmptyPredicate[E] = new EmptyPredicate[E](e)


  /* A way of building a predicate from an element */
  implicit def elementPredicateOps[E <: AnyGraphElement](elem: E):
      ElementPredicateOps[E] = 
      ElementPredicateOps[E](elem)

  case class ElementPredicateOps[E <: AnyGraphElement](elem: E) {

    /* For example: `user ? (name === "bob")` - this operator can be read as "such that" */
    def ?[C <: AnyCondition.OnElement[E]](c: C): 
      AndPredicate[EmptyPredicate[E], C] = AndPredicate(new EmptyPredicate(elem), c)
  }


  /* Adding more conditions to a predicate */
  implicit def predicateOps[P <: AnyPredicate](p: P):
      PredicateOps[P] = 
      PredicateOps[P](p)

  case class PredicateOps[P <: AnyPredicate](pred: P) {

    /* It's basically cons for the internal conditions type-set, 
       but with a restriction on the condtion's element type */
    def and[C <: AnyCondition.OnElement[P#Element]](c: C): 
      AndPredicate[P, C] = AndPredicate(pred, c)
  }
}

