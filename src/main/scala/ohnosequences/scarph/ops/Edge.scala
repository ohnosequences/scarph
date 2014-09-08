package ohnosequences.scarph.ops

import ohnosequences.scarph._, AnyEdge._
import ohnosequences.pointless._, AnyWrap._

object edge {

  trait GetSourceOf[E <: AnyEdge] extends Fn1[RawOf[E]] with Out[ValueOf[SourceOf[E]]]

  trait GetTargetOf[E <: AnyEdge] extends Fn1[RawOf[E]] with Out[ValueOf[TargetOf[E]]]

  trait GetProperty[E <: AnyEdge, P <: AnyProperty]
    extends Fn2[ValueOf[E], P] with Out[ValueOf[P]]

}
