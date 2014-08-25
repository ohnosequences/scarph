package ohnosequences.scarph

import ohnosequences.pointless._

/*
  Declares a Vertex type. They are essentially classified by its label, a `String`.
*/
trait AnyVertexType { 

  val label: String

  // maybe it's useful
  implicit def fromLabel(s: String): Option[this.type] = if (s equals label) Some[this.type](this) else None
}

class VertexType ( val label: String ) extends AnyVertexType

object AnyVertexType {
  
  /* Additional methods */
  // implicit def vertexTypeOps[VT <: AnyVertexType](vt: VT): VertexTypeOps[VT] = VertexTypeOps(vt)

  // case class   VertexTypeOps[VT <: AnyVertexType](val vt: VT) 
  // extends HasPropertiesOps[VT](vt) {}
}

trait AnySealedVertexType extends AnyVertexType {

  type Record <: AnyRecord
  val record: Record
}

class SealedVertexType[R <: AnyRecord](val label: String, val record: R) extends AnySealedVertexType {

  type Record = R
}
