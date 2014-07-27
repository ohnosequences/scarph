package ohnosequences.scarph.ops

import  ohnosequences.scarph._

object default {

  /* Common ops for getting properties */
  implicit def getPropertyOps[T <: Singleton with AnyItem](rep: AnyTag.TaggedWith[T]): 
               GetPropertyOps[T] = GetPropertyOps[T](rep)
  case class   GetPropertyOps[T <: Singleton with AnyItem](rep: AnyTag.TaggedWith[T]) {

    def get[P <: Singleton with AnyProperty: Property.Of[T#Tpe]#is](p: P)
      (implicit mkGetter: p.type => T#PropertyGetter[p.type]): p.Raw = 
        mkGetter(p).apply(rep)
  }

  implicit def itemOps[I <: Singleton with AnyItem](item: I): 
               ItemOps[I] = ItemOps[I](item)
  case class   ItemOps[I <: Singleton with AnyItem](item: I) {

    // def lookup[C <: AnyEQ](c: C)
    //   (implicit check: I HasProperty c.Property, lookupper: I#QueryEval[VertexQuery[item.Tpe, C]]): List[I#Rep] = {
    //     lookupper(VertexQuery(item.tpe: item.tpe.type, c))
    //   }

    def query[Q <: AnySimpleQuery with AnyQuery.On[I#Tpe] with AnyQuery.HeadedBy[AnyEQ]](q: Q)
      (implicit evaluator: I#QueryEval[Q]): q.Out[I#Rep] = {
        evaluator(q)
      }

  }

  /* Vertex representation ops */
  implicit def vertexRepOps[V <: Singleton with AnyVertex](rep: Vertex.RepOf[V]): VertexRepOps[V] = VertexRepOps[V](rep)
  case class   VertexRepOps[V <: Singleton with AnyVertex](rep: Vertex.RepOf[V]) {

    /* OUT edges */
    def out[E <: Singleton with AnyEdge { type Tpe <: From[Singleton with V#Tpe] }]
      (e: E)(implicit mkGetter: E => V#GetOutEdge[E]): E#Tpe#Out[E#Rep] = {
        val getter = mkGetter(e)
        getter(rep)
      }

    /* OUT vertices */
    def outV[E <: Singleton with AnyEdge { type Tpe <: From[Singleton with V#Tpe] }]
      (e: E)(implicit mkGetter: E => V#GetOutEdge[E],
                      getTarget: E#GetTarget): E#Tpe#Out[getTarget.Out] = {
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
    def inV[E <: Singleton with AnyEdge { type Tpe <: To[V#Tpe] }]
      (e: E)(implicit mkGetter: E => V#GetInEdge[E],
                      getSource: E#GetSource): E#Tpe#In[getSource.Out] = {
        val getter = mkGetter(e)
        val f = getter.e.tpe.inFunctor
        f.map(getter(rep))(getSource(_))
      }
  }

  /* Edge representation ops */
  implicit def edgeRepOps[E <: Singleton with AnyEdge](rep: Edge.RepOf[E]): EdgeRepOps[E] = EdgeRepOps(rep)
  case class   EdgeRepOps[E <: Singleton with AnyEdge](rep: Edge.RepOf[E]) {

    def source[S <: Singleton with AnyVertex.ofType[E#Tpe#SourceType]]
      (implicit getter: E#GetSource) = getter(rep)

    def target[T <: Singleton with AnyVertex.ofType[E#Tpe#TargetType]]
      (implicit getter: E#GetTarget) = getter(rep)

  }

}
