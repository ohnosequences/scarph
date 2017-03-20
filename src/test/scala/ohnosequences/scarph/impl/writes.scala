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
    }

  implicit def dummyCanAddEdges[E <: AnyEdge]:
        CanAddEdges[DummyVertex, E, DummyEdge, DummyVertex] =
    new CanAddEdges[DummyVertex, E, DummyEdge, DummyVertex] {

      def addEdge(e: E)(
        src: E#Source := DummyVertex,
        tgt: E#Target := DummyVertex
      ): E := DummyEdge =
         e := DummyEdge
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
