package ohnosequences.scarph

import ohnosequences.pointless._, AnyTypeSet._

/*
  Declares a Vertex type. They are essentially classified by its label, a `String`.
*/
trait AnyVertexType extends AnyType with AnyPropertiesHolder

abstract class VertexType(val label: String) extends AnyVertexType
