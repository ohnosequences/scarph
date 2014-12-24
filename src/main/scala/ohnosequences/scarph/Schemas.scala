package ohnosequences.scarph

object schemas {

  import ohnosequences.cosas._, typeSets._
  import graphTypes._, indexes._

  trait AnySchema extends AnySimpleGraphType {

    type Properties <: AnyTypeSet.Of[AnyGraphProperty]
    val  properties: Properties

    type Vertices <: AnyTypeSet.Of[AnyVertex]
    val  vertices: Vertices

    type Edges <: AnyTypeSet.Of[AnyEdge]
    val  edges: Edges

    type Indexes <: AnyTypeSet.Of[AnyIndex]
    val  indexes: Indexes
  }

  abstract class Schema[
      Ps <: AnyTypeSet.Of[AnyGraphProperty],
      Vs <: AnyTypeSet.Of[AnyVertex],
      Es <: AnyTypeSet.Of[AnyEdge],
      Is <: AnyTypeSet.Of[AnyIndex]
    ](val label: String,
      val properties: Ps,
      val vertices: Vs,
      val edges: Es,
      val indexes: Is
    ) extends AnySchema {

    type Properties = Ps
    type Vertices = Vs
    type Edges = Es
    type Indexes = Is
  }

  object AnySchemaType {

    implicit def schemaOps[GS <: AnySchema](gs: GS):
          SchemaOps[GS] =
      new SchemaOps[GS](gs)
  }

  class SchemaOps[GS <: AnySchema](gs: GS) {

    // TODO: filter schema properties by an owner
    // def propertiesOf[E <: AnyGraphElement](e: E)
    //   (implicit filter: ???): props.Out = props(gs)
  }

}
