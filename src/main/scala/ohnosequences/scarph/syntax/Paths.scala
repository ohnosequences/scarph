package ohnosequences.scarph.syntax


/* This is an example gremlin-like syntax for paths construction */
object paths {

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.steps._, s.predicates._, s.schemas._, s.indexes._


  implicit def graphTypeSyntax[F <: AnyGraphType](f: F):
        GraphTypeSyntax[F] =
    new GraphTypeSyntax[F](f)

  class GraphTypeSyntax[F <: AnyGraphType](f: F) {

    def fork: Fork[F] = Fork(f)

    def merge: Merge[F] = Merge(f)

    // F        : A → B
    //        S :     B ⊗ B → C
    // F fork S : A → B ⊗ B → C
    def fork[S <: AnyGraphType { type In = F#Out ⊕ F#Out }](s: S):
      Fork[F] >=> S =
      Fork(f) >=> s

    //   F   S    |   F left S
    // -----------+------------
    // A → L   B  |  A   L   B
    //     ⊕ → ⊕  |  ⊕ → ⊕ → ⊕
    //     R   C  |  R   R   C
    def left[S <: AnyBiproduct { type Left <: AnyGraphType { type In = F#Out } }](s: S):
      F >=> S#Left =
      f >=> s.left

    //   F   S    |  F right S
    // -----------+------------
    //     L   B  |  L   L   B
    //     ⊕ → ⊕  |  ⊕ → ⊕ → ⊕
    // A → R   C  |  A   R   C
    def right[S <: AnyBiproduct { type Right <: AnyGraphType { type In = F#Out } }](s: S):
      F >=> S#Right =
      f >=> s.right
  }

  implicit def biproductSyntax[F <: AnyGraphType { type Out <: AnyBiproduct }](f: F):
        BiproductSyntax[F] =
    new BiproductSyntax[F](f)

  class BiproductSyntax[F <: AnyGraphType { type Out <: AnyBiproduct }](f: F) {

    // def merge[S <: AnyGraphType { type In = Merge[F]#Out }](s: S):
    //   Merge[F] >=> S =
    //   Merge(f) >=> s
  }


  /* Graph/schema ops */
  implicit def schemaSyntax[S <: AnyGraphSchema](s: S):
        SchemaSyntax[S] =
    new SchemaSyntax[S](s)

  class SchemaSyntax[S <: AnyGraphSchema](s: S) {

    def query[P <: AnyPredicate](p: P): 
      GraphQuery[S, P] = 
      GraphQuery(s, p)

    /* This method takes also an index and checks that the predicate satisfies the 
       index'es restriction, ensuring that it can be utilized for this query */
    def query[I <: AnyIndex, P <: AnyPredicate](i: I, p: P)
      (implicit ch: I#PredicateRestriction[P]): 
        GraphQuery[S, P] =
        GraphQuery(s, p)
  }


  /* Element types */
  implicit def elementSyntax[F <: AnyGraphType { type Out <: AnyGraphElement }](f: F):
        ElementSyntax[F] =
    new ElementSyntax[F](f)

  class ElementSyntax[F <: AnyGraphType { type Out <: AnyGraphElement }](f: F) {

    def get[P <: AnyGraphProperty { type Owner = F#Out }](p: P):
      F >=> Get[P] =
      f >=> Get(p)
  }

  /* Edge types */
  implicit def edgeSyntax[F <: AnyGraphType { type Out <: AnyEdge }](f: F):
        EdgeSyntax[F] =
    new EdgeSyntax[F](f)

  class EdgeSyntax[F <: AnyGraphType { type Out <: AnyEdge }](f: F) {

    // NOTE: in gremlin this is called .outV
    def src: F >=> Source[F#Out] = 
             f >=> Source(f.out)

    // NOTE: in gremlin this is called .inV
    def tgt: F >=> Target[F#Out] =
             f >=> Target(f.out)
  }

  /* Vertex types */
  implicit def vertexSyntax[F <: AnyGraphType { type Out <: AnyVertex }](f: F):
        VertexSyntax[F] =
    new VertexSyntax[F](f)

  class VertexSyntax[F <: AnyGraphType { type Out <: AnyVertex }](f: F) {

    def inE[X, P <: AnyPredicate { type Element <: AnyEdge.To[F#Out] }](x: X)
      (implicit fromX: X => P): F >=> InE[P] = f >=> InE(fromX(x))

    def inV[X, P <: AnyPredicate { type Element <: AnyEdge.To[F#Out] }](x: X)
      (implicit fromX: X => P): F >=> InV[P] = f >=> InV(fromX(x))


    def outE[X, P <: AnyPredicate { type Element <: AnyEdge.From[F#Out] }](x: X)
      (implicit fromX: X => P): F >=> OutE[P] = f >=> OutE(fromX(x))

    def outV[X, P <: AnyPredicate { type Element <: AnyEdge.From[F#Out] }](x: X)
      (implicit fromX: X => P): F >=> OutV[P] = f >=> OutV(fromX(x))
  }

}
