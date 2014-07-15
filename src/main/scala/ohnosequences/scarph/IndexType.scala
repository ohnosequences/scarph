package ohnosequences.scarph

/*
  ## Indexes, predicates and queries

  
  Let's try to map this to DynamoDB. There, predicates are over items, and consist in combinations of conditions over properties, using combinators (`and`, `or`). All this can be defined statically and without any reference to implementations. Given that, an index is something that can help you evaluate those predicates.

*/

trait AnyIndexType {

  val label: String

  type IndexedType
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

abstract class IndexType[IT, P <: Singleton with AnyProperty](
  val label: String,
  val indexedType: IT, 
  val property: P
  )(implicit val indexedTypeHasProperty: IT HasProperty P) extends AnyIndexType {

    type IndexedType = IT
    type Property = P
  }

object AnyIndexType {
  type Over[IT] = AnyIndexType { type IndexedType = IT }
}

// Simple index type, which can be only queried for an exact property match
trait AnyStandardIndexType extends AnyIndexType {

  override val label = "standard"

  type Out[X] = List[X]

  type PredicateType = AnyPredicate.On[IndexedType] 
                  with AnyPredicate.HeadedBy[EQ[Property]]

}
