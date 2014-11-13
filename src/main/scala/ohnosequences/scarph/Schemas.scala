package ohnosequences.scarph

import ohnosequences.cosas._, AnyTypeSet._
// import ohnosequences.pointless.ops.typeSet._

trait AnySchema {

  type Properties <: AnyTypeSet.Of[AnyProp]
  val  properties: Properties

  type VertexTypes <: AnyTypeSet.Of[AnyVertexType]
  val  vertexTypes: VertexTypes

  type EdgeTypes <: AnyTypeSet.Of[AnyEdgeType]
  val  edgeTypes: EdgeTypes

  type Indexes <: AnyTypeSet.Of[AnyIndex]
  val  indexes: Indexes
}

case class Schema[
    Ps <: AnyTypeSet.Of[AnyProp],
    Vs <: AnyTypeSet.Of[AnyVertexType],
    Es <: AnyTypeSet.Of[AnyEdgeType],
    Is <: AnyTypeSet.Of[AnyIndex]
  ](val label: String,
    val properties: Ps,
    val vertexTypes: Vs,
    val edgeTypes: Es,
    val indexes: Is
  ) extends AnySchema {

  type Properties  = Ps
  type VertexTypes = Vs
  type EdgeTypes   = Es
  type Indexes     = Is
}

object AnySchemaType {

  implicit def schemaOps[GS <: AnySchema](gs: GS):
        SchemaOps[GS] =
    new SchemaOps[GS](gs)
}

class SchemaOps[GS <: AnySchema](gs: GS) {

  // TODO: filter schema properties by an owner
  // def propertiesOf[E <: AnyElementType](e: E)
  //   (implicit filter: ???): props.Out = props(gs)
}
