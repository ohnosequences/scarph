package ohnosequences.scarph

import ohnosequences.typesets._

/*
  Witnesses of a sourceType/type adscription to an edge type.
*/
trait AnyEdgeType {

  val label: String

  // TODO add an applicative/monad requirement here
  type In[+X]
  type Out[+X]

  type SourceType <: AnyVertexType
  val sourceType: SourceType

  type TargetType <: AnyVertexType
  val targetType: TargetType
}

object AnyEdgeType {
  implicit def edgeTypeOps[ET <: AnyEdgeType](et: ET) = EdgeTypeOps(et)

  type ==>[S <: AnyVertexType, T <: AnyVertexType] = AnyEdgeType {
    type SourceType = S
    type TargetType = T
  }
}

case class EdgeTypeOps[ET <: AnyEdgeType](val et: ET) {

  /* Handy way of creating an implicit evidence saying that this vertex type has that property */
  def has[P <: AnyProperty](p: P) = HasProperty[ET, P](et, p)

  /* Takes a set of properties and filters out only those, which this vertex "has" */
  def filterMyProps[Ps <: TypeSet : boundedBy[AnyProperty]#is](ps: Ps)(implicit f: FilterProps[ET, Ps]) = f(ps)
}

/* Source/Target */
trait From[S <: AnyVertexType] extends AnyEdgeType { type SourceType = S }
trait   To[T <: AnyVertexType] extends AnyEdgeType { type TargetType = T }

/* Arities */
trait  In[I[+_]] extends AnyEdgeType { type In[+X] = I[X] }
trait Out[O[+_]] extends AnyEdgeType { type Out[+X] = O[X] }

import ohnosequences.typesets._

class EdgeType[S <: AnyVertexType, T <: AnyVertexType, Ps <: TypeSet : boundedBy[AnyProperty]#is]
  (val sourceType: S, val label: String, val targetType: T, val props: Ps = ∅) 
    extends From[S] with To[T]

trait ManyOut extends AnyEdgeType { type Out[+X] =   List[X] }
trait  OneOut extends AnyEdgeType { type Out[+X] = Option[X] }
trait ManyIn  extends AnyEdgeType { type  In[+X] =   List[X] }
trait  OneIn  extends AnyEdgeType { type  In[+X] = Option[X] }

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
