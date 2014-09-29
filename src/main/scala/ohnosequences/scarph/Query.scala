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

case class IdQuery[T <: AnyType](t: T) extends Query[T, T](t, t)

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
  /*type withOutT[T <: AnyType] = AnyQuery { type OutT <: T }*/

  implicit def typeToQuery[T <: AnyType](t: T): IdQuery[T] = IdQuery[T](t)

  implicit def queryElemOps[Q <: AnyQuery { type OutT <: AnyElementType }](q: Q): QueryElemOps[Q] = new QueryElemOps[Q](q)

  implicit def queryVertexOps[Q <: AnyQuery { type OutT <: AnyVertexType }](q: Q): QueryVertexOps[Q] = new QueryVertexOps[Q](q)

  implicit def queryEdgeOps[X, Q <: AnyQuery { type OutT <: AnyEdgeType }]
    (x: X)(implicit conv: X => Q):
        QueryEdgeOps[Q] =
    new QueryEdgeOps[Q](conv(x))

  /*implicit def queryEdgeOps[Q <: AnyQuery { type OutT <: AnyEdgeType }](q: Q): QueryEdgeOps[Q] = new QueryEdgeOps[Q](q)*/
}

class QueryElemOps[Q <: AnyQuery { type OutT <: AnyElementType }](val q: Q) {

  type ET = Q#OutT
  val  et = q.outT

  def get[P <: AnyProperty](p: P):
    Compose[Q, GetProperty[ET, P]] =
    Compose[Q, GetProperty[ET, P]](q, GetProperty(et, p))
}

class QueryVertexOps[Q <: AnyQuery { type OutT <: AnyVertexType }](val q: Q) {

  type VT = Q#OutT

  def inE[ET <: AnyEdgeType { type TargetType = VT }](et: ET):
      Compose[Q, GetInEdges[ET]] =
      Compose[Q, GetInEdges[ET]](q, GetInEdges(et))

  def outE[ET <: AnyEdgeType { type SourceType = VT }](et: ET):
      Compose[Q, GetOutEdges[ET]] =
      Compose[Q, GetOutEdges[ET]](q, GetOutEdges(et))
}

class QueryEdgeOps[Q <: AnyQuery { type OutT <: AnyEdgeType }](val q: Q) {

  type ET = Q#OutT
  val  et = q.outT

  def source:
      Compose[Q, GetSource[ET]] =
      Compose[Q, GetSource[ET]](q, GetSource(et))

  def target:
      Compose[Q, GetTarget[ET]] =
      Compose[Q, GetTarget[ET]](q, GetTarget(et))
}


case class GetProperty[T <: AnyElementType, P <: AnyProperty](t: T, p: P) extends Query[T, P](t, p)

case class GetSource[ET <: AnyEdgeType](et: ET) extends Query[ET, ET#SourceType](et, et.sourceType)
case class GetTarget[ET <: AnyEdgeType](et: ET) extends Query[ET, ET#TargetType](et, et.targetType)

case class  GetInEdges[ET <: AnyEdgeType](et: ET) extends Query[ET#TargetType, ET](et.targetType, et)
case class GetOutEdges[ET <: AnyEdgeType](et: ET) extends Query[ET#SourceType, ET](et.sourceType, et)

// case class  GetInVertices[ET <: AnyEdgeType](et: ET) extends Query[ET#TargetType, ET#SourceType](et.targetType, et.sourceType)
// case class GetOutVertices[ET <: AnyEdgeType](et: ET) extends Query[ET#SourceType, ET#TargetType](et.sourceType, et.targetType)

trait AnyEvalQuery[Q <: AnyQuery] extends AnyFn2 {
  type InW <: AnyDenotation
  type OutW <: AnyDenotation

  type In1 = Q
  type In2 = Container[ValueOf[InW]]

  type Out = Container[ValueOf[OutW]]
}

trait EvalQuery[
  Q <: AnyQuery,
  I0 <: AnyDenotation { type Tpe <: Q#InT },
  O0 <: AnyDenotation { type Tpe <: Q#OutT }
] extends AnyEvalQuery[Q] {
  type InW = I0
  type OutW = O0
}

object AnyEvalQuery {

  implicit def evalId[T <: AnyType, X <: AnyDenotation { type Tpe <: T }]:
        EvalQuery[IdQuery[T], X, X] =
    new EvalQuery[IdQuery[T], X, X] {
      def apply(q: In1, x: In2): Out = x
    }
}

trait EvalOnSchema[Q <: AnyQuery, S <: AnySchema] extends AnyEvalQuery[Q] {
  type Schema = S
}

object EvalOnSchema {

  implicit def simple[
    S <: AnySchema,
    Q <: AnyQuery,
    I <: Implementation[S, Q#InT],
    O <: Implementation[S, Q#OutT]
  ](implicit
    // i: Implements[S, Q#InT] { type Out = I },
    // o: Implements[S, Q#OutT] { type Out = O },
    i: I,
    o: O,
    // ev: AnyEvalQuery[Q] { type InW = I; type OutW = O }
    ev: EvalQuery[Q, I, O]
  ): EvalOnSchema[Q, S] =
    new EvalOnSchema[Q, S] {
      type InW = I
      type OutW = O
      def apply(q: In1, in: In2): Out = ev(q, in)
    }
/*
  implicit def composition[
    S <: AnySchema,
    C <: AnyCompose,
    I <: AnyDenotation { type Tpe = C#InT },
    M <: AnyDenotation { type Tpe = C#MidT },
    O <: AnyDenotation { type Tpe = C#OutT }
  ](implicit
    i: Implements[S, C#InT]  { type Out = I },
    m: Implements[S, C#MidT] { type Out = M },
    o: Implements[S, C#OutT] { type Out = O },
    eval1: EvalOnSchema[C#Query1, S] { type InW = I; type OutW = M },
    eval2: EvalOnSchema[C#Query2, S] { type InW = M; type OutW = O }
  ):  EvalOnSchema[C, S] =
  new EvalOnSchema[C, S] {

    type InW = I
    type OutW = O

    def apply(c: In1, in: In2): Out = {
      val o1 = eval1(c.query1, in)
      eval2(c.query2, o1)
    }
  }
*/
}
