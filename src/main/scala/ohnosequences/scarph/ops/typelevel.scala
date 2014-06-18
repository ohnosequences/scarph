package ohnosequences.scarph.ops

import  ohnosequences.scarph._

/* 
  The point of this is to do all ops on vertex/edge types instead of vertices and edges,
  i.e. `pluto out Pet` instead of `pluto out pet` (where `pet.tpe = Pet`)

  But for using this you have to provide implicits for all your vertices and edges. It doesn't
  change much as you can just create things like `implicit case object pet extends TVertex(Pet)`.
*/
object typelevel {

  implicit def vertexOps[V <: Singleton with AnyVertex](rep: Vertex.RepOf[V]) =  VertexOps[V](rep)
  case class   VertexOps[V <: Singleton with AnyVertex](rep: Vertex.RepOf[V]) {

    type Vertex = rep.Denotation
    type VertexType = rep.DenotedType

    /* OUT edges */
    def out[ET <: From[Vertex#Tpe], E <: Singleton with AnyEdge.ofType[ET]]
      (et: ET)(implicit 
        e: E, 
        mkGetter: E => Vertex#GetOutEdge[E]
      ): ET#Out[E#Rep] = {
        val getter = mkGetter(e)
        getter(rep)
      }

    def outT[ET <: From[Vertex#Tpe], E <: Singleton with AnyEdge.ofType[ET]]
      (et: ET)(implicit 
        toE: ET => E,
        mkGetter: E => Vertex#GetOutEdge[E]
      ): ET#Out[E#Rep] = {
        val e = toE(et)
        val getter = mkGetter(e)
        getter(rep)
      }

    /* OUT vertices */
    def outV[ET <: From[VertexType],
             E <: Singleton with AnyEdge.ofType[ET],
             T <: Singleton with AnyVertex.ofType[ET#TargetType] ]
      (et: ET)(implicit 
        e: E,
        mkGetter: E => Vertex#GetOutEdge[E],
        getTarget: E#GetTarget[T]
      ): ET#Out[T#Rep] = {
        val getter = mkGetter(e)
        val f = getter.e.tpe.outFunctor
        f.map(getter(rep))(getTarget(_))
      }

    /* IN edges */
    def in[ET <: To[Vertex#Tpe], E <: Singleton with AnyEdge.ofType[ET]]
      (et: ET)(implicit e: E, mkGetter: E => Vertex#GetInEdge[E]): ET#In[E#Rep] = {
        val getter = mkGetter(e)
        getter(rep)
      }

    /* IN vertices */
    def inV[ET <: To[VertexType],
            E <: Singleton with AnyEdge.ofType[ET],
            S <: Singleton with AnyVertex.ofType[E#Tpe#SourceType] ]
      (et: ET)(implicit 
        e: E,
        mkGetter: E => Vertex#GetInEdge[E],
        getSource: E#GetSource[S]
      ): ET#In[S#Rep] = {
        val getter = mkGetter(e)
        val f = getter.e.tpe.inFunctor
        f.map(getter(rep))(getSource(_))
      }
  }

  implicit def  edgeOps[E <: Singleton with AnyEdge](rep: Edge.RepOf[E]): 
    ops.default.EdgeOps[E] = ops.default.EdgeOps(rep)

}
