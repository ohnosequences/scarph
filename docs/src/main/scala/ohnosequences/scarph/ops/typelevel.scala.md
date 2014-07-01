
```scala
package ohnosequences.scarph.ops

import  ohnosequences.scarph._
```


The point of this is to do all ops on vertex/edge types instead of vertices and edges,
i.e. `pluto out Pet` instead of `pluto out pet` (where `pet.tpe = Pet`)

But for using this you have to provide implicits for all your vertices and edges. It doesn't
change much as you can just create things like `implicit case object pet extends TVertex(Pet)`.


```scala
object typelevel {

  implicit def vertexOps[V <: Singleton with AnyVertex](rep: Vertex.RepOf[V]): VertexOps[V] = VertexOps[V](rep)
  case class   VertexOps[V <: Singleton with AnyVertex](rep: Vertex.RepOf[V]) {
```

OUT edges

```scala
    def out[ET <: From[V#Tpe], E <: Singleton with AnyEdge.ofType[ET]]
      (et: ET)(implicit 
        e: E, 
        mkGetter: E => V#GetOutEdge[E]
      ): ET#Out[E#Rep] = {
        val getter = mkGetter(e)
        getter(rep)
      }
```

OUT vertices

```scala
    def outV[ET <: From[V#Tpe], E <: Singleton with AnyEdge.ofType[ET]]
      (et: ET)(implicit 
        e: E,
        mkGetter: E => V#GetOutEdge[E],
        getTarget: E#GetTarget
      ): ET#Out[E#Target#Rep] = {
        val getter = mkGetter(e)
        val f = getter.e.tpe.outFunctor
        f.map(getter(rep))(getTarget(_))
      }
```

IN edges

```scala
    def in[ET <: To[V#Tpe], E <: Singleton with AnyEdge.ofType[ET]]
      (et: ET)(implicit e: E, mkGetter: E => V#GetInEdge[E]): ET#In[E#Rep] = {
        val getter = mkGetter(e)
        getter(rep)
      }
```

IN vertices

```scala
    def inV[ET <: To[V#Tpe], E <: Singleton with AnyEdge.ofType[ET]]
      (et: ET)(implicit 
        e: E,
        mkGetter: E => V#GetInEdge[E],
        getSource: E#GetSource
      ): ET#In[E#Source#Rep] = {
        val getter = mkGetter(e)
        val f = getter.e.tpe.inFunctor
        f.map(getter(rep))(getSource(_))
      }
  }

  implicit def  edgeOps[E <: Singleton with AnyEdge](rep: Edge.RepOf[E]): 
    ops.default.EdgeOps[E] = ops.default.EdgeOps(rep)

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

[test/scala/ohnosequences/scarph/properties.scala]: ../../../../../test/scala/ohnosequences/scarph/properties.scala.md
[test/scala/ohnosequences/scarph/edges.scala]: ../../../../../test/scala/ohnosequences/scarph/edges.scala.md
[test/scala/ohnosequences/scarph/vertices.scala]: ../../../../../test/scala/ohnosequences/scarph/vertices.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/expressions.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanOtherOpsTest.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/TitanOtherOpsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/vertexTypes.scala]: ../../../../../test/scala/ohnosequences/scarph/vertexTypes.scala.md
[test/scala/ohnosequences/scarph/edgeTypes.scala]: ../../../../../test/scala/ohnosequences/scarph/edgeTypes.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: ../Expressions.scala.md
[main/scala/ohnosequences/scarph/ops/typelevel.scala]: typelevel.scala.md
[main/scala/ohnosequences/scarph/ops/default.scala]: default.scala.md
[main/scala/ohnosequences/scarph/Denotation.scala]: ../Denotation.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: ../EdgeType.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: ../VertexType.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: ../Vertex.scala.md
[main/scala/ohnosequences/scarph/Edge.scala]: ../Edge.scala.md
[main/scala/ohnosequences/scarph/titan/TitanImplementation.scala]: ../titan/TitanImplementation.scala.md
[main/scala/ohnosequences/scarph/titan/TEdge.scala]: ../titan/TEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TVertex.scala]: ../titan/TVertex.scala.md
[main/scala/ohnosequences/scarph/titan/TSchema.scala]: ../titan/TSchema.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: ../Property.scala.md
[main/scala/ohnosequences/scarph/GraphSchema.scala]: ../GraphSchema.scala.md