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


/* Vertex type is very simple */
trait AnyVertexType extends AnyElementType

abstract class VertexType extends AnyVertexType {

  val label = this.toString
}


trait AnyEdgeType extends AnyElementType with HasInArity with HasOutArity {

  type SourceType <: AnyVertexType
  val  sourceType: SourceType

  type TargetType <: AnyVertexType
  val  targetType: TargetType
}

abstract class EdgeType[
  I <: AnyVertexType, 
  O <: AnyVertexType
](val sourceType: I, val targetType: O) extends AnyEdgeType {

  type SourceType = I
  type TargetType = O

  val label = this.toString
}
