package ohnosequences.scarph.impl.titan

case class evals(val graph: com.thinkaurelius.titan.core.TitanGraph) {

  import shapeless._

  import com.thinkaurelius.titan.core._, schema._
  import scala.collection.JavaConversions._

  import ohnosequences.cosas._, fns._, types._
  // import cosas.ops.typeSets._

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.steps._, s.paths._, s.containers._, s.combinators._, s.evals._, s.predicates._
  import s.impl.titan.predicates._

  import scalaz.{ NonEmptyList => NEList }
  import java.lang.{ Iterable => JIterable }


  // TODO: if it's possible to avoid Id, why not?
  implicit def containerId[X]:
        ValueContainer[ExactlyOne, JIterable[X]] with Out[X] =
    new ValueContainer[ExactlyOne, JIterable[X]] with Out[X] { def apply(in: In1): Out = in.head }

  implicit def containerOption[X]:
        ValueContainer[OneOrNone, JIterable[X]] with Out[Option[X]] =
    new ValueContainer[OneOrNone, JIterable[X]] with Out[Option[X]] { def apply(in: In1): Out = in.headOption }

  // TODO: use scalaz.EphemeralStream instead of Stream
  implicit def containerStream[X]:
        ValueContainer[ManyOrNone, JIterable[X]] with Out[Stream[X]] =
    new ValueContainer[ManyOrNone, JIterable[X]] with Out[Stream[X]] { def apply(in: In1): Out = in.toStream }

  implicit def containerNEList[X]:
        ValueContainer[AtLeastOne, JIterable[X]] with Out[NEList[X]] =
    new ValueContainer[AtLeastOne, JIterable[X]] with Out[NEList[X]] { 
      def apply(in: In1): Out = {
        val l = in.toList
        NEList.nel(l.head, l.tail) 
      }
    }

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
    V <: AnyVertex,
    P <: AnyPredicate.On[V]
  ](implicit transform: ToBlueprintsPredicate[P]): 
      EvalPathOn[P, Query[V], Stream[TitanVertex]] =
  new EvalPathOn[P, Query[V], Stream[TitanVertex]] {
    def apply(path: Path)(in: In): Out = {
      ManyOrNone.of(path.elem) := (
        transform(in.value, graph.query).vertices
          .asInstanceOf[JIterable[com.thinkaurelius.titan.core.TitanVertex]].toStream
      )
    }
  }

  implicit def evalEdgeQuery[
    E <: AnyEdge,
    P <: AnyPredicate.On[E]
  ](implicit transform: ToBlueprintsPredicate[P]): 
      EvalPathOn[P, Query[E], Stream[TitanEdge]] =
  new EvalPathOn[P, Query[E], Stream[TitanEdge]] {
    def apply(path: Path)(in: In): Out = {
      ManyOrNone.of(path.elem) := (
        transform(in.value, graph.query).edges
          .asInstanceOf[JIterable[com.thinkaurelius.titan.core.TitanEdge]].toStream
      )
    }
  }


  import com.tinkerpop.blueprints.Direction

  implicit def evalVertexGet[P <: AnyGraphProperty { type Owner <: AnyVertex }]:
      EvalPathOn[TitanVertex, Get[P], P#Raw] =
  new EvalPathOn[TitanVertex, Get[P], P#Raw] {
    def apply(path: Path)(in: In): Out = ExactlyOne.of(path.property) := ( in.value.getProperty[path.property.Raw](path.property.label) )
  }

  implicit def evalEdgeGet[P <: AnyGraphProperty { type Owner <: AnyEdge }]:
      EvalPathOn[TitanEdge, Get[P], P#Raw] =
  new EvalPathOn[TitanEdge, Get[P], P#Raw] {
    def apply(path: Path)(in: In): Out = ExactlyOne.of(path.property) := ( in.value.getProperty[path.property.Raw](path.property.label) )
  }

  implicit def evalSource[E <: AnyEdge]:
      EvalPathOn[TitanEdge, Source[E], TitanVertex] =
  new EvalPathOn[TitanEdge, Source[E], TitanVertex] {
    def apply(path: Path)(in: In): Out = ExactlyOne.of((path.edge: E).inT) := ( in.value.getVertex(Direction.OUT) )
  }

  implicit def evalTarget[E <: AnyEdge]:
      EvalPathOn[TitanEdge, Target[E], TitanVertex] =
  new EvalPathOn[TitanEdge, Target[E], TitanVertex] {
    def apply(path: Path)(in: In): Out = ExactlyOne.of((path.edge: E).outT) := ( in.value.getVertex(Direction.IN) )
  }

  implicit def evalInE[
    P <: AnyPredicate { type ElementType <: AnyEdge }, O
  ](implicit 
    transform: ToBlueprintsPredicate[P],
    containerVal: ValueContainer[InE[P]#OutC, JIterable[TitanEdge]] { type Out = O }
  ):  EvalPathOn[TitanVertex, InE[P], O] =
  new EvalPathOn[TitanVertex, InE[P], O] {
    def apply(path: Path)(in: In): Out = {
      type E = P#ElementType
      val elem: E = path.pred.elementType
      (elem.inC: E#InC).of(elem: E) := containerVal(
        transform(path.pred, 
          in.value.query
            .labels(path.pred.elementType.label)
            .direction(Direction.IN)
          ).edges
          .asInstanceOf[JIterable[com.thinkaurelius.titan.core.TitanEdge]]
      )
    }
  }

  implicit def evalOutE[
    P <: AnyPredicate { type ElementType <: AnyEdge }, O
  ](implicit
    transform: ToBlueprintsPredicate[P],
    containerVal: ValueContainer[OutE[P]#OutC, JIterable[TitanEdge]] { type Out = O }
  ):  EvalPathOn[TitanVertex, OutE[P], O] =
  new EvalPathOn[TitanVertex, OutE[P], O] {
    def apply(path: Path)(in: In): Out = {
      type E = P#ElementType
      val elem: E = path.pred.elementType
      (elem.outC: E#OutC).of(elem: E) := containerVal(
        transform(path.pred, 
          in.value.query
            .labels(path.pred.elementType.label)
            .direction(Direction.OUT)
          ).edges
          .asInstanceOf[JIterable[com.thinkaurelius.titan.core.TitanEdge]]
      )
    }
  }

  // TODO: this implementation won't work in one step with vertex-query
  // implicit def evalOutVertices[E <: AnyEdge]:
  //     EvalPathOn[TitanVertex, OutVertices[E], TitanVertex] =
  // new EvalPathOn[TitanVertex, OutVertices[E], TitanVertex] {
  //   def apply(path: Path)(in: In): Out = {
  //     in.value
  //       .getVertices(Direction.OUT, path.edge.label)
  //       .asInstanceOf[JIterable[com.thinkaurelius.titan.core.TitanVertex]]
  //       .toList.map{ new Denotes[TitanVertex, E#TargetType]( _ ) }
  //   }
  // }

  // implicit def evalInVertices[E <: AnyEdge]:
  //     EvalPathOn[TitanVertex, InVertices[E], TitanVertex] =
  // new EvalPathOn[TitanVertex, InVertices[E], TitanVertex] {
  //   def apply(path: Path)(in: In): Out = {
  //     in.value
  //       .getVertices(Direction.IN, path.edge.label)
  //       .asInstanceOf[JIterable[com.thinkaurelius.titan.core.TitanVertex]]
  //       .toList.map{ new Denotes[TitanVertex, E#SourceType]( _ ) }
  //   }
  // }

}

