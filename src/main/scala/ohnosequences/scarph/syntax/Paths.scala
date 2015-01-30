package ohnosequences.scarph.syntax


/* This is an example gremlin-like syntax for paths construction */
object paths {

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.steps._, s.predicates._, s.schemas._, s.indexes._


  implicit def graphTypeOps[T <: AnyGraphType](t: T):
        GraphTypeOps[T] =
    new GraphTypeOps[T](t)

  class GraphTypeOps[T <: AnyGraphType](t: T) {

    // def fork: Fork[T] = Fork(t)

    def fork[S <: AnyGraphMorphism { type In = T ⊕ T }](s: S):
      Fork[T] >=> S =
      Fork(t) >=> s
  }


  implicit def biproductOps[TT <: AnyBiproduct](tt: TT):
        BiproductOps[TT] =
    new BiproductOps[TT](tt)

  class BiproductOps[TT <: AnyBiproduct](tt: TT) {

    def merge: Merge[TT] = Merge(tt)
  }


  /* Graph/schema ops */
  implicit def schemaOps[S <: AnyGraphSchema](s: S):
        SchemaOps[S] =
    new SchemaOps[S](s)

  class SchemaOps[S <: AnyGraphSchema](s: S) {

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
  implicit def elementOps[E <: AnyGraphElement](e: E):
        ElementOps[E] =
    new ElementOps[E](e)

  class ElementOps[E <: AnyGraphElement](e: E) {

    def get[P <: AnyGraphProperty { type Owner = E }](p: P): Get[P] = Get(p)


    // def left[S <: AnyOr { type Left <: AnyGraphMorphism { type In = E } }](s: S): S#Left = s.left

    // def right[S <: AnyOr { type Right <: AnyGraphMorphism { type In = E } }](s: S): S#Right = s.right
  }

  implicit def pathElementOps[F <: AnyGraphMorphism { type Out <: AnyGraphElement }](f: F):
        PathElementOps[F] =
    new PathElementOps[F](f)

  class PathElementOps[F <: AnyGraphMorphism { type Out <: AnyGraphElement }](f: F) {

