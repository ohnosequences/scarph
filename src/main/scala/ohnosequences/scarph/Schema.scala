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
    vp: ZipWithProps[Vs, Ps],
    ep: ZipWithProps[Es, Ps]
  ) extends AnySchema {

  type Dependencies = Ds
  type PropertyTypes = Ps
  type VertexTypes = Vs
  type EdgeTypes = Es

  /* These two _values_ store sets of pairs `(vertexType/edgeType, it's properties)` */
  val vTypesWithProps = implicitly[ZipWithProps[Vs, Ps]].apply(vertexTypes, propertyTypes)
  val eTypesWithProps = implicitly[ZipWithProps[Es, Ps]].apply(edgeTypes, propertyTypes)

  def vTypeProps[VT <: AnyVertexType](implicit
      e: VT ∈ Vs,
      f: FilterProps[VT, Ps]
    ): f.Out = f(propertyTypes)

  def eTypeProps[ET <: AnyEdgeType](implicit
      e: ET ∈ Es,
      f: FilterProps[ET, Ps]
    ): f.Out = f(propertyTypes)

  override def toString = s"""${label} schema:
  vertexTypes: ${vTypesWithProps}
  edgeTypes: ${eTypesWithProps}"""
  
}
