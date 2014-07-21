package ohnosequences.scarph

/*
  ## Indexes, predicates and queries

  
  Let's try to map this to DynamoDB. There, predicates are over items, and consist in combinations of conditions over properties, using combinators (`and`, `or`). All this can be defined statically and without any reference to implementations. Given that, an index is something that can help you evaluate those predicates.

*/

trait AnyIndex {

  val label: String

  type IndexedType <: Singleton with AnyItemType
  val  indexedType: IndexedType

  type Property <: Singleton with AnyProperty
  val  property: Property

  // should be provieded implicitly:
  val indexedTypeHasProperty: IndexedType HasProperty Property

  /*
    The type of predicates that this index can be queried for
  */
  type PredicateType <: AnyPredicate.On[IndexedType]

  type Out[X]
}

abstract class Index[IT <: Singleton with AnyItemType, P <: Singleton with AnyProperty](
  val label: String,
  val indexedType: IT, 
  val property: P
  )(implicit val indexedTypeHasProperty: IT HasProperty P) extends AnyIndex {

    type IndexedType = IT
    type Property = P
  }

object AnyIndex {
  type Over[IT] = AnyIndex { type IndexedType = IT }
}

// Simple index type, which can be only queried for an exact property match
trait AnyStandardIndex extends AnyIndex { indexType =>

  override val label = "standard"

  type Out[X] = List[X]

  type PredicateType = AnySimplePredicate {
    type ItemType = indexType.IndexedType
    type Head = EQ[indexType.Property]
  }
                  // with AnyPredicate.On[IndexedType] 
                  // with AnyPredicate.HeadedBy[EQ[Property]]

}
