
```scala
package ohnosequences.scarph.titan

import ohnosequences.scarph._

trait AnyTVertex extends AnyVertex { tvertex =>

  final type Raw = com.thinkaurelius.titan.core.TitanVertex
```

Getting a property from any TitanVertex

```scala
  implicit def unsafeGetProperty[P <: AnyProperty: Property.Of[this.Tpe]#is](p: P) = 
    new GetProperty[P](p) {
      def apply(rep: Rep): p.Raw = rep.getProperty[p.Raw](p.label)
    }

  // TODO: provide ReadFrom for %:

```

Retrieving edges

```scala
  import com.tinkerpop.blueprints.Direction
  import scala.collection.JavaConversions._

  // TODO: when we get all edges with the given label, they can come from vertices with the wrong type

```

OUT

```scala
  implicit def unsafeRetrieveOneOutEdge[
    E <: Singleton with AnyTEdge { type Tpe <: From[tvertex.Tpe] with OneOut }
  ](e: E): RetrieveOutEdge[E] = new RetrieveOutEdge[E](e) {

      def apply(rep: tvertex.Rep): e.tpe.Out[e.Rep] = {
        
        val it = rep.getEdges(Direction.OUT, e.tpe.label).asInstanceOf[java.lang.Iterable[e.Rep]]
        it.headOption: Option[e.Rep]
      }
    }

  implicit def unsafeRetrieveManyOutEdge[
    E <: Singleton with AnyTEdge { type Tpe <: From[tvertex.Tpe] with ManyOut }
  ](e: E): RetrieveOutEdge[E] = new RetrieveOutEdge[E](e) {

      def apply(rep: tvertex.Rep): e.tpe.Out[e.Rep] = {
        val it = rep.getEdges(Direction.OUT, e.tpe.label).asInstanceOf[java.lang.Iterable[e.Rep]]
        it.toList: List[e.Rep]
      }
    }
```

IN

```scala
  implicit def unsafeRetrieveOneInEdge[
    E <: Singleton with AnyTEdge { type Tpe <: To[tvertex.Tpe] with OneIn }
  ](e: E): RetrieveInEdge[E] = new RetrieveInEdge[E](e) {

      def apply(rep: tvertex.Rep): e.tpe.In[e.Rep] = {
        val it = rep.getEdges(Direction.IN, e.tpe.label).asInstanceOf[java.lang.Iterable[e.Rep]]
        it.headOption: Option[e.Rep]
      }
    }

  implicit def unsafeRetrieveManyInEdge[
    E <: Singleton with AnyTEdge { type Tpe <: To[tvertex.Tpe] with ManyIn }
  ](e: E): RetrieveInEdge[E] = new RetrieveInEdge[E](e) {

      def apply(rep: tvertex.Rep): e.tpe.In[e.Rep] = {
        val it = rep.getEdges(Direction.IN, e.tpe.label).asInstanceOf[java.lang.Iterable[e.Rep]]
        it.toList: List[e.Rep]
      }
    }

}

class TVertex[VT <: AnyVertexType](val tpe: VT) 
  extends AnyTVertex { type Tpe = VT }

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
          + [Property.scala][main/scala/ohnosequences/scarph/Property.scala]
          + titan
            + [TEdge.scala][main/scala/ohnosequences/scarph/titan/TEdge.scala]
            + [TSchema.scala][main/scala/ohnosequences/scarph/titan/TSchema.scala]
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
[main/scala/ohnosequences/scarph/GraphSchema.scala]: ../GraphSchema.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: ../Property.scala.md
[main/scala/ohnosequences/scarph/titan/TEdge.scala]: TEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TSchema.scala]: TSchema.scala.md
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