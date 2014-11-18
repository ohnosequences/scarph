
```scala
package ohnosequences.scarph

import ohnosequences.typesets._, AnyTag._
```


`AnyVertex` defines a denotation of the corresponding `VertexType`.

Instances are modeled as instances of other type tagged with the singleton type of a `Vertex`. For example, an instance of a self of type `User` when stored/represented by a `Neo4jNode` is going to be something of type `FieldType[user.type, Neo4jNode]`  where `user.type <: AnyVertex { type VertexType = User.type; type Raw = Neo4jNode }`.

They are designed to be compatible with shapeless records (maybe, we'll see).


```scala
trait AnyVertex extends Denotation[AnyVertexType] with CanGetProperties { vertex =>
```

Getters for incoming/outgoing edges

```scala
  // abstract class GetOutEdge[E <: Singleton with AnyEdge](val e: E) {
  abstract class GetOutEdge[OE <: AnyEdge](val edge: OE) {

    // def apply(rep: vertex.Rep): e.tpe.Out[E#Rep]
    def apply(rep: vertex.Rep): edge.tpe.Out[TaggedWith[OE]]
  }
  abstract class GetInEdge[IE <: AnyEdge](val edge: IE) {

    def apply(rep: vertex.Rep): edge.tpe.In[TaggedWith[IE]]
  }

}

abstract class Vertex[VT <: AnyVertexType](val tpe: VT) 
    extends AnyVertex { type Tpe = VT }

object AnyVertex {
  type ofType[VT <: AnyVertexType] = AnyVertex { type Tpe = VT }
}

object Vertex {
  type RepOf[V <: Singleton with AnyVertex] = AnyTag.TaggedWith[V]
}

// this denotation stuff is weird
trait AnySealedVertex extends AnyVertex { sealedVertex =>

  type Tpe <: AnySealedVertexType

  final type Raw = raw

  type Other
  case class raw(val fields: tpe.record.Rep, val other: Other)
  // double tagging FTW!
  final def fields[R <: TypeSet](r: R)(implicit 
    p: R ~> tpe.record.Raw
  ): tpe.record.Rep = (tpe.record ->> p(r))

  implicit def propertyOps(rep: sealedVertex.Rep): tpe.record.PropertyOps = tpe.record.PropertyOps(rep.fields)
}

abstract class SealedVertex[VT <: AnySealedVertexType](val tpe: VT) extends AnySealedVertex { 

  type Tpe = VT
}
```


------

### Index

+ src
  + main
    + scala
      + ohnosequences
        + scarph
          + [Edge.scala][main/scala/ohnosequences/scarph/Edge.scala]
          + [EdgeType.scala][main/scala/ohnosequences/scarph/EdgeType.scala]
          + [Expressions.scala][main/scala/ohnosequences/scarph/Expressions.scala]
          + [GraphSchema.scala][main/scala/ohnosequences/scarph/GraphSchema.scala]
          + ops
            + [default.scala][main/scala/ohnosequences/scarph/ops/default.scala]
            + [typelevel.scala][main/scala/ohnosequences/scarph/ops/typelevel.scala]
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
          + [sealedStuff.scala][test/scala/ohnosequences/scarph/sealedStuff.scala]
          + titan
            + [expressions.scala][test/scala/ohnosequences/scarph/titan/expressions.scala]
            + [godsImplementation.scala][test/scala/ohnosequences/scarph/titan/godsImplementation.scala]
            + [godsSchema.scala][test/scala/ohnosequences/scarph/titan/godsSchema.scala]
            + [TitanGodsTest.scala][test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]
            + [TitanSchemaTest.scala][test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]

[main/scala/ohnosequences/scarph/Edge.scala]: Edge.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: EdgeType.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: Expressions.scala.md
[main/scala/ohnosequences/scarph/GraphSchema.scala]: GraphSchema.scala.md
[main/scala/ohnosequences/scarph/ops/default.scala]: ops/default.scala.md
[main/scala/ohnosequences/scarph/ops/typelevel.scala]: ops/typelevel.scala.md
[main/scala/ohnosequences/scarph/titan/TitanEdge.scala]: titan/TitanEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]: titan/TitanGraphSchema.scala.md
[main/scala/ohnosequences/scarph/titan/TitanVertex.scala]: titan/TitanVertex.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: Vertex.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: VertexType.scala.md
[test/scala/ohnosequences/scarph/sealedStuff.scala]: ../../../../test/scala/ohnosequences/scarph/sealedStuff.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: ../../../../test/scala/ohnosequences/scarph/titan/expressions.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: ../../../../test/scala/ohnosequences/scarph/titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: ../../../../test/scala/ohnosequences/scarph/titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala.md