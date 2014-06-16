package ohnosequences.scarph.ops

import  ohnosequences.scarph._, AnyDenotation._

object default {

  type VertexTag[V <: Singleton with AnyVertex] = AnyDenotation.TaggedWith[V]

  implicit def vertexOps[V <: Singleton with AnyVertex](rep: VertexTag[V]) =  VertexOps[V](rep)
  case class   VertexOps[V <: Singleton with AnyVertex](val rep: VertexTag[V]) {

    def outT[ET <: From[rep.DenotedType], E <: Singleton with AnyEdge { type Tpe <: ET }]
      (et: ET)(implicit e: E, mkRetriever: E => rep.Denotation#RetrieveOutEdge[E]): E#Tpe#Out[E#Rep] = {
        val retriever = mkRetriever(e)
        retriever(rep)
      }

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

}
