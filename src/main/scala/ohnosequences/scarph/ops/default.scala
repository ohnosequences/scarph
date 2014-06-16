package ohnosequences.scarph.ops

import  ohnosequences.scarph._, AnyDenotation._

object default {

  type VertexTag = AnyDenotation.AnyTag { type Denotation <: Singleton with AnyVertex }

  implicit def vertexOps[R <: VertexTag](rep: R): VertexOps[R] = VertexOps[R](rep)
  case class   VertexOps[R <: VertexTag](rep: R) {

    def outT[ET <: From[rep.Denotation#Tpe], E <: Singleton with AnyEdge { type Tpe <: ET }]
      (et: ET)(implicit e: E, mkRetriever: E => rep.Denotation#RetrieveOutEdge[E]): E#Tpe#Out[E#Rep] = {
        val retriever = mkRetriever(e)
        retriever(rep)
      }

    def out[E <: Singleton with AnyEdge { type Tpe <: From[rep.Denotation#Tpe] }]
      (e: E)(implicit mkRetriever: E => rep.Denotation#RetrieveOutEdge[E]): E#Tpe#Out[E#Rep] = {
        val retriever = mkRetriever(e)
        retriever(rep)
      }

    def in[E <: Singleton with AnyEdge { type Tpe <: To[rep.Denotation#Tpe] }]
      (e: E)(implicit mkRetriever: E => rep.Denotation#RetrieveInEdge[E]): E#Tpe#In[E#Rep] = {
        val retriever = mkRetriever(e)
        retriever(rep)
      }
  }

}
