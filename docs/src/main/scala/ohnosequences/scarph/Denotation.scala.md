
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
  + test
    + scala
      + ohnosequences
        + scarph
          + [properties.scala][test/scala/ohnosequences/scarph/properties.scala]
          + [edges.scala][test/scala/ohnosequences/scarph/edges.scala]
          + [vertices.scala][test/scala/ohnosequences/scarph/vertices.scala]
          + titan
            + [expressions.scala][test/scala/ohnosequences/scarph/titan/expressions.scala]
            + [TitanSchemaTest.scala][test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]
            + [TitanOtherOpsTest.scala][test/scala/ohnosequences/scarph/titan/TitanOtherOpsTest.scala]
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
          + ops
            + [typelevel.scala][main/scala/ohnosequences/scarph/ops/typelevel.scala]
            + [default.scala][main/scala/ohnosequences/scarph/ops/default.scala]
          + [Denotation.scala][main/scala/ohnosequences/scarph/Denotation.scala]
          + [EdgeType.scala][main/scala/ohnosequences/scarph/EdgeType.scala]
          + [VertexType.scala][main/scala/ohnosequences/scarph/VertexType.scala]
          + [Vertex.scala][main/scala/ohnosequences/scarph/Vertex.scala]
          + [Edge.scala][main/scala/ohnosequences/scarph/Edge.scala]
          + titan
            + [TitanImplementation.scala][main/scala/ohnosequences/scarph/titan/TitanImplementation.scala]
            + [TEdge.scala][main/scala/ohnosequences/scarph/titan/TEdge.scala]
            + [TVertex.scala][main/scala/ohnosequences/scarph/titan/TVertex.scala]
            + [TSchema.scala][main/scala/ohnosequences/scarph/titan/TSchema.scala]
          + [Property.scala][main/scala/ohnosequences/scarph/Property.scala]
          + [GraphSchema.scala][main/scala/ohnosequences/scarph/GraphSchema.scala]

[test/scala/ohnosequences/scarph/properties.scala]: ../../../../test/scala/ohnosequences/scarph/properties.scala.md
[test/scala/ohnosequences/scarph/edges.scala]: ../../../../test/scala/ohnosequences/scarph/edges.scala.md
[test/scala/ohnosequences/scarph/vertices.scala]: ../../../../test/scala/ohnosequences/scarph/vertices.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: ../../../../test/scala/ohnosequences/scarph/titan/expressions.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanOtherOpsTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TitanOtherOpsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: ../../../../test/scala/ohnosequences/scarph/titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: ../../../../test/scala/ohnosequences/scarph/titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/vertexTypes.scala]: ../../../../test/scala/ohnosequences/scarph/vertexTypes.scala.md
[test/scala/ohnosequences/scarph/edgeTypes.scala]: ../../../../test/scala/ohnosequences/scarph/edgeTypes.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: Expressions.scala.md
[main/scala/ohnosequences/scarph/ops/typelevel.scala]: ops/typelevel.scala.md
[main/scala/ohnosequences/scarph/ops/default.scala]: ops/default.scala.md
[main/scala/ohnosequences/scarph/Denotation.scala]: Denotation.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: EdgeType.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: VertexType.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: Vertex.scala.md
[main/scala/ohnosequences/scarph/Edge.scala]: Edge.scala.md
[main/scala/ohnosequences/scarph/titan/TitanImplementation.scala]: titan/TitanImplementation.scala.md
[main/scala/ohnosequences/scarph/titan/TEdge.scala]: titan/TEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TVertex.scala]: titan/TVertex.scala.md
[main/scala/ohnosequences/scarph/titan/TSchema.scala]: titan/TSchema.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: Property.scala.md
[main/scala/ohnosequences/scarph/GraphSchema.scala]: GraphSchema.scala.md