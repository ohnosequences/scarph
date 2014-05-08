
```scala
package ohnosequences.scarph

trait AnyEdge extends Denotation[AnyEdgeType] with HasProperties { edge =>

  // NOTE: if I remove this from here type inference fails. Most likely a bug
  type Tpe <: AnyEdgeType
```

Get source/target from this representation

```scala
  abstract class GetSource[S <: AnyVertex.ofType[Tpe#SourceType]](val source: S) {
    def apply(edgeRep: edge.TaggedRep): source.TaggedRep
  }
  abstract class GetTarget[T <: AnyVertex.ofType[Tpe#TargetType]](val target: T) {
    def apply(edgeRep: edge.TaggedRep): target.TaggedRep
  }

  implicit def edgeOps(edgeRep: edge.TaggedRep) = EdgeOps(edgeRep)
  case class   EdgeOps(edgeRep: edge.TaggedRep) {

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
  + main
    + scala
      + ohnosequences
        + scarph
          + [Denotation.scala][main/scala/ohnosequences/scarph/Denotation.scala]
          + [Edge.scala][main/scala/ohnosequences/scarph/Edge.scala]
          + [EdgeType.scala][main/scala/ohnosequences/scarph/EdgeType.scala]
          + [Expressions.scala][main/scala/ohnosequences/scarph/Expressions.scala]
          + [HasProperties.scala][main/scala/ohnosequences/scarph/HasProperties.scala]
          + [Property.scala][main/scala/ohnosequences/scarph/Property.scala]
          + [Vertex.scala][main/scala/ohnosequences/scarph/Vertex.scala]
          + [VertexType.scala][main/scala/ohnosequences/scarph/VertexType.scala]
  + test
    + scala
      + ohnosequences
        + scarph
          + [edges.scala][test/scala/ohnosequences/scarph/edges.scala]
          + [edgeTypes.scala][test/scala/ohnosequences/scarph/edgeTypes.scala]
          + [properties.scala][test/scala/ohnosequences/scarph/properties.scala]
          + [vertexTypes.scala][test/scala/ohnosequences/scarph/vertexTypes.scala]
          + [vertices.scala][test/scala/ohnosequences/scarph/vertices.scala]

[main/scala/ohnosequences/scarph/Denotation.scala]: Denotation.scala.md
[main/scala/ohnosequences/scarph/Edge.scala]: Edge.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: EdgeType.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: Expressions.scala.md
[main/scala/ohnosequences/scarph/HasProperties.scala]: HasProperties.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: Property.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: Vertex.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: VertexType.scala.md
[test/scala/ohnosequences/scarph/edges.scala]: ../../../../test/scala/ohnosequences/scarph/edges.scala.md
[test/scala/ohnosequences/scarph/edgeTypes.scala]: ../../../../test/scala/ohnosequences/scarph/edgeTypes.scala.md
[test/scala/ohnosequences/scarph/properties.scala]: ../../../../test/scala/ohnosequences/scarph/properties.scala.md
[test/scala/ohnosequences/scarph/vertexTypes.scala]: ../../../../test/scala/ohnosequences/scarph/vertexTypes.scala.md
[test/scala/ohnosequences/scarph/vertices.scala]: ../../../../test/scala/ohnosequences/scarph/vertices.scala.md