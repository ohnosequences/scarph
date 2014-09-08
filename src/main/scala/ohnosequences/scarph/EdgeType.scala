package ohnosequences.scarph

import ohnosequences.pointless._
import scalaz._, std.option._, std.list._

// TODO: Add AnyRecord here
/*
  Declares an edge type. it is determined by source/target vertex types and in/out arities
*/
trait AnyEdgeType extends AnyType with AnyPropertiesHolder {

  type In[X]
  type Out[X]
  implicit val inFunctor: Functor[In]
  implicit val outFunctor: Functor[Out]

  type SourceType <: AnyVertexType
  val  sourceType: SourceType

  type TargetType <: AnyVertexType
  val  targetType: TargetType
}

object AnyEdgeType {

  type ==>[S <: AnyVertexType, T <: AnyVertexType] = AnyEdgeType {
    type SourceType = S
    type TargetType = T
  }

  type SourceTypeOf[ET <: AnyEdgeType] = ET#SourceType
  type TargetTypeOf[ET <: AnyEdgeType] = ET#TargetType
}

/* Source/Target */
trait From[S <: AnyVertexType] extends AnyEdgeType { type SourceType = S }
trait   To[T <: AnyVertexType] extends AnyEdgeType { type TargetType = T }

/* Arities */
trait ManyOut extends AnyEdgeType { type Out[X] =   List[X]; val outFunctor = implicitly[Functor[Out]] }
trait  OneOut extends AnyEdgeType { type Out[X] = Option[X]; val outFunctor = implicitly[Functor[Out]] }
trait ManyIn  extends AnyEdgeType { type  In[X] =   List[X]; val  inFunctor = implicitly[Functor[In]] }
trait  OneIn  extends AnyEdgeType { type  In[X] = Option[X]; val  inFunctor = implicitly[Functor[In]] }

abstract class ManyToMany[S <: AnyVertexType, T <: AnyVertexType]
  (val sourceType: S, val label: String, val targetType: T) 
    extends From[S] with To[T] with ManyIn with ManyOut

abstract class OneToMany[S <: AnyVertexType, T <: AnyVertexType]
  (val sourceType: S, val label: String, val targetType: T) 
    extends From[S] with To[T] with OneIn with ManyOut

abstract class ManyToOne[S <: AnyVertexType, T <: AnyVertexType]
  (val sourceType: S, val label: String, val targetType: T) 
    extends From[S] with To[T] with ManyIn with OneOut

abstract class OneToOne[S <: AnyVertexType, T <: AnyVertexType]
  (val sourceType: S, val label: String, val targetType: T) 
    extends From[S] with To[T] with OneIn with OneOut
