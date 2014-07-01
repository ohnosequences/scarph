package ohnosequences.scarph

import ohnosequences.typesets._

trait AnyGraphSchema {

  val label: String

  type Dependencies <: TypeSet
  val  dependencies: Dependencies

  type Properties <: TypeSet
  val  properties: Properties

  type VertexTypes <: TypeSet
  val  vertexTypes: VertexTypes

  type EdgeTypes <: TypeSet
  val  edgeTypes: EdgeTypes

  /* These two _values_ store sets of pairs `(vertexType/edgeType, it's properties)` */
  type VerticesWithProperties <: TypeSet
  val  verticesWithProperties: VerticesWithProperties = vertexPropertyAssoc(vertexTypes, properties)

  type EdgesWithProperties <: TypeSet
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
    Ds <: TypeSet : boundedBy[AnyGraphSchema]#is,
    Ps <: TypeSet : boundedBy[AnyProperty]#is,
    Vs <: TypeSet : boundedBy[AnyVertexType]#is,
    Es <: TypeSet : boundedBy[AnyEdgeType]#is,
    VP <: TypeSet,
    EP <: TypeSet
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
