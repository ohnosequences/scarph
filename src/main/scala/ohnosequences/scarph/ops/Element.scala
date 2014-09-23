package ohnosequences.scarph.ops

import ohnosequences.scarph._, AnyEdge._
import ohnosequences.pointless._, AnyWrap._

object element {

  trait GetProperty[E <: AnyElement, P <: AnyProperty]
    extends Fn2[E#Raw, P] with Out[ValueOf[P]]

  // trait EvalQuery[E <: AnyElement, Q <: AnyQuery.On[E#Tpe]]
  //   extends Fn2[E, Q] with Out[Q#Out[ValueOf[E]]]

}
