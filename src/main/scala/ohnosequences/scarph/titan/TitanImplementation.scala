package ohnosequences.scarph.titan

import ohnosequences.scarph._

object TitanImplementation {

  implicit def VTtoV[VT <: AnyVertexType](vt: VT): TVertex[VT] = {
    case object v extends TVertex(vt)
    v: v.type
  }

  trait ETtoE[ET <: AnyEdgeType] {
    type Out <: AnyEdge.ofType[ET]
    def apply(et: ET): Out
  }

  object ETtoE {
    type Aux[ET <: AnyEdgeType, E <: AnyEdge.ofType[ET]] = ETtoE[ET] { type Out = E }

    implicit def ettoe[ET <: AnyEdgeType](et: ET)(implicit
      st: ET#SourceType => TVertex[ET#SourceType],
      tt: ET#TargetType => TVertex[ET#TargetType]
    ): ETtoE.Aux[ET, TEdge[TVertex[ET#SourceType], ET, TVertex[ET#TargetType]]] = new ETtoE[ET] {
      object x extends TEdge(st(et.sourceType), et, tt(et.targetType))
      type Out = TEdge[TVertex[ET#SourceType], ET, TVertex[ET#TargetType]]// x.type //
      def apply(et: ET): Out = { x: x.type }
    }
  }

  implicit def ETTOE[ET <: AnyEdgeType](et: ET): 
    TEdge[TVertex[ET#SourceType], ET, TVertex[ET#TargetType]] = {

    // val s = VTtoV(et.sourceType)
    // val t = VTtoV(et.targetType)
    case object s extends TVertex[ET#SourceType](et.sourceType)
    case object t extends TVertex[ET#TargetType](et.targetType)
    case object e extends TEdge[TVertex[ET#SourceType], ET, TVertex[ET#TargetType]](s, et, t)
    e: e.type
  }
}
