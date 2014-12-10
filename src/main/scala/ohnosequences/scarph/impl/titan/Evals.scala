package ohnosequences.scarph.impl.titan

import shapeless._
import ohnosequences.cosas._, AnyFn._ 
import ohnosequences.cosas.ops.typeSet._
import com.thinkaurelius.titan.core._, schema._
import scala.collection.JavaConversions._

import ohnosequences.scarph._, steps._, AnyEvalPath._
import ohnosequences.scarph.impl.titan.predicates._

case class evals(val graph: TitanGraph) {

  // implicit def evalSimpleVertexQuery[
  //   V <: AnyVertexType,
  //   P <: AnyPredicate.On[V]
  // ](implicit transform: ToBlueprintsPredicate[P]): 
  //     EvalPathOn[P, Query[V], TitanVertex] =
  // new EvalPathOn[P, Query[V], TitanVertex] {
  //   def apply(path: Path)(in: In): Out = {
  //     transform(in.value, graph.query)
  //       .vertices.asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanVertex]]
  //       .toList.map{ new LabeledBy[TitanVertex, V]( _ ) }
  //   }
  // }

  // implicit def evalSimpleEdgeQuery[
  //   E <: AnyEdgeType,
  //   P <: AnyPredicate.On[E]
  // ](implicit transform: ToBlueprintsPredicate[P]): 
  //     EvalPathOn[P, Query[E], TitanEdge] =
  // new EvalPathOn[P, Query[E], TitanEdge] {
  //   def apply(path: Path)(in: In): Out = {
  //     transform(in.value, graph.query)
  //       .edges.asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]
  //       .toList.map{ new LabeledBy[TitanEdge, E]( _ ) }
  //   }
  // }


  import com.tinkerpop.blueprints.Direction

  implicit def evalVertexGet[P <: AnyProp { type Owner <: AnyVertexType }]:
      EvalPathOn[TitanVertex, Get[P], P#Raw] =
  new EvalPathOn[TitanVertex, Get[P], P#Raw] {
    // def apply(path: Path)(in: In): Out = List(path.prop( in.value.getProperty[P#Raw](path.prop.label) ))
    def apply(path: Path)(in: In): Out = ExactlyOne(path.property)( in.value.getProperty[path.property.Raw](path.property.label) )
  }

  implicit def evalEdgeGet[P <: AnyProp { type Owner <: AnyEdgeType }]:
      EvalPathOn[TitanEdge, Get[P], P#Raw] =
  new EvalPathOn[TitanEdge, Get[P], P#Raw] {
    // def apply(path: Path)(in: In): Out = List(path.prop( in.value.getProperty[P#Raw](path.prop.label) ))
    def apply(path: Path)(in: In): Out = ExactlyOne(path.property)( in.value.getProperty[path.property.Raw](path.property.label) )
  }

  implicit def evalSource[E <: AnyEdgeType]:
      EvalPathOn[TitanEdge, Source[E], TitanVertex] =
  new EvalPathOn[TitanEdge, Source[E], TitanVertex] {
    // def apply(path: Path)(in: In): Out = List(new LabeledBy[TitanVertex, E#SourceType]( in.value.getVertex(Direction.OUT) ))
    def apply(path: Path)(in: In): Out = new (TitanVertex LabeledBy ExactlyOne.C[E#Source])( in.value.getVertex(Direction.OUT) )
  }

  implicit def evalTarget[E <: AnyEdgeType]:
      EvalPathOn[TitanEdge, Target[E], TitanVertex] =
  new EvalPathOn[TitanEdge, Target[E], TitanVertex] {
    // def apply(path: Path)(in: In): Out = List(new LabeledBy[TitanVertex, E#TargetType]( in.value.getVertex(Direction.IN) ))
    def apply(path: Path)(in: In): Out = new (TitanVertex LabeledBy ExactlyOne.C[E#Target])( in.value.getVertex(Direction.IN) )
  }

  // implicit def evalInE[
  //   P <: AnyPredicate { type ElementType <: AnyEdgeType }
  // ](implicit transform: ToBlueprintsPredicate[P]): 
  //     EvalPathOn[TitanVertex, InE[P], TitanEdge] =
  // new EvalPathOn[TitanVertex, InE[P], TitanEdge] {
  //   def apply(path: Path)(in: In): Out = {
  //     transform(path.pred, 
  //       in.value.query
  //         .labels(path.pred.elementType.label)
  //         .direction(Direction.IN)
  //       ).edges
  //       .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]
  //       .toList.map{ new LabeledBy[TitanEdge, P#ElementType]( _ ) }
  //   }
  // }

  // implicit def evalOutE[
  //   P <: AnyPredicate { type ElementType <: AnyEdgeType }
  // ](implicit transform: ToBlueprintsPredicate[P]): 
  //     EvalPathOn[TitanVertex, OutE[P], TitanEdge] =
  // new EvalPathOn[TitanVertex, OutE[P], TitanEdge] {
  //   def apply(path: Path)(in: In): Out = {
  //     transform(path.pred, 
  //       in.value.query
  //         .labels(path.pred.elementType.label)
  //         .direction(Direction.OUT)
  //       ).edges
  //       .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]
  //       .toList.map{ new LabeledBy[TitanEdge, P#ElementType]( _ ) }
  //   }
  // }

  // TODO: this implementation won't work in one step with vertex-query
  // implicit def evalOutVertices[E <: AnyEdgeType]:
  //     EvalPathOn[TitanVertex, OutVertices[E], TitanVertex] =
  // new EvalPathOn[TitanVertex, OutVertices[E], TitanVertex] {
  //   def apply(path: Path)(in: In): Out = {
  //     in.value
  //       .getVertices(Direction.OUT, path.edge.label)
  //       .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanVertex]]
  //       .toList.map{ new LabeledBy[TitanVertex, E#TargetType]( _ ) }
  //   }
  // }

  // implicit def evalInVertices[E <: AnyEdgeType]:
  //     EvalPathOn[TitanVertex, InVertices[E], TitanVertex] =
  // new EvalPathOn[TitanVertex, InVertices[E], TitanVertex] {
  //   def apply(path: Path)(in: In): Out = {
  //     in.value
  //       .getVertices(Direction.IN, path.edge.label)
  //       .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanVertex]]
  //       .toList.map{ new LabeledBy[TitanVertex, E#SourceType]( _ ) }
  //   }
  // }

}

