
```scala
package ohnosequences.scarph.titan

import ohnosequences.scarph._
import com.thinkaurelius.titan.core._
import ohnosequences.pointless._
import shapeless._, poly._
import scala.reflect._

object TitanGraphSchema {

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

  implicit def titanGraphSchemaOps(g: TitanGraph): TitanGraphSchemaOps = TitanGraphSchemaOps(g)
  case class   TitanGraphSchemaOps(g: TitanGraph) {

    // TODO: add uniqueness and indexing parameters
    def addPropertyKey[P <: AnyProperty](p: P)(implicit c: ClassTag[P#Raw]) = {
      val clazz = c.runtimeClass.asInstanceOf[Class[p.Raw]]
      // FIXME: WARNING! This try-catch is needed only because in the gods-graph test some keys are repeated (remove this after changing the test)
      try {g.makeKey(p.label).dataType(clazz).single.make}
      catch { case _: java.lang.IllegalArgumentException => {} }
    }

    // TODO: add sortKey/signature parameters
    def addEdgeLabel[ET <: AnyEdgeType](et: ET)(implicit arityMaker: ET => ArityMaker[ET]) = {
        val arity = arityMaker(et)
        // FIXME: WARNING! This try-catch is needed only because in the gods-graph test some keys are repeated (remove this after changing the test)
        try {arity.apply(g.makeLabel(et.label).directed).make}
        catch { case _: java.lang.IllegalArgumentException => {} }
    }

    def createSchema[S <: AnyGraphSchema](s: S)(implicit
        mkPropertyKeys: MkPropertyKeys[s.Properties],
        mkEdgeLabels: MkEdgeLabels[s.EdgeTypes]
      ): TitanGraph = {
        mkPropertyKeys(g, s.properties)
        mkEdgeLabels(g, s.edgeTypes)
        g
      }
  }
}

trait MkPropertyKeys[Ps <: TypeSet] {
  def apply(g: TitanGraph, ps: Ps): TitanGraph
}

object MkPropertyKeys {
  import TitanGraphSchema._

  def apply[Ps <: TypeSet](implicit mk: MkPropertyKeys[Ps]): MkPropertyKeys[Ps] = mk

  implicit val emptyMkKeys: MkPropertyKeys[?] =
    new MkPropertyKeys[?] {
      def apply(g: TitanGraph, ps: ?) = g
    }

  implicit def consMkKeys[H <: AnyProperty, T <: TypeSet]
    (implicit 
      c: ClassTag[H#Raw], 
      t: MkPropertyKeys[T]
    ):  MkPropertyKeys[H :~: T] =
    new MkPropertyKeys[H :~: T] {
      def apply(g: TitanGraph, ps: H :~: T) = {
        g.addPropertyKey(ps.head)
        t(g, ps.tail)
        g
      }
    }
}

trait MkEdgeLabels[Es <: TypeSet] {
  def apply(g: TitanGraph, es: Es): TitanGraph
}

object MkEdgeLabels {
  import TitanGraphSchema._

  def apply[Es <: TypeSet](implicit mk: MkEdgeLabels[Es]): MkEdgeLabels[Es] = mk

  implicit val emptyMkKeys: MkEdgeLabels[?] =
    new MkEdgeLabels[?] {
      def apply(g: TitanGraph, es: ?) = g
    }

  implicit def consMkKeys[H <: AnyEdgeType, T <: TypeSet]
    (implicit 
      a: H => ArityMaker[H], 
      t: MkEdgeLabels[T]
    ):  MkEdgeLabels[H :~: T] =
    new MkEdgeLabels[H :~: T] {
      def apply(g: TitanGraph, es: H :~: T) = {
        g.addEdgeLabel(es.head)
        t(g, es.tail)
        g
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
[main/scala/ohnosequences/scarph/ops/default.scala]: ../ops/default.scala.md
[main/scala/ohnosequences/scarph/ops/typelevel.scala]: ../ops/typelevel.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: ../Property.scala.md
[main/scala/ohnosequences/scarph/titan/TitanEdge.scala]: TitanEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]: TitanGraphSchema.scala.md
[main/scala/ohnosequences/scarph/titan/TitanVertex.scala]: TitanVertex.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: ../Vertex.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: ../VertexType.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/expressions.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: ../../../../../test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala.md