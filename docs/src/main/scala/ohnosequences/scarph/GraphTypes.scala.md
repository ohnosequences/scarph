
```scala
package ohnosequences.scarph

object graphTypes {

  import ohnosequences.cosas._, types._, properties._
  import steps._, paths._, containers._
```

This is a graph type containing another graph type

```scala
  trait AnyGraphType extends AnyType {

    type Container <: AnyContainer
    val  container: Container

    type Inside <: AnyGraphType
    val  inside: Inside
  }

  @annotation.implicitNotFound(msg = "Can't prove that these graph types are equivalent:\n\tfirst:  ${A}\n\tsecond: ${B}")
  trait ≃[A <: AnyGraphType, B <: AnyGraphType]

  // this is `\simeq` symbol
  object ≃ extends simeq2 {
    // implicit def refl[A <: AnySimpleGraphType]: A ≃ A = new (A ≃ A) {}

    implicit def eq[A <: AnyGraphType, B <: AnyGraphType]
      (implicit eq: A#Container#Of[A#Inside] =:= B#Container#Of[B#Inside]): A ≃ B = new (A ≃ B) {}
  }

  trait simeq2 {
    // implicit def eq[A <: AnyGraphType, B <: AnyGraphType]
    //   (implicit 
    //     cont: A#Container =:= B#Container,
    //     insd: A#Inside ≃ B#Inside
    //   ): A ≃ B = new (A ≃ B) {}
  }
```

This is a non-nested graph type

```scala
  trait AnySimpleGraphType extends AnyGraphType {

    type Container = ExactlyOne
    val  container = ExactlyOne

    type Inside >: this.type <: AnyGraphType
    lazy val inside: Inside = this
  }
```

A graph element is either a vertex or an edge, only they can have properties

```scala
  sealed trait AnyGraphElement extends AnySimpleGraphType
```

Vertex type is very simple

```scala
  trait AnyVertex extends AnyGraphElement

  class Vertex extends AnyVertex { type Inside = this.type; lazy val label = this.toString }
```

Edges connect vertices and have in/out arities

```scala
  // NOTE: this is the same as AnyPath but with restriction on InT/OutT
  trait AnyEdge extends AnyGraphElement {
    
    type Source <: AnyGraphType { type Inside <: AnyVertex }
    val  source: Source

    type     SourceV = Source#Inside
    lazy val sourceV = source.inside: SourceV


    type Target <: AnyGraphType { type Inside <: AnyVertex }
    val  target: Target

    type     TargetV = Target#Inside
    lazy val targetV = target.inside: TargetV
  }
```

This constructor encourages to use this syntax: Edge( ExactlyOne.of(user) -> ManyOrNone.of(tweet) )

```scala
  abstract class Edge[
    S <: AnyGraphType { type Inside <: AnyVertex },
    T <: AnyGraphType { type Inside <: AnyVertex }
  ]( st: (S, T) ) extends AnyEdge {

    type Source = S
    lazy val source = st._1

    type Target = T
    lazy val target = st._2

    val label = this.toString

    type Inside = Edge[S,T]
  }
```

Property is assigned to one element type and has a raw representation

```scala
  trait AnyGraphProperty extends AnySimpleGraphType with AnyProperty {

    type Owner <: AnyGraphElement
    val  owner: Owner
  }

  // TODO: something like edge constructor
  abstract class PropertyOf[O <: AnyGraphElement](val owner: O) extends AnyGraphProperty {
    
    type Owner = O

    val label = this.toString

    type Inside = PropertyOf[O]
  }

  trait AnyParType extends AnySimpleGraphType {

    type First <: AnyGraphType
    val  first: First

    type Second <: AnyGraphType
    val  second: Second
  }

  case class ParType[F <: AnyGraphType, S <: AnyGraphType]
    (val first: F, val second: S) extends AnyParType {

    type First = F
    type Second = S

    lazy val label = s"(first.label ⊗ second.label)"

    type Inside = ParType[F,S]
  }


  trait AnyOrType extends AnySimpleGraphType {

    type Left <: AnyGraphType
    val  left: Left

    type Right <: AnyGraphType
    val  right: Right
  }

  case class OrType[L <: AnyGraphType, R <: AnyGraphType]
    (val left: L, val right: R) extends AnyOrType {

    type Left = L
    type Right = R

    val label = s"(left.label ⊕ right.label)"
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