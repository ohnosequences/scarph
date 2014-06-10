package ohnosequences.scarph

import ohnosequences.typesets._

trait AnySchema {

  val label: String

  type Dependencies <: TypeSet
  val  dependencies: Dependencies

  type PropertyTypes <: TypeSet
  val  propertyTypes: PropertyTypes

  type VertexTypes <: TypeSet
  val  vertexTypes: VertexTypes

  type EdgeTypes <: TypeSet
  val  edgeTypes: EdgeTypes

  // implicit def schemaOps(sch: AnySchema) = SchemaOps(sch)
  // case class   SchemaOps(sch: AnySchema) {}

}

class Schema[
    Ds <: TypeSet : boundedBy[AnySchema]#is,
    Ps <: TypeSet : boundedBy[AnyProperty]#is,
    Vs <: TypeSet : boundedBy[AnyVertexType]#is,
    Es <: TypeSet : boundedBy[AnyEdgeType]#is
  ](val label: String,
    val dependencies: Ds = ∅,
    val propertyTypes: Ps = ∅,
    val vertexTypes: Vs = ∅,
    val edgeTypes: Es = ∅
  ) extends AnySchema {

  type Dependencies = Ds
  type PropertyTypes = Ps
  type VertexTypes = Vs
  type EdgeTypes = Es

}
