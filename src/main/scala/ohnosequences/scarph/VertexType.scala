package ohnosequences.scarph

import ohnosequences.pointless._, AnyTypeSet._

/*
  Declares a Vertex type. They are essentially classified by its label, a `String`.
*/
trait AnyVertexType extends AnyType with AnyPropertiesHolder

abstract class VertexType(val label: String) extends AnyVertexType

// object AnyVertexType {
  
  /* Additional methods */
  // implicit def vertexTypeOps[VT <: AnyVertexType](vt: VT): VertexTypeOps[VT] = VertexTypeOps(vt)

  // case class   VertexTypeOps[VT <: AnyVertexType](val vt: VT) 
  // extends HasPropertiesOps[VT](vt) {}
// }
