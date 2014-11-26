package ohnosequences.scarph

/* This is any graph type that can have properties, i.e. vertex of edge type */
trait AnyElementType extends AnyLabelType

/* Property is assigned to one element type and has a raw representation */
trait AnyProp extends AnyLabelType {

  type Raw

  type Owner <: AnyElementType
  val  owner: Owner
}

abstract class PropertyOf[O <: AnyElementType](val owner: O) extends AnyProp {
  
  type Owner = O

  val label = this.toString
}


object AnyVertexType {

  // implicit def getVertexOps[V <: AnyVertexType](v: V): vertexOps[V] = vertexOps(v)
}

/* Vertex type is very simple */
trait AnyVertexType extends AnyElementType

abstract class VertexType extends AnyVertexType {

  val label = this.toString
}

trait AnyEdgeType extends AnyElementType {

  /* The source vertex for this edge */
  type Source <: AnyVertexType
  val  source: Source

  /* this is the arity for incoming edges */
  type InC <: AnyConstructor
  val inC: InC

  type Target <: AnyVertexType
  val  target: Target

  type OutC <: AnyConstructor
  val outC: OutC
}

class EdgeType[
  I <: AnyVertexType,
  InC0 <: AnyConstructor,
  O <: AnyVertexType,
  OutC0 <: AnyConstructor
]
(
  val inC: InC0,
  val source: I,
  val outC: OutC0,
  val target: O
)
extends AnyEdgeType {

  type Source = I
  type InC = InC0

  type Target = O
  type OutC = OutC0

  val label = this.toString
}