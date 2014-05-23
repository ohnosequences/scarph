package ohnosequences.scarph

import ohnosequences.typesets._

trait AnySchema {

  val label: String

  type PropertyTypes <: TypeSet
  val  propertyTypes: PropertyTypes

  type VertexTypes <: TypeSet
  val  vertexTypes: VertexTypes

  type EdgeTypes <: TypeSet
  val  edgeTypes: EdgeTypes

  // implicit def schemaOps(sch: AnySchema) = SchemaOps(sch)
  // case class   SchemaOps(sch: AnySchema) {}

}

case class Schema[
    Ps <: TypeSet : boundedBy[AnyProperty]#is,
    Vs <: TypeSet : boundedBy[AnyVertexType]#is,
    Es <: TypeSet : boundedBy[AnyEdgeType]#is
  ](val label: String,
    val propertyTypes: Ps,
    val vertexTypes: Vs,
    val edgeTypes: Es
  ) extends AnySchema {

  type PropertyTypes = Ps
  type VertexTypes = Vs
  type EdgeTypes = Es

}
