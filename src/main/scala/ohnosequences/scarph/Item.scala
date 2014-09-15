package ohnosequences.scarph

import ohnosequences.pointless._

/* An item is just something that can have properties. In scarph items are either vertices or edges. */

trait AnyItem extends AnyDenotation { item =>

  type TYPE <: AnyType

  type Graph
  val  graph: Graph

  // abstract class PropertyGetter[P <: AnyProperty](val p: P) {
  //   def apply(rep: item.Rep): p.Raw
  // }

  // abstract class QueryEval[Q <: AnyQuery] {
  //   type Query = Q
  //   def apply(query: Query): query.Out[item.Rep]
  // }
}

trait Item[T <: AnyType] extends AnyItem { type TYPE = T }

object AnyItem {
  type ofType[T <: AnyType] = AnyItem { type Tpe = T }
}
