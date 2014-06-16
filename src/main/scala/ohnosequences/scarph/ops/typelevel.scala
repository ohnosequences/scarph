package ohnosequences.scarph.ops

import  ohnosequences.scarph._, AnyDenotation._

/* 
  The point of this is to do all ops on vertex/edge types instead of vertices and edges,
  i.e. `pluto out Pet` instead of `pluto out pet` (where `pet.tpe = Pet`)

  But for using this you have to provide implicits for all your vertices and edges. It doesn't
  change much as you can just create things like `implicit case object pet extends TVertex(Pet)`.
*/
object typelevel {

  implicit def vertexOps[V <: Singleton with AnyVertex](rep: VertexTag[V]) =  VertexOps[V](rep)
  case class   VertexOps[V <: Singleton with AnyVertex](val rep: VertexTag[V]) {

    def out[ET <: From[rep.Denotation#Tpe], E <: Singleton with AnyEdge { type Tpe <: ET }]
      (et: ET)(implicit e: E, mkRetriever: E => rep.Denotation#RetrieveOutEdge[E]): E#Tpe#Out[E#Rep] = {
        val retriever = mkRetriever(e)
        retriever(rep)
      }

    def in[ET <: To[rep.Denotation#Tpe], E <: Singleton with AnyEdge { type Tpe <: ET }]
      (et: ET)(implicit e: E, mkRetriever: E => rep.Denotation#RetrieveInEdge[E]): E#Tpe#In[E#Rep] = {
        val retriever = mkRetriever(e)
        retriever(rep)
      }
  }

  implicit def  edgeOps[E <: Singleton with AnyEdge](rep: EdgeTag[E]): 
    ops.default.EdgeOps[E] = ops.default.EdgeOps(rep)

}
