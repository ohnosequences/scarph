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
sealed trait AnyArity extends AnyLabelType { 

  type Tpe <: AnyLabelType 
  val  tpe: Tpe
}
abstract class Arity[L <: AnyLabelType](val tpe: L) extends AnyArity { type Tpe = L }

// TODO: there should be 4 things: one/many x non-empty/any
case class  One[L <: AnyLabelType](l: L) extends Arity[L](l) { val label = s"One(${l.label})" }
case class Many[L <: AnyLabelType](l: L) extends Arity[L](l) { val label = s"Many(${l.label})" }

trait AnyEdgeType extends AnyElementType {

  // FIXME: these types should be somehow bounded by AnyVertexType
  type Source <: AnyArity { type Tpe <: AnyVertexType }
  val  source: Source

  type Target <: AnyArity { type Tpe <: AnyVertexType }
  val  target: Target
}

abstract class EdgeType[
  I <: AnyArity { type Tpe <: AnyVertexType }, 
  O <: AnyArity { type Tpe <: AnyVertexType }
](val source: I, val target: O) extends AnyEdgeType {

  type Source = I
  type Target = O

  val label = this.toString
}


///////////////////////////////////////////////////////////

trait AnyTraversal {

  type InT <: AnyArity
  val  inT: InT

  type OutT <: AnyArity
  val  outT: OutT
}

abstract class Traversal[I <: AnyArity, O <: AnyArity](val inT: I, val outT: O) extends AnyTraversal {

  type InT = I
  type OutT = O
}


trait AnyStep extends AnyTraversal

abstract class Step[I <: AnyArity, O <: AnyArity](i: I, o: O) 
  extends Traversal[I, O](i, o) with AnyStep


trait AnyComposition extends AnyTraversal {

  type First <: AnyTraversal
  val  first: First

  type Second <: AnyTraversal //{ type InT = First#OutT }
  val  second: Second

  type InT = First#InT
  val  inT = first.inT

  type OutT = Second#OutT
  val  outT = second.outT
}

case class Compose[F <: AnyTraversal, S <: AnyTraversal] // { type InT = F#OutT }]
  (val first: F, val second: S)(implicit can: CanCompose[F, S]) extends AnyComposition {

  type First = F
  type Second = S
}


object AnyTraversal {

  implicit def traversalOps[T <: AnyTraversal](t: T): TraversalOps[T] = new TraversalOps(t)
}

@annotation.implicitNotFound(msg = "Can't compose ${F} with ${S}")
trait CanCompose[F <: AnyTraversal, S <: AnyTraversal]

object CanCompose {
  implicit def can[F <: AnyTraversal, S <: AnyTraversal { type InT = F#OutT }]: 
      CanCompose[F, S] =
  new CanCompose[F, S] {}
}

class TraversalOps[T <: AnyTraversal](val t: T) {

  def >=>[S <: AnyTraversal](s: S)
    (implicit can: CanCompose[T, S]): Compose[T, S] = Compose[T, S](t, s)(can)

  def evalOn[I, O](i: I LabeledBy T#InT)(implicit ev: EvalTraversal[I, T, O]): O LabeledBy T#OutT = ev(i, t)
}

/* Basic steps: */
case class GetProperty[P <: AnyProp](val prop: P) extends Step[One[P#Owner], One[P]](One(prop.owner), One(prop))

case class GetSource[E <: AnyEdgeType](val edge: E) extends Step[One[E], One[E#Source#Tpe]](One(edge), One(edge.source.tpe))
case class GetTarget[E <: AnyEdgeType](val edge: E) extends Step[One[E], One[E#Target#Tpe]](One(edge), One(edge.target.tpe))

// TODO: these should actually return `E` with the corresponding arity
case class  GetInEdges[E <: AnyEdgeType](val edge: E) extends Step[E#Target, Many[E]](edge.target, Many(edge))
case class GetOutEdges[E <: AnyEdgeType](val edge: E) extends Step[E#Source, Many[E]](edge.source, Many(edge))

case class Lift[T <: AnyTraversal](val traversal: T) extends Traversal[Many[T#InT#Tpe], Many[T#OutT#Tpe]](Many(traversal.inT.tpe), Many(traversal.outT.tpe))

case class Flatten[T <: AnyLabelType](val tpe: T) extends Step[Many[Many[T]], Many[T]](Many(Many(tpe)), Many(tpe))

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
