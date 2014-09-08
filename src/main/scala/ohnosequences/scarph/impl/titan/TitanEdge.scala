package ohnosequences.scarph.impl.titan

import ohnosequences.scarph._, AnyEdgeType._
import AnyTitanVertex._
import ohnosequences.pointless._, AnyProperty._, AnyRecord._

trait AnyTitanEdge extends AnyEdge {

  final type Raw = com.thinkaurelius.titan.core.TitanEdge

  type Source <: AnyVertex.ofType[SourceTypeOf[DenotedType]] with AnyTitanVertex
  type Target <: AnyVertex.ofType[TargetTypeOf[DenotedType]] with AnyTitanVertex
}

// class TitanEdge[
//     S <: AnyVertex.ofType[ET#SourceType] with AnyTitanVertex, 
//     ET <: AnyEdgeType, 
//     T <: AnyVertex.ofType[ET#TargetType] with AnyTitanVertex
//   ](val source: S, val tpe: ET, val target: T) extends AnyTitanEdge { 
//     type Source = S
//     type DenotedType = ET 
//     type Target = T
//   }

object AnyTitanEdge {
  type ofType[ET <: AnyEdgeType] = AnyTitanEdge { type DenotedType = ET }
}
