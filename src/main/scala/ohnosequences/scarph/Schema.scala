package ohnosequences.scarph

import ohnosequences.typesets._

trait AnySchema {

  val label: String

  type Dependencies <: TypeSet
  val  dependencies: Dependencies

  type Properties <: TypeSet
  val  properties: Properties

  type VertexTypes <: TypeSet
  val  vertexTypes: VertexTypes

  type EdgeTypes <: TypeSet
  val  edgeTypes: EdgeTypes

  /* This method returns properties that are associated with the given **vertex** type */
  def vertexProperties[VT <: Singleton with AnyVertexType](vertexType: VT)(implicit
    e: VT ∈ VertexTypes,
    f: FilterProps[VT, Properties]
  ): f.Out = f(properties)

  /* This method returns properties that are associated with the given **edge** type */
  def edgeProperties[ET <: Singleton with AnyEdgeType](edgeType: ET)(implicit
    e: ET ∈ EdgeTypes,
    f: FilterProps[ET, Properties]
  ): f.Out = f(properties)

  val vertexPropertyAssoc: ZipWithProps[VertexTypes, Properties]
  val   edgePropertyAssoc: ZipWithProps[EdgeTypes, Properties]

  /* These two _values_ store sets of pairs `(vertexType/edgeType, it's properties)` */
  val verticesWithProperties = vertexPropertyAssoc(vertexTypes, properties)
  val    edgesWithProperties =   edgePropertyAssoc(edgeTypes,   properties)

  override def toString = s"""${label} schema:
  vertexTypes: ${verticesWithProperties}
    edgeTypes: ${edgesWithProperties}"""
}

case class Schema[
    Ds <: TypeSet : boundedBy[AnySchema]#is,
    Ps <: TypeSet : boundedBy[AnyProperty]#is,
    Vs <: TypeSet : boundedBy[AnyVertexType]#is,
    Es <: TypeSet : boundedBy[AnyEdgeType]#is
  ](val label: String,
    val dependencies: Ds = ∅,
    val properties: Ps = ∅,
    val vertexTypes: Vs = ∅,
    val edgeTypes: Es = ∅
  )(implicit
    val vertexPropertyAssoc: ZipWithProps[Vs, Ps],
    val   edgePropertyAssoc: ZipWithProps[Es, Ps]
  ) extends AnySchema {

  type Dependencies = Ds
  type Properties = Ps
  type VertexTypes = Vs
  type EdgeTypes = Es

}
