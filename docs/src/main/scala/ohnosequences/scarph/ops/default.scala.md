
```scala
package ohnosequences.scarph.ops

import  ohnosequences.scarph._

object default {
```

Common ops for getting properties

```scala
  implicit def propertyGetterOps[T <: Singleton with AnyDenotation with CanGetProperties](rep: AnyTag.TaggedWith[T]): 
               PropertyGetterOps[T] = PropertyGetterOps[T](rep)
  case class   PropertyGetterOps[T <: Singleton with AnyDenotation with CanGetProperties](rep: AnyTag.TaggedWith[T]) {

    def get[P <: Singleton with AnyProperty: Property.Of[T#Tpe]#is](p: P)
      (implicit mkGetter: p.type => T#PropertyGetter[p.type]): p.Raw = 
        mkGetter(p).apply(rep)
  }
```

Vertex representation ops

```scala
  implicit def vertexRepOps[V <: Singleton with AnyVertex](rep: Vertex.RepOf[V]): VertexRepOps[V] = VertexRepOps[V](rep)
  case class   VertexRepOps[V <: Singleton with AnyVertex](rep: Vertex.RepOf[V]) {
```

OUT edges

```scala
    def out[E <: Singleton with AnyEdge { type Tpe <: From[V#Tpe] }]
      (e: E)(implicit mkGetter: E => V#GetOutEdge[E]): E#Tpe#Out[E#Rep] = {
        val getter = mkGetter(e)
        getter(rep)
      }
```

OUT vertices

```scala
    def outV[E <: Singleton with AnyEdge { type Tpe <: From[V#Tpe] }]
      (e: E)(implicit mkGetter: E => V#GetOutEdge[E],
                      getTarget: E#GetTarget): E#Tpe#Out[getTarget.Out] = {
        val getter = mkGetter(e)
        val f = getter.e.tpe.outFunctor
        f.map(getter(rep))(getTarget(_))
      }
```

IN edges

```scala
    def in[E <: Singleton with AnyEdge { type Tpe <: To[V#Tpe] }]
      (e: E)(implicit mkGetter: E => V#GetInEdge[E]): E#Tpe#In[E#Rep] = {
        val getter = mkGetter(e)
        getter(rep)
      }
```

IN vertices

```scala
    def inV[E <: Singleton with AnyEdge { type Tpe <: To[V#Tpe] }]
      (e: E)(implicit mkGetter: E => V#GetInEdge[E],
                      getSource: E#GetSource): E#Tpe#In[getSource.Out] = {
        val getter = mkGetter(e)
        val f = getter.e.tpe.inFunctor
        f.map(getter(rep))(getSource(_))
      }
  }
```

Edge representation ops

```scala
  implicit def edgeRepOps[E <: Singleton with AnyEdge](rep: Edge.RepOf[E]): EdgeRepOps[E] = EdgeRepOps(rep)
  case class   EdgeRepOps[E <: Singleton with AnyEdge](rep: Edge.RepOf[E]) {

    def source[S <: Singleton with AnyVertex.ofType[E#Tpe#SourceType]]
      (implicit getter: E#GetSource) = getter(rep)

    def target[T <: Singleton with AnyVertex.ofType[E#Tpe#TargetType]]
      (implicit getter: E#GetTarget) = getter(rep)

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
          + [GraphSchema.scala][main/scala/ohnosequences/scarph/GraphSchema.scala]
          + ops
            + [default.scala][main/scala/ohnosequences/scarph/ops/default.scala]
            + [typelevel.scala][main/scala/ohnosequences/scarph/ops/typelevel.scala]
          + [Property.scala][main/scala/ohnosequences/scarph/Property.scala]
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
          + titan
            + [expressions.scala][test/scala/ohnosequences/scarph/titan/expressions.scala]
            + [godsImplementation.scala][test/scala/ohnosequences/scarph/titan/godsImplementation.scala]
            + [godsSchema.scala][test/scala/ohnosequences/scarph/titan/godsSchema.scala]
            + [TitanGodsTest.scala][test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]
            + [TitanSchemaTest.scala][test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]

[main/scala/ohnosequences/scarph/Denotation.scala]: ../Denotation.scala.md
[main/scala/ohnosequences/scarph/Edge.scala]: ../Edge.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: ../EdgeType.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: ../Expressions.scala.md
[main/scala/ohnosequences/scarph/GraphSchema.scala]: ../GraphSchema.scala.md
[main/scala/ohnosequences/scarph/ops/default.scala]: default.scala.md
[main/scala/ohnosequences/scarph/ops/typelevel.scala]: typelevel.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: ../Property.scala.md
[main/scala/ohnosequences/scarph/titan/TitanEdge.scala]: ../titan/TitanEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]: ../titan/TitanGraphSchema.scala.md
[main/scala/ohnosequences/scarph/titan/TitanVertex.scala]: ../titan/TitanVertex.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: ../Vertex.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: ../VertexType.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/expressions.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala.md