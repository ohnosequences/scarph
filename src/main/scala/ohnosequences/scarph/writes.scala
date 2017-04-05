package ohnosequences.scarph

import ohnosequences.cosas._, types._

/*
This works exactly the same for properties and edges. The semantics are expected to be in a std graph scenario

1. the input is a set of pairs (source, target); for each pair, add an edge.
2. you will get as output all the newly added edges

For properties of course the target denotations are values (of value types).
*/
trait WriteRelation extends AnyGraphMorphism { write =>

  type Relation <: AnyRelation
  val relation: Relation

  type In = Relation#Source ⊗ Relation#Target ⊗ unit
  val  in: In = (relation.source: Relation#Source) ⊗ (relation.target: Relation#Target) ⊗ unit

  type Out = unit ⊗ Relation
  val  out: Out = unit ⊗ relation

  type Dagger <: DeleteRelation { type Relation = write.Relation }
  val  dagger: Dagger
}

trait DeleteRelation extends AnyGraphMorphism { delete =>

  type Relation <: AnyRelation
  val relation: Relation

  type Out = Relation#Source ⊗ Relation#Target ⊗ unit
  val  out: Out = (relation.source: Relation#Source) ⊗ (relation.target: Relation#Target) ⊗ unit

  type In = unit ⊗ Relation
  val  in: In = unit ⊗ relation

  type Dagger <: WriteRelation { type Relation = delete.Relation }
  val  dagger: Dagger
}

// LEGACY: will be removed

trait AddVertices

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
