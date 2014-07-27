package ohnosequences.scarph

import ohnosequences.typesets._

/*
  `AnyVertex` defines a denotation of the corresponding `VertexType`.

  Instances are modeled as instances of other type tagged with the singleton type of a `Vertex`. For example, an instance of a self of type `User` when stored/represented by a `Neo4jNode` is going to be something of type `FieldType[user.type, Neo4jNode]`  where `user.type <: AnyVertex { type VertexType = User.type; type Raw = Neo4jNode }`.
  
  They are designed to be compatible with shapeless records (maybe, we'll see).
*/

trait AnyVertex extends Denotation[AnyVertexType] with CanGetProperties { vertex =>

  /* Getters for incoming/outgoing edges */
  abstract class GetOutEdge[E <: Singleton with AnyEdge](val e: E) {
    def apply(rep: vertex.Rep): e.tpe.Out[E#Rep]
  }
  abstract class GetInEdge[E <: Singleton with AnyEdge](val e: E) {
    def apply(rep: vertex.Rep): e.tpe.In[E#Rep]
  }

}

abstract class Vertex[VT <: AnyVertexType](val tpe: VT) 
    extends AnyVertex { type Tpe = VT }

object AnyVertex {
  type ofType[VT <: AnyVertexType] = AnyVertex { type Tpe = VT }
}

object Vertex {
  type RepOf[V <: Singleton with AnyVertex] = AnyTag.TaggedWith[V]
}

trait AnySealedVertex extends Denotation[AnySealedVertexType] {

  type RawProperties <: TypeSet
  // why??
  type Tpe <: AnySealedVertexType
  // should be provided implicitly:
  val  representedProperties: Represented.By[Tpe#Properties, RawProperties]
}

abstract class SealedVertex[VT <: AnySealedVertexType, RPs <: TypeSet]
  (val tpe: VT)
  (implicit val representedProperties: Represented.By[VT#Properties, RPs]) 
extends AnySealedVertex {

  type RawProperties = RPs
  type Tpe = VT
} 
