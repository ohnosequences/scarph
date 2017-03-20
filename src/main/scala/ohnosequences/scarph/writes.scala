package ohnosequences.scarph

import ohnosequences.cosas._, types._

trait CanAddVertices[G, V <: AnyVertex, RawVertex] {

  def addVertex(graph: G)(v: V): V := RawVertex
}

trait CanAddEdges[RawSource, R <: AnyEdge, RawEdge, RawTarget] {

  def addEdge(r: R)(
    src: R#Source := RawSource,
    tgt: R#Target := RawTarget
  ): R := RawEdge
}

trait CanSetProperties[
  E <: AnyGraphElement,
  RawElement,
  P <: AnyProperty,
  V <: P#Target#Val
] {

  def setProperty(
    e: E := RawElement,
    p: P,
    v: V
  ): E := RawElement
}
