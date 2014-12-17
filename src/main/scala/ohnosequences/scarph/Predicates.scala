package ohnosequences.scarph

import ohnosequences.cosas._, AnyTypeSet._


/* Predicate is a restriction on properties of an element type */
trait AnyPredicateType extends AnyGraphType {
  
  type ElementType <: AnyElementType
  val  elementType: ElementType

  val label = s"PredicateOn(${elementType.label})"
}

object AnyPredicateType {

  type On[E <: AnyElementType] = AnyPredicateType { type ElementType = E }
}

case class PredicateType[E <: AnyElementType](val elementType: E) 
  extends AnyPredicateType { type ElementType = E }



trait AnyPredicate {

  type ElementType <: AnyElementType
  val  elementType: ElementType

  type Conditions <: AnyTypeSet //.Of[AnyCondition] //.OnElementType[ElementType]]
  val  conditions: Conditions
}

object AnyPredicate {

  type On[E <: AnyElementType] = AnyPredicate { type ElementType = E }
}

/* Empty predicate doesn't have any restrictions */
trait AnyEmptyPredicate extends AnyPredicate {
  type Conditions = ∅
  val  conditions = ∅
}

class EmptyPredicate[E <: AnyElementType](val elementType: E) 
  extends AnyEmptyPredicate { type ElementType = E }


/* This is just like cons, but controlling, that all conditions are on the same element type */
trait AnyAndPredicate extends AnyPredicate {

  type Body <: AnyPredicate
  val  body: Body

  type ElementType = Body#ElementType
  val  elementType = body.elementType

  type Condition <: AnyCondition.OnElementType[Body#ElementType]
  val  condition: Condition

  type Conditions = Condition :~: Body#Conditions
  val  conditions = condition :~: (body.conditions: Body#Conditions)
}

case class AndPredicate[B <: AnyPredicate, C <: AnyCondition.OnElementType[B#ElementType]]
  (val body: B, val condition: C) extends AnyAndPredicate {

  type Body = B
  type Condition = C
}
