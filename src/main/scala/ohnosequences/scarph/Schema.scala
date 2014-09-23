package ohnosequences.scarph

import ohnosequences.pointless._, AnyTypeSet._, AnyFn._
import ohnosequences.pointless.ops.typeSet._

@annotation.implicitNotFound(msg = "Can't construct a set of denoted types for ${S}")
trait DenoteTypes[S <: AnyTypeSet] extends AnyFn with OutBound[AnyTypeSet]

object DenoteTypes {
  type denoteTypes[Ds <: AnyTypeSet, Ts <: AnyTypeSet] = DenoteTypes[Ds] { type Out = Ts }

  implicit val empty: 
        DenoteTypes[∅] with Out[∅] = 
    new DenoteTypes[∅] with Out[∅]

  implicit def cons[H <: AnyDenotation, T <: AnyTypeSet, TR <: AnyTypeSet]
    (implicit 
      t: DenoteTypes[T] { type Out = TR }
    ):  DenoteTypes[H :~: T] with Out[H#Tpe :~: TR] =
    new DenoteTypes[H :~: T] with Out[H#Tpe :~: TR]
}

trait AnySchema extends Denotation[AnySchemaType] {
  import DenoteTypes.denoteTypes

  type Vertices <: AnyTypeSet.Of[AnyVertex]
  val  vertices: Vertices

  val vertexTypesCorrespond: Vertices denoteTypes Tpe#VertexTypes

  type Edges <: AnyTypeSet.Of[AnyEdge]
  val  edges: Edges

  val edgeTypesCorrespond: Edges denoteTypes Tpe#EdgeTypes
}


trait GetVertex[Vs <: AnyTypeSet, VT <: AnyVertexType] extends Fn1[Vs] with OutBound[AnyVertex]

object GetVertex extends GetVertex_2 {

  implicit def foundInHead[H <: AnyVertex, T <: AnyTypeSet]:
        GetVertex[H :~: T, H#Tpe] with Out[H] =
    new GetVertex[H :~: T, H#Tpe] with Out[H] {
      def apply(vs: In1): Out = vs.head
    }
}

trait GetVertex_2 {

  implicit def foundInTail[H <: AnyVertex, T <: AnyTypeSet, V <: AnyVertex]
    (implicit 
      intail: GetVertex[T, V#Tpe] { type Out = V }
    ):  GetVertex[H :~: T, V#Tpe] with Out[V] =
    new GetVertex[H :~: T, V#Tpe] with Out[V] {
      def apply(vs: In1): Out = intail(vs.tail)
    }
}

object AnySchema {

  implicit def schemaOps[S <: AnySchema](s: S):
        SchemaOps[S] =
    new SchemaOps[S](s)
}

class SchemaOps[S <: AnySchema](s: S) {

  def eval[
    I <: AnyVertex,
    O <: AnyVertex,
    Q <: Query[I#Tpe, O#Tpe]
  ](q: Q)(implicit
    in: GetVertex[S#Vertices, Q#InT] { type Out = I },
    out: GetVertex[S#Vertices, Q#OutT] { type Out = O },
    ev: EvalQuery[Q, I, O]
  ): O = ev(q, in(s.vertices))
}


trait EvalQuery[
  Q <: Query[I#Tpe, O#Tpe],
  I <: AnyDenotation,
  O <: AnyDenotation
] extends Fn2[Q, I] with Out[O]
