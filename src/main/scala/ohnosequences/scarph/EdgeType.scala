package ohnosequences.scarph

import ohnosequences.pointless._
import scalaz._, std.option._, std.list._

/*
  Declares an edge type. it is determined by source/target vertex types and in/out arities
*/
trait AnyEdgeType extends AnyType with AnyPropertiesHolder {

  type In[X]
  type Out[X]

  val  in: Arity
  val  out: Arity

  val inFunctor: Functor[In]
  val outFunctor: Functor[Out]

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

  trait From[S <: AnyVertexType] extends AnyEdgeType { type SourceType = S }
  trait   To[T <: AnyVertexType] extends AnyEdgeType { type TargetType = T }

  type SourceTypeOf[ET <: AnyEdgeType] = ET#SourceType
  type TargetTypeOf[ET <: AnyEdgeType] = ET#TargetType
}

/* Source/Target */
trait From[S <: AnyVertexType] extends AnyEdgeType { type SourceType = S }
trait   To[T <: AnyVertexType] extends AnyEdgeType { type TargetType = T }

/* Arities */
sealed trait Arity
case object One  extends Arity
case object Many extends Arity

trait ManyOut extends AnyEdgeType { type Out[X] =   List[X]; val out = Many; val outFunctor = implicitly[Functor[Out]] }
trait  OneOut extends AnyEdgeType { type Out[X] = Option[X]; val out = One;  val outFunctor = implicitly[Functor[Out]] }
trait ManyIn  extends AnyEdgeType { type  In[X] =   List[X]; val  in = Many; val  inFunctor = implicitly[Functor[In]] }
trait  OneIn  extends AnyEdgeType { type  In[X] = Option[X]; val  in = One;  val  inFunctor = implicitly[Functor[In]] }

abstract class EdgeType[S <: AnyVertexType, T <: AnyVertexType, Props <: AnyTypeSet.Of[AnyProperty]]
  (val sourceType: S, val label: String, val targetType: T, val properties: Props) 
    extends From[S] with To[T] with Properties[Props]
