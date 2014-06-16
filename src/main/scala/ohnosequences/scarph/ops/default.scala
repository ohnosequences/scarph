package ohnosequences.scarph.ops

import  ohnosequences.scarph._, AnyDenotation._

object default {

  implicit def vertexOps[V <: Singleton with AnyVertex](rep: VertexTag[V]): VertexOps[V] = VertexOps[V](rep)
  case class   VertexOps[V <: Singleton with AnyVertex](rep: VertexTag[V]) {

    type Vertex = rep.Denotation
    type VertexType = rep.DenotedType

    /* OUT edges */
    def out[E <: Singleton with AnyEdge { type Tpe <: From[VertexType] }]
      (e: E)(implicit mkRetriever: E => Vertex#RetrieveOutEdge[E]): E#Tpe#Out[E#Rep] = {
        val retriever = mkRetriever(e)
        retriever(rep)
      }

    /* OUT vertices */
    def outV[E <: Singleton with AnyEdge { type Tpe <: From[VertexType] },
             T <: Singleton with AnyVertex.ofType[E#Tpe#TargetType] ]
      (e: E)(implicit mkRetriever: E => Vertex#RetrieveOutEdge[E],
                      getTarget: E#GetTarget[T]): E#Tpe#Out[T#Rep] = {
        val retriever = mkRetriever(e)
        val f = retriever.e.tpe.outFunctor
        f.map(retriever(rep))(getTarget(_))
      }

    /* IN edges */
    def in[E <: Singleton with AnyEdge { type Tpe <: To[VertexType] }]
      (e: E)(implicit mkRetriever: E => Vertex#RetrieveInEdge[E]): E#Tpe#In[E#Rep] = {
        val retriever = mkRetriever(e)
        retriever(rep)
      }

    /* IN vertices */
    def inV[E <: Singleton with AnyEdge { type Tpe <: To[VertexType] },
            S <: Singleton with AnyVertex.ofType[E#Tpe#SourceType] ]
      (e: E)(implicit mkRetriever: E => Vertex#RetrieveInEdge[E],
                      getSource: E#GetSource[S]): E#Tpe#In[S#Rep] = {
        val retriever = mkRetriever(e)
        val f = retriever.e.tpe.inFunctor
        f.map(retriever(rep))(getSource(_))
      }
  }

  implicit def edgeOps[E <: Singleton with AnyEdge](rep: EdgeTag[E]): EdgeOps[E] = EdgeOps(rep)
  case class   EdgeOps[E <: Singleton with AnyEdge](rep: EdgeTag[E]) {

    type Edge = rep.Denotation

    def source[S <: Singleton with AnyVertex.ofType[Edge#Tpe#SourceType]]
      (implicit getter: Edge#GetSource[S]) = getter(rep)

    def target[T <: Singleton with AnyVertex.ofType[Edge#Tpe#TargetType]]
      (implicit getter: Edge#GetTarget[T]) = getter(rep)

  }

}
