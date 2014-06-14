
```scala
package ohnosequences.scarph

import shapeless.record._
```


This trait represents a mapping between 

- members `Tpe` of a universe of types `TYPE`
- and `Raw` a type meant to be a denotation of `Tpe` thus the name

Tagging is used for being able to operate on `Raw` values knowing what they are denotating; `Rep` is just `Raw` tagged with the `.type` of this denotation. So, summarizing

- `Tpe` is the denotated type
- `Raw` is its denotation
- `Rep <: Raw` is just `Raw` tagged with `this.type`


```scala
trait AnyDenotationLike {

 type Raw
 type Rep <: Raw
}
trait AnyDenotation extends AnyDenotationLike { self =>
```

The base type for the types that this thing denotes

```scala
  type TYPE
  type Tpe <: TYPE
  // TODO what about a version without this val?
  val tpe: Tpe
```


The type used to denotate `Tpe`.


```scala
  type Raw
```


`Raw` tagged with `self.type`; this lets you recognize a denotation while being able to operate on it as `Raw`.


```scala
  final type Rep = AnyDenotation.TaggedWith[self.type]
```


`Raw` enters, `Rep` leaves


```scala
  final def ->>(r: Raw): self.Rep = AnyDenotation.tagWith[self.type](r)
}
```


Bound the universe of types to be `T`s


```scala
trait Denotation[T] extends AnyDenotation { 

  type TYPE = T
}
```


The companion object contains mainly tagging functionality.


```scala
object AnyDenotation {

  type TaggedWith[D <: AnyDenotation] = D#Raw with Tag[D]

  def tagWith[D <: AnyDenotation with Singleton] = new TagBuilder[D]

  class TagBuilder[D <: AnyDenotation] {
    def apply(dr : D#Raw): TaggedWith[D] = dr.asInstanceOf[TaggedWith[D]]
  }

  trait AnyTag {

    type Denotation <: AnyDenotation
    type DenotedType = Denotation#Tpe
  }

  trait Tag[D <: AnyDenotation] extends AnyTag with KeyTag[D, D#Raw] {

    type Denotation = D
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
          + [properties.scala][test/scala/ohnosequences/scarph/properties.scala]
          + restricted
            + [SimpleSchema.scala][test/scala/ohnosequences/scarph/restricted/SimpleSchema.scala]
            + [RestrictedSchemaTest.scala][test/scala/ohnosequences/scarph/restricted/RestrictedSchemaTest.scala]
            + [SimpleSchemaImplementation.scala][test/scala/ohnosequences/scarph/restricted/SimpleSchemaImplementation.scala]
          + [edges.scala][test/scala/ohnosequences/scarph/edges.scala]
          + [vertices.scala][test/scala/ohnosequences/scarph/vertices.scala]
          + titan
            + [expressions.scala][test/scala/ohnosequences/scarph/titan/expressions.scala]
            + [TitanSchemaTest.scala][test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]
            + [TitanGodsTest.scala][test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]
            + [godsImplementation.scala][test/scala/ohnosequences/scarph/titan/godsImplementation.scala]
            + [godsSchema.scala][test/scala/ohnosequences/scarph/titan/godsSchema.scala]
          + [vertexTypes.scala][test/scala/ohnosequences/scarph/vertexTypes.scala]
          + [edgeTypes.scala][test/scala/ohnosequences/scarph/edgeTypes.scala]
  + main
    + scala
      + ohnosequences
        + scarph
          + [Expressions.scala][main/scala/ohnosequences/scarph/Expressions.scala]
          + [Denotation.scala][main/scala/ohnosequences/scarph/Denotation.scala]
          + [EdgeType.scala][main/scala/ohnosequences/scarph/EdgeType.scala]
          + [VertexType.scala][main/scala/ohnosequences/scarph/VertexType.scala]
          + [Vertex.scala][main/scala/ohnosequences/scarph/Vertex.scala]
          + [Schema.scala][main/scala/ohnosequences/scarph/Schema.scala]
          + [Edge.scala][main/scala/ohnosequences/scarph/Edge.scala]
          + titan
            + [TEdge.scala][main/scala/ohnosequences/scarph/titan/TEdge.scala]
            + [TVertex.scala][main/scala/ohnosequences/scarph/titan/TVertex.scala]
            + [TSchema.scala][main/scala/ohnosequences/scarph/titan/TSchema.scala]
          + [Property.scala][main/scala/ohnosequences/scarph/Property.scala]

[test/scala/ohnosequences/scarph/properties.scala]: ../../../../test/scala/ohnosequences/scarph/properties.scala.md
[test/scala/ohnosequences/scarph/restricted/SimpleSchema.scala]: ../../../../test/scala/ohnosequences/scarph/restricted/SimpleSchema.scala.md
[test/scala/ohnosequences/scarph/restricted/RestrictedSchemaTest.scala]: ../../../../test/scala/ohnosequences/scarph/restricted/RestrictedSchemaTest.scala.md
[test/scala/ohnosequences/scarph/restricted/SimpleSchemaImplementation.scala]: ../../../../test/scala/ohnosequences/scarph/restricted/SimpleSchemaImplementation.scala.md
[test/scala/ohnosequences/scarph/edges.scala]: ../../../../test/scala/ohnosequences/scarph/edges.scala.md
[test/scala/ohnosequences/scarph/vertices.scala]: ../../../../test/scala/ohnosequences/scarph/vertices.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: ../../../../test/scala/ohnosequences/scarph/titan/expressions.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: ../../../../test/scala/ohnosequences/scarph/titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: ../../../../test/scala/ohnosequences/scarph/titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/vertexTypes.scala]: ../../../../test/scala/ohnosequences/scarph/vertexTypes.scala.md
[test/scala/ohnosequences/scarph/edgeTypes.scala]: ../../../../test/scala/ohnosequences/scarph/edgeTypes.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: Expressions.scala.md
[main/scala/ohnosequences/scarph/Denotation.scala]: Denotation.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: EdgeType.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: VertexType.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: Vertex.scala.md
[main/scala/ohnosequences/scarph/Schema.scala]: Schema.scala.md
[main/scala/ohnosequences/scarph/Edge.scala]: Edge.scala.md
[main/scala/ohnosequences/scarph/titan/TEdge.scala]: titan/TEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TVertex.scala]: titan/TVertex.scala.md
[main/scala/ohnosequences/scarph/titan/TSchema.scala]: titan/TSchema.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: Property.scala.md