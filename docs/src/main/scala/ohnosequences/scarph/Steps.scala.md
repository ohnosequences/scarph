
```scala
package ohnosequences.scarph
```

Basic steps:

```scala
object steps {

  import graphTypes._, paths._, containers._, predicates._, schemas._, indexes._


  case class Get[P <: AnyGraphProperty](val property: P) 
    extends Step[P#Owner, P](property.owner, property)

  case class InE[P <: AnyPredicate { type Element <: AnyEdge }](val predicate: P) extends AnyStep {

      type     Edge = P#Element
      lazy val edge = predicate.element: Edge

      type     In = Edge#TargetV
      lazy val in = edge.targetV

      type     Out = Edge#Source#Container#Of[Edge]
      lazy val out = edge.source.container.of(edge)
  }

  case class OutE[P <: AnyPredicate { type Element <: AnyEdge }](val predicate: P) extends AnyStep {

      type     Edge = P#Element
      lazy val edge = predicate.element: Edge

      type     In = Edge#SourceV
      lazy val in = edge.sourceV

      type     Out = Edge#Target#Container#Of[Edge]
      lazy val out = edge.target.container.of(edge)
  }


  case class InV[P <: AnyPredicate { type Element <: AnyEdge }](val predicate: P) extends AnyStep {

      type     Edge = P#Element
      lazy val edge = predicate.element: Edge

      type     In = Edge#TargetV
      lazy val in = edge.targetV

      type     Out = Edge#SourceV
      lazy val out = edge.sourceV
  }

  case class OutV[P <: AnyPredicate { type Element <: AnyEdge }](val predicate: P) extends AnyStep {

      type     Edge = P#Element
      lazy val edge = predicate.element: Edge

      type     In = Edge#SourceV
      lazy val in = edge.sourceV

      type     Out = Edge#Target
      lazy val out = edge.target
  }

  // TODO: inV/outV

  case class Source[E <: AnyEdge](val edge: E) extends AnyStep {

    type In = E
    val  in = edge

    type     Out = E#SourceV
    lazy val out = edge.sourceV
  }

  case class Target[E <: AnyEdge](val edge: E) extends AnyStep {

    type In = E
    val  in = edge

    type     Out = E#TargetV
    lazy val out = edge.targetV
  }

  case class GraphQuery[S <: AnySchema, C <: AnyContainer, P <: AnyPredicate]
    (val graph: S, val container: C, val predicate: P)
      extends Step[S, C#Of[P#Element]](graph, container.of(predicate.element))
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

[test/scala/ohnosequences/scarph/ContainersTest.scala]: ../../../../test/scala/ohnosequences/scarph/ContainersTest.scala.md
[test/scala/ohnosequences/scarph/ScalazEquality.scala]: ../../../../test/scala/ohnosequences/scarph/ScalazEquality.scala.md
[test/scala/ohnosequences/scarph/titan/TwitterTitanTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TwitterTitanTest.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[main/scala/ohnosequences/scarph/GraphTypes.scala]: GraphTypes.scala.md
[main/scala/ohnosequences/scarph/Containers.scala]: Containers.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Schema.scala]: impl/titan/Schema.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Evals.scala]: impl/titan/Evals.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Predicates.scala]: impl/titan/Predicates.scala.md
[main/scala/ohnosequences/scarph/Paths.scala]: Paths.scala.md
[main/scala/ohnosequences/scarph/Indexes.scala]: Indexes.scala.md
[main/scala/ohnosequences/scarph/Evals.scala]: Evals.scala.md
[main/scala/ohnosequences/scarph/Conditions.scala]: Conditions.scala.md
[main/scala/ohnosequences/scarph/Steps.scala]: Steps.scala.md
[main/scala/ohnosequences/scarph/Predicates.scala]: Predicates.scala.md
[main/scala/ohnosequences/scarph/Schemas.scala]: Schemas.scala.md
[main/scala/ohnosequences/scarph/Combinators.scala]: Combinators.scala.md
[main/scala/ohnosequences/scarph/syntax/GraphTypes.scala]: syntax/GraphTypes.scala.md
[main/scala/ohnosequences/scarph/syntax/Paths.scala]: syntax/Paths.scala.md
[main/scala/ohnosequences/scarph/syntax/Conditions.scala]: syntax/Conditions.scala.md
[main/scala/ohnosequences/scarph/syntax/Predicates.scala]: syntax/Predicates.scala.md