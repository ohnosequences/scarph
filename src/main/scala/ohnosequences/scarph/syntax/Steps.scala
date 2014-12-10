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

    def get[P <: AnyProp](p: P):
      // (implicit c: Composable[Base, Get[P]] { type Out = Base#OutArity }):
        Composition[Base, Get[P]] =
    new Composition[Base, Get[P]](base, Get(p))
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
    def source:
      // (implicit c: Composable[Base, Source[Base#OutT]] { type Out = Base#OutArity }):
        Composition[Base, Source[Base#OutT]] =
    new Composition[Base, Source[Base#OutT]](base, Source(base.outT))

    // NOTE: in gremlin this is called .inV
    def target:
      // (implicit c: Composable[Base, Target[Base#OutT]] { type Out = Base#OutArity }):
        Composition[Base, Target[Base#OutT]] =
    new Composition[Base, Target[Base#OutT]](base, Target(base.outT))
  }

  /* Vertex types */
  implicit def fromVertex[E <: AnyVertexType](e: E):
        VertexOps[IdStep[E]] =
    new VertexOps[IdStep[E]](IdStep(e))
  implicit def fromVertexPath[B <: AnyPath { type OutT <: AnyVertexType }](b: B):
        VertexOps[B] =
    new VertexOps[B](b)

  class VertexOps[Base <: AnyPath { type OutT <: AnyVertexType }](base: Base) {

    // def inE[P <: AnyPredicate { type ElementType <: AnyEdgeType }](p: P):
    //   // (implicit c: Composable[Base, InE[P]] { type Out = A }):
    //     Composition[Base, InE[P]] =
    // new Composition[Base, InE[P]](base, InE(p))

    // def outE[P <: AnyPredicate { type ElementType <: AnyEdgeType }](p: P):
    //   // (implicit c: Composable[Base, OutE[P]] { type Out = A }):
    //     Composition[Base, OutE[P]] =
    // new Composition[Base, OutE[P]](base, OutE(p))
  }

}
