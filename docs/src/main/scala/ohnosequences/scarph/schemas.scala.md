
```scala
package ohnosequences.scarph

object schemas {

  import graphTypes._


  trait AnyGraphSchema extends AnyGraphObject {

    val vertices: Set[AnyVertex]
    val edges: Set[AnyEdge]
    val valueTypes: Set[AnyValueType]
    val properties: Set[AnyGraphProperty]
  }

  class GraphSchema(
    val label: String,
    val vertices: Set[AnyVertex],
    val edges: Set[AnyEdge],
    val valueTypes: Set[AnyValueType],
    val properties: Set[AnyGraphProperty]
  ) extends AnyGraphSchema

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

[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: ../../../../test/scala/ohnosequences/scarph/impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: ../../../../test/scala/ohnosequences/scarph/impl/dummy.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: morphisms.scala.md
[main/scala/ohnosequences/scarph/predicates.scala]: predicates.scala.md
[main/scala/ohnosequences/scarph/monoidalStructures.scala]: monoidalStructures.scala.md
[main/scala/ohnosequences/scarph/evals.scala]: evals.scala.md
[main/scala/ohnosequences/scarph/implementations.scala]: implementations.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: schemas.scala.md
[main/scala/ohnosequences/scarph/naturalIsomorphisms.scala]: naturalIsomorphisms.scala.md
[main/scala/ohnosequences/scarph/graphTypes.scala]: graphTypes.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/predicates.scala]: syntax/predicates.scala.md
[main/scala/ohnosequences/scarph/syntax/graphTypes.scala]: syntax/graphTypes.scala.md
[main/scala/ohnosequences/scarph/syntax/conditions.scala]: syntax/conditions.scala.md
[main/scala/ohnosequences/scarph/conditions.scala]: conditions.scala.md