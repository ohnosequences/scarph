package ohnosequences.scarph.impl.titan

import ohnosequences.scarph._
import ohnosequences.pointless._

trait AnyTitanElement extends AnyElement {

  type Graph = com.thinkaurelius.titan.core.TitanGraph

  type Raw <: com.thinkaurelius.titan.core.TitanElement
}

object AnyTitanElement {

  type ofType[ET <: AnyElementType] = AnyTitanElement { type DenotedType = ET }
}
