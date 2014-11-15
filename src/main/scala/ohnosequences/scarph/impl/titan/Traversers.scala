package ohnosequences.scarph.impl.titan

import shapeless._
import ohnosequences.cosas._, AnyFn._ 
import ohnosequences.cosas.ops.typeSet._
import com.thinkaurelius.titan.core._, schema._
import scala.collection.JavaConversions._

import ohnosequences.scarph._, steps._
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

  implicit def evalVertexGet[P <: AnyProp { type Owner <: AnyVertexType }]:
      Traverser[TitanVertex, Get[P], P#Raw] =
  new Traverser[TitanVertex, Get[P], P#Raw] {
    def apply(in: In, path: Path): Out = List(path.prop( in.value.getProperty[P#Raw](path.prop.label) ))
  }

  implicit def evalEdgeGet[P <: AnyProp { type Owner <: AnyEdgeType }]:
      Traverser[TitanEdge, Get[P], P#Raw] =
  new Traverser[TitanEdge, Get[P], P#Raw] {
    def apply(in: In, path: Path): Out = List(path.prop( in.value.getProperty[P#Raw](path.prop.label) ))
  }

  implicit def evalSource[E <: AnyEdgeType]:
      Traverser[TitanEdge, Source[E], TitanVertex] =
  new Traverser[TitanEdge, Source[E], TitanVertex] {
    def apply(in: In, t: Path): Out = List(new LabeledBy[TitanVertex, E#SourceType]( in.value.getVertex(Direction.OUT) ))
  }

  implicit def evalTarget[E <: AnyEdgeType]:
      Traverser[TitanEdge, Target[E], TitanVertex] =
  new Traverser[TitanEdge, Target[E], TitanVertex] {
    def apply(in: In, t: Path): Out = List(new LabeledBy[TitanVertex, E#TargetType]( in.value.getVertex(Direction.IN) ))
  }

  implicit def evalInE[
    P <: AnyPredicate { type ElementType <: AnyEdgeType }
  ](implicit transform: ToBlueprintsPredicate[P]): 
      Traverser[TitanVertex, InE[P], TitanEdge] =
  new Traverser[TitanVertex, InE[P], TitanEdge] {
    def apply(in: In, path: Path): Out = {
      transform(path.pred, 
        in.value.query
          .labels(path.pred.elementType.label)
          .direction(Direction.IN)
        ).edges
        .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]
        .toList.map{ new LabeledBy[TitanEdge, P#ElementType]( _ ) }
    }
  }

  implicit def evalOutE[
    P <: AnyPredicate { type ElementType <: AnyEdgeType }
  ](implicit transform: ToBlueprintsPredicate[P]): 
      Traverser[TitanVertex, OutE[P], TitanEdge] =
  new Traverser[TitanVertex, OutE[P], TitanEdge] {
    def apply(in: In, path: Path): Out = {
      transform(path.pred, 
        in.value.query
          .labels(path.pred.elementType.label)
          .direction(Direction.OUT)
        ).edges
        .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]
        .toList.map{ new LabeledBy[TitanEdge, P#ElementType]( _ ) }
    }
  }

  // TODO: this implementation won't work in one step with vertex-query
  // implicit def evalOutVertices[E <: AnyEdgeType]:
  //     Traverser[TitanVertex, OutVertices[E], TitanVertex] =
  // new Traverser[TitanVertex, OutVertices[E], TitanVertex] {
  //   def apply(in: In, path: Path): Out = {
  //     in.value
  //       .getVertices(Direction.OUT, path.edge.label)
  //       .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanVertex]]
  //       .toList.map{ new LabeledBy[TitanVertex, E#TargetType]( _ ) }
  //   }
  // }

  // implicit def evalInVertices[E <: AnyEdgeType]:
  //     Traverser[TitanVertex, InVertices[E], TitanVertex] =
  // new Traverser[TitanVertex, InVertices[E], TitanVertex] {
  //   def apply(in: In, path: Path): Out = {
  //     in.value
  //       .getVertices(Direction.IN, path.edge.label)
  //       .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanVertex]]
  //       .toList.map{ new LabeledBy[TitanVertex, E#SourceType]( _ ) }
  //   }
  // }

}

