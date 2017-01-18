package ohnosequences.scarph

trait AnyGraphSchema extends AnyGraphType {

  val vertices: Set[AnyVertex]
  val edges: Set[AnyEdge]
  val valueTypes: Set[AnyValueType]
  val properties: Set[AnyProperty]
}

abstract class GraphSchema(
  val label: String,
  val vertices: Set[AnyVertex],
  val edges: Set[AnyEdge],
  val valueTypes: Set[AnyValueType],
  val properties: Set[AnyProperty]
) extends AnyGraphSchema
