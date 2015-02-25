package ohnosequences.scarph

object schemas {

  import ohnosequences.cosas._, typeSets._
  import graphTypes._

  trait AnyGraphSchema extends AnyGraphObject {

    type Vertices <: AnyTypeSet.Of[AnyVertex]
    val  vertices: Vertices

    type Edges <: AnyTypeSet.Of[AnyEdge]
    val  edges: Edges

    type RawTypes <: AnyTypeSet.Of[AnyRawType]
    val  rawTypes: RawTypes

    type Properties <: AnyTypeSet.Of[AnyGraphProperty]
    val  properties: Properties
  }

  abstract class GraphSchema[
      Rs <: AnyTypeSet.Of[AnyRawType],
      Ps <: AnyTypeSet.Of[AnyGraphProperty],
      Vs <: AnyTypeSet.Of[AnyVertex],
      Es <: AnyTypeSet.Of[AnyEdge]
    ](val label: String,
      val vertices: Vs,
      val edges: Es,
      val rawTypes: Rs,
      val properties: Ps
    ) extends AnyGraphSchema {

    type RawTypes = Rs
    type Properties = Ps
    type Vertices = Vs
    type Edges = Es
  }

}
