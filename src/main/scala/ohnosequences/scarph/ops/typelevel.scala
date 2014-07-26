package ohnosequences.scarph.ops

import  ohnosequences.scarph._

/* 
  The point of this is to do all ops on vertex/edge types instead of vertices and edges,
  i.e. `pluto out Pet` instead of `pluto out pet` (where `pet.tpe = Pet`)

  But for using this you have to provide implicits for all your vertices and edges. It doesn't
  change much as you can just create things like `implicit case object pet extends TitanVertex(Pet)`.
*/
object typelevel {

  implicit def getPropertyOps[T <: Singleton with AnyItem](rep: AnyTag.TaggedWith[T]): 
               ops.default.GetPropertyOps[T] = ops.default.GetPropertyOps[T](rep)


  def query[Q <: AnySimpleQuery with AnyQuery.HeadedBy[AnyEQ], I <: Singleton with AnyItem.ofType[Q#ItemType]](q: Q)
    (implicit i: I, evaluator: I#QueryEval[Q]): q.Out[I#Rep] = evaluator(q)

  implicit def vertexRepOps[V <: Singleton with AnyVertex](rep: Vertex.RepOf[V]): VertexRepOps[V] = VertexRepOps[V](rep)
  case class   VertexRepOps[V <: Singleton with AnyVertex](rep: Vertex.RepOf[V]) {

    /* evaluate vertex-local in-queries */
    def inQuery[
      Q <: AnySimpleQuery with AnyQuery.HeadedBy[AnyEQ] with AnyQuery.On[ET],
      ET <: To[V#Tpe], E <: Singleton with AnyEdge.ofType[ET]  
    ]
    (edgeT: ET, query: Q)
    (implicit 
      edge: E, 
      mkEvaluator: (E,Q) => V#InVertexQueryEval[E,Q]
    )
    : Q#Out[E#Rep] = {

      val evaluator = mkEvaluator(edge, query)
      evaluator(rep)
    }

    /* OUT edges */
    def out[ET <: From[V#Tpe], E <: Singleton with AnyEdge.ofType[ET]]
      (et: ET)(implicit 
        e: E, 
        mkGetter: E => V#GetOutEdge[E]
      ): ET#Out[E#Rep] = {
        val getter = mkGetter(e)
        getter(rep)
      }

    /* OUT vertices */
    def outV[ET <: From[V#Tpe], E <: Singleton with AnyEdge.ofType[ET]]
      (et: ET)(implicit 
        e: E,
        mkGetter: E => V#GetOutEdge[E],
        getTarget: E#GetTarget
      ): ET#Out[getTarget.Out] = {
        val getter = mkGetter(e)
        val f = getter.e.tpe.outFunctor
        f.map(getter(rep))(getTarget(_))
      }

    /* IN edges */
    def in[ET <: To[V#Tpe], E <: Singleton with AnyEdge.ofType[ET]]
      (et: ET)(implicit e: E, mkGetter: E => V#GetInEdge[E]): ET#In[E#Rep] = {
        val getter = mkGetter(e)
        getter(rep)
      }

    /* IN vertices */
    def inV[ET <: To[V#Tpe], E <: Singleton with AnyEdge.ofType[ET]]
      (et: ET)(implicit 
        e: E,
        mkGetter: E => V#GetInEdge[E],
        getSource: E#GetSource
      ): ET#In[getSource.Out] = {
        val getter = mkGetter(e)
        val f = getter.e.tpe.inFunctor
        f.map(getter(rep))(getSource(_))
      }
  }

  implicit def  edgeRepOps[E <: Singleton with AnyEdge](rep: Edge.RepOf[E]): 
    ops.default.EdgeRepOps[E] = ops.default.EdgeRepOps(rep)

}
