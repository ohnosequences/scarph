package ohnosequences.scarph

/*
  `AnyVertex` defines a denotation of the corresponding `VertexType`.

  Instances are modeled as instances of other type tagged with the singleton type of a `Vertex`. For example, an instance of a self of type `User` when stored/represented by a `Neo4jNode` is going to be something of type `FieldType[user.type, Neo4jNode]`  where `user.type <: AnyVertex { type VertexType = User.type; type Raw = Neo4jNode }`.
  
  They are designed to be compatible with shapeless records (maybe, we'll see).
*/

trait AnyVertex extends Item[AnyVertexType] { vertex =>

  /* Getters for incoming/outgoing edges */
  abstract class GetOutEdge[E <: Singleton with AnyEdge](val e: E) {
    def apply(rep: vertex.Rep): e.tpe.Out[E#Rep]
  }
  abstract class GetInEdge[E <: Singleton with AnyEdge](val e: E) {
    def apply(rep: vertex.Rep): e.tpe.In[E#Rep]
  }

  // no bounds for now, maybe later
  abstract class OutVertexQueryEval[E <: Singleton with AnyEdge, Q <: AnyQuery] {

    type Query = Q
    // evaluate the query at the vertex rep
    def apply(rep: vertex.Rep, query: Query): query.Out[E#Rep]
  }

  abstract class InVertexQueryEval[E <: Singleton with AnyEdge, Q <: AnyQuery](val e: E, val query: Q) {

    type Query = Q
    // evaluate the query at the vertex rep
    def apply(rep: vertex.Rep): query.Out[e.Rep]
  }

}

abstract class Vertex[G, VT <: AnyVertexType](val graph: G, val tpe: VT) extends AnyVertex { 
  type Graph = G
  type Tpe = VT
}

object AnyVertex {
  type ofType[VT <: AnyVertexType] = AnyVertex { type Tpe = VT }
}

object Vertex {
  type RepOf[V <: Singleton with AnyVertex] = AnyTag.TaggedWith[V]
}
