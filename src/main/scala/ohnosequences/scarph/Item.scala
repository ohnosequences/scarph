package ohnosequences.scarph

/* An item is just something that can have properties. In scarph items are either vertices or edges. */

trait AnyItem extends AnyDenotation { self =>

  type TYPE <: AnyItemType

  type Graph
  val  graph: Graph

  abstract class PropertyGetter[P <: AnyProperty](val p: P) {
    def apply(rep: self.Rep): p.Raw
  }

}

trait Item[T <: AnyItemType] extends AnyItem { type TYPE = T }
