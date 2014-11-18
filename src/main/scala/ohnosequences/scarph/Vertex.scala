package ohnosequences.scarph

import ohnosequences.cosas._, AnyDenotation._, AnyWrap._
import AnyEdge._

/*
  `AnyVertex` defines a denotation of the corresponding `VertexType`.

  Instances are modeled as instances of other type tagged with the singleton type of a `Vertex`. For example, an instance of a self of type `User` when stored/represented by a `Neo4jNode` is going to be something of type `FieldType[user.type, Neo4jNode]`  where `user.type <: AnyVertex { type VertexType = User.type; type Raw = Neo4jNode }`.
  
  They are designed to be compatible with shapeless records (maybe, we'll see).
*/

  // // no bounds for now, maybe later
  // abstract class OutVertexQueryEval[E <: AnyEdge, Q <: AnyQuery](val e: E, val query: Q) {

  //   type Query = Q
  //   // evaluate the query at the vertex rep
  //   def apply(rep: vertex.Rep, query: Query): e.tpe.Out[E#Rep]
  // }

  // abstract class InVertexQueryEval[E <: AnyEdge, Q <: AnyQuery](val e: E, val query: Q) {

  //   type Query = Q
  //   // evaluate the query at the vertex rep
  //   def apply(rep: vertex.Rep): e.tpe.In[E#Rep]
  // }
trait AnyVertex extends AnyElementOf[AnyVertexType]

abstract class Vertex[VT <: AnyVertexType]
  (val  tpe : VT) extends AnyVertex { 
   type Tpe = VT
}

object AnyVertex {

  type ofType[VT <: AnyVertexType] = AnyVertex { type Tpe = VT }

  type VertexTypeOf[V <: AnyVertex] = V#Tpe
}
