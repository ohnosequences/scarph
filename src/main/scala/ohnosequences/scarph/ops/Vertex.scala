package ohnosequences.scarph.ops

import ohnosequences.pointless._, AnyWrap._
import ohnosequences.scarph._, AnyEdge._

object vertex {

  trait GetOutEdge[E <: AnyEdge]
    extends Fn2[RawOf[SourceOf[E]], E] with Out[EdgeTypeOf[E]#Container[ValueOf[E]]]

  trait GetInEdge[E <: AnyEdge]
    extends Fn2[RawOf[TargetOf[E]], E] with Out[EdgeTypeOf[E]#Container[ValueOf[E]]]

}
  
