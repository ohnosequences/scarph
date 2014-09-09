package ohnosequences.scarph.impl.titan

import ohnosequences.scarph._, AnyEdgeType._
import AnyTitanVertex._
import ohnosequences.pointless._, AnyProperty._, AnyRecord._

trait AnyTitanEdge extends AnyEdge {

  final type Raw = com.thinkaurelius.titan.core.TitanEdge

  type Source <: AnyVertex.ofType[SourceTypeOf[DenotedType]] with AnyTitanVertex
  type Target <: AnyVertex.ofType[TargetTypeOf[DenotedType]] with AnyTitanVertex
}

class TitanEdge[
    S <: AnyVertex.ofType[ET#SourceType] with AnyTitanVertex, 
    ET <: AnyEdgeType, 
    T <: AnyVertex.ofType[ET#TargetType] with AnyTitanVertex
  ](s: S, et: ET, t: T) extends Edge(s, et, t) with AnyTitanEdge 

object AnyTitanEdge {

  type ofType[ET <: AnyEdgeType] = AnyTitanEdge { type DenotedType = ET }
  type withType[ET <: AnyEdgeType] = AnyTitanEdge { type DenotedType <: ET }
}
