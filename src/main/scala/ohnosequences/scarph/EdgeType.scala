package ohnosequences.scarph

import ohnosequences.typesets._
import scalaz._, std.option._, std.list._

/*
  Declares an edge type. it is determined my a label, source/target vertex types and in/out arities
*/
trait AnyEdgeType {

  val label: String

  // TODO add an applicative/monad requirement here
  type In[X]
  type Out[X]
  implicit val inFunctor: Functor[In]
  implicit val outFunctor: Functor[Out]

  type SourceType <: AnyVertexType
  val sourceType: SourceType

  type TargetType <: AnyVertexType
  val targetType: TargetType
}

object AnyEdgeType {
  /* Additional methods */
  implicit def edgeTypeOps[ET <: AnyEdgeType](et: ET) = EdgeTypeOps(et)
  case class   EdgeTypeOps[ET <: AnyEdgeType](et: ET) 
    extends HasPropertiesOps(et) {}

  type ==>[S <: AnyVertexType, T <: AnyVertexType] = AnyEdgeType {
    type SourceType = S
    type TargetType = T
  }
}

trait AnySealedEdgeType extends AnyEdgeType {

  // type SourceType <: AnySealedVertexType
  // type TargetType <: AnySealedVertexType

  type Record <: Singleton with AnyRecord
  val record: Record
}

abstract class SealedEdgeType [
  S <: AnyVertexType,
  R <: Singleton with AnyRecord,
  T <: AnyVertexType
](
  val sourceType: S,
  val label: String,
  val record: R,
  val targetType: T
) 
extends AnySealedEdgeType with From[S] with To[T] {

  type Record = R
}

/* Source/Target */
trait From[S <: AnyVertexType] extends AnyEdgeType { type SourceType = S }
trait   To[T <: AnyVertexType] extends AnyEdgeType { type TargetType = T }

/* Arities */
trait ManyOut extends AnyEdgeType { type Out[X] =   List[X]; val outFunctor = implicitly[Functor[Out]] }
trait  OneOut extends AnyEdgeType { type Out[X] = Option[X]; val outFunctor = implicitly[Functor[Out]] }
trait ManyIn  extends AnyEdgeType { type  In[X] =   List[X]; val  inFunctor = implicitly[Functor[In]] }
trait  OneIn  extends AnyEdgeType { type  In[X] = Option[X]; val  inFunctor = implicitly[Functor[In]] }

class ManyToMany[S <: AnyVertexType, T <: AnyVertexType]
  (val sourceType: S, val label: String, val targetType: T) 
    extends From[S] with To[T] with ManyIn with ManyOut

class OneToMany[S <: AnyVertexType, T <: AnyVertexType]
  (val sourceType: S, val label: String, val targetType: T) 
    extends From[S] with To[T] with OneIn with ManyOut

class ManyToOne[S <: AnyVertexType, T <: AnyVertexType]
  (val sourceType: S, val label: String, val targetType: T) 
    extends From[S] with To[T] with ManyIn with OneOut

class OneToOne[S <: AnyVertexType, T <: AnyVertexType]
  (val sourceType: S, val label: String, val targetType: T) 
    extends From[S] with To[T] with OneIn with OneOut
