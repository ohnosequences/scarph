package ohnosequences.scarph.impl.titan

import shapeless._
import ohnosequences.cosas._, AnyFn._ 
import ohnosequences.cosas.ops.typeSet._
import com.thinkaurelius.titan.core._, schema._
import scala.collection.JavaConversions._

import ohnosequences.scarph._
import ohnosequences.scarph.impl.titan.predicates._

case class traversers(val graph: TitanGraph) {

  implicit def evalSimpleVertexQuery[
    V <: AnyVertexType,
    P <: AnyPredicate.On[V]
  ](implicit transform: ToBlueprintsPredicate[P]): 
      Traverser[P, Query[V], TitanVertex] =
  new Traverser[P, Query[V], TitanVertex] {
    def apply(in: In, path: Path): Out = {
      transform(in.value, graph.query)
        .vertices.asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanVertex]]
        .toList.map{ new LabeledBy[TitanVertex, P#ElementType]( _ ) }
    }
  }

  implicit def evalSimpleEdgeQuery[
    E <: AnyEdgeType,
    P <: AnyPredicate.On[E]
  ](implicit transform: ToBlueprintsPredicate[P]): 
      Traverser[P, Query[E], TitanEdge] =
  new Traverser[P, Query[E], TitanEdge] {
    def apply(in: In, path: Path): Out = {
      transform(in.value, graph.query)
        .edges.asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]
        .toList.map{ new LabeledBy[TitanEdge, P#ElementType]( _ ) }
    }
  }


  import com.tinkerpop.blueprints.Direction

  implicit def evalGetVertexProperty[P <: AnyProp { type Owner <: AnyVertexType }]:
      Traverser[TitanVertex, GetProperty[P], P#Raw] =
  new Traverser[TitanVertex, GetProperty[P], P#Raw] {
    def apply(in: In, path: Path): Out = List(path.prop( in.value.getProperty[P#Raw](path.prop.label) ))
  }

  implicit def evalGetEdgeProperty[P <: AnyProp { type Owner <: AnyEdgeType }]:
      Traverser[TitanEdge, GetProperty[P], P#Raw] =
  new Traverser[TitanEdge, GetProperty[P], P#Raw] {
    def apply(in: In, path: Path): Out = List(path.prop( in.value.getProperty[P#Raw](path.prop.label) ))
  }

  implicit def evalGetSource[E <: AnyEdgeType]:
      Traverser[TitanEdge, GetSource[E], TitanVertex] =
  new Traverser[TitanEdge, GetSource[E], TitanVertex] {
    def apply(in: In, t: Path): Out = List(new LabeledBy[TitanVertex, E#SourceType]( in.value.getVertex(Direction.OUT) ))
  }

  implicit def evalGetTarget[E <: AnyEdgeType]:
      Traverser[TitanEdge, GetTarget[E], TitanVertex] =
  new Traverser[TitanEdge, GetTarget[E], TitanVertex] {
    def apply(in: In, t: Path): Out = List(new LabeledBy[TitanVertex, E#TargetType]( in.value.getVertex(Direction.IN) ))
  }

  implicit def evalGetOutEdges[E <: AnyEdgeType]:
      Traverser[TitanVertex, GetOutEdges[E], TitanEdge] =
  new Traverser[TitanVertex, GetOutEdges[E], TitanEdge] {
    def apply(in: In, path: Path): Out = {
      val mgmt = graph.getManagementSystem
      val lbl = mgmt.getEdgeLabel(path.edge.label)
      val result = in.value
        .getTitanEdges(Direction.OUT, lbl)
        .toList.map{ new LabeledBy[TitanEdge, E]( _ ) }
      mgmt.commit
      result
    }
  }

  implicit def evalGetInEdges[E <: AnyEdgeType]:
      Traverser[TitanVertex, GetInEdges[E], TitanEdge] =
  new Traverser[TitanVertex, GetInEdges[E], TitanEdge] {
    def apply(in: In, path: Path): Out = {
      val mgmt = graph.getManagementSystem
      val lbl = mgmt.getEdgeLabel(path.edge.label)
      val result = in.value
        .getTitanEdges(Direction.IN, lbl)
        .toList.map{ new LabeledBy[TitanEdge, E]( _ ) }
      mgmt.commit
      result
    }
  }

  implicit def evalGetOutVertices[E <: AnyEdgeType]:
      Traverser[TitanVertex, GetOutVertices[E], TitanVertex] =
  new Traverser[TitanVertex, GetOutVertices[E], TitanVertex] {
    def apply(in: In, path: Path): Out = {
      in.value
        .getVertices(Direction.OUT, path.edge.label)
        .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanVertex]]
        .toList.map{ new LabeledBy[TitanVertex, E#TargetType]( _ ) }
    }
  }

  implicit def evalGetInVertices[E <: AnyEdgeType]:
      Traverser[TitanVertex, GetInVertices[E], TitanVertex] =
  new Traverser[TitanVertex, GetInVertices[E], TitanVertex] {
    def apply(in: In, path: Path): Out = {
      in.value
        .getVertices(Direction.IN, path.edge.label)
        .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanVertex]]
        .toList.map{ new LabeledBy[TitanVertex, E#SourceType]( _ ) }
    }
  }

}

