package ohnosequences.scarph

import ohnosequences.pointless._, AnyTypeSet._

trait AnyGraphSchema {

  val label: String

  type Dependencies <: AnyTypeSet
  val  dependencies: Dependencies

  type Properties <: AnyTypeSet
  val  properties: Properties

  type VertexTypes <: AnyTypeSet
  val  vertexTypes: VertexTypes

  type EdgeTypes <: AnyTypeSet
  val  edgeTypes: EdgeTypes

  /* These two _values_ store sets of pairs `(vertexType/edgeType, it's properties)` */
  type VerticesWithProperties <: AnyTypeSet
  val  verticesWithProperties: VerticesWithProperties = vertexPropertyAssoc(vertexTypes, properties)

  type EdgesWithProperties <: AnyTypeSet
  val  edgesWithProperties: EdgesWithProperties = edgePropertyAssoc(edgeTypes, properties)

  val vertexPropertyAssoc: ZipWithProps.Aux[VertexTypes, Properties, VerticesWithProperties]
  val   edgePropertyAssoc: ZipWithProps.Aux[EdgeTypes, Properties, EdgesWithProperties]

  override def toString = s"""${label} schema:
  vertexTypes: ${verticesWithProperties}
    edgeTypes: ${edgesWithProperties}"""

}

object AnyGraphSchema {

  /* Additional methods */
  implicit def schemaOps[S <: AnyGraphSchema](sch: S): GraphSchemaOps[S] = GraphSchemaOps[S](sch)
  case class   GraphSchemaOps[S <: AnyGraphSchema](schema: S) {

    /* This method returns properties that are associated with the given **vertex** type */
    def vertexProperties[VT <: Singleton with AnyVertexType](vertexType: VT)(implicit
      e: VT ∈ schema.VertexTypes,
      f: FilterProps[VT, schema.Properties]
    ): f.Out = f(schema.properties)

    /* This method returns properties that are associated with the given **edge** type */
    def edgeProperties[ET <: Singleton with AnyEdgeType](edgeType: ET)(implicit
      e: ET ∈ schema.EdgeTypes,
      f: FilterProps[ET, schema.Properties]
    ): f.Out = f(schema.properties)
  }
}

case class GraphSchema[
    Ds <: AnyTypeSet : boundedBy[AnyGraphSchema]#is,
    Ps <: AnyTypeSet : boundedBy[AnyProperty]#is,
    Vs <: AnyTypeSet : boundedBy[AnyVertexType]#is,
    Es <: AnyTypeSet : boundedBy[AnyEdgeType]#is,
    VP <: AnyTypeSet,
    EP <: AnyTypeSet
  ](val label: String,
    val dependencies: Ds = ∅,
    val properties:   Ps = ∅,
    val vertexTypes:  Vs = ∅,
    val edgeTypes:    Es = ∅
  )(implicit
    val vertexPropertyAssoc: ZipWithProps.Aux[Vs, Ps, VP],
    val   edgePropertyAssoc: ZipWithProps.Aux[Es, Ps, EP]
  ) extends AnyGraphSchema {

  type Dependencies = Ds
  type Properties   = Ps
  type VertexTypes  = Vs
  type EdgeTypes    = Es
  type VerticesWithProperties = VP
  type    EdgesWithProperties = EP

}
