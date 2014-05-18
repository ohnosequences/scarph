package ohnosequences.scarph

/*
  Declares a Vertex type. They are essentially classified by its label, a `String`.
*/
trait AnyVertexType {  val label: String  }

class VertexType(val label: String) extends AnyVertexType

object AnyVertexType {
  implicit def vertexTypeOps[VT <: AnyVertexType](et: VT) = VertexTypeOps(et)
}

case class VertexTypeOps[VT <: AnyVertexType](val vt: VT) {
  def has[P <: AnyProperty](p: P) = HasProperty[VT, P](vt, p)
}
