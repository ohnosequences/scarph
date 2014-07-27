package ohnosequences.scarph

import ohnosequences.typesets._
import scalaz._, std.option._, std.list._

import shapeless._

/*
  Declares an edge type. it is determined my a label, source/target vertex types and in/out arities
*/
trait AnyEdgeType extends AnyItemType { edgeT =>

  // TODO add an applicative/monad requirement here
  type In[X]
  type Out[X]
  implicit val inFunctor: Functor[In]
  implicit val outFunctor: Functor[Out]

  type SourceType <: Singleton with AnyVertexType
  val  sourceType: SourceType

  type TargetType <: AnyVertexType
  val  targetType: TargetType

  val asMorphism : Simple[edgeT.type] = new Simple[edgeT.type](edgeT){ 

    type Source = (edgeT.sourceType.type :: HNil)
    val source: (edgeT.sourceType.type :: HNil) = ((edgeT.sourceType: edgeT.sourceType.type) :: HNil)

    type Target = (edgeT.targetType.type :: HNil)
    val target: (edgeT.targetType.type :: HNil) = ((edgeT.targetType: edgeT.targetType.type) :: HNil)
  }
}

object AnyEdgeType {
  /* Additional methods */
  implicit def edgeTypeOps[ET <: AnyEdgeType](et: ET) = EdgeTypeOps(et)
  case class   EdgeTypeOps[ET <: AnyEdgeType](et: ET) 
    extends HasPropertiesOps(et) {}

  type ==>[S <: Singleton with AnyVertexType, T <: AnyVertexType] = AnyEdgeType {
    type SourceType = S
    type TargetType = T
  }
}


/* Source/Target */
trait From[S <: Singleton with AnyVertexType] extends AnyEdgeType { type SourceType = S }
trait   To[T <: AnyVertexType] extends AnyEdgeType { type TargetType = T }

/* Arities */
trait ManyOut extends AnyEdgeType { type Out[X] =   List[X]; val outFunctor = implicitly[Functor[Out]] }
trait  OneOut extends AnyEdgeType { type Out[X] = Option[X]; val outFunctor = implicitly[Functor[Out]] }
trait ManyIn  extends AnyEdgeType { type  In[X] =   List[X]; val  inFunctor = implicitly[Functor[In]] }
trait  OneIn  extends AnyEdgeType { type  In[X] = Option[X]; val  inFunctor = implicitly[Functor[In]] }

class ManyToMany[S <: Singleton with AnyVertexType, T <: AnyVertexType]
  (val sourceType: S, val label: String, val targetType: T) 
    extends From[S] with To[T] with ManyIn with ManyOut

class OneToMany[S <: Singleton with AnyVertexType, T <: AnyVertexType]
  (val sourceType: S, val label: String, val targetType: T) 
    extends From[S] with To[T] with OneIn with ManyOut

class ManyToOne[S <: Singleton with AnyVertexType, T <: AnyVertexType]
  (val sourceType: S, val label: String, val targetType: T) 
    extends From[S] with To[T] with ManyIn with OneOut

class OneToOne[S <: Singleton with AnyVertexType, T <: AnyVertexType]
  (val sourceType: S, val label: String, val targetType: T) 
    extends From[S] with To[T] with OneIn with OneOut
