package ohnosequences.scarph

trait AnyIndex extends Denotation[AnyIndexType] { index =>

  type IndexedType = tpe.IndexedType
  type PredicateType = tpe.PredicateType
  // type Out <: Tpe#Out[item.Rep]

  // TODO: couldn't place this bound
  type Item <: AnyDenotation //.Of[IndexedType]
  val  item: Item

  abstract class LookupItem[P <: PredicateType](val predicate: P) {

    type Out = index.tpe.Out[index.item.Rep]
    def apply(rep: index.Rep): Out
  }
  

}

trait AnyStandardIndex extends AnyIndex { index =>

  type Tpe <: AnyStandardIndexType
  val tpe: Tpe

  abstract class LookupItem[P <: PredicateType](val predicate: P) {

    type Out = index.tpe.Out[index.item.Rep]
    def apply(rep: index.Rep): Out
  }
}
