package ohnosequences.scarph

import ohnosequences.cosas._, types._

trait CanAddVertices[G, V <: AnyVertex, RawVertex] {

  def addVertex(graph: G)(v: V): V := RawVertex
}

trait CanAddRelations[G, R <: AnyRelation, RawRelation, RawSource, RawTarget] {

  def addRelation(graph: G)(r: R)(
    src: R#Source := RawSource,
    tgt: R#Target := RawTarget
  ): R := RawRelation
}
