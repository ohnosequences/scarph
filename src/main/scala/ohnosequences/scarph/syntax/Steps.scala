package ohnosequences.scarph.syntax

import ohnosequences.scarph._

/* This is an example gremlin-like syntax for paths construction */
object steps {
  import ohnosequences.scarph.steps._

  /* Element types */
  implicit def fromElement[E <: AnyElementType](e: E):
        ElementOps[IdStep[E]] =
    new ElementOps[IdStep[E]](IdStep(e))
  implicit def fromElementPath[B <: AnyPath { type OutT <: AnyElementType }](b: B):
        ElementOps[B] =
    new ElementOps[B](b)

  class ElementOps[Base <: AnyPath { type OutT <: AnyElementType }](base: Base) {

    def get[P <: AnyProp](p: P)
      (implicit c: Composable[Base, Get[P]] { type Out = Base#OutArity }):
        Compose[Base, Get[P], Base#OutArity] =
    new Compose[Base, Get[P], Base#OutArity](base, Get(p))(c)
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
    def source(implicit c: Composable[Base, Source[Base#OutT]] { type Out = Base#OutArity }):
        Compose[Base, Source[Base#OutT], Base#OutArity] =
    new Compose[Base, Source[Base#OutT], Base#OutArity](base, Source(base.outT))(c)

    // NOTE: in gremlin this is called .inV
    def target(implicit c: Composable[Base, Target[Base#OutT]] { type Out = Base#OutArity }):
        Compose[Base, Target[Base#OutT], Base#OutArity] =
    new Compose[Base, Target[Base#OutT], Base#OutArity](base, Target(base.outT))(c)
  }

  /* Vertex types */
  implicit def fromVertex[E <: AnyVertexType](e: E):
        VertexOps[IdStep[E]] =
    new VertexOps[IdStep[E]](IdStep(e))
  implicit def fromVertexPath[B <: AnyPath { type OutT <: AnyVertexType }](b: B):
        VertexOps[B] =
    new VertexOps[B](b)

  class VertexOps[Base <: AnyPath { type OutT <: AnyVertexType }](base: Base) {

    def inE[P <: AnyPredicate { type ElementType <: AnyEdgeType }, A <: AnyArity](p: P)
      (implicit c: Composable[Base, InE[P]] { type Out = A }):
        Compose[Base, InE[P], A] =
    new Compose[Base, InE[P], A](base, InE(p))(c)

    def outE[P <: AnyPredicate { type ElementType <: AnyEdgeType }, A <: AnyArity](p: P)
      (implicit c: Composable[Base, OutE[P]] { type Out = A }):
        Compose[Base, OutE[P], A] =
    new Compose[Base, OutE[P], A](base, OutE(p))(c)
  }

}
