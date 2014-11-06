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
// sealed trait AnyArity extends AnyLabelType { 

//   type T <: AnyVertexType 
//   val  t: T
// }
// abstract class Arity[V <: AnyVertexType](val t: V) extends AnyArity { type T = V }
// case class  One[V <: AnyVertexType](v: V) extends Arity[V](v) { val label = s"$One(${v.label})" }
// case class Many[V <: AnyVertexType](v: V) extends Arity[V](v) { val label = s"$Many(${v.label})" }

sealed trait AnyArity
trait Many extends AnyArity
trait One extends AnyArity

trait CombineArities[A <: AnyArity, B <: AnyArity] {
  type Out <: AnyArity
}

object CombineArities {
  implicit def ManyToMany: CombineArities[Many, Many] = new CombineArities[Many, Many] { type Out = Many }
  implicit def OneToMany: CombineArities[One, Many] = new CombineArities[One, Many] { type Out = Many }
  implicit def ManyToOne: CombineArities[Many, One] = new CombineArities[Many, One] { type Out = One }
  implicit def OneToOne: CombineArities[One, One] = new CombineArities[One, One] { type Out = One }
}

trait InOutArities {

  type InArity <: AnyArity
  type OutArity <: AnyArity
}

trait ManyOut extends InOutArities { type OutArity = Many }
trait  OneOut extends InOutArities { type OutArity =  One }
trait ManyIn  extends InOutArities { type  InArity = Many }
trait  OneIn  extends InOutArities { type  InArity =  One }

trait AnyEdgeType extends AnyElementType {

  type Source <: AnyVertexType
  val  source: Source

  type Target <: AnyVertexType
  val  target: Target

  // TODO: always defined
}

abstract class EdgeType[I <: AnyVertexType, O <: AnyVertexType](val source: I, val target: O) extends AnyEdgeType {
  type Source = I
  type Target = O

  val label = this.toString
}


///////////////////////////////////////////////////////////

trait AnyTraversal extends InOutArities {

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

class Compose[F <: AnyTraversal, S <: AnyTraversal { type InT = F#OutT }]
  (val first: F, val second: S) extends AnyComposition {

  type First = F
  type Second = S
}


object AnyTraversal {

  implicit def traversalOps[T <: AnyTraversal](t: T): TraversalOps[T] = new TraversalOps(t)
}

class TraversalOps[T <: AnyTraversal](val t: T) {

  def >=>[S <: AnyTraversal { type InT = T#OutT }](s: S): Compose[T, S] = new Compose[T, S](t, s)

  def evalOn[I, O](i: I LabeledBy T#InT)(implicit ev: EvalTraversal[I, T, O]): O LabeledBy T#OutT = ev(i, t)
}

/* Basic steps: */
case class GetProperty[P <: AnyProp](val prop: P) extends Step[P#Owner, P](prop.owner, prop)

case class GetSource[E <: AnyEdgeType](val edge: E) extends Step[E, E#Source](edge, edge.source)
case class GetTarget[E <: AnyEdgeType](val edge: E) extends Step[E, E#Target](edge, edge.target)

case class  GetInEdges[E <: AnyEdgeType](val edge: E) extends Step[E#Target, E](edge.target, edge)
case class GetOutEdges[E <: AnyEdgeType](val edge: E) extends Step[E#Source, E](edge.source, edge)

/* See how cool it is: you can define composed traversals and then (optionnaly) have a more effective evaluation for it */
case class  GetInVertices[E <: AnyEdgeType](val edge: E) extends Compose(GetInEdges(edge), GetSource(edge))
case class GetOutVertices[E <: AnyEdgeType](val edge: E) extends Compose(GetOutEdges(edge), GetTarget(edge))

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
