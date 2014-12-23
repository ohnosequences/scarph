package ohnosequences.scarph

object predicates {

  import ohnosequences.cosas._, typeSets._
  import graphTypes._, conditions._


  trait AnyPredicate {

    type ElementType <: AnyGraphElement
    val  elementType: ElementType

    type Conditions <: AnyTypeSet //.Of[AnyCondition]
    val  conditions: Conditions
  }

  object AnyPredicate {

    type On[E <: AnyGraphElement] = AnyPredicate { type ElementType = E }
  }

  /* Empty predicate doesn't have any restrictions */
  trait AnyEmptyPredicate extends AnyPredicate {
    type Conditions = ∅
    val  conditions = ∅
  }

  class EmptyPredicate[E <: AnyGraphElement](val elementType: E) 
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

}
