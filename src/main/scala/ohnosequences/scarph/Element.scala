package ohnosequences.scarph

import ohnosequences.cosas._

/* An item is just something that can have properties. In scarph items are either vertices or edges. */

trait AnyElementType extends AnyType with AnyPropertiesHolder

trait AnyElement extends AnyDenotationOf[AnyElementType] {

  type Graph
  val  graph: Graph
}

trait AnyElementOf[T <: AnyElementType] extends AnyElement { type Tpe <: T }

object AnyElement {

  type ofType[T <: AnyElementType] = AnyElement { type Tpe = T }
}
