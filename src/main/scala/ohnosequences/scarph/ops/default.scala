package ohnosequences.scarph.ops

import  ohnosequences.scarph._

object default {

  /* Common ops for getting properties */
  implicit def propertyGetterOps[T <: Singleton with AnyItem](rep: AnyTag.TaggedWith[T]): 
               PropertyGetterOps[T] = PropertyGetterOps[T](rep)
  case class   PropertyGetterOps[T <: Singleton with AnyItem](rep: AnyTag.TaggedWith[T]) {

    def get[P <: Singleton with AnyProperty: Property.Of[T#Tpe]#is](p: P)
      (implicit mkGetter: p.type => T#PropertyGetter[p.type]): p.Raw = 
        mkGetter(p).apply(rep)
  }

  /* Vertex representation ops */
  implicit def vertexRepOps[V <: Singleton with AnyVertex](rep: Vertex.RepOf[V]): VertexRepOps[V] = VertexRepOps[V](rep)
  case class   VertexRepOps[V <: Singleton with AnyVertex](rep: Vertex.RepOf[V]) {

    /* OUT edges */
    def out[E <: Singleton with AnyEdge { type Tpe <: From[V#Tpe] }]
      (e: E)(implicit mkGetter: E => V#GetOutEdge[E]): E#Tpe#Out[E#Rep] = {
        val getter = mkGetter(e)
        getter(rep)
      }

    /* OUT vertices */
    def outV[E <: Singleton with AnyEdge { type Tpe <: From[V#Tpe] }]
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


  /* Edge representation ops */
  implicit def indexRepOps[I <: Singleton with AnyIndex](rep: I#Rep)
    (implicit getI: I#Rep => I): IndexRepOps[I] = {
      val i = getI(rep)
      IndexRepOps[I](i, rep)
    }
  case class   IndexRepOps[I <: Singleton with AnyIndex](i: I, rep: I#Rep) {

    type Pred = i.tpe.PredicateType

    // def lookup[P <: AnyPredicate](p: P)
    //   (implicit lookupper: I#LookupItem[P]) = {
    //     // val lookupper = mkLookupper(p)
    //     lookupper(p, rep)
    //   }

  }
}
