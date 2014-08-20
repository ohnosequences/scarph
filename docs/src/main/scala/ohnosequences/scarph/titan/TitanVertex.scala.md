
```scala
package ohnosequences.scarph.titan

import ohnosequences.scarph._
import ohnosequences.typesets._, AnyTag._

trait AnyTitanVertex extends AnyVertex { tvertex =>

  final type Raw = com.thinkaurelius.titan.core.TitanVertex
```

Getting a property from any TitanVertex

```scala
  implicit def unsafeGetProperty[P <: Singleton with AnyProperty: Property.Of[this.Tpe]#is](p: P) = 
    new PropertyGetter[P](p) {
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
  implicit def unsafeGetOneOutEdge [
    E <: Singleton with AnyTitanEdge { type Tpe <: From[tvertex.Tpe] with OneOut }
  ]
  (e: E): GetOutEdge[E] = new GetOutEdge[E](e) {

      def apply(rep: tvertex.Rep): e.tpe.Out[TaggedWith[E]] = {
        
        val it = rep.getEdges( Direction.OUT, e.tpe.label )
          .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]

        it.headOption map { (e:E) ->> _ }
      }
    }

  implicit def unsafeGetManyOutEdge [
    OE <: Singleton with AnyTitanEdge { type Tpe <: From[tvertex.Tpe] with ManyOut }
  ]
  (edge: OE): GetOutEdge[OE] = new GetOutEdge[OE](edge) {

      def apply(rep: tvertex.Rep): edge.tpe.Out[TaggedWith[OE]] = {

        val it = rep.getEdges( Direction.OUT, edge.tpe.label )
          .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]

        it.toList map { (edge: OE) ->> _ }
      }
    }
```

IN

```scala
  implicit def unsafeGetOneInEdge [
    IE <: Singleton with AnyTitanEdge { type Tpe <: To[tvertex.Tpe] with OneIn }
  ]
  (edge: IE): GetInEdge[IE] = new GetInEdge[IE](edge) {

      def apply(rep: tvertex.Rep): edge.tpe.In[TaggedWith[IE]] = {

        val it = rep.getEdges(Direction.IN, edge.tpe.label)
          .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]

        it.headOption map { (edge: IE) ->> _ }
      }
    }

  implicit def unsafeGetManyInEdge [
    IE <: Singleton with AnyTitanEdge { type Tpe <: To[tvertex.Tpe] with ManyIn }
  ]
  (edge: IE): GetInEdge[IE] = new GetInEdge[IE](edge) {
        
      def apply(rep: tvertex.Rep): edge.tpe.In[TaggedWith[IE]] = {

        val it = rep.getEdges( Direction.IN, edge.tpe.label )
          .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]

        it.toList map { (edge: IE) ->> _ }
      }
    }

}

class TitanVertex[VT <: AnyVertexType](val tpe: VT) 
  extends AnyTitanVertex { type Tpe = VT }

object AnyTitanVertex {
  type ofType[VT <: AnyVertexType] = AnyTitanVertex { type Tpe = VT }
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