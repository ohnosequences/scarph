package ohnosequences.scarph.impl.titan

import ohnosequences.scarph._
import ohnosequences.pointless._

trait AnyTitanVertex extends AnyVertex {

  type Raw = com.thinkaurelius.titan.core.TitanVertex
}

class TitanVertex[VT <: AnyVertexType](vt: VT) 
  extends Vertex[VT](vt) with AnyTitanVertex

object AnyTitanVertex {

  type ofType[VT <: AnyVertexType] = AnyTitanVertex { type DenotedType = VT }
}
