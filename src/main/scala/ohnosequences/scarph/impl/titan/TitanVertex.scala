package ohnosequences.scarph.impl.titan

import ohnosequences.scarph._
import ohnosequences.cosas._
import com.thinkaurelius.titan.core.{ TitanGraph => TGraph }

trait AnyTitanVertex extends AnyVertex with AnyTitanElement {

  type Raw = com.thinkaurelius.titan.core.TitanVertex
}

class TitanVertex[VT <: AnyVertexType](val graph: TGraph, vt: VT) 
  extends Vertex[VT](vt) with AnyTitanVertex

object AnyTitanVertex {

  type ofType[VT <: AnyVertexType] = AnyTitanVertex { type Tpe = VT }
}