    def get[P <: AnyGraphProperty { type Owner = F#Out }](p: P):
      F >=> Get[P] =
      f >=> Get(p)
  }

  /* Edge types */
  implicit def edgeOps[E <: AnyEdge](e: E):
        EdgeOps[E] =
    new EdgeOps[E](e)

  class EdgeOps[E <: AnyEdge](e: E) {

    def src: Source[E] = s.steps.Source(e)
    def tgt: Target[E] = s.steps.Target(e)
  }

  implicit def pathEdgeOps[F <: AnyGraphMorphism { type Out <: AnyEdge }](f: F):
        PathEdgeOps[F] =
    new PathEdgeOps[F](f)

  class PathEdgeOps[F <: AnyGraphMorphism { type Out <: AnyEdge }](f: F) {

    // NOTE: in gremlin this is called .outV
    def src: F >=> Source[F#Out] = 
             f >=> Source(f.out)

    // NOTE: in gremlin this is called .inV
    def tgt: F >=> Target[F#Out] =
             f >=> Target(f.out)
  }

  /* Vertex types */
  implicit def vertexOps[V <: AnyVertex](v: V):
        VertexOps[V] =
    new VertexOps[V](v)

  class VertexOps[V <: AnyVertex](v: V) {

    // NOTE: this implicit conversion allows us to use edges for predicates on them
    def inE[X, P <: AnyPredicate { type Element <: AnyEdge.To[V] }](x: X)
      (implicit fromX: X => P): InE[P] = InE(fromX(x))

    def inV[X, P <: AnyPredicate { type Element <: AnyEdge.To[V] }](x: X)
      (implicit fromX: X => P): InV[P] = InV(fromX(x))


    def outE[X, P <: AnyPredicate { type Element <: AnyEdge.From[V] }](x: X)
      (implicit fromX: X => P): OutE[P] = OutE(fromX(x))

    def outV[X, P <: AnyPredicate { type Element <: AnyEdge.From[V] }](x: X)
      (implicit fromX: X => P): OutV[P] = OutV(fromX(x))
  }

  implicit def pathVertexOps[F <: AnyGraphMorphism { type Out <: AnyVertex }](f: F):
        PathVertexOps[F] =
    new PathVertexOps[F](f)

  class PathVertexOps[F <: AnyGraphMorphism { type Out <: AnyVertex }](f: F) {

    def inE[X, P <: AnyPredicate { type Element <: AnyEdge.To[F#Out] }](x: X)
      (implicit fromX: X => P): F >=> InE[P] = f >=> InE(fromX(x))

    def inV[X, P <: AnyPredicate { type Element <: AnyEdge.To[F#Out] }](x: X)
      (implicit fromX: X => P): F >=> InV[P] = f >=> InV(fromX(x))


    def outE[X, P <: AnyPredicate { type Element <: AnyEdge.From[F#Out] }](x: X)
      (implicit fromX: X => P): F >=> OutE[P] = f >=> OutE(fromX(x))

    def outV[X, P <: AnyPredicate { type Element <: AnyEdge.From[F#Out] }](x: X)
      (implicit fromX: X => P): F >=> OutV[P] = f >=> OutV(fromX(x))
  }

  /* This gives user nice warnings and doesn't add unnecessary constructors */
  // implicit def pathWarnOps[F <: AnyGraphMorphism { type Out <: AnyGraphType { type Container = ExactlyOne }}](f: F): 
  //       PathWarnOps[F] =
  //   new PathWarnOps[F](f)

  // class PathWarnOps[F <: AnyGraphMorphism { type Out <: AnyGraphType { type Container = ExactlyOne }}](f: F) {
  //   @deprecated("You are trying to 'flatten' a non-nested structure, you don't need it", "")
  //   def flatten: F = f

  //   @deprecated("You are trying to 'map' over one value, you don't need it", "")
  //   def map[S <: AnyGraphMorphism](s: S)
  //     (implicit cmp: F#Out ≃ S#In): F >=> S = f >=> s

  //   @deprecated("You are trying to 'flatMap' over one value, you don't need it", "")
  //   def flatMap[S <: AnyGraphMorphism](s: S)
  //     (implicit cmp: F#Out ≃ S#In): F >=> S = f >=> s

  //   @deprecated("You are trying to 'forkMap' over one value, you don't need it (use simple 'fork' method instead)", "")
  //   def forkMap[S <: AnyPar { type In = ParType[F#Out, F#Out] }](s: S):
  //     Fork[F] >=> S = Composition(Fork(f), s)
  //     // Fork(f) >=> s

  // }

  /* Any paths */
  implicit def pathOps[F <: AnyGraphMorphism](f: F): 
        PathOps[F] =
    new PathOps[F](f)

  class PathOps[F <: AnyGraphMorphism](f: F) {

    // F        : A → B
    //        S :     B ⊗ B → C
    // F fork S : A → B ⊗ B → C
    def fork[S <: AnyGraphMorphism { type In = F#Out ⊕ F#Out }](s: S):
      F >=> Fork[F#Out] >=> S =
      f >=> Fork[F#Out](f.out) >=> s

    //   F   S    |   F left S
    // -----------+------------
    // A → L   B  |  A   L   B
    //     ⊕ → ⊕  |  ⊕ → ⊕ → ⊕
    //     R   C  |  R   R   C
    def left[S <: AnyBiproduct { type Left <: AnyGraphMorphism { type In = F#Out } }](s: S):
      F >=> S#Left =
      f >=> s.left

    //   F   S    |  F right S
    // -----------+------------
    //     L   B  |  L   L   B
    //     ⊕ → ⊕  |  ⊕ → ⊕ → ⊕
    // A → R   C  |  A   R   C
    def right[S <: AnyBiproduct { type Right <: AnyGraphMorphism { type In = F#Out } }](s: S):
      F >=> S#Right =
      f >=> s.right
  }

}
