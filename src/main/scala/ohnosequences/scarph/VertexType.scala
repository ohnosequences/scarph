package ohnosequences.scarph

import ohnosequences.typesets._

/*
  Declares a Vertex type. They are essentially classified by its label, a `String`.
*/
trait AnyVertexType {
  val label: String

  type Props <: TypeSet
  val props: Props
}

// class VertexType(val label: String) extends AnyVertexType
class VertexType[Ps <: TypeSet : boundedBy[AnyProperty]#is](val label: String, val props: Ps = ∅) 
  extends AnyVertexType { type Props = Ps }

object AnyVertexType {
  implicit def vertexTypeOps[VT <: AnyVertexType](et: VT) = VertexTypeOps(et)
}

case class VertexTypeOps[VT <: AnyVertexType](val vt: VT) {

  /* Handy way of creating an implicit evidence saying that this vertex type has that property */
  def has[P <: AnyProperty](p: P) = HasProperty[VT, P](vt, p)

  /* Takes a set of properties and filters out only those, which this vertex "has" */
  def filterMyProps[Ps <: TypeSet : boundedBy[AnyProperty]#is](ps: Ps)(implicit f: FilterProps[VT, Ps]) = f(ps)
}
