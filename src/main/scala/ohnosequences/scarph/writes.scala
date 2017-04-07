package ohnosequences.scarph

import ohnosequences.cosas._, types._

/*
  This works exactly the same for properties and edges. The semantics are expected to be in a std graph scenario

  1. the input is a set of pairs (source, target); for each pair, add an edge.
  2. you will get as output all the newly added edges

  For properties of course the target denotations are values (of value types).
*/
//
// aspiring to syntax like
//
// def newUser(name: Name.Value, surname: Name.Value): unit --> User =
//   add(User) >=> (User.name to Name("Paco")) >=> (User.surname to Name("Rodríguez"))
//
// val alumnos =
//   sanitariosDeInstituto >=> cursos >=> alumnos
//
// val deleteInstituto =
//   // first delete edges, then vertices
//   lookup(ID) >=> delete(cursos) >=> (delete(Instituto) ⊗ delete(Curso))

trait AnyWriteRelation extends AnyGraphMorphism { write =>

  lazy val label: String = s"(add ${in.label} -(${relation.label})-> ${out.label})"

  type Relation <: AnyRelation
  val relation: Relation

  type In = Relation#Source ⊗ Relation#Target
  val  in: In = (relation.source: Relation#Source) ⊗ (relation.target: Relation#Target)

  type Out = Relation
  val  out: Out = relation

  type Dagger <: AnyDeleteRelation { type Relation = write.Relation }
  val  dagger: Dagger
}

case class WriteRelation[R <: AnyRelation](val relation: R) extends AnyWriteRelation {

  type Relation = R

  type Dagger = DeleteRelation[R]
  val dagger: Dagger = DeleteRelation(relation)
}

trait AnyDeleteRelation extends AnyGraphMorphism { delete =>

  lazy val label: String = s"(delete ${in.label} -(${relation.label})-> ${out.label})"

  type Relation <: AnyRelation
  val relation: Relation

  type Out = Relation#Source ⊗ Relation#Target
  val  out: Out = (relation.source: Relation#Source) ⊗ (relation.target: Relation#Target)

  type In = Relation
  val  in: In = relation

  type Dagger <: AnyWriteRelation { type Relation = delete.Relation }
  val  dagger: Dagger
}

case class DeleteRelation[R <: AnyRelation](val relation: R) extends AnyDeleteRelation {

  type Relation = R

  type Dagger = WriteRelation[R]
  val dagger: Dagger = WriteRelation(relation)
}

trait AnyWriteVertex extends AnyGraphMorphism { write =>

  lazy val label: String = s"(add ${out.label})"

  type Vertex <: AnyVertex
  val vertex: Vertex

  type In = unit
  val  in: In = unit

  type Out = Vertex
  val  out: Out = vertex

  type Dagger <: AnyDeleteVertex { type Vertex = write.Vertex }
  val  dagger: Dagger
}

case class WriteVertex[V <: AnyVertex](val vertex: V) extends AnyWriteVertex {

  type Vertex = V

  type Dagger = DeleteVertex[V]
  val dagger: Dagger = DeleteVertex(vertex)
}

trait AnyDeleteVertex extends AnyGraphMorphism { delete =>

  lazy val label: String = s"(delete ${out.label})"

  type Vertex <: AnyVertex
  val vertex: Vertex

  type In = Vertex
  val  in: In = vertex

  type Out = unit
  val  out: Out = unit

  type Dagger <: AnyWriteVertex { type Vertex = delete.Vertex }
  val  dagger: Dagger
}

case class DeleteVertex[V <: AnyVertex](val vertex: V) extends AnyDeleteVertex {

  type Vertex = V

  type Dagger = WriteVertex[V]
  val dagger: Dagger = WriteVertex(vertex)
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
