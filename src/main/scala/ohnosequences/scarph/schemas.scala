package ohnosequences.scarph

object schemas {

  import objects._


  trait AnyGraphSchema extends AnyGraphType {

    val vertices: Set[AnyVertex]
    val edges: Set[AnyEdge]
    val valueTypes: Set[AnyValueType]
    val properties: Set[AnyProperty]
  }

  class GraphSchema(
    val label: String,
    val vertices: Set[AnyVertex],
    val edges: Set[AnyEdge],
    val valueTypes: Set[AnyValueType],
    val properties: Set[AnyProperty]
  ) extends AnyGraphSchema

}