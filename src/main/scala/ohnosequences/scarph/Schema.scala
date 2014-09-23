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
import DenoteTypes.denoteTypes

trait AnySchema extends Denotation[AnySchemaType] {

  type Vertices <: AnyTypeSet.Of[AnyVertex]
  val  vertices: Vertices

  // val vertexTypesCorrespond: Vertices denoteTypes Tpe#VertexTypes

  type Edges <: AnyTypeSet.Of[AnyEdge]
  val  edges: Edges

  // val edgeTypesCorrespond: Edges denoteTypes Tpe#EdgeTypes
}

case class Schema[Vs <: AnyTypeSet.Of[AnyVertex], Es <: AnyTypeSet.Of[AnyEdge], ST <: AnySchemaType]
  (val tpe: ST,
   val vertices: Vs,
   val edges: Es
  // )(implicit 
  //   val vertexTypesCorrespond: Vs denoteTypes ST#VertexTypes,
  //   val edgeTypesCorrespond: Es denoteTypes ST#EdgeTypes
  ) extends AnySchema {

    type Tpe = ST
    type Vertices = Vs
    type Edges = Es
  }

trait GetElement[Es <: AnyTypeSet, ET <: AnyElementType] extends Fn1[Es] with OutBound[AnyElement]

object GetElement extends GetElement_2 {

  implicit def foundInHead[HT <: AnyElementType, H <: AnyElement.ofType[HT], T <: AnyTypeSet]:
        GetElement[H :~: T, HT] with Out[H] =
    new GetElement[H :~: T, HT] with Out[H] {
      def apply(vs: In1): Out = vs.head
    }
}

trait GetElement_2 {

  implicit def foundInTail[ET <: AnyElementType, H <: AnyElement, T <: AnyTypeSet, E <: AnyElement]
    (implicit 
      intail: GetElement[T, ET] { type Out = E }
    ):  GetElement[H :~: T, ET] with Out[E] =
    new GetElement[H :~: T, ET] with Out[E] {
      def apply(vs: In1): Out = intail(vs.tail)
    }
}

object AnySchema {

  implicit def schemaOps[S <: AnySchema](s: S):
        SchemaOps[S] =
    new SchemaOps[S](s)
}

class SchemaOps[S <: AnySchema](s: S) {

  def elementOfType[ET <: AnyElementType, Es <: AnyTypeSet](et: ET)
    (implicit 
      es: (S#Vertices ∪ S#Edges) { type Out = Es },
      get: GetElement[Es, ET]
    ): get.Out = get(es(s.vertices, s.edges))

  // def eval[
  //   I <: AnyElement,
  //   O <: AnyElement,
  //   Q <: Query[I#Tpe, O#Tpe],
  //   Es <: AnyTypeSet
  // ](q: Q, in: ValueOf[I])(implicit
  //   u: (S#Vertices ∪ S#Edges) { type Out = Es },
  //   i: GetElement[Es, Q#InT] { type Out = I },
  //   o: GetElement[Es, Q#OutT] { type Out = O },
  //   ev: EvalQuery[Q, I, O]
  // ): ValueOf[O] = ev(in.raw)
}


trait EvalQuery[
  Q <: Query[I#Tpe, O#Tpe],
  I <: AnyDenotation,
  O <: AnyDenotation
] extends Fn1[I#Raw] with Out[ValueOf[O]]
