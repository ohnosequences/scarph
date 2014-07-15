package ohnosequences.scarph

trait AnyIndex extends Denotation[AnyIndexType] { index =>

  type IndexedType //= tpe.IndexedType
  type PredicateType //= tpe.PredicateType
  type Out //<: tpe.Out[item.Rep]

  // TODO: couldn't place this bound
  type Item <: AnyDenotation //.Of[IndexedType]
  val  item: Item

  abstract class LookupItem[P <: PredicateType](val predicate: P) {
    def apply(rep: index.Rep): index.Out
  }

}
