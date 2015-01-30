
```scala
package ohnosequences.scarph.syntax
```

This is an example gremlin-like syntax for paths construction

```scala
object paths {

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.paths._, s.steps._, s.combinators._, s.containers._, s.predicates._, s.schemas._, s.indexes._
```

Graph/schema ops

```scala
  implicit def schemaOps[S <: AnySchema](s: S):
        SchemaOps[S] =
    new SchemaOps[S](s)

  class SchemaOps[S <: AnySchema](s: S) {

    def query[P <: AnyPredicate](p: P): 
      GraphQuery[S, ManyOrNone, P] = 
      GraphQuery(s, ManyOrNone, p)
```

This method takes also an index and checks that the predicate satisfies the 
index'es restriction, ensuring that it can be utilized for this query

```scala
    def query[I <: AnyIndex, P <: AnyPredicate, C <: AnyContainer](i: I, p: P)
      (implicit
        ch: I#PredicateRestriction[P],
        cn: IndexContainer[I] { type Out = C }
      ): GraphQuery[S, C, P] =
         GraphQuery(s, cn.apply, p)
  }
```

Element types

```scala
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
      f >=> Get[P](p)
  }
```

Edge types

```scala
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
```

Vertex types

```scala
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
```

This gives user nice warnings and doesn't add unnecessary constructors

```scala
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
      Fork[F] >=> S =
      Fork(f) >=> s

  }
```

Any paths

```scala
  implicit def pathOps[F <: AnyPath](f: F): 
        PathOps[F] =
    new PathOps[F](f)

  class PathOps[F <: AnyPath](f: F) {

    // F       : K[A] -> M[B]
    //       S :           B  ->   N[C]
    // F map S : K[A] -> M[B] -> M[N[C]] 
    def map[S <: AnyPath](s: S)
      (implicit cmp: F#Out ≃ MapOver[S, F#Out#Container]#In): 
       F >=> MapOver[S, F#Out#Container] = 
      (f >=> MapOver[S, F#Out#Container](s, f.out.container))(cmp)

    // F           : K[A] -> M[B]
    //           S :           B  ->   N[C]
    // F flatMap S : K[A] -> M[B] -> M×N[C]
    def flatMap[S <: AnyPath, C <: AnyContainer](s: S)
      (implicit 
        cmp: F#Out ≃ (S MapOver F#Out#Container)#In,
        // NOTE: this is just (F#Out#Container × S#Out#Container), but compiler needs this explicit crap:
        mul: (F#Out#Container#Of[S#Out]#Container × F#Out#Container#Of[S#Out]#Inside#Container) { type Out = C }
      ): Flatten[(F >=> (S MapOver F#Out#Container)), C] = 
         Flatten[(F >=> (S MapOver F#Out#Container)), C](f.map(s))(mul)

    // F         : K[A] -> L[M[B]]
    // F.flatten : K[A] -> L×M[F]
    def flatten[C <: AnyContainer](implicit mul: (F#Out#Container × F#Out#Inside#Container) { type Out = C }):
      Flatten[F, C] =
      Flatten[F, C](f)(mul)


    def par[S <: AnyPath](s: S): Par[F, S] = Par(f, s)
    def  ⊗[S <: AnyPath](s: S): F ⊗ S = Par(f, s)


    // F        : A -> B
    // S        :      B ⊗ B -> C ⊗ D
    // F fork S : A -> B ⊗ B -> C ⊗ D
    def fork[S <: AnyPar { type In = ParType[F#Out, F#Out] }](s: S):
      // (implicit cmp: Fork[F]#Out ≃ S#In):
      Fork[F] >=> S =
      Fork(f) >=> s

    // F           : A -> M[B]
    // S           :        B  ⊗   B  ->   C  ⊗   D
    // F forkMap S : A -> M[B] ⊗ M[B] -> M[C] ⊗ M[D]
    def forkMap[S <: AnyPar { type In = ParType[F#Out#Inside, F#Out#Inside] }](s: S)
      (implicit // NOTE: this implicit is needed formally, but actually it's always there (because of the bound on S)
        cmp1: Fork[F]#Out ≃ Par[MapOver[S#First, F#Out#Container], MapOver[S#Second, F#Out#Container]]#In
      ): Fork[F] >=> Par[MapOver[S#First, F#Out#Container], MapOver[S#Second, F#Out#Container]] =
         Fork(f) >=> Par(MapOver(s.first, f.out.container), MapOver(s.second, f.out.container))


    def or[S <: AnyPath](s: S): F Or S = Or(f, s)
    def ⊕[S <: AnyPath](s: S): F ⊕ S = Or(f, s)

    //   F     S    |  F left S
    // -------------+------------
    // A -> L    B  |  A    L    B
    //      ⊕ -> ⊕  |  ⊕ -> ⊕ -> ⊕
    //      R    C  |  R    R    C
    def left[S <: AnyOr](s: S)
      (implicit cmp: F#Out ≃ S#Left#In):
        F >=> S#Left =
        f >=> (s: S).left

    //   F     S    |   F right S
    // -------------+------------
    //      L    B  |  L    L    B
    //      ⊕ -> ⊕  |  ⊕ -> ⊕ -> ⊕
    // A -> R    C  |  A    R    C
    def right[S <: AnyOr](s: S)
      (implicit cmp: F#Out ≃ S#Right#In):
        F >=> S#Right =
        f >=> (s: S).right

  }

}

```


