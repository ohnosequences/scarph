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
  type QueryType <: AnyQuery.On[IndexedType]

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
  type WithQuery[P] = AnyIndex { type QueryType = P }
}

// Simple index type, which can be only queried for an exact property match
trait AnyStandardIndex extends AnyIndex { indexType =>

  override val label = "standard"

  type Out[X] = List[X]

  type QueryType = AnySimpleQuery {
    type ItemType = indexType.IndexedType
    type Head = EQ[indexType.Property]
  }
}

class StandardIndex[IT <: Singleton with AnyItemType, P <: Singleton with AnyProperty](
  val indexedType: IT, 
  val property: P
)(implicit val indexedTypeHasProperty: IT HasProperty P) extends AnyStandardIndex {

  type IndexedType = IT
  type Property = P
}
