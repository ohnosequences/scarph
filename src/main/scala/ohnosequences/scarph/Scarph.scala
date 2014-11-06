package ohnosequences.scarph

import ohnosequences.pointless._
import shapeless._

trait AnyLabelType {
  val label: String
}

object AnyLabelType {

  implicit def labelTypeOps[T <: AnyLabelType](t: T): LabelTypeOps[T] = new LabelTypeOps[T](t)
}

class LabelTypeOps[T <: AnyLabelType](t: T) {

  def apply[V](v: V): V LabeledBy T = new LabeledBy(v)
}


sealed trait AnyLabeledValue extends Any {

  type Tpe <: AnyLabelType
  // val  tpe: Tpe

  type Value
  val  value: Value
}

trait AnyValueLabeledBy[T <: AnyLabelType] extends Any with AnyLabeledValue {
  type Tpe = T
}

trait LabeledValue[V, T <: AnyLabelType] extends Any with AnyValueLabeledBy[T] {
  type Value = V
}

final class LabeledBy[V, T <: AnyLabelType](val value: V) extends AnyVal with LabeledValue[V, T] {

  // NOTE: it may be confusing:
  override def toString = value.toString
}

///////////////////////////////////////////////////////////

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


/* Edge type has in/out vertex types, arities, etc. */
sealed trait Arity { 

  type T <: AnyVertexType 
  val  t: T
}
abstract class AnyArity[V <: AnyVertexType](val t: V) extends Arity { type T = V }
case class  One[V <: AnyVertexType](v: V) extends AnyArity[V](v)
case class Many[V <: AnyVertexType](v: V) extends AnyArity[V](v)

trait AnyEdgeType extends AnyElementType {

  type Source <: Arity
  val  source: Source

  type Target <: Arity
  val  target: Target

  // TODO: always defined
}

abstract class EdgeType[I <: Arity, O <: Arity](val source: I, val target: O) extends AnyEdgeType {
  type Source = I
  type Target = O

  val label = this.toString
}


///////////////////////////////////////////////////////////

trait AnyTraversal {

  type InT <: AnyLabelType
  val  inT: InT

  type OutT <: AnyLabelType
  val  outT: OutT
}


trait AnyStep extends AnyTraversal

abstract class Step[I <: AnyLabelType, O <: AnyLabelType](val inT: I, val outT: O) extends AnyStep {

  type InT = I
  type OutT = O
}


trait AnyComposition extends AnyTraversal {

  type First <: AnyTraversal
  val  first: First

  type Second <: AnyTraversal { type InT = First#OutT }
  val  second: Second

  type InT = First#InT
  val  inT = first.inT

  type OutT = Second#OutT
  val  outT = second.outT
}

case class Compose[F <: AnyTraversal, S <: AnyTraversal { type InT = F#OutT }]
  (val first: F, val second: S) extends AnyComposition {

  type First = F
  type Second = S
}


object AnyTraversal {

  implicit def traversalOps[T <: AnyTraversal](t: T): TraversalOps[T] = new TraversalOps(t)
}

class TraversalOps[T <: AnyTraversal](val t: T) {

  def >=>[S <: AnyTraversal { type InT = T#OutT }](s: S): Compose[T, S] = Compose[T, S](t, s)

  def evalOn[I, O](i: I LabeledBy T#InT)(implicit ev: EvalTraversal[I, T, O]): O LabeledBy T#OutT = ev(i, t)
}

/* Basic steps: */
case class GetProperty[P <: AnyProp](val prop: P) extends Step[P#Owner, P](prop.owner, prop)

case class GetSource[E <: AnyEdgeType](val edge: E) extends Step[E, E#Source#T](edge, edge.source.t)
case class GetTarget[E <: AnyEdgeType](val edge: E) extends Step[E, E#Target#T](edge, edge.target.t)

case class  GetInEdges[E <: AnyEdgeType](val edge: E) extends Step[E#Target#T, E](edge.target.t, edge)
case class GetOutEdges[E <: AnyEdgeType](val edge: E) extends Step[E#Source#T, E](edge.source.t, edge)


///////////////////////////////////////////////////////////

trait AnyEvalTraversal[T <: AnyTraversal] {

  type Traversal = T

  type InVal
  type OutVal

  type In = InVal LabeledBy Traversal#InT
  type Out = OutVal LabeledBy Traversal#OutT

  def apply(in: In, s: Traversal): Out
}

trait EvalTraversal[I, T <: AnyTraversal, O] 
  extends AnyEvalTraversal[T] {

  type InVal = I
  type OutVal = O
}

object AnyEvalTraversal {

  implicit def evalComposition[
    F <: AnyTraversal, S <: AnyTraversal { type InT = F#OutT },
    I, M, O
  ](implicit
    evalFirst:  EvalTraversal[I, F, M],
    evalSecond: EvalTraversal[M, S, O]
  ):  EvalTraversal[I, Compose[F, S], O] =
  new EvalTraversal[I, Compose[F, S], O] {
    def apply(in: In, t: Traversal): Out = {
      val bodyOut = evalFirst(in, t.first)
      evalSecond(bodyOut, t.second)
    }
  }
}
