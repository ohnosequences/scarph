package ohnosequences.scarph

trait AnyIndex extends Denotation[AnyIndexType] { index =>

  type Item <: Singleton with AnyDenotation { 
    type Tpe = index.tpe.IndexedType 
  }
  val  item: Item

  type PredicateType = tpe.PredicateType
  type Out[X] = tpe.Out[X]

  abstract class LookupItem[P <: PredicateType](val predicate: P) {
    def apply(rep: index.Rep): Out[item.Rep]
  }

}
