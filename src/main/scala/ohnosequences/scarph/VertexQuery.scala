// package ohnosequences.scarph


// /*
// # Vertex queries

// Or vertex-local queries, or pullbacks.

// */
// trait AnyVertexLocalQuery extends AnyQuery {
  
//   // refine
//   type Body <: AnyVertexLocalQuery
//   val  body: Body

//   type Head <: AnyCondition
//   val  head: Head

//   type TYPE = AnyEdgeType
//   type ItemType <: Singleton with TYPE
//   val  itemType: ItemType

//   type Out[X]
// }

// object AnyVertexLocalQuery {


//   type HeadedBy[C <: AnyCondition] = AnyVertexLocalQuery { type Head <: C }

//   type On[I] = AnyVertexLocalQuery { type ItemType = I }

//   /* 
//     With this you can write `item ? condition` which means `SimpleQuery(item, condition)`
//   */
//   implicit def vertexQueryOps[VT <: Singleton with AnyVertexType](vt: VT): VertexQueryOps[VT] = VertexQueryOps(vt)
//   case class   VertexQueryOps[VT <: Singleton with AnyVertexType](vt: VT) {
//     def ?[C <: AnyCondition](c: C)(implicit 
//         ev: VT HasProperty c.Property
//       ): VertexQuery[VT, C] = VertexQuery(vt, c)
//   }

//   implicit def edgeQueryOps[ET <: Singleton with AnyEdgeType](et: ET): EdgeQueryOps[ET] = EdgeQueryOps(et)
//   case class   EdgeQueryOps[ET <: Singleton with AnyEdgeType](et: ET) {
//     def ?[C <: AnyCondition](c: C)(implicit 
//         ev: ET HasProperty c.Property
//       ): EdgeQuery[ET, C] = EdgeQuery(et, c)
//   }
// }


// /* 
//   ### Simple Queries

//   It contains only one condition and can be extended either to `OR` or `AND` predicate
// */
// trait AnySimpleVertexLocalQuery extends AnyOrQuery with AnyAndQuery with AnyVertexLocalQuery {

//   type Body = this.type
//   val  body = this: this.type
// }

// case class VertexLocalQuery[
//   E <: Singleton with AnyEdgeType,
//   C <: AnyCondition
// ](
//   val itemType : E,
//   val head : C
// ) extends AnySimpleVertexLocalQuery {

//   type ItemType = E 
//   type Head = C
// }
