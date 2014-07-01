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
    val e = new TEdge(st(et.sourceType), et, tt(et.targetType))
    e: e.type
  }

  // trait EdgeFabric[ET <: AnyEdgeType] {
  //   type E <: Singleton with AnyEdge.ofType[ET]
  //   def apply(et: ET): E
  // }

  // object EdgeFabric {
  //   type Aux[ET <: AnyEdgeType, E_ <: Singleton with AnyEdge.ofType[ET]] = EdgeFabric[ET] { type E = E_ }

  //   implicit def createEdge[
  //     ET <: Singleton with AnyEdgeType, 
  //     E0 <: Singleton with AnyEdge.ofType[ET]
  //   ]: EdgeFabric.Aux[ET, E0] = new EdgeFabric[ET] {

  //     type E = E0

  //     def apply(et: ET): E = {
  //       e //: e.type
  //     }
  //   }
  // }

}
