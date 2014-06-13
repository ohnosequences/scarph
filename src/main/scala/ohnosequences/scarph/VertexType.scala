package ohnosequences.scarph

/*
  Declares a Vertex type. They are essentially classified by its label, a `String`.
*/
trait AnyVertexType { val label: String }
class    VertexType ( val label: String ) extends AnyVertexType

object AnyVertexType {
  /* Additional methods */
  implicit def vertexTypeOps[VT <: AnyVertexType](vt: VT) = VertexTypeOps(vt)
  case class   VertexTypeOps[VT <: AnyVertexType](val vt: VT) 
    extends HasPropertiesOps(vt) {}
}

