package ohnosequences.scarph

object schemas {

  import ohnosequences.cosas._, typeSets._
  import graphTypes._

  trait AnyGraphSchema extends AnyGraphObject {

    type Vertices <: AnyTypeSet.Of[AnyVertex]
    val  vertices: Vertices

    type Edges <: AnyTypeSet.Of[AnyEdge]
    val  edges: Edges

    type ValueTypes <: AnyTypeSet.Of[AnyValueType]
    val  valueTypes: ValueTypes

    type Properties <: AnyTypeSet.Of[AnyGraphProperty]
    val  properties: Properties
  }

  abstract class GraphSchema[
      Rs <: AnyTypeSet.Of[AnyValueType],
      Ps <: AnyTypeSet.Of[AnyGraphProperty],
      Vs <: AnyTypeSet.Of[AnyVertex],
      Es <: AnyTypeSet.Of[AnyEdge]
    ](val label: String,
      val vertices: Vs,
      val edges: Es,
      val valueTypes: Rs,
      val properties: Ps
    ) extends AnyGraphSchema {

    type ValueTypes = Rs
    type Properties = Ps
    type Vertices = Vs
    type Edges = Es
  }

}
