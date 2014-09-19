package ohnosequences.scarph

import ohnosequences.pointless._

/*
  ## Queries

  Queries represent expressions combining several conditions for a particular item.
  You can combine conditions **either** by `OR` or by `AND` conditional operator (_you can't mix them_).
  Query constructors check that the item has the attribute used in the applied condition.
*/
trait AnyQuery {
  type Body <: AnyQuery
  val  body: Body

  type Head <: AnyCondition
  val  head: Head

  type ElementType <: AnyElementType
  val  elementType: ElementType

  type Out[X] = List[X]
}

/*
  ### OR Queries
*/
trait AnyOrQuery extends AnyQuery {

  type Body <: AnyOrQuery

  def or[Head <: AnyCondition](other: Head)(implicit 
    ev: ElementType HasProperty other.Property
  ): OR[Body, Head] = 
     OR(body, other)
}

case class OR[B <: AnyOrQuery, H <: AnyCondition]
  (val body : B,  val head : H) extends AnyOrQuery {
  type Body = B; type Head = H

  type ElementType = body.ElementType
  val  elementType = body.elementType
} 


/* 
  ### AND Queries
*/
trait AnyAndQuery extends AnyQuery {

  type Body <: AnyAndQuery

  def and[Head <: AnyCondition](other: Head)(implicit 
    ev: ElementType HasProperty other.Property
  ): AND[Body, Head] = 
     AND(body, other)
}

case class AND[B <: AnyAndQuery, H <: AnyCondition]
  (val body : B,  val head : H) extends AnyAndQuery {
  type Body = B; type Head = H

  type ElementType = body.ElementType
  val  elementType = body.elementType 
}


/* 
  ### Simple Queries

  It contains only one condition and can be extended either to `OR` or `AND` predicate
*/
trait AnySimpleQuery extends AnyOrQuery with AnyAndQuery {
  type Body = this.type
  val  body = this: this.type
}

case class SimpleQuery[E <: AnyElementType, C <: AnyCondition]
  (val elementType : E,  val head : C) extends AnySimpleQuery {
  type ElementType = E; type Head = C
}

// trait AnyVertexQuery extends AnySimpleQuery {
//  type TYPE = AnyVertexType
// }

// case class VertexQuery[I <: AnyVertexType, C <: AnyCondition]
//   (val elementType : I,  val head : C) extends AnyVertexQuery {
//   type ElementType = I; type Head = C
// }


// trait AnyEdgeQuery extends AnySimpleQuery {
//   type TYPE = AnyEdgeType
// }

// case class EdgeQuery[I <: AnyEdgeType, C <: AnyCondition]
//   (val elementType : I,  val head : C) extends AnyEdgeQuery {
//   type ElementType = I; type Head = C
// }


object AnyQuery {

  type HeadedBy[C <: AnyCondition] = AnyQuery { type Head <: C }

  type On[I] = AnyQuery { type ElementType = I }

  /* 
    With this you can write `item ? condition` which means `SimpleQuery(item, condition)`
  */
  implicit def elementQueryOps[ET <: AnyElementType](et: ET): ElementQueryOps[ET] = ElementQueryOps(et)
  case class   ElementQueryOps[ET <: AnyElementType](et: ET) {
    def ?[C <: AnyCondition](c: C)(implicit 
        ev: ET HasProperty c.Property
      ): SimpleQuery[ET, C] = SimpleQuery(et, c)
  }

  // implicit def edgeQueryOps[ET <: AnyEdgeType](et: ET): EdgeQueryOps[ET] = EdgeQueryOps(et)
  // case class   EdgeQueryOps[ET <: AnyEdgeType](et: ET) {
  //   def ?[C <: AnyCondition](c: C)(implicit 
  //       ev: ET HasProperty c.Property
  //     ): EdgeQuery[ET, C] = EdgeQuery(et, c)
  // }

  
}
