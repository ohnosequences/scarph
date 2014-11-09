package ohnosequences.scarph

/* This is any graph type that can have properties, i.e. vertex of edge type */
trait AnyElementType extends AnyLabelType

// TODO Fn
/*
Containers are type constructors
*/
trait AnyContainer extends AnyLabelType {

  type Of <: AnyLabelType
  val of: Of
}
sealed trait Container[C[X <: AnyLabelType] <: Container[C,X], X <: AnyLabelType] extends AnyContainer with AnyLabelType {

  type Of = X
  val of: X

  def apply[Y <: AnyLabelType](y: Y): C[Y]
}

final case class oneOrNone[T <: AnyLabelType](val of: T) extends Container[oneOrNone, T] with AnyLabelType {

  def apply[Y <: AnyLabelType](y: Y): oneOrNone[Y] = oneOrNone[Y](y)
  lazy val label = s"oneOrNone(${of.label})"

}
  
trait AnyExactlyOne extends AnyContainer
final case class exactlyOne[T <: AnyLabelType](val of: T) extends Container[exactlyOne, T] with AnyExactlyOne {

  def apply[X <: AnyLabelType](x: X): exactlyOne[X] = exactlyOne[X](x)
  lazy val label = s"exactlyOne(${of.label})"
}
final case class manyOrNone[T <: AnyLabelType](val of: T) extends Container[manyOrNone, T] with AnyLabelType {

  def apply[X <: AnyLabelType](x: X): manyOrNone[X] = manyOrNone[X](x)
  lazy val label = s"manyOrNone(${of.label})"
}
final case class atLeastOne[T <: AnyLabelType](val of: T) extends Container[atLeastOne, T] with AnyLabelType {
  
  def apply[X <: AnyLabelType](x: X): atLeastOne[X] = atLeastOne[X](x)
  lazy val label = s"atLeastOne(${of.label})"
}

object AnyContainer {

  implicit def oneOrNoneV[X <: AnyElementType](x: X): oneOrNone[X] = oneOrNone[X](x)
  implicit def exactlyOneV[X <: AnyElementType](x: X): exactlyOne[X] = exactlyOne[X](x)
}

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
