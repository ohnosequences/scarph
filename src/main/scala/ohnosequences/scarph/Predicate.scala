package ohnosequences.scarph

import ohnosequences.typesets._

/*
  ## Predicates

  Predicates represent expressions combining several conditions for a particular item.
  You can combine conditions **either** by `OR` or by `AND` conditional operator (_you can't mix them_).
  Predicate constructors check that the item has the attribute used in the applied condition.
*/
trait AnyPredicate {
  type Body <: AnyPredicate
  val  body: Body

  type Head <: AnyCondition
  val  head: Head

  type TYPE
  type Item <: Singleton with TYPE
  val  item: Item
}

/*
  ### OR Predicates
*/
trait AnyOrPredicate extends AnyPredicate {

  type Body <: AnyOrPredicate

  def or[Head <: AnyCondition](other: Head)(implicit 
    ev: Item HasProperty other.Property
  ): OR[Body, Head] = 
     OR(body, other)
}

case class OR[B <: AnyOrPredicate, H <: AnyCondition]
  (val body : B,  val head : H) extends AnyOrPredicate {
  type Body = B; type Head = H

  type TYPE = body.TYPE
  type Item = body.Item
  val  item = body.item
} 


/* 
  ### AND Predicates
*/
trait AnyAndPredicate extends AnyPredicate {

  type Body <: AnyAndPredicate

  def and[Head <: AnyCondition](other: Head)(implicit 
    ev: Item HasProperty other.Property
  ): AND[Body, Head] = 
     AND(body, other)
}

case class AND[B <: AnyAndPredicate, H <: AnyCondition]
  (val body : B,  val head : H) extends AnyAndPredicate {
  type Body = B; type Head = H

  type TYPE = body.TYPE
  type Item = body.Item
  val  item = body.item 
}


/* 
  ### Simple Predicates

  It contains only one condition and can be extended either to `OR` or `AND` predicate
*/
trait AnyVertexPredicate extends AnyOrPredicate with AnyAndPredicate {
  type Body = this.type
  val  body = this: this.type

  type TYPE = AnyVertexType
}

case class VertexPredicate[I <: Singleton with AnyVertexType, C <: AnyCondition]
  (val item : I,  val head : C) extends AnyVertexPredicate {
  type Item = I; type Head = C
}

trait AnyEdgePredicate extends AnyOrPredicate with AnyAndPredicate {
  type Body = this.type
  val  body = this: this.type

  type TYPE = AnyEdgeType
}

case class EdgePredicate[I <: Singleton with AnyEdgeType, C <: AnyCondition]
  (val item : I,  val head : C) extends AnyEdgePredicate {
  type Item = I; type Head = C
}


object AnyPredicate {

  type HeadedBy[C <: AnyCondition] = AnyPredicate { type Head <: C }

  type On[I] = AnyPredicate { type Item = I }

  /* 
    With this you can write `item ? condition` which means `SimplePredicate(item, condition)`
  */
  implicit def vertexPredicateOps[I <: Singleton with AnyVertexType](item: I): VertexPredicateOps[I] = VertexPredicateOps(item)
  case class   VertexPredicateOps[I <: Singleton with AnyVertexType](item: I) {
    def ?[C <: AnyCondition](c: C)(implicit 
        ev: I HasProperty c.Property
      ): VertexPredicate[I, C] = VertexPredicate(item, c)
  }

  implicit def edgePredicateOps[I <: Singleton with AnyEdgeType](item: I): EdgePredicateOps[I] = EdgePredicateOps(item)
  case class   EdgePredicateOps[I <: Singleton with AnyEdgeType](item: I) {
    def ?[C <: AnyCondition](c: C)(implicit 
        ev: I HasProperty c.Property
      ): EdgePredicate[I, C] = EdgePredicate(item, c)
  }
}
