package ohnosequences.scarph

/* An item is just something that can have properties. In scarph items are either vertices or edges. */

trait AnyItem extends AnyDenotation { item =>

  type TYPE <: AnyItemType

  type Graph
  val  graph: Graph

  abstract class PropertyGetter[P <: AnyProperty](val p: P) {
    def apply(rep: item.Rep): p.Raw
  }

  abstract class LookupItem[I <: AnyIndex.Over[Tpe]](val index: I) {

    type Out = index.Out[item.Rep]

    def apply(p: index.PredicateType, rep: item.Rep): Out
  }
}

trait Item[T <: AnyItemType] extends AnyItem { type TYPE = T }
