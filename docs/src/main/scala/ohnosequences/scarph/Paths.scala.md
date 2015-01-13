
```scala
package ohnosequences.scarph

object paths {

  import ohnosequences.cosas._, types._
  import graphTypes._, containers._
```


_Path_ describes some graph traversal. It contains of steps that are combined in various ways.

Note that `AnyPath` hierarchy is sealed, meaning that a path is either a step or a combinator.
You can create steps and combinators extending `AnyStep` and `AnyPathCombinator` correspondingly.

In the following code there is a naming convention:
- `C` suffix means (arity) _Container_
- `T` suffix means (label) _Type_


```scala
  sealed trait AnyPath {
```

Input

```scala
    type In <: AnyGraphType
    val  in: In
```

Output

```scala
    type Out <: AnyGraphType
    val  out: Out

    // TODO: add Reverse member
  }
```

A _step_ is a simple atomic _path_ which can be evaluated directly

```scala
  trait AnyStep extends AnyPath {

    type In <: AnySimpleGraphType
    val  in: In
  }

  abstract class Step[
    I <: AnySimpleGraphType,
    O <: AnyGraphType
  ](val in: I, val out: O) extends AnyStep {

    type In = I
    type Out = O
  }
```

See available combinators in [Combinators.scala]

```scala
  trait AnyPathCombinator extends AnyPath
```

Adding useful methods

```scala
  object AnyPath {

    implicit def pathOps[T <: AnyPath](t: T) = PathOps(t)
  }

  case class PathOps[P <: AnyPath](val p: P) {

    import combinators._
    // it's left here and not moved to syntax, because using syntax you shouldn't need it
    def >=>[S <: AnyPath](s: S)(implicit c: P#Out ≃ S#In): Composition[P, S] = Composition(p, s)(c)

    import evals._
    def evalOn[I, O](input: I Denotes P#In)
      (implicit eval: EvalPathOn[I, P, O]): O Denotes P#Out = eval(p)(input)
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