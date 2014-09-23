package ohnosequences.scarph

import ohnosequences.pointless._
import scalaz._, std.option._, std.list._

/* Arities */
sealed trait Arity
trait One  extends Arity
trait Many extends Arity

/* Arities */
sealed trait IsDefined
trait Always    extends IsDefined
trait Sometimes extends IsDefined

/*
  Declares an edge type. it is determined by source/target vertex types and in/out arities
*/
trait AnyEdgeType extends AnyElementType {

  type Container[X] = List[X]

  type  InArity <: Arity
  type OutArity <: Arity

  type Defined <: IsDefined

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

trait ManyOut extends AnyEdgeType { type OutArity = Many }
trait  OneOut extends AnyEdgeType { type OutArity =  One }
trait ManyIn  extends AnyEdgeType { type  InArity = Many }
trait  OneIn  extends AnyEdgeType { type  InArity =  One }

trait AlwaysDefined extends AnyEdgeType { type Defined = Always }
trait SometimesDefined extends AnyEdgeType { type Defined = Sometimes }

abstract class EdgeType[S <: AnyVertexType, T <: AnyVertexType, Props <: AnyTypeSet.Of[AnyProperty]]
  (val sourceType: S, val label: String, val targetType: T, val properties: Props) 
    extends From[S] with To[T] with Properties[Props]
