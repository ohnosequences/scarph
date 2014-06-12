package ohnosequences.scarph

import ohnosequences.typesets._

/*
  Declares a Vertex type. They are essentially classified by its label, a `String`.
*/
trait AnyVertexType { val label: String }

class VertexType(val label: String) extends AnyVertexType

object AnyVertexType {
  implicit def vertexTypeOps[VT <: AnyVertexType](et: VT) = VertexTypeOps(et)
}

case class VertexTypeOps[VT <: AnyVertexType](val vt: VT) {

  /* Handy way of creating an implicit evidence saying that this vertex type has that property */
  def has[P <: AnyProperty](p: P) = new (VT HasProperty P)
  def has[Ps <: TypeSet : boundedBy[AnyProperty]#is](ps: Ps) = new (VT HasProperties Ps)

  /* Takes a set of properties and filters out only those, which this vertex "has" */
  def filterMyProps[Ps <: TypeSet : boundedBy[AnyProperty]#is](ps: Ps)(implicit f: FilterProps[VT, Ps]) = f(ps)
}
