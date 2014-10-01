package ohnosequences.scarph.ops

import ohnosequences.scarph._, AnyEdge._
import ohnosequences.cosas._, AnyWrap._

object edge {

  trait GetSource[E <: AnyEdge] extends Fn1[RawOf[E]] with Out[ValueOf[SourceOf[E]]]

  trait GetTarget[E <: AnyEdge] extends Fn1[RawOf[E]] with Out[ValueOf[TargetOf[E]]]

}
