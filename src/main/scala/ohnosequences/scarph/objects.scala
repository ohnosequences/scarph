package ohnosequences.scarph

import scala.reflect.ClassTag
import ohnosequences.cosas._, types._

trait AnyGraphType extends AnyType {

  type Raw = Any
}



// FIXME: should be sealed
trait AnyGraphObject extends AnyGraphType



trait AnyRelation extends AnyGraphObject {

  type SourceArity <: AnyArity
  val  sourceArity: SourceArity

  type Source = SourceArity#GraphObject
  lazy val source: Source = sourceArity.graphObject

  type TargetArity <: AnyArity
  val  targetArity: TargetArity

  type Target = TargetArity#GraphObject
  lazy val target: Target = targetArity.graphObject
}



// NOTE in tradititional graph data models, only "elements" can have properties
sealed trait AnyGraphElement extends AnyGraphObject



trait AnyVertex extends AnyGraphElement
class Vertex(val label: String) extends AnyVertex



trait AnyEdge extends AnyRelation with AnyGraphElement {

  type SourceArity <: AnyArity.OfVertices
  type TargetArity <: AnyArity.OfVertices
}

case object AnyEdge {

  type From[S <: AnyVertex] = AnyEdge { type Source = S }
  type   To[T <: AnyVertex] = AnyEdge { type Target = T }
}

/* This constructor encourages to use this syntax: Edge(user -> tweet)("tweeted") */
abstract class Edge[
  S <: AnyArity.OfVertices,
  T <: AnyArity.OfVertices
](st: (S, T))(val label: String) extends AnyEdge {

  type SourceArity = S
  lazy val sourceArity: SourceArity = st._1

  type TargetArity = T
  lazy val targetArity: TargetArity = st._2
}

/* Property values have raw types that are covered as graph objects */
trait AnyValueType extends AnyGraphObject {

  type Val
  def valueTag: ClassTag[Val]
}

abstract class ValueOfType[V](val label: String)(implicit val valueTag: ClassTag[V]) extends AnyValueType {

  type Val = V
}
