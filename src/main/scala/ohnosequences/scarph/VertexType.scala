package ohnosequences.scarph

import ohnosequences.pointless._, AnyTypeSet._

/*
  Declares a Vertex type. They are essentially classified by its label, a `String`.
*/
trait AnyVertexType extends AnyType with AnyPropertiesHolder

class VertexType[Props <: AnyTypeSet.Of[AnyProperty]](
  val label: String,
  val properties: Props
) extends AnyVertexType { type Properties = Props }
