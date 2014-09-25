package ohnosequences.scarph

import ohnosequences.pointless._

trait AnyQuery {

  type InT <: AnyType
  val  inT: InT

  type OutT <: AnyType
  val  outT: OutT
}

abstract class Query[I <: AnyType, O <: AnyType](val inT: I, val outT: O) extends AnyQuery {

  type InT = I
  type OutT = O
}

case class GetProperty[T <: AnyElementType, P <: AnyProperty](t: T, p: P) extends Query[T, P](t, p)

case class GetSource[ET <: AnyEdgeType](et: ET) extends Query[ET, ET#SourceType](et, et.sourceType)
case class GetTarget[ET <: AnyEdgeType](et: ET) extends Query[ET, ET#TargetType](et, et.targetType)

case class  GetInEdges[ET <: AnyEdgeType](et: ET) extends Query[ET#TargetType, ET](et.targetType, et)
case class GetOutEdges[ET <: AnyEdgeType](et: ET) extends Query[ET#SourceType, ET](et.sourceType, et)

case class  GetInVertices[ET <: AnyEdgeType](et: ET) extends Query[ET#TargetType, ET#SourceType](et.targetType, et.sourceType)
case class GetOutVertices[ET <: AnyEdgeType](et: ET) extends Query[ET#SourceType, ET#TargetType](et.sourceType, et.targetType)


trait AnyCompose extends AnyQuery { comp =>
  type MidT <: AnyType

  type Query1 <: AnyQuery { type InT <: comp.InT; type OutT <: comp.MidT }
  val  query1: Query1

  type Query2 <: AnyQuery { type InT <: comp.MidT; type OutT <: comp.OutT }
  val  query2: Query2

  val  inT  = query1.inT
  val  midT = query1.outT
  val  outT = query2.outT
}

case class Compose[
  Q1 <: AnyQuery, Q2 <: AnyQuery { type InT = Q1#OutT }
](val query1: Q1, val query2: Q2)
  extends AnyCompose {
    type InT = Q1#InT
    type MidT = Q1#OutT
    type OutT = Q2#OutT

    type Query1 = Q1
    type Query2 = Q2
  }

object AnyQuery {

  implicit def queryElemOps[Q <: AnyQuery { type OutT <: AnyElementType }](q: Q): QueryElemOps[Q] = new QueryElemOps[Q](q)
}

class QueryElemOps[Q <: AnyQuery { type OutT <: AnyElementType }](q: Q) {

  def get[P <: AnyProperty](p: P): 
    Compose[Q, GetProperty[Q#OutT, P]] = 
    Compose[Q, GetProperty[Q#OutT, P]](q, GetProperty(q.outT, p))
}


trait AnyEvalQuery[Q <:AnyQuery] extends AnyFn2 {
  type InW <: AnyDenotation
  type OutW <: AnyDenotation

  type In1 = Q
  type In2 = InW#Raw
  type Out = ValueOf[OutW]
}

trait EvalQuery[
  Q <: AnyQuery,
  I0 <: AnyDenotation { type Tpe <: Q#InT },
  O0 <: AnyDenotation { type Tpe <: Q#OutT }
] extends AnyEvalQuery[Q] {
  type InW = I0
  type OutW = O0
}
