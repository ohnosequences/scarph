
```scala
package ohnosequences.scarph.syntax
```

This is an example gremlin-like syntax for paths construction

```scala
object graphTypes {

  import scalaz.\/

  import ohnosequences.cosas._, types._

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.monoidalStructures._


  implicit def graphObjectValOps[F <: AnyGraphObject, VF](vt: F := VF):
        GraphObjectValOps[F, VF] =
    new GraphObjectValOps[F, VF](vt)

  class GraphObjectValOps[F <: AnyGraphObject, VF](vt: F := VF) {

    // (F := t) ⊗ (S := s) : (F ⊗ S) := (t, s)
    def ⊗[S <: AnyGraphObject, VS](vs: S := VS): (F ⊗ S) := (VF, VS) =
      new Denotes( (vt.value, vs.value) )

    // (F := t) ⊕ (S := s) : (F ⊕ S) := (t, s)
    def ⊕[S <: AnyGraphObject, VS](vs: S := VS): (F ⊕ S) := (VF, VS) =
      new Denotes( (vt.value, vs.value) )
  }

  implicit def graphMorphismValOps[F <: AnyGraphMorphism, VF](vt: F := VF):
        GraphMorphismValOps[F, VF] =
    new GraphMorphismValOps[F, VF](vt)

  class GraphMorphismValOps[F <: AnyGraphMorphism, VF](vt: F := VF) {

    // (F := t) ⊗ (S := s) : (F ⊗ S) := (t, s)
    def ⊗[S <: AnyGraphMorphism, VS](vs: S := VS): TensorMorph[F, S] := (VF, VS) =
      new Denotes( (vt.value, vs.value) )

    // (F := t) ⊕ (S := s) : (F ⊕ S) := (t, s)
    def ⊕[S <: AnyGraphMorphism, VS](vs: S := VS): BiproductMorph[F, S] := (VF, VS) =
      new Denotes( (vt.value, vs.value) )
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
          + [TwitterQueries.scala][test/scala/ohnosequences/scarph/TwitterQueries.scala]
          + impl
            + [dummyTest.scala][test/scala/ohnosequences/scarph/impl/dummyTest.scala]
            + [dummy.scala][test/scala/ohnosequences/scarph/impl/dummy.scala]
          + [TwitterSchema.scala][test/scala/ohnosequences/scarph/TwitterSchema.scala]
  + main
    + scala
      + ohnosequences
        + scarph
          + [morphisms.scala][main/scala/ohnosequences/scarph/morphisms.scala]
          + [predicates.scala][main/scala/ohnosequences/scarph/predicates.scala]
          + [monoidalStructures.scala][main/scala/ohnosequences/scarph/monoidalStructures.scala]
          + [evals.scala][main/scala/ohnosequences/scarph/evals.scala]
          + [implementations.scala][main/scala/ohnosequences/scarph/implementations.scala]
          + [schemas.scala][main/scala/ohnosequences/scarph/schemas.scala]
          + [naturalIsomorphisms.scala][main/scala/ohnosequences/scarph/naturalIsomorphisms.scala]
          + [graphTypes.scala][main/scala/ohnosequences/scarph/graphTypes.scala]
          + syntax
            + [morphisms.scala][main/scala/ohnosequences/scarph/syntax/morphisms.scala]
            + [predicates.scala][main/scala/ohnosequences/scarph/syntax/predicates.scala]
            + [graphTypes.scala][main/scala/ohnosequences/scarph/syntax/graphTypes.scala]
            + [conditions.scala][main/scala/ohnosequences/scarph/syntax/conditions.scala]
          + [conditions.scala][main/scala/ohnosequences/scarph/conditions.scala]

[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummy.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: ../morphisms.scala.md
[main/scala/ohnosequences/scarph/predicates.scala]: ../predicates.scala.md
[main/scala/ohnosequences/scarph/monoidalStructures.scala]: ../monoidalStructures.scala.md
[main/scala/ohnosequences/scarph/evals.scala]: ../evals.scala.md
[main/scala/ohnosequences/scarph/implementations.scala]: ../implementations.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: ../schemas.scala.md
[main/scala/ohnosequences/scarph/naturalIsomorphisms.scala]: ../naturalIsomorphisms.scala.md
[main/scala/ohnosequences/scarph/graphTypes.scala]: ../graphTypes.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/predicates.scala]: predicates.scala.md
[main/scala/ohnosequences/scarph/syntax/graphTypes.scala]: graphTypes.scala.md
[main/scala/ohnosequences/scarph/syntax/conditions.scala]: conditions.scala.md
[main/scala/ohnosequences/scarph/conditions.scala]: ../conditions.scala.md