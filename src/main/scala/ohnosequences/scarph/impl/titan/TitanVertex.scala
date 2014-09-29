package ohnosequences.scarph.impl.titan

import ohnosequences.scarph._
import ohnosequences.pointless._
import com.thinkaurelius.titan.core.{ TitanGraph => TGraph }

trait AnyTitanVertex extends AnyVertex with AnyTitanElement {

  type Raw = com.thinkaurelius.titan.core.TitanVertex
}

class TitanVertex[VT <: AnyVertexType, S <: AnySchema](val s: S, val tpe: VT)
  extends AnyTitanVertex with Implementation[S, VT]

object AnyTitanVertex {

  type ofType[VT <: AnyVertexType] = AnyTitanVertex { type Tpe = VT }
}
