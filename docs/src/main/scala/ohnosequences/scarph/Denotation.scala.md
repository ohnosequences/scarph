
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
trait Representable { self =>

  type Raw
```


`Raw` tagged with `self.type`; this lets you recognize a denotation while being able to operate on it as `Raw`.


```scala
  final type Rep = AnyTag.TaggedWith[self.type]
```


`Raw` enters, `Rep` leaves


```scala
  final def ->>(r: Raw): self.Rep = AnyTag.TagWith[self.type](self)(r)
```


This lets you get the instance of the singleton type from a tagged `Rep` value.


```scala
  implicit def fromRep(x: self.Rep): self.type = self
}


trait AnyDenotation extends Representable {
```

The base type for the types that this thing denotes

```scala
  type TYPE
  type Tpe <: TYPE
  val  tpe: Tpe
}
```


Bound the universe of types to be `T`s


```scala
trait Denotation[T] extends AnyDenotation { type TYPE = T }
```


The companion object contains mainly tagging functionality.


```scala
object AnyTag {

  case class TagWith[D <: Singleton with Representable](val d: D) {
    def apply(dr : d.Raw): TaggedWith[d.type] = dr.asInstanceOf[TaggedWith[d.type]]
  }

  type TaggedWith[D <: Singleton with Representable] = D#Raw with Tag[D]

  // Has to be empty! See http://www.scala-lang.org/old/node/11165.html#comment-49097
  sealed trait AnyTag 
  sealed trait Tag[D <: Singleton with Representable] extends AnyTag with KeyTag[D, D#Raw]

}

```


------

### Index

+ src
  + main
    + scala
      + ohnosequences
        + scarph
          + [Denotation.scala][main/scala/ohnosequences/scarph/Denotation.scala]
          + [Edge.scala][main/scala/ohnosequences/scarph/Edge.scala]
          + [EdgeType.scala][main/scala/ohnosequences/scarph/EdgeType.scala]
          + [Expressions.scala][main/scala/ohnosequences/scarph/Expressions.scala]
          + [GraphSchema.scala][main/scala/ohnosequences/scarph/GraphSchema.scala]
          + ops
            + [default.scala][main/scala/ohnosequences/scarph/ops/default.scala]
            + [typelevel.scala][main/scala/ohnosequences/scarph/ops/typelevel.scala]
          + [Property.scala][main/scala/ohnosequences/scarph/Property.scala]
          + titan
            + [TitanEdge.scala][main/scala/ohnosequences/scarph/titan/TitanEdge.scala]
            + [TitanGraphSchema.scala][main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]
            + [TitanVertex.scala][main/scala/ohnosequences/scarph/titan/TitanVertex.scala]
          + [Vertex.scala][main/scala/ohnosequences/scarph/Vertex.scala]
          + [VertexType.scala][main/scala/ohnosequences/scarph/VertexType.scala]
  + test
    + scala
      + ohnosequences
        + scarph
          + titan
            + [expressions.scala][test/scala/ohnosequences/scarph/titan/expressions.scala]
            + [godsImplementation.scala][test/scala/ohnosequences/scarph/titan/godsImplementation.scala]
            + [godsSchema.scala][test/scala/ohnosequences/scarph/titan/godsSchema.scala]
            + [TitanGodsTest.scala][test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]
            + [TitanSchemaTest.scala][test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]

[main/scala/ohnosequences/scarph/Denotation.scala]: Denotation.scala.md
[main/scala/ohnosequences/scarph/Edge.scala]: Edge.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: EdgeType.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: Expressions.scala.md
[main/scala/ohnosequences/scarph/GraphSchema.scala]: GraphSchema.scala.md
[main/scala/ohnosequences/scarph/ops/default.scala]: ops/default.scala.md
[main/scala/ohnosequences/scarph/ops/typelevel.scala]: ops/typelevel.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: Property.scala.md
[main/scala/ohnosequences/scarph/titan/TitanEdge.scala]: titan/TitanEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]: titan/TitanGraphSchema.scala.md
[main/scala/ohnosequences/scarph/titan/TitanVertex.scala]: titan/TitanVertex.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: Vertex.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: VertexType.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: ../../../../test/scala/ohnosequences/scarph/titan/expressions.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: ../../../../test/scala/ohnosequences/scarph/titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: ../../../../test/scala/ohnosequences/scarph/titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala.md