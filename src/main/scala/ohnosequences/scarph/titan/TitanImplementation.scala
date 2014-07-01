package ohnosequences.scarph.titan

import ohnosequences.scarph._

object TitanImplementation {

  implicit def VTtoV[VT <: AnyVertexType](vt: VT): TVertex[VT] = new TVertex(vt)

  implicit def ETtoE[ET <: AnyEdgeType](et: ET)(implicit
    st: ET#SourceType => TVertex[ET#SourceType],
    tt: ET#TargetType => TVertex[ET#TargetType]
  ): TEdge[TVertex[ET#SourceType], ET, TVertex[ET#TargetType]] = new TEdge(st(et.sourceType), et, tt(et.targetType))

}
