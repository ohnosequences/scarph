package ohnosequences.scarph.syntax

object predicates {

  import ohnosequences.cosas._, types._

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.conditions._, s.predicates._, s.morphisms._


  /* When you don't want to restrict the query anyhow (let's imagine it makes sence),
     you can just say: `query(user).out(..).blah.evalOn(any(user))` */
  //implicit def any[E <: AnyGraphElement](e: E): EmptyPredicate[E] = EmptyPredicate[E](e)


  /* A way of building a predicate from an element */
  implicit def elementPredicateOps[E <: AnyGraphElement](elem: E):
      ElementPredicateOps[E] =
      ElementPredicateOps[E](elem)

  case class ElementPredicateOps[E <: AnyGraphElement](elem: E) {

    /* For example: `user suchThat (name === "bob")` */
    def suchThat[P <: AnyPredicate.On[E]](p: P):
      quantify[P] =
      quantify(p)
  }

//  implicit def conditionToPredicate[C <: AnyCondition](c: C):
//    AndPredicate[EmptyPredicate[C#Element], C] =
//    AndPredicate(EmptyPredicate(c.element), c)

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

  /* Adding more conditions to a predicate */
  implicit def PredicateInitOps[C1 <: AnyCondition](c1: C1):
      PredicateInitOps[C1] =
      PredicateInitOps[C1](c1)

  case class PredicateInitOps[C1 <: AnyCondition](c1: C1) {

//    def and[C2 <: AnyCondition.OnElement[C1#Element]](c2: C2):
//      AndPredicate[AndPredicate[EmptyPredicate[C1#Element], C1], C2] =
//      AndPredicate(AndPredicate(EmptyPredicate(c1.element), c1), c2)
  }
}
