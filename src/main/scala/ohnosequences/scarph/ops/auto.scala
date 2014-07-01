package ohnosequences.scarph.ops

import  ohnosequences.scarph._

/* 
  The point of this is to do all ops on vertex/edge types instead of vertices and edges,
  i.e. `pluto out Pet` instead of `pluto out pet` (where `pet.tpe = Pet`)

  But for using this you have to provide implicits for all your vertices and edges. It doesn't
  change much as you can just create things like `implicit case object pet extends TVertex(Pet)`.
*/
object auto {

  implicit def vertexOps[V <: Singleton with AnyVertex](rep: Vertex.RepOf[V]): VertexOps[V] = VertexOps[V](rep)
  case class   VertexOps[V <: Singleton with AnyVertex](rep: Vertex.RepOf[V]) {

    /* OUT edges */
    def out[ET <: Singleton with From[V#Tpe], G <: V#GetOutEdgeT[ET]]
      (et: ET)(implicit 
        mkGetter: ET => G
      ): G#Out = {
        val getter = mkGetter(et)
        getter(rep)//: getter.Out //: et.Out[E#Rep]
      }

    /* OUT vertices */
    // def outV[ET <: From[V#Tpe], E <: Singleton with AnyEdge.ofType[ET]]
    //   (et: ET)(implicit 
    //     e: E,
    //     mkGetter: E => V#GetOutEdge[E],
    //     getTarget: E#GetTarget
    //   ): ET#Out[E#Target#Rep] = {
    //     val getter = mkGetter(e)
    //     val f = getter.e.tpe.outFunctor
    //     f.map(getter(rep))(getTarget(_))
    //   }

    /* IN edges */
    // def in[ET <: To[V#Tpe], E <: Singleton with AnyEdge.ofType[ET]]
    //   (et: ET)(implicit 
    //     mkGetter: ET => V#GetInEdge[E]
    //   ): ET#In[E#Rep] = {
    //     val getter = mkGetter(et)
    //     getter(rep)
    //   }

    /* IN vertices */
  //   def inV[ET <: To[V#Tpe], E <: Singleton with AnyEdge.ofType[ET]]
  //     (et: ET)(implicit 
  //       e: E,
  //       mkGetter: E => V#GetInEdge[E],
  //       getSource: E#GetSource
  //     ): ET#In[E#Source#Rep] = {
  //       val getter = mkGetter(e)
  //       val f = getter.e.tpe.inFunctor
  //       f.map(getter(rep))(getSource(_))
  //     }

  }

  implicit def  edgeOps[E <: Singleton with AnyEdge](rep: Edge.RepOf[E]): 
    ops.default.EdgeOps[E] = ops.default.EdgeOps(rep)

}
