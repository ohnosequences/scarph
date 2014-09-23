package ohnosequences.scarph.syntax

import ohnosequences.pointless._, AnyDenotation._, AnyWrap._, AnyFn._
import ohnosequences.scarph._, AnyEdge._

object simple {

  implicit def elementTypeOps[ET <: AnyElementType](et: ET): 
        ElementTypeOps[ET] = 
    new ElementTypeOps[ET](et)

  class ElementTypeOps[ET <: AnyElementType](val et: ET) extends AnyVal {

    def get[P <: AnyProperty](p: P): GetProperty[ET, P] = GetProperty(et, p)

    // def query[Q <: AnyQuery.On[E#Tpe]](q: Q)
    //   (implicit eval: EvalQuery[E, Q]): Q#Out[ValueOf[E]] = eval(e, q)
  }


  implicit def vertexRawOps[V <: AnyVertex](rep: ValueOf[V]): 
        VertexRawOps[V] = 
    new VertexRawOps[V](rep.raw)

  class VertexRawOps[V <: AnyVertex](val raw: RawOf[V]) extends AnyVal {

    // def in[E <: AnyEdge.withTarget[V]](e: E)
    //   (implicit in: GetInEdge[E]): EdgeTypeOf[E]#In[ValueOf[E]] = in(raw, e)

    // def out[E <: AnyEdge.withSource[V]](e: E)
    //   (implicit out: GetOutEdge[E]): EdgeTypeOf[E]#Out[ValueOf[E]] = out(raw, e)

    // def outV[E <: AnyEdge.withSource[V]](e: E)
    //   (implicit 
    //     // NOTE: Functor is invariant so don't know how to do this without dots
    //     out: GetOutEdge[E],
    //     tgts: GetTarget[E]
    //   ): EdgeTypeOf[E]#Out[ValueOf[TargetOf[E]]] = {

    //     val edges = out(raw, e)
    //     val eT: E#Tpe = e.tpe
    //     val eTF: scalaz.Functor[E#Tpe#Out]= eT.outFunctor
    //     eTF.map(edges){ eVal => tgts(eVal.raw) }
    //   }

  }


  implicit def edgeTypeOps[ET <: AnyEdgeType](et: ET): 
        EdgeTypeOps[ET] = 
    new EdgeTypeOps[ET](et)

  class EdgeTypeOps[ET <: AnyEdgeType](val et: ET) {

    def src: GetSource[ET] = GetSource(et)
    def tgt: GetTarget[ET] = GetTarget(et)
  }

}
