package ohnosequences.scarph.syntax


/* This is an example gremlin-like syntax for paths construction */
object paths {

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.paths._, s.steps._, s.combinators._, s.containers._, s.predicates._, s.schemas._, s.indexes._


  /* Graph/schema ops */
  implicit def schemaOps[S <: AnySchema](s: S):
        SchemaOps[S] =
    new SchemaOps[S](s)

  class SchemaOps[S <: AnySchema](s: S) {

    def query[P <: AnyPredicate](p: P): 
      GraphQuery[S, P, ManyOrNone] = 
      GraphQuery(s, p, ManyOrNone)

    /* This method takes also an index and checks that the predicate satisfies the 
       index'es restriction, ensuring that it can be utilized for this query */
    def query[I <: AnyIndex, P <: AnyPredicate, C <: AnyContainer](i: I, p: P)
      (implicit
        ch: I#PredicateRestriction[P],
        cn: IndexContainer[I] { type Out = C }
      ): GraphQuery[S, P, C] =
         GraphQuery(s, p, cn.apply)
  }


  /* Element types */
  implicit def elementOps[E <: AnyGraphElement](e: E):
        ElementOps[E] =
    new ElementOps[E](e)

  class ElementOps[E <: AnyGraphElement](e: E) {

    def get[P <: AnyGraphProperty { type Owner = E }](p: P): Get[P] = Get(p)
  }

  implicit def pathElementOps[F <: AnyPath { type Out <: AnyGraphElement }](f: F):
        PathElementOps[F] =
    new PathElementOps[F](f)

  class PathElementOps[F <: AnyPath { type Out <: AnyGraphElement }](f: F) {

    def get[P <: AnyGraphProperty { type Owner = F#Out }](p: P):
      F >=> Get[P] =
      f >=> Get[P](p)
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
      F >=> InE[P] =
      f >=> InE(fromX(x))

    def outE[X, P <: AnyPredicate { 
      type Element <: AnyEdge { 
        type Source <: AnyGraphType { 
          type Inside = F#Out 
        } 
      } 
    }](x: X)(implicit fromX: X => P):
      F >=> OutE[P] =
      f >=> OutE(fromX(x))
  }

  /* This gives user nice warnings and doesn't add unnecessary constructors */
  implicit def pathWarnOps[F <: AnyPath { type Out <: AnyGraphType { type Container = ExactlyOne }}](f: F): 
        PathWarnOps[F] =
    new PathWarnOps[F](f)

  class PathWarnOps[F <: AnyPath { type Out <: AnyGraphType { type Container = ExactlyOne }}](f: F) {
    @deprecated("You are trying to flatten a non-nested structure, you don't need it", "")
    def flatten: F = f

    @deprecated("You are trying to map over one value, you don't need it", "")
    def map[S <: AnyPath](s: S)
      (implicit cmp: F#Out ≃ S#In): F >=> S = f >=> s

    @deprecated("You are trying to flatMap over one value, you don't need it", "")
    def flatMap[S <: AnyPath](s: S)
      (implicit cmp: F#Out ≃ S#In): F >=> S = f >=> s
  }

  /* Any paths */
  implicit def pathOps[F <: AnyPath](f: F): 
        PathOps[F] =
    new PathOps[F](f)

  class PathOps[F <: AnyPath](f: F) {

    // F:       K[A] -> M[B]
    //       S:           B  ->   N[C]
    // F map S: K[A] -> M[B] -> M[N[C]] 
    def map[S <: AnyPath](s: S)
      (implicit cmp: F#Out ≃ MapOver[S, F#Out#Container]#In): 
       F >=> MapOver[S, F#Out#Container] = 
      (f >=> MapOver[S, F#Out#Container](s, f.out.container))(cmp)

    // F:           K[A] -> M[B]
    //           S:           B  ->   N[C]
    // F flatMap S: K[A] -> M[B] -> M×N[C]
    def flatMap[S <: AnyPath, C <: AnyContainer](s: S)
      (implicit 
        cmp: F#Out ≃ (S MapOver F#Out#Container)#In,
        // NOTE: this is just (F#Out#Container × S#Out#Container), but compiler needs this explicit crap:
        mul: (F#Out#Container#Of[S#Out]#Container × F#Out#Container#Of[S#Out]#Inside#Container) { type Out = C }
      ): Flatten[(F >=> (S MapOver F#Out#Container)), C] = 
         Flatten[(F >=> (S MapOver F#Out#Container)), C](f.map(s))(mul)

    // F:         K[A] -> L[M[B]]
    // F.flatten: K[A] -> L×M[F]
    def flatten[C <: AnyContainer](implicit mul: (F#Out#Container × F#Out#Inside#Container) { type Out = C }):
      Flatten[F, C] =
      Flatten[F, C](f)(mul)

    // // TODO: bounds:
    // def or[S <: AnyPath](s: S): (F ⨁ S) = Or(f, s)
    // def ⨁[S <: AnyPath](s: S): (F ⨁ S) = Or(f, s)

    // // TODO: bounds:
    // def par[S <: AnyPath](s: S): (F ⨂ S) = Par(f, s)
    // def  ⨂[S <: AnyPath](s: S): (F ⨂ S) = Par(f, s)
  }

}