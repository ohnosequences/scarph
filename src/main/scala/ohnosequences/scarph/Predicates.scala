package ohnosequences.scarph

object predicates {

  import ohnosequences.cosas._, typeSets._
  import graphTypes._, conditions._


  trait AnyPredicate extends AnySimpleGraphType {

    type Element <: AnyGraphElement
    val  element: Element

    type Conditions <: AnyTypeSet //.Of[AnyCondition]
    val  conditions: Conditions

    lazy val label: String = s"(${element.label} ? ${conditions.toString})"
  }

  object AnyPredicate {

    type On[E <: AnyGraphElement] = AnyPredicate { type Element = E }
  }

  /* Empty predicate doesn't have any restrictions */
  trait AnyEmptyPredicate extends AnyPredicate {
    type Conditions = ∅
    val  conditions = ∅
  }

  class EmptyPredicate[E <: AnyGraphElement](val element: E) 
    extends AnyEmptyPredicate { 

      type Element = E
      type Inside = this.type
    }


  /* This is just like cons, but controlling, that all conditions are on the same element type */
  trait AnyAndPredicate extends AnyPredicate {

    type Body <: AnyPredicate
    val  body: Body

    type     Element = Body#Element
    lazy val element = body.element

    type Condition <: AnyCondition.OnElement[Body#Element]
    val  condition: Condition

    type     Conditions = Condition :~: Body#Conditions
    lazy val conditions = condition :~: (body.conditions: Body#Conditions)
  }

  case class AndPredicate[B <: AnyPredicate, C <: AnyCondition.OnElement[B#Element]]
    (val body: B, val condition: C) extends AnyAndPredicate {

    type Body = B
    type Condition = C

    type Inside = AndPredicate[B,C]
  }

}
