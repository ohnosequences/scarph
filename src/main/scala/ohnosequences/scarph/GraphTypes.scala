package ohnosequences.scarph

object types {

  // type ==>[
  //   I <: AnyVertexType,
  //   InC0[X <: AnyLabelType] <: Container[InC0], 
  //   O <: AnyVertexType,
  //   OutC0[X <: AnyLabelType] <: Container[OutC0]
  // ] = EdgeType[I,InC0,O,OutC0]
}

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

object AnyEdgeType {

  // implicit def getEdgeOps[E <: AnyEdgeType](e: E): edgeOps[E] = edgeOps(e)
}

trait AnyEdgeType extends AnyElementType {

  /* The source vertex for this edge */
  type Source <: AnyVertexType
  val  source: Source

  /* this is the arity for incoming edges */
  type InC[X <: AnyLabelType] <: Container[InC] with Of[X]
  type InV = InC[Source]
  val inV: InV

  type Target <: AnyVertexType
  val  target: Target

  type OutC[X <: AnyLabelType] <: Container[OutC] with Of[X]
  type OutV = OutC[Target]
  val outV: OutV
}

class EdgeType[
  I <: AnyVertexType,
  InC0[X <: AnyLabelType] <: Container[InC0] with Of[X], 
  O <: AnyVertexType,
  OutC0[X <: AnyLabelType] <: Container[OutC0] with Of[X]
]
(
  val inV: InC0[I],
  val outV: OutC0[O]
)
extends AnyEdgeType {

  type Source = I
  val source = inV.of
  type InC[X <: AnyLabelType] = InC0[X]

  type Target = O
  val target = outV.of
  type OutC[X <: AnyLabelType] = OutC0[X]

  val label = this.toString
}

// abstract class EdgeType[
//   I <: AnyVertexType, 
//   O <: AnyVertexType
// ](val source: I, val target: O) extends AnyEdgeType {

//   type Source = I
//   type Target = O

//   val label = this.toString
// }