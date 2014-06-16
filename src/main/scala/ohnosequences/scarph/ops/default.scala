package ohnosequences.scarph.ops

import  ohnosequences.scarph._, AnyDenotation._

object default {

  implicit def vertexOps[V <: Singleton with AnyVertex](rep: VertexTag[V]): VertexOps[V] = VertexOps[V](rep)
  case class   VertexOps[V <: Singleton with AnyVertex](rep: VertexTag[V]) {

    def out[E <: Singleton with AnyEdge { type Tpe <: From[rep.DenotedType] }]
      (e: E)(implicit mkRetriever: E => rep.Denotation#RetrieveOutEdge[E]): E#Tpe#Out[E#Rep] = {
        val retriever = mkRetriever(e)
        retriever(rep)
      }

    def in[E <: Singleton with AnyEdge { type Tpe <: To[rep.DenotedType] }]
      (e: E)(implicit mkRetriever: E => rep.Denotation#RetrieveInEdge[E]): E#Tpe#In[E#Rep] = {
        val retriever = mkRetriever(e)
        retriever(rep)
      }

  }

  implicit def edgeOps[E <: Singleton with AnyEdge](rep: EdgeTag[E]): EdgeOps[E] = EdgeOps(rep)
  case class   EdgeOps[E <: Singleton with AnyEdge](rep: EdgeTag[E]) {

    def source[S <: Singleton with AnyVertex.ofType[rep.Denotation#Tpe#SourceType]]
      (implicit getter: rep.Denotation#GetSource[S]) = getter(rep)

    def target[T <: Singleton with AnyVertex.ofType[rep.Denotation#Tpe#TargetType]]
      (implicit getter: rep.Denotation#GetTarget[T]) = getter(rep)

  }

}
