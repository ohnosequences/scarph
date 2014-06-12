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
  )(implicit
    val vp: ZipWithProps[Vs, Ps],
    val ep: ZipWithProps[Es, Ps]
  ) extends AnySchema {

  type Dependencies = Ds
  type PropertyTypes = Ps
  type VertexTypes = Vs
  type EdgeTypes = Es

  /* These two _values_ store sets of pairs `(vertexType/edgeType, it's properties)` */
  val vTypesWithProps = vp.apply(vertexTypes, propertyTypes)
  val eTypesWithProps = ep.apply(edgeTypes, propertyTypes)

  /* This method returns properties that are associated with the given **vertex** type */
  def vTypeProps[VT <: AnyVertexType](implicit
      e: VT ∈ Vs,
      f: FilterProps[VT, Ps]
    ): f.Out = f(propertyTypes)

  /* This method returns properties that are associated with the given **edge** type */
  def eTypeProps[ET <: AnyEdgeType](implicit
      e: ET ∈ Es,
      f: FilterProps[ET, Ps]
    ): f.Out = f(propertyTypes)

  override def toString = s"""${label} schema:
  vertexTypes: ${vTypesWithProps}
  edgeTypes: ${eTypesWithProps}"""
  
}
