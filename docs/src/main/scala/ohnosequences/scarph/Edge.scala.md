
```scala
package ohnosequences.scarph

trait AnyEdge extends Denotation[AnyEdgeType] with CanGetPropertiesOfTpe { edge =>

  // NOTE: if I remove this from here type inference fails. Most likely a bug
  type Tpe <: AnyEdgeType

  type Source <: AnyVertex.ofType[Tpe#SourceType]
  val  source: Source

  type Target <: AnyVertex.ofType[Tpe#TargetType]
  val  target: Target
```

Get source/target from this representation

```scala
  abstract class GetSource {
    type Out = source.Rep
    def apply(edgeRep: edge.Rep): Out
  }
  abstract class GetTarget { 
    type Out = target.Rep 
    def apply(edgeRep: edge.Rep): Out
  }

}

class Edge[
    S <: AnyVertex.ofType[ET#SourceType],
    ET <: AnyEdgeType, 
    T <: AnyVertex.ofType[ET#TargetType]
  ](val source: S, val tpe: ET, val target: T) extends AnyEdge { 
    type Source = S
    type Tpe = ET 
    type Target = T
  }

object AnyEdge {
  import AnyEdgeType._

  type ofType[ET <: AnyEdgeType] = AnyEdge { type Tpe = ET }

  type -->[S <: AnyVertexType, T <: AnyVertexType] = AnyEdge { type Tpe <: S ==> T }
}

object Edge {
  type RepOf[E <: Singleton with AnyEdge] = AnyTag.TaggedWith[E]
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