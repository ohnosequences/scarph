package ohnosequences.scarph

trait AnyIndex extends Denotation[AnyIndexType] { index =>

  type Item <: Singleton with AnyDenotation { 
    type Tpe = index.tpe.IndexedType 
  }
  val  item: Item

  abstract class LookupItem {
    type Out = tpe.Out[item.Rep]
    def apply(prop: tpe.Property, predicate: tpe.PredicateType): Out
  }

}
