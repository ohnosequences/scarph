package ohnosequences.scarph.impl.titan

import ohnosequences.scarph._
import ohnosequences.pointless._
import com.thinkaurelius.titan.core.{ TitanGraph => TGraph }

trait AnyTitanVertex extends AnyVertex with AnyTitanElement {

  type Raw = com.thinkaurelius.titan.core.TitanVertex
}

case class TitanVertex[VT <: AnyVertexType](val tpe: VT) 
  extends AnyTitanVertex { type Tpe = VT }

object AnyTitanVertex {

  type ofType[VT <: AnyVertexType] = AnyTitanVertex { type Tpe = VT }
}
