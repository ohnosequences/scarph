package ohnosequences.scarph

import ohnosequences.pointless._

trait AnyQuery extends AnyFn{

  type InT  <: AnyType
  type OutT <: AnyType
}

trait Query[I <: AnyType, O <: AnyType] extends AnyQuery {

  type InT = I
  type OutT = O
}

case class GetProperty[T <: AnyElementType, P <: AnyProperty](t: T, p: P) extends Query[T, P]

case class GetSource[ET <: AnyEdgeType](et: ET) extends Query[ET, ET#SourceType]
case class GetTarget[ET <: AnyEdgeType](et: ET) extends Query[ET, ET#TargetType]

case class  GetInEdges[ET <: AnyEdgeType](et: ET) extends Query[ET#TargetType, ET]
case class GetOutEdges[ET <: AnyEdgeType](et: ET) extends Query[ET#SourceType, ET]

case class  GetInVertices[ET <: AnyEdgeType](et: ET) extends Query[ET#TargetType, ET#SourceType]
case class GetOutVertices[ET <: AnyEdgeType](et: ET) extends Query[ET#SourceType, ET#TargetType]


case class Compose[Q1 <: AnyQuery { type OutT <: Q2#InT }, Q2 <: AnyQuery](q1: Q1, q2: Q2) extends Query[Q1#InT, Q2#OutT]


trait EvalQuery[
  Q <: AnyQuery,
  I <: AnyDenotation { type Tpe <: Q#InT },
  O <: AnyDenotation { type Tpe <: Q#OutT }
] extends Fn1[I#Raw] with Out[ValueOf[O]]

// /*
//   ## Queries

//   Queries represent expressions combining several conditions for a particular item.
//   You can combine conditions **either** by `OR` or by `AND` conditional operator (_you can't mix them_).
//   Query constructors check that the item has the attribute used in the applied condition.
// */
// trait AnyQuery {
//   type Body <: AnyQuery
//   val  body: Body

//   type Head <: AnyCondition
//   val  head: Head

//   type ElementType <: AnyElementType
//   val  elementType: ElementType

//   type Out[X] = List[X]
// }

// /*
//   ### OR Queries
// */
// trait AnyOrQuery extends AnyQuery {

//   type Body <: AnyOrQuery

//   def or[Head <: AnyCondition](other: Head)(implicit 
//     ev: ElementType HasProperty other.Property
//   ): OR[Body, Head] = 
//      OR(body, other)
// }

// case class OR[B <: AnyOrQuery, H <: AnyCondition]
//   (val body : B,  val head : H) extends AnyOrQuery {
//   type Body = B; type Head = H

//   type ElementType = body.ElementType
//   val  elementType = body.elementType
// } 


// /* 
//   ### AND Queries
// */
// trait AnyAndQuery extends AnyQuery {

//   type Body <: AnyAndQuery

//   def and[Head <: AnyCondition](other: Head)(implicit 
//     ev: ElementType HasProperty other.Property
//   ): AND[Body, Head] = 
//      AND(body, other)
// }

// case class AND[B <: AnyAndQuery, H <: AnyCondition]
//   (val body : B,  val head : H) extends AnyAndQuery {
//   type Body = B; type Head = H

//   type ElementType = body.ElementType
//   val  elementType = body.elementType 
// }


// /* 
//   ### Simple Queries

//   It contains only one condition and can be extended either to `OR` or `AND` predicate
// */
// trait AnySimpleQuery extends AnyOrQuery with AnyAndQuery {
//   type Body = this.type
//   val  body = this: this.type
// }

// case class SimpleQuery[E <: AnyElementType, C <: AnyCondition]
//   (val elementType : E,  val head : C) extends AnySimpleQuery {
//   type ElementType = E; type Head = C
// }

// // trait AnyVertexQuery extends AnySimpleQuery {
// //  type TYPE = AnyVertexType
// // }

// // case class VertexQuery[I <: AnyVertexType, C <: AnyCondition]
// //   (val elementType : I,  val head : C) extends AnyVertexQuery {
// //   type ElementType = I; type Head = C
// // }


// // trait AnyEdgeQuery extends AnySimpleQuery {
// //   type TYPE = AnyEdgeType
// // }

// // case class EdgeQuery[I <: AnyEdgeType, C <: AnyCondition]
// //   (val elementType : I,  val head : C) extends AnyEdgeQuery {
// //   type ElementType = I; type Head = C
// // }


// object AnyQuery {

//   type HeadedBy[C <: AnyCondition] = AnyQuery { type Head <: C }

//   type On[I] = AnyQuery { type ElementType = I }

//   /* 
//     With this you can write `item ? condition` which means `SimpleQuery(item, condition)`
//   */
//   implicit def elementQueryOps[ET <: AnyElementType](et: ET): ElementQueryOps[ET] = ElementQueryOps(et)
//   case class   ElementQueryOps[ET <: AnyElementType](et: ET) {
//     def ?[C <: AnyCondition](c: C)(implicit 
//         ev: ET HasProperty c.Property
//       ): SimpleQuery[ET, C] = SimpleQuery(et, c)
//   }

//   // implicit def edgeQueryOps[ET <: AnyEdgeType](et: ET): EdgeQueryOps[ET] = EdgeQueryOps(et)
//   // case class   EdgeQueryOps[ET <: AnyEdgeType](et: ET) {
//   //   def ?[C <: AnyCondition](c: C)(implicit 
//   //       ev: ET HasProperty c.Property
//   //     ): EdgeQuery[ET, C] = EdgeQuery(et, c)
//   // }

  
// }
