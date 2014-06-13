package ohnosequences.scarph

/*
  ## Indexes, predicates and queries

  
  Let's try to map this to DynamoDB. There, predicates are over items, and consist in combinations of conditions over properties, using combinators (`and`, `or`). All this can be defined statically and without any reference to implementations. Given that, an index is something that can help you evaluate those predicates.

*/

trait AnyIndexType {

  // TODO bound on something with properties
  type IndexedType
  val indexedType: IndexedType

  type Property <: AnyProperty
  val property: Property

  /*
    The type of predicates that this index can be queried for
  */
  type PredicateType

  type Out[+X]
}

trait AnyVertexIndexType {

  type IndexedType <: AnyVertexType 
}