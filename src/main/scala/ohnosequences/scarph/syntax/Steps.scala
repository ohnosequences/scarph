package ohnosequences.scarph.syntax

import ohnosequences.scarph._

/* This is an example gremlin-like syntax for paths construction */
object steps {
  import ohnosequences.scarph.paths._
  import ohnosequences.scarph.steps._
  import ohnosequences.scarph.combinators._


  /* Element types */
  implicit def fromElement[E <: AnyElementType](e: E):
        ElementOps[IdStep[E]] =
    new ElementOps[IdStep[E]](IdStep(e))
  implicit def fromElementPath[B <: AnyPath { type OutT <: AnyElementType }](b: B):
        ElementOps[B] =
    new ElementOps[B](b)

  class ElementOps[Base <: AnyPath { type OutT <: AnyElementType }](base: Base) {

    def get[P <: AnyGraphProperty](p: P):
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

    def inE[P <: AnyPredicate { type ElementType <: AnyEdgeType }](p: P):
      // (implicit c: Composable[Base, InE[P]] { type Out = A }):
        Composition[Base, InE[P]] =
    new Composition[Base, InE[P]](base, InE(p))

    def outE[P <: AnyPredicate { type ElementType <: AnyEdgeType }](p: P):
      // (implicit c: Composable[Base, OutE[P]] { type Out = A }):
        Composition[Base, OutE[P]] =
    new Composition[Base, OutE[P]](base, OutE(p))
  }

  /* Any paths */
  implicit def fromElementToPath[E <: AnyElementType](e: E):
        PathOps[IdStep[E]] =
    new PathOps[IdStep[E]](IdStep(e))
  implicit def fromPath[T <: AnyPath](t: T): PathOps[T] = new PathOps[T](t)

  class PathOps[Base <: AnyPath](base: Base) {

    // TODO: add witnesses for composition to workaround P <:!< P { type In = P#In }
    // def ∘[F <: AnyPath { type Out = P#In }](f: F): Composition[F,P] 

    def map[P <: AnyPath { type InC = ExactlyOne.type; type InT = Base#OutT }](p: P): 
      Composition[Base, P MapOver Base#OutC] = 
      Composition[Base, P MapOver Base#OutC](base, MapOver(p, base.outC))

    def or[P <: AnyPath](p: P): (Base ⨁ P) = Or(base, p)
    def ⨁[P <: AnyPath](p: P): (Base ⨁ P) = Or(base, p)

    def par[P <: AnyPath](p: P): (Base ⨂ P) = Par(base, p)
    def ⨂[P <: AnyPath](p: P): (Base ⨂ P) = Par(base, p)
  }


  implicit def nextedPathOps[T <: AnyPath { type OutT <: AnyContainerType }](t: T): NestedPathOps[T] = new NestedPathOps[T](t)

  class NestedPathOps[Base <: AnyPath { type OutT <: AnyContainerType }](base: Base) {

    def flatten[C <: AnyContainer](implicit mul: (Base#OutC x Base#OutT#Container) { type Out = C }): 
      Flatten[Base, C] =
      Flatten[Base, C](base)(mul)
  }
}
