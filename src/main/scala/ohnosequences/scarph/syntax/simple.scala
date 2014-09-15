package ohnosequences.scarph.syntax

import ohnosequences.pointless._, AnyDenotation._, AnyWrap._, AnyFn._
import ohnosequences.scarph._, AnyEdge._

object simple {

  implicit def elementRawOps[E <: AnyElement](rep: ValueOf[E]): 
        ElementRawOps[E] = 
    new ElementRawOps[E](rep.raw)

  class ElementRawOps[E <: AnyElement](val raw: RawOf[E]) extends AnyVal {
    import ohnosequences.scarph.ops.element._

    def get[P <: AnyProperty](prop: P)
      (implicit getter: GetProperty[E, P]): ValueOf[P] = getter(raw, prop)
  }


  implicit def vertexRawOps[V <: AnyVertex](rep: ValueOf[V]): 
        VertexRawOps[V] = 
    new VertexRawOps[V](rep.raw)

  class VertexRawOps[V <: AnyVertex](val raw: RawOf[V]) extends AnyVal {
    import ohnosequences.scarph.ops.vertex._
    import ohnosequences.scarph.ops.edge._

    def in[E <: AnyEdge.withTarget[V]](e: E)
      (implicit in: GetInEdge[E]): EdgeTypeOf[E]#In[ValueOf[E]] = in(raw, e)

    def out[E <: AnyEdge.withSource[V]](e: E)
      (implicit out: GetOutEdge[E]): EdgeTypeOf[E]#Out[ValueOf[E]] = out(raw, e)

    // def outV[E <: AnyEdge.withSource[V]](e: E)
    //   (implicit 
    //     // NOTE: Functor is invariant so don't know how to do this without dots
    //     out: GetOutEdge[E],
    //     tgts: GetTarget[E]
    //   ): EdgeTypeOf[E]#Out[ValueOf[TargetOf[E]]] = {

    //     val edges = out(raw, e)
    //     val eT: E#DenotedType = e.denotedType
    //     val eTF: scalaz.Functor[E#DenotedType#Out]= eT.outFunctor
    //     eTF.map(edges){ eVal => tgts(eVal.raw) }
    //   }

  }


  implicit def edgeRawOps[E <: AnyEdge](rep: ValueOf[E]): 
        EdgeRawOps[E] = 
    new EdgeRawOps[E](rep.raw)

  class EdgeRawOps[E <: AnyEdge](val raw: RawOf[E]) extends AnyVal {
    import ohnosequences.scarph.ops.edge._

    def src(implicit src: GetSource[E]): ValueOf[SourceOf[E]] = src(raw)

    def tgt(implicit tgt: GetTarget[E]): ValueOf[TargetOf[E]] = tgt(raw)

  }

}
