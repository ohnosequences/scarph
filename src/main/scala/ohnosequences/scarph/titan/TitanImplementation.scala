package ohnosequences.scarph.titan

import ohnosequences.scarph._

object TitanImplementation {

  implicit def VTtoV[VT <: AnyVertexType](vt: VT): Singleton with TVertex[VT] = {
    val v = new TVertex(vt)
    v: v.type
  }

  implicit def ettoe[ET <: AnyEdgeType](et: ET)(implicit
    st: ET#SourceType => TVertex[ET#SourceType],
    tt: ET#TargetType => TVertex[ET#TargetType]
  ): Singleton with TEdge[TVertex[ET#SourceType], ET, TVertex[ET#TargetType]] = {
    val x = new TEdge(st(et.sourceType), et, tt(et.targetType))
    x: x.type
  }

}
