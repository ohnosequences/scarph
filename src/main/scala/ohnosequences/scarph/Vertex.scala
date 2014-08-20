package ohnosequences.scarph

import ohnosequences.typesets._, AnyTag._

/*
  `AnyVertex` defines a denotation of the corresponding `VertexType`.

  Instances are modeled as instances of other type tagged with the singleton type of a `Vertex`. For example, an instance of a self of type `User` when stored/represented by a `Neo4jNode` is going to be something of type `FieldType[user.type, Neo4jNode]`  where `user.type <: AnyVertex { type VertexType = User.type; type Raw = Neo4jNode }`.
  
  They are designed to be compatible with shapeless records (maybe, we'll see).
*/

trait AnyVertex extends Denotation[AnyVertexType] with CanGetProperties { vertex =>

  /* Getters for incoming/outgoing edges */
  // abstract class GetOutEdge[E <: Singleton with AnyEdge](val e: E) {
  abstract class GetOutEdge[OE <: AnyEdge](val edge: OE) {

    // def apply(rep: vertex.Rep): e.tpe.Out[E#Rep]
    def apply(rep: vertex.Rep): edge.tpe.Out[TaggedWith[OE]]
  }
  abstract class GetInEdge[IE <: AnyEdge](val edge: IE) {

    def apply(rep: vertex.Rep): edge.tpe.In[TaggedWith[IE]]
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

// this denotation stuff is weird
trait AnySealedVertex extends AnyVertex { sealedVertex =>

  type Tpe <: AnySealedVertexType

  final type Raw = raw

  type Other
  case class raw(val fields: tpe.record.Rep, val other: Other)
  // double tagging FTW!
  final def fields[R <: TypeSet](r: R)(implicit 
    p: R ~> tpe.record.Raw
  ): tpe.record.Rep = (tpe.record ->> p(r))

  implicit def propertyOps(rep: sealedVertex.Rep): tpe.record.PropertyOps = tpe.record.PropertyOps(rep.fields)
}

abstract class SealedVertex[VT <: AnySealedVertexType](val tpe: VT) extends AnySealedVertex { 

  type Tpe = VT
}