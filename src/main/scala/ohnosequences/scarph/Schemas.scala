package ohnosequences.scarph

object schemas {

  import ohnosequences.cosas._, typeSets._
  import graphTypes._, indexes._

  trait AnyGraphSchema extends AnyGraphType {

    type Properties <: AnyTypeSet.Of[AnyGraphProperty]
    val  properties: Properties

    type Vertices <: AnyTypeSet.Of[AnyVertex]
    val  vertices: Vertices

    type Edges <: AnyTypeSet.Of[AnyEdge]
    val  edges: Edges

    type Indexes <: AnyTypeSet.Of[AnyIndex]
    val  indexes: Indexes
  }

  abstract class GraphSchema[
      Ps <: AnyTypeSet.Of[AnyGraphProperty],
      Vs <: AnyTypeSet.Of[AnyVertex],
      Es <: AnyTypeSet.Of[AnyEdge],
      Is <: AnyTypeSet.Of[AnyIndex]
    ](val label: String,
      val properties: Ps,
      val vertices: Vs,
      val edges: Es,
      val indexes: Is
    ) extends AnyGraphSchema {

    type Properties = Ps
    type Vertices = Vs
    type Edges = Es
    type Indexes = Is
  }

  object AnyGraphSchemaType {

    implicit def schemaOps[GS <: AnyGraphSchema](gs: GS):
          GraphSchemaOps[GS] =
      new GraphSchemaOps[GS](gs)
  }

  class GraphSchemaOps[GS <: AnyGraphSchema](gs: GS) {

    // TODO: filter schema properties by an owner
    // def propertiesOf[E <: AnyGraphElement](e: E)
    //   (implicit filter: ???): props.Out = props(gs)
  }

}
