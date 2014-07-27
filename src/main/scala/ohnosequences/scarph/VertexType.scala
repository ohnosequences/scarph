package ohnosequences.scarph

import ohnosequences.typesets._

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


trait AnySealedVertexType extends AnyVertexType {

  /* a fixed set of properties */
  type Properties <: TypeSet
  val  properties: Properties
  // should be provided implicitly:
  val  propertiesBound: Properties << AnyProperty
}

class SealedVertexType[Ps <: TypeSet](val label: String, val properties: Ps)
(implicit 
  val propertiesBound: Ps << AnyProperty
)
extends AnySealedVertexType {

  type Properties = Ps
}
