package ohnosequences.scarph.syntax

import ohnosequences.pointless._, AnyDenotation._, AnyWrap._, AnyFn._
import ohnosequences.scarph._, AnyEdge._

object simple {

  implicit def elementTypeOps[ET <: AnyElementType](et: ET):
        ElementTypeOps[ET] =
    new ElementTypeOps[ET](et)

  class ElementTypeOps[ET <: AnyElementType](val et: ET) {

    // def get[P <: AnyProperty](p: P): GetProperty[ET, P] = GetProperty(et, p)

    // def query[Q <: AnyQuery.On[E#Tpe]](q: Q)
    //   (implicit eval: EvalQuery[E, Q]): Q#Out[ValueOf[E]] = eval(e, q)
  }


  implicit def vertexTypeOps[VT <: AnyVertexType](vt: VT):
        VertexTypeOps[VT] =
    new VertexTypeOps[VT](vt)

  class VertexTypeOps[VT <: AnyVertexType](val vt: VT) {

    // def in[E <: AnyEdge.withTarget[VT]](e: E)
    //   (implicit in: GetInEdge[E]): EdgeTypeOf[E]#In[ValueOf[E]] = in(raw, e)

    // def outE[ET <: AnyEdgeType { type SourceType = VT }](et: ET):
    //   GetOutEdges[ET] = GetOutEdges[ET](et)

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

    // def src: GetSource[ET] = GetSource(et)
    // def tgt: GetTarget[ET] = GetTarget(et)
  }

}
