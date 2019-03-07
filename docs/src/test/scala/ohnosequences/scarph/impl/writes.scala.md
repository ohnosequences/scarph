
```scala
package ohnosequences.scarph.test

import ohnosequences.scarph.test.dummy._
import ohnosequences.scarph._
import ohnosequences.cosas.types._

case object writes {

  implicit def dummyCanAddVertices[V <: AnyVertex]:
        CanAddVertices[DummyUnit, V, DummyVertex] =
    new CanAddVertices[DummyUnit, V, DummyVertex] {

      def addVertex(graph: DummyUnit)(v: V):
        V := DummyVertex =
        v := DummyVertex

      def removeVertex(graph: DummyUnit)(v: V := DummyVertex): DummyUnit =
        graph
    }

  implicit def dummyCanAddEdges[E <: AnyEdge]:
        CanAddEdges[DummyVertex, E, DummyEdge, DummyVertex] =
    new CanAddEdges[DummyVertex, E, DummyEdge, DummyVertex] {

      def addEdge(e: E)(
        src: E#Source := DummyVertex,
        tgt: E#Target := DummyVertex
      ): E := DummyEdge =
         e := DummyEdge

      def removeEdge(r: E := DummyEdge): Unit =
        ()
    }

  implicit def dummyCanSetProperties[
    E <: AnyGraphElement,
    DE <: DummyElement,
    P <: AnyProperty,
    V <: P#Target#Val
  ]:  CanSetProperties[E, DE, P, V] =
  new CanSetProperties[E, DE, P, V] {

    def setProperty(
      e: E := DE,
      p: P,
      v: V
    ): E := DE = e

  }

}

// case object WritesExample {
//
//   val tx = twitterGraph.newTransaction
//   val tw = unit := tx
//
//   import ohnosequences.scarph.test.twitter._
//
//   tx.commit()
// }

```




[main/scala/ohnosequences/scarph/axioms.scala]: ../../../../../main/scala/ohnosequences/scarph/axioms.scala.md
[main/scala/ohnosequences/scarph/tensor.scala]: ../../../../../main/scala/ohnosequences/scarph/tensor.scala.md
[main/scala/ohnosequences/scarph/predicates.scala]: ../../../../../main/scala/ohnosequences/scarph/predicates.scala.md
[main/scala/ohnosequences/scarph/impl/biproducts.scala]: ../../../../../main/scala/ohnosequences/scarph/impl/biproducts.scala.md
[main/scala/ohnosequences/scarph/impl/tensors.scala]: ../../../../../main/scala/ohnosequences/scarph/impl/tensors.scala.md
[main/scala/ohnosequences/scarph/impl/evals.scala]: ../../../../../main/scala/ohnosequences/scarph/impl/evals.scala.md
[main/scala/ohnosequences/scarph/impl/distributivity.scala]: ../../../../../main/scala/ohnosequences/scarph/impl/distributivity.scala.md
[main/scala/ohnosequences/scarph/impl/relations.scala]: ../../../../../main/scala/ohnosequences/scarph/impl/relations.scala.md
[main/scala/ohnosequences/scarph/impl/category.scala]: ../../../../../main/scala/ohnosequences/scarph/impl/category.scala.md
[main/scala/ohnosequences/scarph/rewrites.scala]: ../../../../../main/scala/ohnosequences/scarph/rewrites.scala.md
[main/scala/ohnosequences/scarph/package.scala]: ../../../../../main/scala/ohnosequences/scarph/package.scala.md
[main/scala/ohnosequences/scarph/arities.scala]: ../../../../../main/scala/ohnosequences/scarph/arities.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: ../../../../../main/scala/ohnosequences/scarph/objects.scala.md
[main/scala/ohnosequences/scarph/writes.scala]: ../../../../../main/scala/ohnosequences/scarph/writes.scala.md
[main/scala/ohnosequences/scarph/biproduct.scala]: ../../../../../main/scala/ohnosequences/scarph/biproduct.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: ../../../../../main/scala/ohnosequences/scarph/schemas.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: ../../../../../main/scala/ohnosequences/scarph/morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/package.scala]: ../../../../../main/scala/ohnosequences/scarph/syntax/package.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: ../../../../../main/scala/ohnosequences/scarph/syntax/objects.scala.md
[main/scala/ohnosequences/scarph/syntax/writes.scala]: ../../../../../main/scala/ohnosequences/scarph/syntax/writes.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: ../../../../../main/scala/ohnosequences/scarph/syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/isomorphisms.scala]: ../../../../../main/scala/ohnosequences/scarph/isomorphisms.scala.md
[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: dummy.scala.md
[test/scala/ohnosequences/scarph/impl/writes.scala]: writes.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: dummyTest.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../TwitterSchema.scala.md
[test/scala/ohnosequences/scarph/asserts.scala]: ../asserts.scala.md
[test/scala/ohnosequences/scarph/SchemaCreation.scala]: ../SchemaCreation.scala.md
[test/scala/ohnosequences/scarph/implicitSearch.scala]: ../implicitSearch.scala.md