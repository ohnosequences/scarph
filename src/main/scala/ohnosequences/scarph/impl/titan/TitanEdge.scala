package ohnosequences.scarph.impl.titan

import ohnosequences.scarph._, AnyEdgeType._, AnyTitanVertex._
import ohnosequences.pointless._
import com.thinkaurelius.titan.core.{ TitanGraph => TGraph }

trait AnyTitanEdge extends AnyEdge with AnyTitanElement {

  type Raw = com.thinkaurelius.titan.core.TitanEdge
}

class TitanEdge[ET <: AnyEdgeType, S <: AnySchema](val s: S, val tpe: ET)
  extends AnyTitanEdge with Implementation[S, ET]

object AnyTitanEdge {

  type ofType[ET <: AnyEdgeType] = AnyTitanEdge { type Tpe = ET }
  type withType[ET <: AnyEdgeType] = AnyTitanEdge { type Tpe <: ET }
}
