package ohnosequences.scarph

object schemas {

  import graphTypes._

  trait AnyGraphSchema extends AnyGraphObject {

    val  vertices: Set[AnyVertex]
    val  edges: Set[AnyEdge]
    val  valueTypes: Set[AnyValueType]
    val  properties: Set[AnyGraphProperty]
  }

  case class GraphSchema(
    val label: String,
    val vertices: Set[AnyVertex],
    val edges: Set[AnyEdge],
    val valueTypes: Set[AnyValueType],
    val properties: Set[AnyGraphProperty]
  ) extends AnyGraphSchema

}
