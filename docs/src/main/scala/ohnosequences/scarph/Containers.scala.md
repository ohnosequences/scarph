
```scala
package ohnosequences.scarph

object containers {

  import graphTypes._
```

These classes are only for convenience of definitions inside this object

```scala
  abstract sealed private class NestedGraphType[C <: AnyContainer, T <: AnyGraphType]
    (val container: C, val inside: T) extends AnyGraphType {

    type Container = C
    type Inside = T

    lazy val label = this.toString
  }

  private[containers] case class ExactlyOneOf[T <: AnyGraphType](t: T) extends NestedGraphType(ExactlyOne, t)
  private[containers] case class  OneOrNoneOf[T <: AnyGraphType](t: T) extends NestedGraphType(OneOrNone, t)
  private[containers] case class ManyOrNoneOf[T <: AnyGraphType](t: T) extends NestedGraphType(ManyOrNone, t)
  private[containers] case class AtLeastOneOf[T <: AnyGraphType](t: T) extends NestedGraphType(AtLeastOne, t)
```

Here are 4 types of arity containers that we can use for wrapping graph types

```scala
  sealed trait AnyContainer {

    type Of[T <: AnyGraphType] <: AnyGraphType
    def  of[T <: AnyGraphType](t: T): Of[T]
  }


  case object ExactlyOne extends AnyContainer { 
    type Of[T <: AnyGraphType] = T
    def  of[T <: AnyGraphType](t: T): T = t
  }
  type ExactlyOne = ExactlyOne.type

  case object OneOrNone extends AnyContainer { 
    type Of[T <: AnyGraphType] = OneOrNoneOf[T]  
    def  of[T <: AnyGraphType](t: T): Of[T] = OneOrNoneOf[T](t)
  }
  type OneOrNone = OneOrNone.type

  case object AtLeastOne extends AnyContainer { 
    type Of[T <: AnyGraphType] = AtLeastOneOf[T] 
    def  of[T <: AnyGraphType](t: T): Of[T] = AtLeastOneOf[T](t)
  }
  type AtLeastOne = AtLeastOne.type

  case object ManyOrNone extends AnyContainer { 
    type Of[T <: AnyGraphType] = ManyOrNoneOf[T] 
    def  of[T <: AnyGraphType](t: T): Of[T] = ManyOrNoneOf[T](t)
  }
  type ManyOrNone = ManyOrNone.type


  import ohnosequences.cosas._, fns._
```

This is for mapping (in implementation) ManyOrNone to List, OneOrNone to Option, etc.

```scala
  trait ValueContainer[C <: AnyContainer, X] extends Fn1[X]
```

Containers multiplication (`\times` symbol)

```scala
  trait ×[A <: AnyContainer, B <: AnyContainer] extends Fn2[A, B] with OutBound[AnyContainer]

  object × extends x_2 {
    implicit def idemp[A <: AnyContainer]: 
        (A × A) with Out[A] = 
    new (A × A) with Out[A] { def apply(a: In1, b: In2): Out = a }
  }

  trait x_2 extends x_3 {
    implicit def unitL[B <: AnyContainer]: 
        (ExactlyOne × B) with Out[B] = 
    new (ExactlyOne × B) with Out[B] { def apply(a: In1, b: In2): Out = b }

    implicit def unitR[A <: AnyContainer]: 
        (A × ExactlyOne) with Out[A] = 
    new (A × ExactlyOne) with Out[A] { def apply(a: In1, b: In2): Out = a }
  }

  trait x_3 {
    implicit def rest[A <: AnyContainer, B <: AnyContainer]: 
        (A × B) with Out[ManyOrNone] = 
    new (A × B) with Out[ManyOrNone] { def apply(a: In1, b: In2): Out = ManyOrNone }
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