package ohnosequences.scarph.impl.titan

import ohnosequences.scarph._, AnyEdgeType._, AnyTitanVertex._
import ohnosequences.pointless._
import com.thinkaurelius.titan.core.{ TitanGraph => TGraph }

trait AnyTitanEdge extends AnyEdge with AnyTitanElement {

  type Raw = com.thinkaurelius.titan.core.TitanEdge

  type Source <: AnyVertex.ofType[SourceTypeOf[DenotedType]] with AnyTitanVertex
  type Target <: AnyVertex.ofType[TargetTypeOf[DenotedType]] with AnyTitanVertex
}

class TitanEdge[
    S <: AnyVertex.ofType[ET#SourceType] with AnyTitanVertex, 
    ET <: AnyEdgeType, 
    T <: AnyVertex.ofType[ET#TargetType] with AnyTitanVertex
  ](val graph: TGraph, s: S, et: ET, t: T) extends Edge(s, et, t) with AnyTitanEdge 

object AnyTitanEdge {

  type ofType[ET <: AnyEdgeType] = AnyTitanEdge { type DenotedType = ET }
  type withType[ET <: AnyEdgeType] = AnyTitanEdge { type DenotedType <: ET }
}
