package ohnosequences.scarph

import ohnosequences.pointless._

trait AnyQuery {

  type InT <: AnyType
  val  inT: InT

  type OutT <: AnyType
  val  outT: OutT
}



trait AnyStep extends AnyQuery

abstract class Step[I <: AnyType, O <: AnyType](val inT: I, val outT: O) extends AnyStep {

  type InT = I
  type OutT = O
}


trait AnyComposition extends AnyQuery {

  type Body <: AnyQuery
  val  body: Body

  type Head <: AnyStep { type InT = Body#OutT }
  val  head: Head

  type InT = Body#InT
  val  inT = body.inT

  type MidT = Head#InT
  val  midT = head.inT

  type OutT = Head#OutT
  val  outT = head.outT
}

case class >=>[B <: AnyQuery, H <: AnyStep { type InT = B#OutT }](val body: B, val head: H) extends AnyComposition {

  type Body = B
  type Head = H
}


// Some basic steps:

case class GetProperty[T <: AnyElementType, P <: AnyProperty](t: T, p: P) extends Step[T, P](t, p)

case class GetSource[ET <: AnyEdgeType](et: ET) extends Step[ET, ET#SourceType](et, et.sourceType)
case class GetTarget[ET <: AnyEdgeType](et: ET) extends Step[ET, ET#TargetType](et, et.targetType)

case class  GetInEdges[ET <: AnyEdgeType](et: ET) extends Step[ET#TargetType, ET](et.targetType, et)
case class GetOutEdges[ET <: AnyEdgeType](et: ET) extends Step[ET#SourceType, ET](et.sourceType, et)

// case class  GetInVertices[ET <: AnyEdgeType](et: ET) extends Query[ET#TargetType, ET#SourceType](et.targetType, et.sourceType)
// case class GetOutVertices[ET <: AnyEdgeType](et: ET) extends Query[ET#SourceType, ET#TargetType](et.sourceType, et.targetType)

// TODO: a step for querying index by a predicate

/*  The idea is that we can combine them into a "query":
GetSource(Liked) >=> GetOutEdges(Follows) >=> GetProperty(User, age)
*/


// We can define some nice DSL for this, but it's optional

// object AnyQuery {
//   /*type withOutT[T <: AnyType] = AnyQuery { type OutT <: T }*/

//   implicit def typeToQuery[T <: AnyType](t: T): IdQuery[T] = IdQuery[T](t)

//   implicit def queryElemOps[Q <: AnyQuery { type OutT <: AnyElementType }](q: Q): QueryElemOps[Q] = new QueryElemOps[Q](q)

//   implicit def queryVertexOps[Q <: AnyQuery { type OutT <: AnyVertexType }](q: Q): QueryVertexOps[Q] = new QueryVertexOps[Q](q)

//   implicit def queryEdgeOps[X, Q <: AnyQuery { type OutT <: AnyEdgeType }]
//     (x: X)(implicit conv: X => Q):
//         QueryEdgeOps[Q] =
//     new QueryEdgeOps[Q](conv(x))

//   /*implicit def queryEdgeOps[Q <: AnyQuery { type OutT <: AnyEdgeType }](q: Q): QueryEdgeOps[Q] = new QueryEdgeOps[Q](q)*/
// }

// class QueryElemOps[Q <: AnyQuery { type OutT <: AnyElementType }](val q: Q) {

//   type ET = Q#OutT
//   val  et = q.outT

//   def get[P <: AnyProperty](p: P):
//     Compose[Q, GetProperty[ET, P]] =
//     Compose[Q, GetProperty[ET, P]](q, GetProperty(et, p))
// }

// class QueryVertexOps[Q <: AnyQuery { type OutT <: AnyVertexType }](val q: Q) {

//   type VT = Q#OutT

//   def inE[ET <: AnyEdgeType { type TargetType = VT }](et: ET):
//       Compose[Q, GetInEdges[ET]] =
//       Compose[Q, GetInEdges[ET]](q, GetInEdges(et))

//   def outE[ET <: AnyEdgeType { type SourceType = VT }](et: ET):
//       Compose[Q, GetOutEdges[ET]] =
//       Compose[Q, GetOutEdges[ET]](q, GetOutEdges(et))
// }

// class QueryEdgeOps[Q <: AnyQuery { type OutT <: AnyEdgeType }](val q: Q) {

//   type ET = Q#OutT
//   val  et = q.outT

//   def source:
//       Compose[Q, GetSource[ET]] =
//       Compose[Q, GetSource[ET]](q, GetSource(et))

//   def target:
//       Compose[Q, GetTarget[ET]] =
//       Compose[Q, GetTarget[ET]](q, GetTarget(et))
// }


trait AnyEvalStep[Q <: AnyQuery] extends AnyFn2 {
  type Query = Q
  type InW  <: AnyDenotation { type Tpe <: Query#InT }
  type OutW <: AnyDenotation { type Tpe <: Query#OutT }

  type In1 = Query
  type In2 = Container[ValueOf[InW]]
  type Out = Container[ValueOf[OutW]]
}

trait EvalStep[
  Q <: AnyQuery, 
  I <: AnyDenotation { type Tpe <: Q#InT },
  O <: AnyDenotation { type Tpe <: Q#OutT }
] extends AnyEvalStep[Q] {

  type InW = I
  type OutW = O
}


trait EvalOnSchema[Q <: AnyQuery, S <: AnySchema] extends AnyEvalStep[Q] {
  type Schema = S
}

object EvalOnSchema {

  implicit def simple[
    Q <: AnyStep,
    S <: AnySchema,
    I <: Implementation[S, Q#InT],
    O <: Implementation[S, Q#OutT]
  ](implicit
    i: I, o: O,
    ev: EvalStep[Q, I, O]
  ): EvalOnSchema[Q, S] =
    new EvalOnSchema[Q, S] {
      type InW = I
      type OutW = O
      def apply(q: In1, in: In2): Out = ev(q, in)
    }


  // implicit def composition[
  //   S <: AnySchema,
  //   B <: AnyQuery,
  //   H <: AnyStep { type InT = B#OutT },
  //   I <: Implementation[S, B#InT],
  //   M <: Implementation[S, H#InT],
  //   O <: Implementation[S, H#OutT]
  // ](implicit
  //   i: I,
  //   m: M,
  //   o: O,
  //   evBody: EvalOnSchema[B, S] { type InW = I; type OutW = M },
  //   evHead: EvalStep[H, M, O]
  // ):  EvalOnSchema[B >=> H, S] =
  // new EvalOnSchema[B >=> H, S] {

  //   type InW = I
  //   type OutW = O

  //   def apply(q: In1, in: In2): Out = {
  //     val o1 = evBody(q.body, in)
  //     evHead(q.head, o1)
  //   }
  // }


}
