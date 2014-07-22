package ohnosequences.scarph

import ohnosequences.typesets._

/*
  ## Querys

  Querys represent expressions combining several conditions for a particular item.
  You can combine conditions **either** by `OR` or by `AND` conditional operator (_you can't mix them_).
  Query constructors check that the item has the attribute used in the applied condition.
*/
trait AnyQuery {
  type Body <: AnyQuery
  val  body: Body

  type Head <: AnyCondition
  val  head: Head

  type TYPE <: AnyItemType
  type ItemType <: Singleton with TYPE
  val  itemType: ItemType

  type Out[X] = List[X]
}

/*
  ### OR Querys
*/
trait AnyOrQuery extends AnyQuery {

  type Body <: AnyOrQuery

  def or[Head <: AnyCondition](other: Head)(implicit 
    ev: ItemType HasProperty other.Property
  ): OR[Body, Head] = 
     OR(body, other)
}

case class OR[B <: AnyOrQuery, H <: AnyCondition]
  (val body : B,  val head : H) extends AnyOrQuery {
  type Body = B; type Head = H

  type TYPE = body.TYPE
  type ItemType = body.ItemType
  val  itemType = body.itemType
} 


/* 
  ### AND Querys
*/
trait AnyAndQuery extends AnyQuery {

  type Body <: AnyAndQuery

  def and[Head <: AnyCondition](other: Head)(implicit 
    ev: ItemType HasProperty other.Property
  ): AND[Body, Head] = 
     AND(body, other)
}

case class AND[B <: AnyAndQuery, H <: AnyCondition]
  (val body : B,  val head : H) extends AnyAndQuery {
  type Body = B; type Head = H

  type TYPE = body.TYPE
  type ItemType = body.ItemType
  val  itemType = body.itemType 
}


/* 
  ### Simple Querys

  It contains only one condition and can be extended either to `OR` or `AND` predicate
*/
trait AnySimpleQuery extends AnyOrQuery with AnyAndQuery {
  type Body = this.type
  val  body = this: this.type
}

trait AnyVertexQuery extends AnySimpleQuery {
 type TYPE = AnyVertexType
}

case class VertexQuery[I <: Singleton with AnyVertexType, C <: AnyCondition]
  (val itemType : I,  val head : C) extends AnyVertexQuery {
  type ItemType = I; type Head = C
}


trait AnyEdgeQuery extends AnySimpleQuery {
  type TYPE = AnyEdgeType
}

case class EdgeQuery[I <: Singleton with AnyEdgeType, C <: AnyCondition]
  (val itemType : I,  val head : C) extends AnyEdgeQuery {
  type ItemType = I; type Head = C
}


object AnyQuery {

  type HeadedBy[C <: AnyCondition] = AnyQuery { type Head <: C }

  type On[I] = AnyQuery { type ItemType = I }

  /* 
    With this you can write `item ? condition` which means `SimpleQuery(item, condition)`
  */
  implicit def vertexQueryOps[VT <: Singleton with AnyVertexType](vt: VT): VertexQueryOps[VT] = VertexQueryOps(vt)
  case class   VertexQueryOps[VT <: Singleton with AnyVertexType](vt: VT) {
    def ?[C <: AnyCondition](c: C)(implicit 
        ev: VT HasProperty c.Property
      ): VertexQuery[VT, C] = VertexQuery(vt, c)
  }

  implicit def edgeQueryOps[ET <: Singleton with AnyEdgeType](et: ET): EdgeQueryOps[ET] = EdgeQueryOps(et)
  case class   EdgeQueryOps[ET <: Singleton with AnyEdgeType](et: ET) {
    def ?[C <: AnyCondition](c: C)(implicit 
        ev: ET HasProperty c.Property
      ): EdgeQuery[ET, C] = EdgeQuery(et, c)
  }
}
