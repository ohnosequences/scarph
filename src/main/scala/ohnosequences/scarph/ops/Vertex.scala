package ohnosequences.scarph.ops

import ohnosequences.scarph._, AnyEdge._
import ohnosequences.pointless._

object vertex {

  trait GetOutEdge[E <: AnyEdge]
    extends Fn2[ValueOf[SourceOf[E]], E] with Out[EdgeTypeOf[E]#Out[ValueOf[E]]]

  trait GetInEdge[E <: AnyEdge]
    extends Fn2[ValueOf[TargetOf[E]], E] with Out[EdgeTypeOf[E]#In[ValueOf[E]]]

  trait GetProperty[V <: AnyVertex, P <: AnyProperty]
    extends Fn2[ValueOf[V], P] with Out[ValueOf[P]]

}
  
