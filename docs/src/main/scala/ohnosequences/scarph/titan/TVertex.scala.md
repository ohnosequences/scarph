
```scala
package ohnosequences.scarph.titan

import ohnosequences.scarph._

trait AnyTVertex extends AnyVertex { tvertex =>

  type Rep = com.thinkaurelius.titan.core.TitanVertex
```

Reading any property from a TitanVertex

```scala
  import AnyProperty._
  implicit def readFromTitanVertex(vr: TaggedRep) = 
    new ReadFrom[TaggedRep](vr) {
      def apply[P <: AnyProperty](p: P): p.Rep = vr.getProperty[p.Rep](p.label)
    }
```

Getting a property from any TitanVertex

```scala
  import SmthHasProperty._
  implicit def unsafeGetProperty[P <: AnyProperty: PropertyOf[this.Tpe]#is](p: P) = 
    new GetProperty[P](p) {
      def apply(rep: TaggedRep): p.Rep = rep.getProperty[p.Rep](p.label)
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
    E <: Singleton with AnyEdge { type Tpe <: From[tvertex.Tpe] with OneOut }
  ](e: E): RetrieveOutEdge[E] = new RetrieveOutEdge[E](e) {

      def apply(rep: tvertex.TaggedRep): e.tpe.Out[e.TaggedRep] = {
        
        val it = rep.getEdges(Direction.OUT, e.tpe.label).asInstanceOf[java.lang.Iterable[e.TaggedRep]]
        it.headOption: Option[e.TaggedRep]
      }
    }

  implicit def unsafeRetrieveManyOutEdge[
    E <: Singleton with AnyEdge { type Tpe <: From[tvertex.Tpe] with ManyOut }
  ](e: E): RetrieveOutEdge[E] = new RetrieveOutEdge[E](e) {

      def apply(rep: tvertex.TaggedRep): e.tpe.Out[e.TaggedRep] = {
        val it = rep.getEdges(Direction.OUT, e.tpe.label).asInstanceOf[java.lang.Iterable[e.TaggedRep]]
        it.toList: List[e.TaggedRep]
      }
    }
```

IN

```scala
  implicit def unsafeRetrieveOneInEdge[
    E <: Singleton with AnyEdge { type Tpe <: To[tvertex.Tpe] with OneIn }
  ](e: E): RetrieveInEdge[E] = new RetrieveInEdge[E](e) {

      def apply(rep: tvertex.TaggedRep): e.tpe.In[e.TaggedRep] = {
        val it = rep.getEdges(Direction.IN, e.tpe.label).asInstanceOf[java.lang.Iterable[e.TaggedRep]]
        it.headOption: Option[e.TaggedRep]
      }
    }

  implicit def unsafeRetrieveManyInEdge[
    E <: Singleton with AnyEdge { type Tpe <: To[tvertex.Tpe] with ManyIn }
  ](e: E): RetrieveInEdge[E] = new RetrieveInEdge[E](e) {

      def apply(rep: tvertex.TaggedRep): e.tpe.In[e.TaggedRep] = {
        val it = rep.getEdges(Direction.IN, e.tpe.label).asInstanceOf[java.lang.Iterable[e.TaggedRep]]
        it.toList: List[e.TaggedRep]
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
          + titan
            + [TEdge.scala][main/scala/ohnosequences/scarph/titan/TEdge.scala]
            + [TitanGraphSchema.scala][main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]
            + [TVertex.scala][main/scala/ohnosequences/scarph/titan/TVertex.scala]
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

[main/scala/ohnosequences/scarph/titan/TEdge.scala]: TEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]: TitanGraphSchema.scala.md
[main/scala/ohnosequences/scarph/titan/TVertex.scala]: TVertex.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/expressions.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala.md