
```scala
package ohnosequences.scarph.ops

import  ohnosequences.scarph._

import ohnosequences.typesets._
```


The point of this is to do all ops on vertex/edge types instead of vertices and edges,
i.e. `pluto out Pet` instead of `pluto out pet` (where `pet.tpe = Pet`)

But for using this you have to provide implicits for all your vertices and edges. It doesn't
change much as you can just create things like `implicit case object pet extends TitanVertex(Pet)`.


```scala
object typelevel {

  // implicit def propertyGetterOps[T <: Singleton with AnyDenotation with CanGetProperties](rep: AnyTag.TaggedWith[T]): 
  //              ops.default.PropertyGetterOps[T] = ops.default.PropertyGetterOps[T](rep)


  implicit def vertexRepOps[V <: Singleton with AnyVertex](rep: Vertex.RepOf[V]): VertexRepOps[V] = VertexRepOps[V](rep)
  case class   VertexRepOps[V <: Singleton with AnyVertex](rep: Vertex.RepOf[V]) {
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
      ): ET#Out[getTarget.Out] = {
        val getter = mkGetter(e)
        val f = getter.edge.tpe.outFunctor
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
      ): ET#In[getSource.Out] = {
        val getter = mkGetter(e)
        val f = getter.edge.tpe.inFunctor
        f.map(getter(rep))(getSource(_))
      }
  }

  implicit def  edgeRepOps[E <: Singleton with AnyEdge](rep: Edge.RepOf[E]): 
    ops.default.EdgeRepOps[E] = ops.default.EdgeRepOps(rep)

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
[main/scala/ohnosequences/scarph/ops/default.scala]: default.scala.md
[main/scala/ohnosequences/scarph/ops/typelevel.scala]: typelevel.scala.md
[main/scala/ohnosequences/scarph/titan/TitanEdge.scala]: ../titan/TitanEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]: ../titan/TitanGraphSchema.scala.md
[main/scala/ohnosequences/scarph/titan/TitanVertex.scala]: ../titan/TitanVertex.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: ../Vertex.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: ../VertexType.scala.md
[test/scala/ohnosequences/scarph/sealedStuff.scala]: ../../../../../test/scala/ohnosequences/scarph/sealedStuff.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/expressions.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala.md