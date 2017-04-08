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

case class WriteRelation[
  S <: AnyGraphObject,
  T <: AnyGraphObject,
  E <: AnyRelation {
    type SourceArity <: AnyArity { type GraphObject = S };
    type TargetArity <: AnyArity { type GraphObject = T }
  }
]
(val relation: E) extends AnyGraphMorphism {

  lazy val label: String = s"(add ${in.label} -(${relation.label})-> ${out.label})"

  type In = S ⊗ T
  lazy val  in: In = (relation:E).source ⊗ (relation:E).target

  type Out = E
  val  out: Out = relation

  type Dagger = DeleteRelation[S,T,E]
  val dagger: Dagger = DeleteRelation(relation)
}

case class DeleteRelation[
  S <: AnyGraphObject,
  T <: AnyGraphObject,
  E <: AnyRelation {
    type SourceArity <: AnyArity { type GraphObject = S };
    type TargetArity <: AnyArity { type GraphObject = T }
  }
](val relation: E) extends AnyGraphMorphism {

  lazy val label: String = s"(delete ${in.label} -(${relation.label})-> ${out.label})"

  type In = E
  val  in: In = relation

  type Out = S ⊗ T
  lazy val  out: Out = relation.source ⊗ relation.target

  type Dagger = WriteRelation[S,T,E]
  val dagger: Dagger = WriteRelation(relation)
}

case class WriteVertex[V <: AnyVertex](val vertex: V) extends AnyGraphMorphism {

  lazy val label: String = s"(add ${out.label})"

  type In = unit
  val  in: In = unit

  type Out = V
  val  out: Out = vertex

  type Dagger = DeleteVertex[V]
  val dagger: Dagger = DeleteVertex(vertex)
}

case class DeleteVertex[V <: AnyVertex](val vertex: V) extends AnyGraphMorphism {

  lazy val label: String = s"(delete ${out.label})"

  type In = V
  val  in: In = vertex

  type Out = unit
  val  out: Out = unit

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
