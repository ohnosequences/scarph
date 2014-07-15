package ohnosequences.scarph

trait AnyIndex extends Denotation[AnyIndexType] { index =>

  type Item <: AnyDenotation.Of[index.tpe.IndexedType]
  val  item: Item

  type PredicateType = tpe.PredicateType
  type Out //<: tpe.Out[item.Rep]

  abstract class LookupItem[P <: PredicateType](val predicate: P) {
    def apply(rep: index.Rep): index.Out
  }

}
