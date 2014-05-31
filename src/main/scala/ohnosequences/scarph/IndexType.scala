package ohnosequences.scarph

trait AnyIndexType {

  type IndexedType
  val indexedType: IndexedType

  type Property <: AnyProperty
  val property: Property

  type PredicateType

  type Out[+X]
}

trait AnyVertexIndexType {

  type IndexedType <: AnyVertexType 
}

trait AnyListVertexIndexType extends AnyVertexIndexType {

  type Out[+X] = List[X]
}

trait Predicate