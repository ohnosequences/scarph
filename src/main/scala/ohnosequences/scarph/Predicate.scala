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
  type ItemType <: Singleton with TYPE
  val  itemType: ItemType
}

/*
  ### OR Predicates
*/
trait AnyOrPredicate extends AnyPredicate {

  type Body <: AnyOrPredicate

  def or[Head <: AnyCondition](other: Head)(implicit 
    ev: ItemType HasProperty other.Property
  ): OR[Body, Head] = 
     OR(body, other)
}

case class OR[B <: AnyOrPredicate, H <: AnyCondition]
  (val body : B,  val head : H) extends AnyOrPredicate {
  type Body = B; type Head = H

  type TYPE = body.TYPE
  type ItemType = body.ItemType
  val  itemType = body.itemType
} 


/* 
  ### AND Predicates
*/
trait AnyAndPredicate extends AnyPredicate {

  type Body <: AnyAndPredicate

  def and[Head <: AnyCondition](other: Head)(implicit 
    ev: ItemType HasProperty other.Property
  ): AND[Body, Head] = 
     AND(body, other)
}

case class AND[B <: AnyAndPredicate, H <: AnyCondition]
  (val body : B,  val head : H) extends AnyAndPredicate {
  type Body = B; type Head = H

  type TYPE = body.TYPE
  type ItemType = body.ItemType
  val  itemType = body.itemType 
}


/* 
  ### Simple Predicates

  It contains only one condition and can be extended either to `OR` or `AND` predicate
*/
trait AnySimplePredicate extends AnyOrPredicate with AnyAndPredicate {
  type Body = this.type
  val  body = this: this.type
}

trait AnyVertexPredicate extends AnySimplePredicate {
 type TYPE = AnyVertexType
}

case class VertexPredicate[I <: Singleton with AnyVertexType, C <: AnyCondition]
  (val itemType : I,  val head : C) extends AnyVertexPredicate {
  type ItemType = I; type Head = C
}


trait AnyEdgePredicate extends AnySimplePredicate {
  type TYPE = AnyEdgeType
}

case class EdgePredicate[I <: Singleton with AnyEdgeType, C <: AnyCondition]
  (val itemType : I,  val head : C) extends AnyEdgePredicate {
  type ItemType = I; type Head = C
}


object AnyPredicate {

  type HeadedBy[C <: AnyCondition] = AnyPredicate { type Head = C }

  type On[I] = AnyPredicate { type ItemType = I }

  /* 
    With this you can write `item ? condition` which means `SimplePredicate(item, condition)`
  */
  implicit def vertexPredicateOps[VT <: Singleton with AnyVertexType](vt: VT): VertexPredicateOps[VT] = VertexPredicateOps(vt)
  case class   VertexPredicateOps[VT <: Singleton with AnyVertexType](vt: VT) {
    def ?[C <: AnyCondition](c: C)(implicit 
        ev: VT HasProperty c.Property
      ): VertexPredicate[VT, C] = VertexPredicate(vt, c)
  }

  implicit def edgePredicateOps[ET <: Singleton with AnyEdgeType](et: ET): EdgePredicateOps[ET] = EdgePredicateOps(et)
  case class   EdgePredicateOps[ET <: Singleton with AnyEdgeType](et: ET) {
    def ?[C <: AnyCondition](c: C)(implicit 
        ev: ET HasProperty c.Property
      ): EdgePredicate[ET, C] = EdgePredicate(et, c)
  }
}