------

### Index

+ src
  + test
    + scala
      + ohnosequences
        + scarph
          + [ContainersTest.scala][test/scala/ohnosequences/scarph/ContainersTest.scala]
          + [ScalazEquality.scala][test/scala/ohnosequences/scarph/ScalazEquality.scala]
          + titan
            + [TwitterTitanTest.scala][test/scala/ohnosequences/scarph/titan/TwitterTitanTest.scala]
          + [TwitterSchema.scala][test/scala/ohnosequences/scarph/TwitterSchema.scala]
    + resources
  + main
    + scala
      + ohnosequences
        + scarph
          + [GraphTypes.scala][main/scala/ohnosequences/scarph/GraphTypes.scala]
          + [Containers.scala][main/scala/ohnosequences/scarph/Containers.scala]
          + impl
            + titan
              + [Schema.scala][main/scala/ohnosequences/scarph/impl/titan/Schema.scala]
              + [Evals.scala][main/scala/ohnosequences/scarph/impl/titan/Evals.scala]
              + [Predicates.scala][main/scala/ohnosequences/scarph/impl/titan/Predicates.scala]
          + [Paths.scala][main/scala/ohnosequences/scarph/Paths.scala]
          + [Indexes.scala][main/scala/ohnosequences/scarph/Indexes.scala]
          + [Evals.scala][main/scala/ohnosequences/scarph/Evals.scala]
          + [Conditions.scala][main/scala/ohnosequences/scarph/Conditions.scala]
          + [Steps.scala][main/scala/ohnosequences/scarph/Steps.scala]
          + [Predicates.scala][main/scala/ohnosequences/scarph/Predicates.scala]
          + [Schemas.scala][main/scala/ohnosequences/scarph/Schemas.scala]
          + [Combinators.scala][main/scala/ohnosequences/scarph/Combinators.scala]
          + syntax
            + [GraphTypes.scala][main/scala/ohnosequences/scarph/syntax/GraphTypes.scala]
            + [Paths.scala][main/scala/ohnosequences/scarph/syntax/Paths.scala]
            + [Conditions.scala][main/scala/ohnosequences/scarph/syntax/Conditions.scala]
            + [Predicates.scala][main/scala/ohnosequences/scarph/syntax/Predicates.scala]

[test/scala/ohnosequences/scarph/ContainersTest.scala]: ../../../../../test/scala/ohnosequences/scarph/ContainersTest.scala.md
[test/scala/ohnosequences/scarph/ScalazEquality.scala]: ../../../../../test/scala/ohnosequences/scarph/ScalazEquality.scala.md
[test/scala/ohnosequences/scarph/titan/TwitterTitanTest.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/TwitterTitanTest.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[main/scala/ohnosequences/scarph/GraphTypes.scala]: ../GraphTypes.scala.md
[main/scala/ohnosequences/scarph/Containers.scala]: ../Containers.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Schema.scala]: ../impl/titan/Schema.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Evals.scala]: ../impl/titan/Evals.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Predicates.scala]: ../impl/titan/Predicates.scala.md
[main/scala/ohnosequences/scarph/Paths.scala]: ../Paths.scala.md
[main/scala/ohnosequences/scarph/Indexes.scala]: ../Indexes.scala.md
[main/scala/ohnosequences/scarph/Evals.scala]: ../Evals.scala.md
[main/scala/ohnosequences/scarph/Conditions.scala]: ../Conditions.scala.md
[main/scala/ohnosequences/scarph/Steps.scala]: ../Steps.scala.md
[main/scala/ohnosequences/scarph/Predicates.scala]: ../Predicates.scala.md
[main/scala/ohnosequences/scarph/Schemas.scala]: ../Schemas.scala.md
[main/scala/ohnosequences/scarph/Combinators.scala]: ../Combinators.scala.md
[main/scala/ohnosequences/scarph/syntax/GraphTypes.scala]: GraphTypes.scala.md
[main/scala/ohnosequences/scarph/syntax/Paths.scala]: Paths.scala.md
[main/scala/ohnosequences/scarph/syntax/Conditions.scala]: Conditions.scala.md
[main/scala/ohnosequences/scarph/syntax/Predicates.scala]: Predicates.scala.md