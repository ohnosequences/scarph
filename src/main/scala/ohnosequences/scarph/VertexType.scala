package ohnosequences.scarph

import ohnosequences.typesets._

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
  implicit def vertexTypeOps[VT <: Singleton with AnyVertexType](vt: VT): VertexTypeOps[VT] = VertexTypeOps(vt)

  case class   VertexTypeOps[VT <: Singleton with AnyVertexType](val vt: VT) 
  extends HasPropertiesOps[VT](vt) {}
}

trait AnySealedVertexType extends AnyVertexType {

  type Record <: Singleton with AnyRecord
  val record: Record
}

class SealedVertexType[R <: Singleton with AnyRecord](val label: String, val record: R) extends AnySealedVertexType {

  type Record = R
}
