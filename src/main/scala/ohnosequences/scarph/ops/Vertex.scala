package ohnosequences.scarph.ops

import ohnosequences.pointless._, AnyWrap._
import ohnosequences.scarph._, AnyEdge._

object vertex {

  trait GetOutEdge[E <: AnyEdge]
    extends Fn2[RawOf[SourceOf[E]], E] with Out[EdgeTypeOf[E]#Out[ValueOf[E]]]

  trait GetInEdge[E <: AnyEdge]
    extends Fn2[RawOf[TargetOf[E]], E] with Out[EdgeTypeOf[E]#In[ValueOf[E]]]

  trait GetProperty[V <: AnyVertex, P <: AnyProperty]
    extends Fn2[RawOf[V], P] with Out[ValueOf[P]]

}
  
