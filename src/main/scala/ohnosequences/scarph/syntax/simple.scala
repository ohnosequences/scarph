package ohnosequences.scarph.syntax

import ohnosequences.scarph._

/* This is an example gremlin-like syntax for paths construction */
object simple {

  /* Element types */
  implicit def fromElement[E <: AnyElementType](e: E):
        ElementOps[IdStep[E]] =
    new ElementOps[IdStep[E]](IdStep(e))
  implicit def fromElementPath[B <: AnyPath { type OutT <: AnyElementType }](b: B):
        ElementOps[B] =
    new ElementOps[B](b)

  class ElementOps[Base <: AnyPath { type OutT <: AnyElementType }](base: Base) {

    def get[P <: AnyProp](p: P)
      (implicit c: Composable[Base, GetProperty[P]] { type Out = Base#OutArity }):
        Compose[Base, GetProperty[P], Base#OutArity] =
    new Compose[Base, GetProperty[P], Base#OutArity](base, GetProperty(p))(c)
  }

  /* Edge types */
  implicit def fromEdge[E <: AnyEdgeType](e: E):
        EdgeOps[IdStep[E]] =
    new EdgeOps[IdStep[E]](IdStep(e))
  implicit def fromEdgePath[B <: AnyPath { type OutT <: AnyEdgeType }](b: B):
        EdgeOps[B] =
    new EdgeOps[B](b)

  class EdgeOps[Base <: AnyPath { type OutT <: AnyEdgeType }](base: Base) {

    // NOTE: in gremlin this is called .outV
    def source(implicit c: Composable[Base, GetSource[Base#OutT]] { type Out = Base#OutArity }):
        Compose[Base, GetSource[Base#OutT], Base#OutArity] =
    new Compose[Base, GetSource[Base#OutT], Base#OutArity](base, GetSource(base.outT))(c)

    // NOTE: in gremlin this is called .inV
    def target(implicit c: Composable[Base, GetTarget[Base#OutT]] { type Out = Base#OutArity }):
        Compose[Base, GetTarget[Base#OutT], Base#OutArity] =
    new Compose[Base, GetTarget[Base#OutT], Base#OutArity](base, GetTarget(base.outT))(c)
  }

  /* Vertex types */
  implicit def fromVertex[E <: AnyVertexType](e: E):
        VertexOps[IdStep[E]] =
    new VertexOps[IdStep[E]](IdStep(e))
  implicit def fromVertexPath[B <: AnyPath { type OutT <: AnyVertexType }](b: B):
        VertexOps[B] =
    new VertexOps[B](b)

  class VertexOps[Base <: AnyPath { type OutT <: AnyVertexType }](base: Base) {

    def inE[E <: AnyEdgeType, A <: AnyArity](e: E)
      (implicit c: Composable[Base, GetInEdges[E]] { type Out = A }):
        Compose[Base, GetInEdges[E], A] =
    new Compose[Base, GetInEdges[E], A](base, GetInEdges(e))(c)

    def outE[E <: AnyEdgeType, A <: AnyArity](e: E)
      (implicit c: Composable[Base, GetOutEdges[E]] { type Out = A }):
        Compose[Base, GetOutEdges[E], A] =
    new Compose[Base, GetOutEdges[E], A](base, GetOutEdges(e))(c)


    def in[E <: AnyEdgeType, A <: AnyArity](e: E)
      (implicit c: Composable[Base, GetInVertices[E]] { type Out = A }):
        Compose[Base, GetInVertices[E], A] =
    new Compose[Base, GetInVertices[E], A](base, GetInVertices(e))(c)

    def out[E <: AnyEdgeType, A <: AnyArity](e: E)
      (implicit c: Composable[Base, GetOutVertices[E]] { type Out = A }):
        Compose[Base, GetOutVertices[E], A] =
    new Compose[Base, GetOutVertices[E], A](base, GetOutVertices(e))(c)
  }
}
