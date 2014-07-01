
```scala
package ohnosequences.scarph.test

object properties {

  import ohnosequences.scarph.Property
  import ohnosequences.typesets._
  
  case object since extends Property[Int]
  case object validUntil extends Property[Int]
  case object name extends Property[String]
  case object isPublic extends Property[Boolean]
  case object id extends Property[String]

  val allProperties = since :~: validUntil :~: name :~: isPublic :~: id :~: ∅
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

[test/scala/ohnosequences/scarph/properties.scala]: properties.scala.md
[test/scala/ohnosequences/scarph/edges.scala]: edges.scala.md
[test/scala/ohnosequences/scarph/vertices.scala]: vertices.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: titan/expressions.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: titan/TitanSchemaTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanOtherOpsTest.scala]: titan/TitanOtherOpsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/vertexTypes.scala]: vertexTypes.scala.md
[test/scala/ohnosequences/scarph/edgeTypes.scala]: edgeTypes.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: ../../../../main/scala/ohnosequences/scarph/Expressions.scala.md
[main/scala/ohnosequences/scarph/ops/typelevel.scala]: ../../../../main/scala/ohnosequences/scarph/ops/typelevel.scala.md
[main/scala/ohnosequences/scarph/ops/default.scala]: ../../../../main/scala/ohnosequences/scarph/ops/default.scala.md
[main/scala/ohnosequences/scarph/Denotation.scala]: ../../../../main/scala/ohnosequences/scarph/Denotation.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: ../../../../main/scala/ohnosequences/scarph/EdgeType.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: ../../../../main/scala/ohnosequences/scarph/VertexType.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: ../../../../main/scala/ohnosequences/scarph/Vertex.scala.md
[main/scala/ohnosequences/scarph/Edge.scala]: ../../../../main/scala/ohnosequences/scarph/Edge.scala.md
[main/scala/ohnosequences/scarph/titan/TitanImplementation.scala]: ../../../../main/scala/ohnosequences/scarph/titan/TitanImplementation.scala.md
[main/scala/ohnosequences/scarph/titan/TEdge.scala]: ../../../../main/scala/ohnosequences/scarph/titan/TEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TVertex.scala]: ../../../../main/scala/ohnosequences/scarph/titan/TVertex.scala.md
[main/scala/ohnosequences/scarph/titan/TSchema.scala]: ../../../../main/scala/ohnosequences/scarph/titan/TSchema.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: ../../../../main/scala/ohnosequences/scarph/Property.scala.md
[main/scala/ohnosequences/scarph/GraphSchema.scala]: ../../../../main/scala/ohnosequences/scarph/GraphSchema.scala.md