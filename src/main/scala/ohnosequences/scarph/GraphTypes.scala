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

  implicit def getVertexOps[V <: AnyVertexType](v: V): vertexOps[V] = vertexOps(v)
}

/* Vertex type is very simple */
trait AnyVertexType extends AnyElementType

abstract class VertexType extends AnyVertexType {

  val label = this.toString
}

object AnyEdgeType {

  implicit def getEdgeOps[E <: AnyEdgeType](e: E): edgeOps[E] = edgeOps(e)
}

trait AnyEdgeType extends AnyElementType with HasInArity with HasOutArity {

  type Source <: AnyVertexType
  val  source: Source

  type InC[X <: AnyLabelType] <: Container[InC,X]
  type InV = InC[Source]
  val inV: InV

  type Target <: AnyVertexType
  val  target: Target

  type OutC[X <: AnyLabelType] <: Container[OutC,X]
  type OutV = OutC[Target]
  val outV: OutV
}

abstract class EdgeType[
  I <: AnyVertexType, 
  O <: AnyVertexType
](val source: I, val target: O) extends AnyEdgeType {

  type Source = I
  type Target = O

  val label = this.toString
}
