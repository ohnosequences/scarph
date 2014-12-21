package ohnosequences.scarph.syntax


/* This is an example gremlin-like syntax for paths construction */
object paths {

  import ohnosequences.scarph._
  import ohnosequences.scarph.paths._
  import ohnosequences.scarph.steps._
  import ohnosequences.scarph.combinators._


  /* Element types */
  implicit def elementOps[E <: AnyElementType](e: E):
        ElementOps[E] =
    new ElementOps[E](e)

  class ElementOps[E <: AnyElementType](e: E) {

    def get[B <: AnyGraphProperty { type Owner = E }](b: B): Get[B] = Get(b)
  }

  implicit def pathElementOps[F <: AnyPath { type OutC = ExactlyOne.type; type OutT <: AnyElementType }](f: F):
        PathElementOps[F] =
    new PathElementOps[F](f)

  class PathElementOps[F <: AnyPath { type OutC = ExactlyOne.type; type OutT <: AnyElementType }](f: F) {

    def get[S <: AnyGraphProperty { type Owner = F#OutT }](s: S):
      F >=> Get[S] =
      f >=> Get(s)
  }

  /* Edge types */
  implicit def edgeOps[E <: AnyEdgeType](e: E):
        EdgeOps[E] =
    new EdgeOps[E](e)

  class EdgeOps[E <: AnyEdgeType](e: E) {

    def source: Source[E] = steps.Source(e)
    def target: Target[E] = steps.Target(e)
  }

  implicit def pathEdgeOps[F <: AnyPath { type OutC = ExactlyOne.type; type OutT <: AnyEdgeType }](f: F):
        PathEdgeOps[F] =
    new PathEdgeOps[F](f)

  class PathEdgeOps[F <: AnyPath { type OutC = ExactlyOne.type; type OutT <: AnyEdgeType }](f: F) {

    // NOTE: in gremlin this is called .outV
    def source: F >=> Source[F#OutT] =
                f >=> Source(f.outT)

    // NOTE: in gremlin this is called .inV
    def target: F >=> Target[F#OutT] =
                f >=> Target(f.outT)
  }

  /* Vertex types */
  implicit def vertexOps[V <: AnyVertexType](v: V):
        VertexOps[V] =
    new VertexOps[V](v)

  class VertexOps[V <: AnyVertexType](v: V) {

    def inE[S <: AnyPredicate { 
        type ElementType <: AnyEdgeType { type OutT = V }
      }](s: S): InE[S] = InE(s)

    def outE[S <: AnyPredicate { 
        type ElementType <: AnyEdgeType { type InT = V }
      }](s: S): OutE[S] = OutE(s)
  }

  implicit def pathVertexOps[F <: AnyPath { type OutC = ExactlyOne.type; type OutT <: AnyVertexType }](f: F):
        PathVertexOps[F] =
    new PathVertexOps[F](f)

  class PathVertexOps[F <: AnyPath { type OutC = ExactlyOne.type; type OutT <: AnyVertexType }](f: F) {

    def inE[S <: AnyPredicate { type ElementType <: AnyEdgeType { type OutT = F#OutT } }](s: S):
        F >=> InE[S] =
        f >=> InE(s)

    def outE[S <: AnyPredicate { type ElementType <: AnyEdgeType { type InT = F#OutT } }](s: S):
        F >=> OutE[S] =
        f >=> OutE(s)
  }

  /* Any paths */
  implicit def pathOps[T <: AnyPath](t: T): 
        PathOps[T] =
    new PathOps[T](t)

  class PathOps[F <: AnyPath](f: F) {

    // F:       K[A] -> M[B]
    //       S:           B  ->   N[C]
    // F map S: K[A] -> M[B] -> M[N[C]] 
    def map[S <: AnyPath { type InC = ExactlyOne.type; type InT = F#OutT }](s: S): 
      F >=> (S MapOver F#OutC) = 
      f >=> MapOver(s, f.outC)

    // F:           K[A] -> M[B]
    //           S:           B  ->   N[C]
    // F flatMap S: K[A] -> M[B] -> M×N[C]
    def flatMap[S <: AnyPath { 
        type InC = ExactlyOne.type
        type InT = F#OutT 
      }, C <: AnyContainer
    ](s: S)(implicit mul: (F#OutC x OutOf[S]#Container) { type Out = C }):
      Flatten[(F >=> (S MapOver F#OutC)), C] = Flatten(f.map(s))(mul)

    // TODO: bounds:
    def or[S <: AnyPath](s: S): (F ⨁ S) = Or(f, s)
    def ⨁[S <: AnyPath](s: S): (F ⨁ S) = Or(f, s)

    // TODO: bounds:
    def par[S <: AnyPath](s: S): (F ⨂ S) = Par(f, s)
    def  ⨂[S <: AnyPath](s: S): (F ⨂ S) = Par(f, s)
  }


  implicit def nestedPathOps[T <: AnyPath { type OutT <: AnyContainerType }](t: T): 
        NestedPathOps[T] = 
    new NestedPathOps[T](t)

  class NestedPathOps[F <: AnyPath { type OutT <: AnyContainerType }](f: F) {

    // F:         K[A] -> L[M[B]]
    // F.flatten: K[A] -> L×M[F]
    def flatten[C <: AnyContainer](implicit mul: (F#OutC x F#OutT#Container) { type Out = C }):
      Flatten[F, C] =
      Flatten[F, C](f)(mul)
  }
}
