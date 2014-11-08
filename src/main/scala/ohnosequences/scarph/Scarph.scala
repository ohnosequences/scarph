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


///////////////////////////////////////////////////////////

/* Edge type has in/out vertex types, arities, etc. */
sealed trait AnyArity 

// 4 things: one/many x non-empty/any
// NOTE: don't know if the values of them are needed
trait OneOrNone extends AnyArity
trait ExactlyOne extends AnyArity
trait ManyOrNone extends AnyArity
trait AtLeastOne extends AnyArity

trait HasInArity  { type InArity  <: AnyArity }
trait HasOutArity { type OutArity <: AnyArity }

trait InArity[A <: AnyArity] extends HasInArity { type InArity = A }
trait OutArity[A <: AnyArity] extends HasOutArity { type OutArity = A }

trait ManyOut extends OutArity[ManyOrNone]
trait  OneOut extends OutArity[OneOrNone]
trait ManyIn  extends InArity[ManyOrNone]
trait  OneIn  extends InArity[OneOrNone]


/* Arities multiplication */
// not the best name, but it may look cool: A x B
trait x[A <: AnyArity, B <: AnyArity] extends AnyFn with OutBound[AnyArity]

object x extends x_2 {
  implicit def idemp[A <: AnyArity]: A x A = new x[A, A] { type Out = A }
}

trait x_2 extends x_3 {
  implicit def unitL[A <: AnyArity]: ExactlyOne x A = new x[ExactlyOne, A] { type Out = A }
  implicit def unitR[A <: AnyArity]: A x ExactlyOne = new x[A, ExactlyOne] { type Out = A }
}

trait x_3 extends x_4 {
  implicit def oneornoneL[A <: AnyArity]: OneOrNone x A = new x[OneOrNone, A] { type Out = ManyOrNone }
  implicit def oneornoneR[A <: AnyArity]: A x OneOrNone = new x[A, OneOrNone] { type Out = ManyOrNone }
}

trait x_4 {
  implicit def atleastoneL[A <: AnyArity]: AtLeastOne x A = new x[AtLeastOne, A] { type Out = AtLeastOne }
  implicit def atleastoneR[A <: AnyArity]: A x AtLeastOne = new x[A, AtLeastOne] { type Out = AtLeastOne }
}


trait Pack[X, A <: AnyArity] extends Fn1[List[X]]

// NOTE: these are example conversions. real ones should be provided by an implementation
object Pack {

  implicit def oneornone[X]: 
      Pack[X, OneOrNone] with Out[Option[X]] = 
  new Pack[X, OneOrNone] with Out[Option[X]] { def apply(list: In1): Out = list.headOption }

  // do we need a container here?
  implicit def exactlynone[X]: 
      Pack[X, ExactlyOne] with Out[X] = 
  new Pack[X, ExactlyOne] with Out[X] { def apply(list: In1): Out = list.head }

  implicit def manyornone[X]: 
      Pack[X, ManyOrNone] with Out[List[X]] = 
  new Pack[X, ManyOrNone] with Out[List[X]] { def apply(list: In1): Out = list }

  import scalaz._
  implicit def atleastone[X]: 
      Pack[X, AtLeastOne] with Out[NonEmptyList[X]] = 
  new Pack[X, AtLeastOne] with Out[NonEmptyList[X]] { def apply(list: In1): Out = NonEmptyList.nel(list.head, list.tail) }
}

///////////////////////////////////////////////////////////

trait AnyEdgeType extends AnyElementType with HasInArity with HasOutArity {

  type Source <: AnyVertexType
  val  source: Source

  type Target <: AnyVertexType
  val  target: Target
}

abstract class EdgeType[
  I <: AnyVertexType, 
  O <: AnyVertexType
](val source: I, val target: O) extends AnyEdgeType {

  type Source = I
  type Target = O

  val label = this.toString
}


///////////////////////////////////////////////////////////

trait AnyPath extends HasOutArity {

  type InT <: AnyLabelType
  val  inT: InT

  type OutT <: AnyLabelType
  val  outT: OutT
}

abstract class Path[I <: AnyLabelType, O <: AnyLabelType](val inT: I, val outT: O) extends AnyPath {

  type InT = I
  type OutT = O
}


trait AnyStep extends AnyPath

abstract class Step[I <: AnyLabelType, O <: AnyLabelType](i: I, o: O) 
  extends Path[I, O](i, o) with AnyStep


trait AnyComposition extends AnyPath {

  type First <: AnyPath
  val  first: First

  type Second <: AnyPath //{ type InT = First#OutT }
  val  second: Second

  type InT = First#InT
  val  inT = first.inT

