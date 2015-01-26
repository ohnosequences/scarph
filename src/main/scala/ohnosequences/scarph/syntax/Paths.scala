package ohnosequences.scarph.syntax


/* This is an example gremlin-like syntax for paths construction */
object paths {

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.paths._, s.steps._, s.combinators._, s.containers._, s.predicates._, s.schemas._, s.indexes._


  /* Graph/schema ops */
  implicit def schemaOps[S <: AnyGraphSchema](s: S):
        SchemaOps[S] =
    new SchemaOps[S](s)

  class SchemaOps[S <: AnyGraphSchema](s: S) {

    def query[P <: AnyPredicate](p: P): 
      GraphQuery[S, ManyOrNone, P] = 
      GraphQuery(s, ManyOrNone, p)

    /* This method takes also an index and checks that the predicate satisfies the 
       index'es restriction, ensuring that it can be utilized for this query */
    def query[I <: AnyIndex, P <: AnyPredicate, C <: AnyContainer](i: I, p: P)
      (implicit
        ch: I#PredicateRestriction[P],
        cn: IndexContainer[I] { type Out = C }
      ): GraphQuery[S, C, P] =
         GraphQuery(s, cn.apply, p)
  }


  /* Element types */
  implicit def elementOps[E <: AnyGraphElement](e: E):
        ElementOps[E] =
    new ElementOps[E](e)

  class ElementOps[E <: AnyGraphElement](e: E) {

    def get[P <: AnyGraphProperty { type Owner = E }](p: P): Get[P] = Get(p)


    def left[S <: AnyOr { type Left <: AnyPath { type In = E } }](s: S): S#Left = s.left

    def right[S <: AnyOr { type Right <: AnyPath { type In = E } }](s: S): S#Right = s.right
  }

  implicit def pathElementOps[F <: AnyPath { type Out <: AnyGraphElement }](f: F):
        PathElementOps[F] =
    new PathElementOps[F](f)

  class PathElementOps[F <: AnyPath { type Out <: AnyGraphElement }](f: F) {

