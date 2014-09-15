package ohnosequences.scarph

import ohnosequences.pointless._

/* An item is just something that can have properties. In scarph items are either vertices or edges. */

trait AnyElementType extends AnyType with AnyPropertiesHolder

trait AnyElement extends AnyDenotation {

  type TypeBound <: AnyElementType

  type Graph
  val  graph: Graph

  // abstract class QueryEval[Q <: AnyQuery] {
  //   type Query = Q
  //   def apply(query: Query): query.Out[item.Rep]
  // }
}

trait AnyElementOf[T <: AnyElementType] extends AnyElement { type TypeBound = T }

// abstract class Element[T <: AnyElementType](val denotedType: T) 
//   extends AnyElement { type DenotedType = T }

object AnyElement {
  type ofType[T <: AnyElementType] = AnyElement { type DenotedType = T }
}
