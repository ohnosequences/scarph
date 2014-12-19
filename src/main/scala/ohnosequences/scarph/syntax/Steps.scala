package ohnosequences.scarph.syntax

import ohnosequences.scarph._

/* This is an example gremlin-like syntax for paths construction */
object steps {
  import ohnosequences.scarph.paths._
  import ohnosequences.scarph.steps._
  import ohnosequences.scarph.combinators._


  /* Element types */
  implicit def elementOps[E <: AnyElementType](e: E):
        ElementOps[E] =
    new ElementOps[E](e)

  class ElementOps[E <: AnyElementType](e: E) {

    def get[P <: AnyGraphProperty { type Owner = E }](p: P): Get[P] = Get(p)
    // def get[P <: PropertyOf[E]](p: P): Get[P] = Get(p)
  }

  implicit def pathElementOps[B <: AnyPath { type OutC = ExactlyOne.type; type OutT <: AnyElementType }](b: B):
        PathElementOps[B] =
    new PathElementOps[B](b)

  class PathElementOps[Base <: AnyPath { type OutC = ExactlyOne.type; type OutT <: AnyElementType }](base: Base) {

    def get[P <: AnyGraphProperty { type Owner = Base#OutT }](p: P):
      // (implicit c: Composable[Base, Get[P]] { type Out = Base#OutArity }):
        Composition[Base, Get[P]] =
    new Composition[Base, Get[P]](base, Get(p))
  }

  /* Edge types */
  implicit def edgeOps[E <: AnyEdgeType](e: E):
        EdgeOps[E] =
    new EdgeOps[E](e)

  class EdgeOps[E <: AnyEdgeType](e: E) {

    def source: Source[E] = ohnosequences.scarph.steps.Source(e)
    def target: Target[E] = ohnosequences.scarph.steps.Target(e)
  }

  implicit def pathEdgeOps[B <: AnyPath { type OutC = ExactlyOne.type; type OutT <: AnyEdgeType }](b: B):
        PathEdgeOps[B] =
    new PathEdgeOps[B](b)

  class PathEdgeOps[Base <: AnyPath { type OutC = ExactlyOne.type; type OutT <: AnyEdgeType }](base: Base) {

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
  implicit def vertexOps[V <: AnyVertexType](v: V):
        VertexOps[V] =
    new VertexOps[V](v)

  class VertexOps[V <: AnyVertexType](v: V) {

    def inE[P <: AnyPredicate { 
        type ElementType <: AnyEdgeType { type OutT = V }
      }](p: P): InE[P] = InE(p)

    def outE[P <: AnyPredicate { 
        type ElementType <: AnyEdgeType { type InT = V }
      }](p: P): OutE[P] = OutE(p)

    // def outE[E <: AnyEdgeType { type Source = V }](e: E): OutE[EmptyPredicate[E]] = OutE(new EmptyPredicate(e))
  }

  implicit def pathVertexOps[B <: AnyPath { type OutC = ExactlyOne.type; type OutT <: AnyVertexType }](b: B):
        PathVertexOps[B] =
    new PathVertexOps[B](b)

  class PathVertexOps[Base <: AnyPath { type OutC = ExactlyOne.type; type OutT <: AnyVertexType }](base: Base) {

    def inE[P <: AnyPredicate { type ElementType <: AnyEdgeType { type OutT = Base#OutT } }](p: P):
      // (implicit c: Composable[Base, InE[P]] { type Out = A }):
        Composition[Base, InE[P]] =
    new Composition[Base, InE[P]](base, InE(p))

    def outE[P <: AnyPredicate { type ElementType <: AnyEdgeType { type InT = Base#OutT } }](p: P):
      // (implicit c: Composable[Base, OutE[P]] { type Out = A }):
        Composition[Base, OutE[P]] =
    new Composition[Base, OutE[P]](base, OutE(p))
  }

  /* Any paths */
  // implicit def fromElementToPath[E <: AnyElementType](e: E):
  //       PathOps[E] =
  //   new PathOps[E](e)
  implicit def pathOps[T <: AnyPath](t: T): 
        PathOps[T] =
    new PathOps[T](t)

  class PathOps[Base <: AnyPath](base: Base) {

    // TODO: add witnesses for composition to workaround P <:!< P { type In = P#In }
    // def ∘[F <: AnyPath { type Out = P#In }](f: F): Composition[F,P] 

    def map[P <: AnyPath { type InC = ExactlyOne.type; type InT = Base#OutT }](p: P): 
      Composition[Base, P MapOver Base#OutC] = 
      Composition[Base, P MapOver Base#OutC](base, MapOver(p, base.outC))

    def flatMap[P <: AnyPath { 
        type InC = ExactlyOne.type
        type InT = Base#OutT 
      }, C <: AnyContainer
    ](p: P)(implicit mul: (Base#OutC x OutOf[P]#Container) { type Out = C }):
      Flatten[Composition[Base, P MapOver Base#OutC], C] = 
      Flatten[Composition[Base, P MapOver Base#OutC], C](base.map(p))(mul)

    def or[P <: AnyPath](p: P): (Base ⨁ P) = Or(base, p)
    def ⨁[P <: AnyPath](p: P): (Base ⨁ P) = Or(base, p)

    def par[P <: AnyPath](p: P): (Base ⨂ P) = Par(base, p)
    def  ⨂[P <: AnyPath](p: P): (Base ⨂ P) = Par(base, p)
  }


  implicit def nestedPathOps[T <: AnyPath { type OutT <: AnyContainerType }](t: T): 
        NestedPathOps[T] = 
    new NestedPathOps[T](t)

  class NestedPathOps[Base <: AnyPath { type OutT <: AnyContainerType }](base: Base) {

    // F:       K[A] -> M[B]
    //       S:           B  ->   N[C]
    // F map S: K[A] -> M[B] -> M[N[C]] 

    def flatten[C <: AnyContainer](implicit mul: (Base#OutC x Base#OutT#Container) { type Out = C }):
      Flatten[Base, C] =
      Flatten[Base, C](base)(mul)
  }
}
