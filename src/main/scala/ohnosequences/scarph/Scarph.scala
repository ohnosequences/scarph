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

  type Body <: AnyTraversal
  val  body: Body

  type Head <: AnyStep { type InT = Body#OutT }
  val  head: Head

  type InT = Body#InT
  val  inT = body.inT

  type OutT = Head#OutT
  val  outT = head.outT
}

case class Compose[B <: AnyTraversal, H <: AnyStep { type InT = B#OutT }]
  (val body: B, val head: H) extends AnyComposition {

  type Body = B
  type Head = H
}


object AnyTraversal {

  implicit def traversalOps[T <: AnyTraversal](t: T): TraversalOps[T] = new TraversalOps(t)
}

class TraversalOps[T <: AnyTraversal](val t: T) {

  def >=>[S <: AnyStep { type InT = T#OutT }](s: S): Compose[T, S] = Compose[T, S](t, s)

  def evalOn[I, O](i: I LabeledBy T#InT)(implicit ev: EvalTraversal[I, T, O]): O LabeledBy T#OutT = ev(i, t)
}

/* Dasic steps: */
case class GetProperty[P <: AnyProp](val prop: P) extends Step[P#Owner, P](prop.owner, prop)

case class GetSource[E <: AnyEdgeType](edge: E) extends Step[E, E#Source#T](edge, edge.source.t)
case class GetTarget[E <: AnyEdgeType](edge: E) extends Step[E, E#Target#T](edge, edge.target.t)

case class  GetInEdges[E <: AnyEdgeType](edge: E) extends Step[E#Target#T, E](edge.target.t, edge)
case class GetOutEdges[E <: AnyEdgeType](edge: E) extends Step[E#Source#T, E](edge.source.t, edge)


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

trait AnyEvalStep[S <: AnyStep] extends AnyEvalTraversal[S]

trait EvalStep[I, S <: AnyStep, O] extends AnyEvalStep[S] with EvalTraversal[I, S, O]

object AnyEvalTraversal {

  implicit def evalStep[
    I, S <: AnyStep, O
  ](implicit 
    ev: EvalStep[I, S, O]
  ):  EvalTraversal[I, S, O] =
  new EvalTraversal[I, S, O] {
    def apply(in: In, t: Traversal): Out = ev(in, t)
  }

  implicit def evalComposition[
    B <: AnyTraversal, H <: AnyStep { type InT = B#OutT },
    I, M, O
  ](implicit
    evalHead: EvalStep[M, H, O],
    evalBody: EvalTraversal[I, B, M]
  ):  EvalTraversal[I, Compose[B, H], O] =
  new EvalTraversal[I, Compose[B, H], O] {
    def apply(in: In, t: Traversal): Out = {
      val bodyOut = evalBody(in, t.body)
      evalHead(bodyOut, t.head)
    }
  }
}
