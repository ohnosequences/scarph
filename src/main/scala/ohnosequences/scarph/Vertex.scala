package ohnosequences.scarph

/*
  `AnyVertex` defines a denotation of the corresponding `VertexType`.

  Instances are modeled as instances of other type tagged with the singleton type of a `Vertex`. For example, an instance of a self of type `User` when stored/represented by a `Neo4jNode` is going to be something of type `FieldType[user.type, Neo4jNode]`  where `user.type <: AnyVertex { type VertexType = User.type; type Raw = Neo4jNode }`.
  
  They are designed to be compatible with shapeless records (maybe, we'll see).
*/

trait AnyVertex extends Denotation[AnyVertexType] with CanHaveProperties { vertex =>

  /* Getters for incoming/outgoing edges */
  trait AnyRetrieveOutEdge {

    type Edge <: AnyEdge
    val e: Edge

    def apply(rep: vertex.Rep): e.tpe.Out[e.Rep]
  }

  abstract class RetrieveOutEdge[E <: Singleton with AnyEdge](val e: E) 
    extends AnyRetrieveOutEdge { type Edge = E }

  trait AnyRetrieveInEdge {

    type Edge <: AnyEdge
    val e: Edge

    def apply(rep: vertex.Rep): e.tpe.In[e.Rep]
  }
  abstract class RetrieveInEdge[E <: Singleton with AnyEdge](val e: E) 
      extends AnyRetrieveInEdge { type Edge = E }

  abstract class Out[E <: Singleton with AnyEdge](edge: E) extends RetrieveOutEdge[E](edge)

  implicit def vertexOps(rep: vertex.Rep) = VertexOps(rep)
  case class   VertexOps(rep: vertex.Rep) {

    def out[E <: Singleton with AnyEdge { type Tpe <: From[vertex.Tpe] }]
      (e: E)(implicit mkRetriever: E => RetrieveOutEdge[E]): E#Tpe#Out[E#Rep] = {
        val retriever = mkRetriever(e)
        retriever(rep)
      }

    def in[E <: Singleton with AnyEdge { type Tpe <: To[vertex.Tpe] }]
      (e: E)(implicit mkRetriever: E => RetrieveInEdge[E]): E#Tpe#In[E#Rep] = {
        val retriever = mkRetriever(e)
        retriever(rep)
      }

  }

}

abstract class Vertex[VT <: AnyVertexType](val tpe: VT) 
  extends AnyVertex { type Tpe = VT }

object AnyVertex {
  type ofType[VT <: AnyVertexType] = AnyVertex { type Tpe = VT }
}

