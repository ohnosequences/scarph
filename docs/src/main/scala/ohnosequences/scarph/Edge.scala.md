
```scala
package ohnosequences.scarph

trait AnyEdge extends Denotation[AnyEdgeType] with HasProperties { edge =>

  // NOTE: if I remove this from here type inference fails. Most likely a bug
  type Tpe <: AnyEdgeType
```

Get source/target from this representation

```scala
  abstract class GetSource[S <: AnyVertex.ofType[Tpe#SourceType]](val source: S) {
    def apply(edgeRep: edge.Rep): source.Rep
  }
  abstract class GetTarget[T <: AnyVertex.ofType[Tpe#TargetType]](val target: T) {
    def apply(edgeRep: edge.Rep): target.Rep
  }

  implicit def edgeOps(edgeRep: edge.Rep) = EdgeOps(edgeRep)
  case class   EdgeOps(edgeRep: edge.Rep) {

    def source[S <: Singleton with AnyVertex.ofType[Tpe#SourceType]](implicit getter: GetSource[S]) = getter(edgeRep)

    def target[T <: Singleton with AnyVertex.ofType[Tpe#TargetType]](implicit getter: GetTarget[T]) = getter(edgeRep)
  }

}

class Edge[ET <: AnyEdgeType](val tpe: ET) 
  extends AnyEdge { type Tpe = ET }

object AnyEdge {
  import AnyEdgeType._

  type -->[S <: AnyVertexType, T <: AnyVertexType] = AnyEdge { type Tpe <: S ==> T }
}

```


------

### Index

+ src
  + test
    + scala
      + ohnosequences
        + scarph
          + [vertices.scala][test/scala/ohnosequences/scarph/vertices.scala]
          + [edges.scala][test/scala/ohnosequences/scarph/edges.scala]
          + [edgeTypes.scala][test/scala/ohnosequences/scarph/edgeTypes.scala]
          + titan
            + [godsImplementation.scala][test/scala/ohnosequences/scarph/titan/godsImplementation.scala]
            + [godsSchema.scala][test/scala/ohnosequences/scarph/titan/godsSchema.scala]
            + [TitanSchemaTest.scala][test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]
            + [TitanGodsTest.scala][test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]
            + [expressions.scala][test/scala/ohnosequences/scarph/titan/expressions.scala]
          + [vertexTypes.scala][test/scala/ohnosequences/scarph/vertexTypes.scala]
          + [properties.scala][test/scala/ohnosequences/scarph/properties.scala]
  + main
    + scala
      + ohnosequences
        + scarph
          + [Property.scala][main/scala/ohnosequences/scarph/Property.scala]
          + [Denotation.scala][main/scala/ohnosequences/scarph/Denotation.scala]
          + [EdgeType.scala][main/scala/ohnosequences/scarph/EdgeType.scala]
          + [HasProperties.scala][main/scala/ohnosequences/scarph/HasProperties.scala]
          + [VertexType.scala][main/scala/ohnosequences/scarph/VertexType.scala]
          + titan
            + [TEdge.scala][main/scala/ohnosequences/scarph/titan/TEdge.scala]
            + [TVertex.scala][main/scala/ohnosequences/scarph/titan/TVertex.scala]
            + [TitanGraphSchema.scala][main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]
          + [Vertex.scala][main/scala/ohnosequences/scarph/Vertex.scala]
          + [Expressions.scala][main/scala/ohnosequences/scarph/Expressions.scala]
          + [Edge.scala][main/scala/ohnosequences/scarph/Edge.scala]

[test/scala/ohnosequences/scarph/vertices.scala]: ../../../../test/scala/ohnosequences/scarph/vertices.scala.md
[test/scala/ohnosequences/scarph/edges.scala]: ../../../../test/scala/ohnosequences/scarph/edges.scala.md
[test/scala/ohnosequences/scarph/edgeTypes.scala]: ../../../../test/scala/ohnosequences/scarph/edgeTypes.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: ../../../../test/scala/ohnosequences/scarph/titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: ../../../../test/scala/ohnosequences/scarph/titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: ../../../../test/scala/ohnosequences/scarph/titan/expressions.scala.md
[test/scala/ohnosequences/scarph/vertexTypes.scala]: ../../../../test/scala/ohnosequences/scarph/vertexTypes.scala.md
[test/scala/ohnosequences/scarph/properties.scala]: ../../../../test/scala/ohnosequences/scarph/properties.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: Property.scala.md
[main/scala/ohnosequences/scarph/Denotation.scala]: Denotation.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: EdgeType.scala.md
[main/scala/ohnosequences/scarph/HasProperties.scala]: HasProperties.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: VertexType.scala.md
[main/scala/ohnosequences/scarph/titan/TEdge.scala]: titan/TEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TVertex.scala]: titan/TVertex.scala.md
[main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]: titan/TitanGraphSchema.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: Vertex.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: Expressions.scala.md
[main/scala/ohnosequences/scarph/Edge.scala]: Edge.scala.md