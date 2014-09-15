package ohnosequences.scarph.ops

import ohnosequences.scarph._, AnyEdge._
import ohnosequences.pointless._, AnyWrap._

object element {

  trait GetProperty[E <: AnyElement, P <: AnyProperty]
    extends Fn2[RawOf[E], P] with Out[ValueOf[P]]

}
