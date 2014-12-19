package ohnosequences.scarph.impl.titan

import shapeless._

import com.thinkaurelius.titan.core._, schema._
import scala.collection.JavaConversions._

import ohnosequences.cosas._, fns._, types._
import ohnosequences.cosas.ops.typeSets._

import ohnosequences.scarph._, steps._, AnyEvalPath._
import ohnosequences.scarph.impl.titan.predicates._

import scalaz.{ NonEmptyList => NEList }
// TODO: use scalaz.EphemeralStream instead of Stream

case class evals(val graph: TitanGraph) {

  implicit def flattenSS[X]: 
        FlattenVals[Stream, Stream, X] with Out[Stream[X]] =
    new FlattenVals[Stream, Stream, X] with Out[Stream[X]] { def apply(in: In1): Out = in.flatten }
  implicit def flattenSO[X]: 
        FlattenVals[Stream, Option, X] with Out[Stream[X]] =
    new FlattenVals[Stream, Option, X] with Out[Stream[X]] { def apply(in: In1): Out = in.flatten }
  implicit def flattenOS[X]: 
        FlattenVals[Option, Stream, X] with Out[Stream[X]] =
    new FlattenVals[Option, Stream, X] with Out[Stream[X]] { def apply(in: In1): Out = in.getOrElse(Stream[X]()) }
  implicit def flattenOO[X]: 
        FlattenVals[Option, Option, X] with Out[Option[X]] =
    new FlattenVals[Option, Option, X] with Out[Option[X]] { def apply(in: In1): Out = in.flatten }
  implicit def flattenNN[X]: 
        FlattenVals[NEList, NEList, X] with Out[NEList[X]] =
    new FlattenVals[NEList, NEList, X] with Out[NEList[X]] { def apply(in: In1): Out = in.flatMap(s => s) }
  implicit def flattenNS[X]: 
        FlattenVals[NEList, Stream, X] with Out[Stream[X]] =
    new FlattenVals[NEList, Stream, X] with Out[Stream[X]] { def apply(in: In1): Out = in.stream.flatten }
  implicit def flattenSN[X]: 
        FlattenVals[Stream, NEList, X] with Out[Stream[X]] =
    new FlattenVals[Stream, NEList, X] with Out[Stream[X]] { def apply(in: In1): Out = in.flatMap(s => s.stream) }
  implicit def flattenNO[X]: 
        FlattenVals[NEList, Option, X] with Out[Stream[X]] =
    new FlattenVals[NEList, Option, X] with Out[Stream[X]] { def apply(in: In1): Out = in.stream.flatten }
  implicit def flattenON[X]: 
        FlattenVals[Option, NEList, X] with Out[Stream[X]] =
    new FlattenVals[Option, NEList, X] with Out[Stream[X]] { def apply(in: In1): Out = in.map(_.stream).getOrElse(Stream[X]()) }

  implicit def evalVertexQuery[
    V <: AnyVertexType,
    P <: AnyPredicate.On[V]
  ](implicit transform: ToBlueprintsPredicate[P]): 
      EvalPathOn[P, Query[V], Stream[TitanVertex]] =
  new EvalPathOn[P, Query[V], Stream[TitanVertex]] {
    def apply(path: Path)(in: In): Out = {
      ManyOrNone(path.elem) := (
        transform(in.value, graph.query).vertices
          .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanVertex]].toStream
      )
    }
  }

  implicit def evalEdgeQuery[
    E <: AnyEdgeType,
    P <: AnyPredicate.On[E]
  ](implicit transform: ToBlueprintsPredicate[P]): 
      EvalPathOn[P, Query[E], Stream[TitanEdge]] =
  new EvalPathOn[P, Query[E], Stream[TitanEdge]] {
    def apply(path: Path)(in: In): Out = {
      ManyOrNone(path.elem) := (
        transform(in.value, graph.query).edges
          .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]].toStream
      )
    }
  }


  import com.tinkerpop.blueprints.Direction

  implicit def evalVertexGet[P <: AnyGraphProperty { type Owner <: AnyVertexType }]:
      EvalPathOn[TitanVertex, Get[P], P#Raw] =
  new EvalPathOn[TitanVertex, Get[P], P#Raw] {
    def apply(path: Path)(in: In): Out = ExactlyOne(path.property) := ( in.value.getProperty[path.property.Raw](path.property.label) )
  }

  implicit def evalEdgeGet[P <: AnyGraphProperty { type Owner <: AnyEdgeType }]:
      EvalPathOn[TitanEdge, Get[P], P#Raw] =
  new EvalPathOn[TitanEdge, Get[P], P#Raw] {
    def apply(path: Path)(in: In): Out = ExactlyOne(path.property) := ( in.value.getProperty[path.property.Raw](path.property.label) )
  }

  implicit def evalSource[E <: AnyEdgeType]:
      EvalPathOn[TitanEdge, Source[E], TitanVertex] =
  new EvalPathOn[TitanEdge, Source[E], TitanVertex] {
    def apply(path: Path)(in: In): Out = ExactlyOne((path.edge: E).inT) := ( in.value.getVertex(Direction.OUT) )
  }

  implicit def evalTarget[E <: AnyEdgeType]:
      EvalPathOn[TitanEdge, Target[E], TitanVertex] =
  new EvalPathOn[TitanEdge, Target[E], TitanVertex] {
    def apply(path: Path)(in: In): Out = ExactlyOne((path.edge: E).outT) := ( in.value.getVertex(Direction.IN) )
  }

  implicit def evalInE[
    P <: AnyPredicate { type ElementType <: AnyEdgeType }
  ](implicit transform: ToBlueprintsPredicate[P]): 
      EvalPathOn[TitanVertex, InE[P], Stream[TitanEdge]] =
  new EvalPathOn[TitanVertex, InE[P], Stream[TitanEdge]] {
    def apply(path: Path)(in: In): Out = {
      type E = P#ElementType
      val elem: E = path.pred.elementType
      (elem.inC: E#InC)(elem: E) := (
        transform(path.pred, 
          in.value.query
            .labels(path.pred.elementType.label)
            .direction(Direction.IN)
          ).edges
          .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]].toStream
      )
    }
  }

  implicit def evalOutE[
    P <: AnyPredicate { type ElementType <: AnyEdgeType }
  ](implicit transform: ToBlueprintsPredicate[P]): 
      EvalPathOn[TitanVertex, OutE[P], Stream[TitanEdge]] =
  new EvalPathOn[TitanVertex, OutE[P], Stream[TitanEdge]] {
    def apply(path: Path)(in: In): Out = {
      type E = P#ElementType
      val elem: E = path.pred.elementType
      (elem.outC: E#OutC)(elem: E) := (
        transform(path.pred, 
          in.value.query
            .labels(path.pred.elementType.label)
            .direction(Direction.OUT)
          ).edges
          .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]].toStream
      )
    }
  }

  // TODO: this implementation won't work in one step with vertex-query
  // implicit def evalOutVertices[E <: AnyEdgeType]:
  //     EvalPathOn[TitanVertex, OutVertices[E], TitanVertex] =
  // new EvalPathOn[TitanVertex, OutVertices[E], TitanVertex] {
  //   def apply(path: Path)(in: In): Out = {
  //     in.value
  //       .getVertices(Direction.OUT, path.edge.label)
  //       .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanVertex]]
  //       .toList.map{ new Denotes[TitanVertex, E#TargetType]( _ ) }
  //   }
  // }

  // implicit def evalInVertices[E <: AnyEdgeType]:
  //     EvalPathOn[TitanVertex, InVertices[E], TitanVertex] =
  // new EvalPathOn[TitanVertex, InVertices[E], TitanVertex] {
  //   def apply(path: Path)(in: In): Out = {
  //     in.value
  //       .getVertices(Direction.IN, path.edge.label)
  //       .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanVertex]]
  //       .toList.map{ new Denotes[TitanVertex, E#SourceType]( _ ) }
  //   }
  // }

}

