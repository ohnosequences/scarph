
```scala
package ohnosequences.scarph.titan

import ohnosequences.scarph._
import com.thinkaurelius.titan.core._

object MakeKeys {

  abstract class ArityMaker[ET <: AnyEdgeType](et: ET) {
    def apply: (LabelMaker => LabelMaker)
  }

  object ArityMaker {
    implicit def manyToManyMaker[ET <: ManyIn with ManyOut](et: ET): ArityMaker[ET] = 
      new ArityMaker[ET](et) { def apply = _.manyToMany }
    implicit def  manyToOneMaker[ET <: ManyIn with OneOut](et: ET): ArityMaker[ET] = 
      new ArityMaker[ET](et) { def apply = _.manyToOne }
    implicit def  oneToManyMaker[ET <: OneIn with ManyOut](et: ET): ArityMaker[ET] = 
      new ArityMaker[ET](et) { def apply = _.oneToMany }
    implicit def   oneToOneMaker[ET <: OneIn with OneOut](et: ET): ArityMaker[ET] = 
      new ArityMaker[ET](et) { def apply = _.oneToOne }
  }

  implicit def graphSchemaOps(g: TitanGraph): GraphSchemaOps = GraphSchemaOps(g)
  case class   GraphSchemaOps(g: TitanGraph) {

    import scala.reflect._

    // TODO: add uniqueness and indexing parameters
    def addPropertyKey[P <: AnyProperty](p: P)(implicit c: ClassTag[p.Raw]): TitanKey = {
      val clazz = c.runtimeClass.asInstanceOf[Class[p.Raw]]
      g.makeKey(p.label).dataType(clazz).single.make
    }

    // TODO: add sortKey/signature parameters
    def addEdgeLabel[E <: AnyEdge](e: E)(implicit arityMaker: e.Tpe => ArityMaker[e.Tpe]): TitanLabel = {
        val arity = arityMaker(e.tpe)
        arity.apply(g.makeLabel(e.tpe.label).directed).make
    }

  }
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
          + titan
            + [TEdge.scala][main/scala/ohnosequences/scarph/titan/TEdge.scala]
            + [TitanGraphSchema.scala][main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]
            + [TVertex.scala][main/scala/ohnosequences/scarph/titan/TVertex.scala]
          + [Vertex.scala][main/scala/ohnosequences/scarph/Vertex.scala]
          + [VertexType.scala][main/scala/ohnosequences/scarph/VertexType.scala]
  + test
    + scala
      + ohnosequences
        + scarph
          + [edges.scala][test/scala/ohnosequences/scarph/edges.scala]
          + [edgeTypes.scala][test/scala/ohnosequences/scarph/edgeTypes.scala]
          + [properties.scala][test/scala/ohnosequences/scarph/properties.scala]
          + restricted
            + [RestrictedSchemaTest.scala][test/scala/ohnosequences/scarph/restricted/RestrictedSchemaTest.scala]
            + [SimpleSchema.scala][test/scala/ohnosequences/scarph/restricted/SimpleSchema.scala]
            + [SimpleSchemaImplementation.scala][test/scala/ohnosequences/scarph/restricted/SimpleSchemaImplementation.scala]
          + titan
            + [expressions.scala][test/scala/ohnosequences/scarph/titan/expressions.scala]
            + [godsImplementation.scala][test/scala/ohnosequences/scarph/titan/godsImplementation.scala]
            + [godsSchema.scala][test/scala/ohnosequences/scarph/titan/godsSchema.scala]
            + [TitanGodsTest.scala][test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]
            + [TitanSchemaTest.scala][test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]
          + [vertexTypes.scala][test/scala/ohnosequences/scarph/vertexTypes.scala]
          + [vertices.scala][test/scala/ohnosequences/scarph/vertices.scala]

[main/scala/ohnosequences/scarph/Denotation.scala]: ../Denotation.scala.md
[main/scala/ohnosequences/scarph/Edge.scala]: ../Edge.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: ../EdgeType.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: ../Expressions.scala.md
[main/scala/ohnosequences/scarph/HasProperties.scala]: ../HasProperties.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: ../Property.scala.md
[main/scala/ohnosequences/scarph/titan/TEdge.scala]: TEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]: TitanGraphSchema.scala.md
[main/scala/ohnosequences/scarph/titan/TVertex.scala]: TVertex.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: ../Vertex.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: ../VertexType.scala.md
[test/scala/ohnosequences/scarph/edges.scala]: ../../../../../test/scala/ohnosequences/scarph/edges.scala.md
[test/scala/ohnosequences/scarph/edgeTypes.scala]: ../../../../../test/scala/ohnosequences/scarph/edgeTypes.scala.md
[test/scala/ohnosequences/scarph/properties.scala]: ../../../../../test/scala/ohnosequences/scarph/properties.scala.md
[test/scala/ohnosequences/scarph/restricted/RestrictedSchemaTest.scala]: ../../../../../test/scala/ohnosequences/scarph/restricted/RestrictedSchemaTest.scala.md
[test/scala/ohnosequences/scarph/restricted/SimpleSchema.scala]: ../../../../../test/scala/ohnosequences/scarph/restricted/SimpleSchema.scala.md
[test/scala/ohnosequences/scarph/restricted/SimpleSchemaImplementation.scala]: ../../../../../test/scala/ohnosequences/scarph/restricted/SimpleSchemaImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/expressions.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala.md
[test/scala/ohnosequences/scarph/vertexTypes.scala]: ../../../../../test/scala/ohnosequences/scarph/vertexTypes.scala.md
[test/scala/ohnosequences/scarph/vertices.scala]: ../../../../../test/scala/ohnosequences/scarph/vertices.scala.md