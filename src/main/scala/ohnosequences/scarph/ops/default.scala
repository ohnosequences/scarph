package ohnosequences.scarph.ops

import  ohnosequences.scarph._

object default {

  implicit def vertexOps[V <: Singleton with AnyVertex](rep: Vertex.RepOf[V]): VertexOps[V] = VertexOps[V](rep)
  case class   VertexOps[V <: Singleton with AnyVertex](rep: Vertex.RepOf[V]) {

    /* OUT edges */
    def out[E <: Singleton with AnyEdge { type Tpe <: From[V#Tpe] }]
      (e: E)(implicit mkGetter: E => V#GetOutEdge[E]): E#Tpe#Out[E#Rep] = {
        val getter = mkGetter(e)
        getter(rep)
      }

    /* OUT vertices */
    def outV[E <: Singleton with AnyEdge { type Tpe <: From[V#Tpe] },
             T <: Singleton with AnyVertex.ofType[E#Tpe#TargetType] ]
      (e: E)(implicit mkGetter: E => V#GetOutEdge[E],
                      getTarget: E#GetTarget[T]): E#Tpe#Out[T#Rep] = {
        val getter = mkGetter(e)
        val f = getter.e.tpe.outFunctor
        f.map(getter(rep))(getTarget(_))
      }

    /* IN edges */
    def in[E <: Singleton with AnyEdge { type Tpe <: To[V#Tpe] }]
      (e: E)(implicit mkGetter: E => V#GetInEdge[E]): E#Tpe#In[E#Rep] = {
        val getter = mkGetter(e)
        getter(rep)
      }

    /* IN vertices */
    def inV[E <: Singleton with AnyEdge { type Tpe <: To[V#Tpe] },
            S <: Singleton with AnyVertex.ofType[E#Tpe#SourceType] ]
      (e: E)(implicit mkGetter: E => V#GetInEdge[E],
                      getSource: E#GetSource[S]): E#Tpe#In[S#Rep] = {
        val getter = mkGetter(e)
        val f = getter.e.tpe.inFunctor
        f.map(getter(rep))(getSource(_))
      }
  }

  implicit def edgeOps[E <: Singleton with AnyEdge](rep: Edge.RepOf[E]): EdgeOps[E] = EdgeOps(rep)
  case class   EdgeOps[E <: Singleton with AnyEdge](rep: Edge.RepOf[E]) {

    def source[S <: Singleton with AnyVertex.ofType[E#Tpe#SourceType]]
      (implicit getter: E#GetSource[S]) = getter(rep)

    def target[T <: Singleton with AnyVertex.ofType[E#Tpe#TargetType]]
      (implicit getter: E#GetTarget[T]) = getter(rep)

  }

}