  type OutT = Second#OutT
  val  outT = second.outT

  // should be provided implicitly:
  val canCompose: Composable[First, Second]
}

abstract class Composition[F <: AnyPath, S <: AnyPath](val first: F, val second: S) extends AnyComposition {

  type First = F
  type Second = S
}

class Compose[F <: AnyPath, S <: AnyPath, OutA <: AnyArity] // { type InT = F#OutT }]
  (f: F, s: S)(implicit val canCompose: Composable[F, S] { type OutArity = OutA })
    extends Composition[F, S](f, s) {

  type OutArity = OutA
}


object AnyPath {

  implicit def traversalOps[T <: AnyPath](t: T): PathOps[T] = new PathOps(t)
}

/* This checks that the paths are composable and multiplies their out-arities */
@annotation.implicitNotFound(msg = "Can't compose ${F} with ${S}")
trait Composable[F <: AnyPath, S <: AnyPath] extends HasOutArity {
  val mutlipliedArities: (F#OutArity x S#OutArity)
}

object Composable {
  implicit def can[F <: AnyPath, S <: AnyPath { type InT = F#OutT }, OutA <: AnyArity]
    (implicit m: (F#OutArity x S#OutArity) { type Out = OutA }): 
        Composable[F, S] =
    new Composable[F, S] {
      val mutlipliedArities = m
      type OutArity = OutA
    }
}

class PathOps[F <: AnyPath](val t: F) {

  def >=>[S <: AnyPath, OutA <: AnyArity](s: S)
    (implicit canCompose: Composable[F, S] { type OutArity = OutA }): 
        Compose[F, S, OutA] = 
    new Compose[F, S, OutA](t, s)(canCompose)

  def evalOn[I, O, PO](i: I LabeledBy F#InT)
    (implicit 
      ev: EvalPath[I, F, O],
      // FIXME: !!! this is an important final step, it should be in some EvalEval thing
      pack: Pack[O LabeledBy F#OutT, F#OutArity] { type Out = PO }
    ): PO = pack(ev(i, t))
}

/* Basic steps: */
case class GetProperty[P <: AnyProp](val prop: P) extends Step[P#Owner, P](prop.owner, prop) with OutArity[ExactlyOne]

case class GetSource[E <: AnyEdgeType](val edge: E) extends Step[E, E#Source](edge, edge.source) with OutArity[ExactlyOne]
case class GetTarget[E <: AnyEdgeType](val edge: E) extends Step[E, E#Target](edge, edge.target) with OutArity[ExactlyOne]

case class  GetInEdges[E <: AnyEdgeType](val edge: E) extends Step[E#Target, E](edge.target, edge) with OutArity[E#InArity]
case class GetOutEdges[E <: AnyEdgeType](val edge: E) extends Step[E#Source, E](edge.source, edge) with OutArity[E#OutArity]

// this looks not nice, but so far I don't see a simpler way to declare it generically
case class GetInVertices[E <: AnyEdgeType](val edge: E)
  (implicit c: Composable[GetInEdges[E], GetSource[E]] { type OutArity = E#InArity }) 
    extends Compose[GetInEdges[E], GetSource[E], E#InArity](GetInEdges(edge), GetSource(edge))(c)

case class GetOutVertices[E <: AnyEdgeType](val edge: E)
  (implicit c: Composable[GetOutEdges[E], GetTarget[E]] { type OutArity = E#OutArity }) 
    extends Compose[GetOutEdges[E], GetTarget[E], E#OutArity](GetOutEdges(edge), GetTarget(edge))(c)

///////////////////////////////////////////////////////////

trait AnyEvalPath[P <: AnyPath] {

  type Path = P

  type InVal
  type OutVal

  type In = InVal LabeledBy Path#InT
  type Out = List[OutVal LabeledBy Path#OutT]

  def apply(in: In, p: Path): Out
}

trait EvalPath[I, P <: AnyPath, O] 
  extends AnyEvalPath[P] {

  type InVal = I
  type OutVal = O
}

object AnyEvalPath {

  implicit def evalComposition[
    F <: AnyPath, S <: AnyPath { type InT = F#OutT },
    I, M, O
  ](implicit
    evalFirst:  EvalPath[I, F, M],
    evalSecond: EvalPath[M, S, O]
  ):  EvalPath[I, Composition[F, S], O] =
  new EvalPath[I, Composition[F, S], O] {
    def apply(in: In, p: Path): Out = {
      val bodyOut: List[M LabeledBy F#OutT] = evalFirst(in, p.first)
      // new LabeledBy[List[O], S#OutT](
      bodyOut.flatMap{ b => evalSecond(b, p.second) }
    }
  }
}
