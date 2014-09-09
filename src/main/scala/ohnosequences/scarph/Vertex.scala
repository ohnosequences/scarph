package ohnosequences.scarph

import ohnosequences.pointless._, AnyDenotation._
import AnyEdge._

/*
  `AnyVertex` defines a denotation of the corresponding `VertexType`.

  Instances are modeled as instances of other type tagged with the singleton type of a `Vertex`. For example, an instance of a self of type `User` when stored/represented by a `Neo4jNode` is going to be something of type `FieldType[user.type, Neo4jNode]`  where `user.type <: AnyVertex { type VertexType = User.type; type Raw = Neo4jNode }`.
  
  They are designed to be compatible with shapeless records (maybe, we'll see).
*/

trait AnyVertex extends Denotation[AnyVertexType]

abstract class Vertex[VT <: AnyVertexType](val denotedType: VT) 
  extends AnyVertex { type DenotedType = VT }

object AnyVertex {

  type ofType[VT <: AnyVertexType] = AnyVertex { type DenotedType = VT }

  type VertexTypeOf[V <: AnyVertex] = V#DenotedType

  implicit def vertexValueOps[V <: AnyVertex](rep: ValueOf[V]): VertexValueOps[V] = new VertexValueOps[V](rep)
}

class VertexValueOps[V <: AnyVertex](rep: ValueOf[V]) {
  import ohnosequences.scarph.ops.vertex._

  def get[P <: AnyProperty](prop: P)
    (implicit getter: GetProperty[V, P]): ValueOf[P] = getter(rep, prop)

  def in[E <: AnyEdge.withTarget[V]](e: E)
    (implicit in: GetInEdge[E]): EdgeTypeOf[E]#In[ValueOf[E]] = in(rep, e)

  def out[E <: AnyEdge.withSource[V]](e: E)
    (implicit out: GetOutEdge[E]): EdgeTypeOf[E]#Out[ValueOf[E]] = out(rep, e)

}
