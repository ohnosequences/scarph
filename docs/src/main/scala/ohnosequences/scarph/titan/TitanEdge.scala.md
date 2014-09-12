
```scala
package ohnosequences.scarph.titan

import ohnosequences.scarph._
import ohnosequences.typesets._

trait AnyTitanEdge extends AnyEdge { tedge =>

  final type Raw = com.thinkaurelius.titan.core.TitanEdge

  type Source <: AnyVertex.ofType[Tpe#SourceType] with AnyTitanVertex
  type Target <: AnyVertex.ofType[Tpe#TargetType] with AnyTitanVertex
```

Getting a property from any TitanEdge

```scala
  implicit def unsafeGetProperty[P <: AnyProperty: Property.Of[this.Tpe]#is](p: P) = 
    new PropertyGetter[P](p) {
      def apply(rep: tedge.Rep): p.Raw = rep.getProperty[p.Raw](p.label)
    }

  import com.tinkerpop.blueprints.Direction
```

Getting source vertex

```scala
  implicit val sourceGetter = new GetSource {
    def apply(rep: tedge.Rep): Out = source ->> rep.getVertex(Direction.OUT)
  }
```

Getting target vertex

```scala
  implicit val targetGetter = new GetTarget {
    def apply(rep: tedge.Rep): Out = target ->> rep.getVertex(Direction.IN)
  }

}

class TitanEdge[
    S <: AnyVertex.ofType[ET#SourceType] with AnyTitanVertex, 
    ET <: AnyEdgeType, 
    T <: AnyVertex.ofType[ET#TargetType] with AnyTitanVertex
  ](val source: S, val tpe: ET, val target: T) extends AnyTitanEdge { 
    type Source = S
    type Tpe = ET 
    type Target = T
  }

object AnyTitanEdge {
  type ofType[ET <: AnyEdgeType] = AnyTitanEdge { type Tpe = ET }
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

[main/scala/ohnosequences/scarph/Edge.scala]: ../Edge.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: ../EdgeType.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: ../Expressions.scala.md
[main/scala/ohnosequences/scarph/GraphSchema.scala]: ../GraphSchema.scala.md
[main/scala/ohnosequences/scarph/ops/default.scala]: ../ops/default.scala.md
[main/scala/ohnosequences/scarph/ops/typelevel.scala]: ../ops/typelevel.scala.md
[main/scala/ohnosequences/scarph/titan/TitanEdge.scala]: TitanEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]: TitanGraphSchema.scala.md
[main/scala/ohnosequences/scarph/titan/TitanVertex.scala]: TitanVertex.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: ../Vertex.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: ../VertexType.scala.md
[test/scala/ohnosequences/scarph/sealedStuff.scala]: ../../../../../test/scala/ohnosequences/scarph/sealedStuff.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/expressions.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala.md