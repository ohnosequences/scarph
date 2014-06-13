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

  val vertexPropertyAssoc: ZipWithProps[VertexTypes, Properties]
  val   edgePropertyAssoc: ZipWithProps[EdgeTypes, Properties]

  /* These two _values_ store sets of pairs `(vertexType/edgeType, it's properties)` */
  val verticesWithProperties = vertexPropertyAssoc(vertexTypes, properties)
  val    edgesWithProperties =   edgePropertyAssoc(edgeTypes,   properties)

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
    Es <: TypeSet : boundedBy[AnyEdgeType]#is
  ](val label: String,
    val dependencies: Ds = ∅,
    val properties:   Ps = ∅,
    val vertexTypes:  Vs = ∅,
    val edgeTypes:    Es = ∅
  )(implicit
    val vertexPropertyAssoc: ZipWithProps[Vs, Ps],
    val   edgePropertyAssoc: ZipWithProps[Es, Ps]
  ) extends AnyGraphSchema {

  type Dependencies = Ds
  type Properties   = Ps
  type VertexTypes  = Vs
  type EdgeTypes    = Es

}