    def get[P <: AnyGraphProperty { type Owner = F#Out }](p: P):
      F >=> Get[P] =
      Composition(f,Get[P](p))//(≃.eq[F#Out,Get[P]#In])
  }

  /* Edge types */
  implicit def edgeOps[E <: AnyEdge](e: E):
        EdgeOps[E] =
    new EdgeOps[E](e)

  class EdgeOps[E <: AnyEdge](e: E) {

    def src: Source[E] = s.steps.Source(e)
    def tgt: Target[E] = s.steps.Target(e)
  }

  implicit def pathEdgeOps[F <: AnyPath { type Out <: AnyEdge }](f: F):
        PathEdgeOps[F] =
    new PathEdgeOps[F](f)

  class PathEdgeOps[F <: AnyPath { type Out <: AnyEdge }](f: F) {

    // NOTE: in gremlin this is called .outV
    def src: F >=> Source[F#Out] = Composition(f, Source(f.out: F#Out))//(≃.eq[F#Out, Source[F#Out]#In])
             // f >=> Source(f.out: F#Out)

    // NOTE: in gremlin this is called .inV
    def tgt: F >=> Target[F#Out] = Composition(f, Target(f.out: F#Out))//(≃.eq[F#Out, Target[F#Out]#In])
             // f >=> Target(f.out: F#Out)
  }

  /* Vertex types */
  implicit def vertexOps[V <: AnyVertex](v: V):
        VertexOps[V] =
    new VertexOps[V](v)

  class VertexOps[V <: AnyVertex](v: V) {

    def inE[X, P <: AnyPredicate { 
      type Element <: AnyEdge { 
        type Target <: AnyGraphType { 
          type Inside = V
        }
      }
    // NOTE: this implicit conversion allows us to use edges for predicates on them
    }](x: X)(implicit fromX: X => P): InE[P] = InE(fromX(x))

    def outE[X, P <: AnyPredicate { 
      type Element <: AnyEdge { 
        type Source <: AnyGraphType { 
          type Inside = V
        }
      }
    }](x: X)(implicit fromX: X => P): OutE[P] = OutE(fromX(x))

    def inV[X, P <: AnyPredicate { 
      type Element <: AnyEdge { 
        type Target <: AnyGraphType { 
          type Inside = V
        }
      }
    }](x: X)(implicit fromX: X => P): InV[P] = InV(fromX(x))

    def outV[X, P <: AnyPredicate { 
      type Element <: AnyEdge { 
        type Source <: AnyGraphType { 
          type Inside = V
        }
      }
    }](x: X)(implicit fromX: X => P): OutV[P] = OutV(fromX(x))
  }

  implicit def pathVertexOps[F <: AnyPath { type Out <: AnyVertex }](f: F):
        PathVertexOps[F] =
    new PathVertexOps[F](f)

  class PathVertexOps[F <: AnyPath { type Out <: AnyVertex }](f: F) {

    def inE[X, P <: AnyPredicate { 
      type Element <: AnyEdge { 
        type Target <: AnyGraphType { 
          type Inside = F#Out
        } 
      } 
    }](x: X)(implicit fromX: X => P):
      F >=> InE[P] = Composition(f, InE(fromX(x)))//(≃.eq[F#Out, InE[P]#In])
      // f >=> InE(fromX(x))

    def outE[X, P <: AnyPredicate { 
      type Element <: AnyEdge { 
        type Source <: AnyGraphType { 
          type Inside = F#Out 
        } 
      } 
    }](x: X)(implicit fromX: X => P):
      F >=> OutE[P] = Composition(f, OutE(fromX(x)))//(≃.eq[F#Out, OutE[P]#In])
      // f >=> OutE(fromX(x))
  }

  /* This gives user nice warnings and doesn't add unnecessary constructors */
  implicit def pathWarnOps[F <: AnyPath { type Out <: AnyGraphType { type Container = ExactlyOne }}](f: F): 
        PathWarnOps[F] =
    new PathWarnOps[F](f)

  class PathWarnOps[F <: AnyPath { type Out <: AnyGraphType { type Container = ExactlyOne }}](f: F) {
    @deprecated("You are trying to 'flatten' a non-nested structure, you don't need it", "")
    def flatten: F = f

    @deprecated("You are trying to 'map' over one value, you don't need it", "")
    def map[S <: AnyPath](s: S)
      (implicit cmp: F#Out ≃ S#In): F >=> S = f >=> s

    @deprecated("You are trying to 'flatMap' over one value, you don't need it", "")
    def flatMap[S <: AnyPath](s: S)
      (implicit cmp: F#Out ≃ S#In): F >=> S = f >=> s

    @deprecated("You are trying to 'forkMap' over one value, you don't need it (use simple 'fork' method instead)", "")
    def forkMap[S <: AnyPar { type In = ParType[F#Out, F#Out] }](s: S):
      Fork[F] >=> S = Composition(Fork(f), s)
      // Fork(f) >=> s

  }

  /* Any paths */
  implicit def pathOps[F <: AnyPath](f: F): 
        PathOps[F] =
    new PathOps[F](f)

  class PathOps[F <: AnyPath](f: F) {

    // F       : K[A] → M[B]
    //       S :          B  →   N[C]
    // F map S : K[A] → M[B] → M[N[C]] 
    def map[S <: AnyPath](s: S)
      (implicit cmp: F#Out ≃ MapOver[S, F#Out#Container]#In): 
       F >=> MapOver[S, F#Out#Container] = 
      (f >=> MapOver[S, F#Out#Container](s, f.out.container))(cmp)

    // F           : K[A] → M[B]
    //           S :          B  →   N[C]
    // F flatMap S : K[A] → M[B] → M×N[C]
    def flatMap[S <: AnyPath, C <: AnyContainer](s: S)
      (implicit 
        cmp: F#Out ≃ (S MapOver F#Out#Container)#In,
        // NOTE: this is just (F#Out#Container × S#Out#Container), but compiler needs this explicit crap:
        // mul: (F#Out#Container#Of[S#Out]#Container × F#Out#Container#Of[S#Out]#Inside#Container) { type Out = C }
        mul: (F#Out#Container#Of[S#Out]#Container × F#Out#Container#Of[S#Out]#Inside#Container) {type Out = C}
      ): Flatten[(F >=> (S MapOver F#Out#Container)), C] = 
         Flatten[(F >=> (S MapOver F#Out#Container)), C](f.map(s))(mul)

    // F         : K[A] → L[M[B]]
    // F.flatten : K[A] → L×M[B]
    def flatten[C <: AnyContainer](implicit mul: (F#Out#Container × F#Out#Inside#Container) { type Out = C }):
      Flatten[F, C] =
      Flatten[F, C](f)(mul)


    def par[S <: AnyPath](s: S): Par[F, S] = Par(f, s)
    def   ⊗[S <: AnyPath](s: S): Par[F, S] = Par(f, s)


    // F        : A → B
    //        S :     B ⊗ B → C ⊗ D
    // F fork S : A → B ⊗ B → C ⊗ D
    def fork[S <: AnyPar { type In = ParType[F#Out, F#Out] }](s: S):
      // (implicit cmp: Fork[F]#Out ≃ S#In):
      Fork[F] >=> S = Composition(Fork(f), s)
      // Fork(f) >=> s

    // F           : A → M[B]
    //           S :       B  ⊗   B  →   C  ⊗   D
    // F forkMap S : A → M[B] ⊗ M[B] → M[C] ⊗ M[D]
    def forkMap[S <: AnyPar { type In = ParType[F#Out#Inside, F#Out#Inside] }](s: S)
      (implicit // NOTE: this implicit is needed formally, but actually it's always there (because of the bound on S)
        cmp1: Fork[F]#Out ≃ Par[MapOver[S#First, F#Out#Container], MapOver[S#Second, F#Out#Container]]#In
      ): Fork[F] >=> Par[MapOver[S#First, F#Out#Container], MapOver[S#Second, F#Out#Container]] =
         Fork(f) >=> Par(MapOver(s.first, f.out.container), MapOver(s.second, f.out.container))


    def or[S <: AnyPath](s: S): F Or S = Or(f, s)
    def  ⊕[S <: AnyPath](s: S): F Or S = Or(f, s)

    //   F   S    |   F left S
    // -----------+------------
    // A → L   B  |  A   L   B
    //     ⊕ → ⊕  |  ⊕ → ⊕ → ⊕
    //     R   C  |  R   R   C
    def left[S <: AnyOr](s: S)
      (implicit cmp: F#Out ≃ S#Left#In):
        F >=> S#Left =
        f >=> (s: S).left

    //   F   S    |  F right S
    // -----------+------------
    //     L   B  |  L   L   B
    //     ⊕ → ⊕  |  ⊕ → ⊕ → ⊕
    // A → R   C  |  A   R   C
    def right[S <: AnyOr](s: S)
      (implicit cmp: F#Out ≃ S#Right#In):
        F >=> S#Right =
        f >=> (s: S).right

  }


  // FIXME: it doesn't work with two type parameters, but I don't know how to do it use it only F
  implicit def parPathOps[
    F <: AnyPar 
  ](f: F):
        ParPathOps[F] =
    new ParPathOps[F](f)

  class ParPathOps[
    F <: AnyPar 
  ](f: F) {

    def merge[C <: AnyContainer]
      (implicit 
        sum: (F#First#Out#Container + F#Second#Out#Container) { type Out = C },
        ex: F#First#Out#Inside ≃ F#Second#Out#Inside,
        cmp: F#Out ≃ Merge[F#First#Out, F#Second#Out, C]#In
      ): F >=> Merge[F#First#Out, F#Second#Out, C] =
         f >=> Merge[F#First#Out, F#Second#Out, C](f.first.out, f.second.out)(sum, ex)

  }

}
