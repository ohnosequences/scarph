package ohnosequences.scarph.impl.titan

case object evals {

  import shapeless._

  import com.thinkaurelius.titan.core._, schema._
  import scala.collection.JavaConversions._

  import ohnosequences.cosas._, fns._, types._
  // import cosas.ops.typeSets._

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.steps._, s.paths._, s.containers._, s.combinators._, s.evals._, s.predicates._, s.schemas._
  import s.impl.titan.predicates._

  import scalaz.{ NonEmptyList => NEList }
  import java.lang.{ Iterable => JIterable }

  case class DataInconsistencyException(msg: String) extends Exception(msg)

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
        val head = l.headOption
          .getOrElse(throw DataInconsistencyException("A non empty iterable was expected, check consistency of your data"))
        val tail = l.drop(1)
        NEList.nel(head, tail) 
      }
    }


  /* The general eval for MapOver needs scalaz.Functor instances, so we re-export them */
  implicit val optionFunctor: scalaz.Functor[Option] = scalaz.std.option.optionInstance
  implicit val streamFunctor: scalaz.Functor[Stream] = scalaz.std.stream.streamInstance
  // NOTE: NEList has instances in its companion object


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
    S <: AnySchema, P <: AnyPredicate { type Element <: AnyVertex }, C <: AnyContainer, O
  ](implicit 
    toBlueprintsQuery: ToBlueprintsPredicate[P],
    packValue: ValueContainer[C, JIterable[TitanVertex]] { type Out = O }
  ):  EvalPathOn[TitanGraph, GraphQuery[S, C, P], O] =
  new EvalPathOn[TitanGraph, GraphQuery[S, C, P], O] {
    def apply(path: Path)(in: In): Out = {
      path.out := packValue(
        toBlueprintsQuery(path.predicate, in.value.query).vertices
          .asInstanceOf[JIterable[TitanVertex]]
      )
    }
  }

  implicit def evalEdgeQuery[
    S <: AnySchema, P <: AnyPredicate { type Element <: AnyEdge }, C <: AnyContainer, O
  ](implicit 
    toBlueprintsQuery: ToBlueprintsPredicate[P],
    packValue: ValueContainer[C, JIterable[TitanEdge]] { type Out = O }
  ):  EvalPathOn[TitanGraph, GraphQuery[S, C, P], O] =
  new EvalPathOn[TitanGraph, GraphQuery[S, C, P], O] {
    def apply(path: Path)(in: In): Out = {
      path.out := packValue(
        toBlueprintsQuery(path.predicate, in.value.query).edges
          .asInstanceOf[JIterable[TitanEdge]]
      )
    }
  }


  import com.tinkerpop.blueprints.Direction

  implicit def evalVertexGet[P <: AnyGraphProperty { type Owner <: AnyVertex }]:
      EvalPathOn[TitanVertex, Get[P], P#Raw] =
  new EvalPathOn[TitanVertex, Get[P], P#Raw] {
    def apply(path: Path)(in: In): Out = path.property := in.value.getProperty[path.property.Raw](path.property.label)
  }

  implicit def evalEdgeGet[P <: AnyGraphProperty { type Owner <: AnyEdge }]:
      EvalPathOn[TitanEdge, Get[P], P#Raw] =
  new EvalPathOn[TitanEdge, Get[P], P#Raw] {
    def apply(path: Path)(in: In): Out = path.property := in.value.getProperty[path.property.Raw](path.property.label)
  }

  implicit def evalSource[E <: AnyEdge]:
      EvalPathOn[TitanEdge, Source[E], TitanVertex] =
  new EvalPathOn[TitanEdge, Source[E], TitanVertex] {
    def apply(path: Path)(in: In): Out = (path.out: Path#Out) := in.value.getVertex(Direction.OUT)
  }

  implicit def evalTarget[E <: AnyEdge]:
      EvalPathOn[TitanEdge, Target[E], TitanVertex] =
  new EvalPathOn[TitanEdge, Target[E], TitanVertex] {
    def apply(path: Path)(in: In): Out = (path.out: Path#Out) := in.value.getVertex(Direction.IN)
  }

  implicit def evalInE[
    P <: AnyPredicate { type Element <: AnyEdge }, O
  ](implicit 
    toBlueprintsQuery: ToBlueprintsPredicate[P],
    packValue: ValueContainer[InE[P]#Out#Container, JIterable[TitanEdge]] { type Out = O }
  ):  EvalPathOn[TitanVertex, InE[P], O] =
  new EvalPathOn[TitanVertex, InE[P], O] {
    def apply(path: Path)(in: In): Out = {
      (path.out: Path#Out) := packValue(
        toBlueprintsQuery(path.predicate, 
          in.value.query
            .labels(path.predicate.element.label)
            .direction(Direction.IN)
        ).asInstanceOf[TitanVertexQuery[_]]
        .titanEdges
      )
    }
  }

  implicit def evalOutE[
    P <: AnyPredicate { type Element <: AnyEdge }, O
  ](implicit
    toBlueprintsQuery: ToBlueprintsPredicate[P],
    packValue: ValueContainer[OutE[P]#Out#Container, JIterable[TitanEdge]] { type Out = O }
  ):  EvalPathOn[TitanVertex, OutE[P], O] =
  new EvalPathOn[TitanVertex, OutE[P], O] {
    def apply(path: Path)(in: In): Out = {
      (path.out: Path#Out) := packValue(
        toBlueprintsQuery(path.predicate, 
          in.value.query
            .labels(path.predicate.element.label)
            .direction(Direction.OUT)
        ).asInstanceOf[TitanVertexQuery[_]]
        .titanEdges
      )
    }
  }

  implicit def evalInV[
    P <: AnyPredicate { type Element <: AnyEdge }, O
  ](implicit
    toBlueprintsQuery: ToBlueprintsPredicate[P],
    packValue: ValueContainer[InV[P]#Out#Container, JIterable[TitanVertex]] { type Out = O }
  ):  EvalPathOn[TitanVertex, InV[P], O] =
  new EvalPathOn[TitanVertex, InV[P], O] {
    def apply(path: Path)(in: In): Out = {
      (path.out: Path#Out) := packValue(
        toBlueprintsQuery(path.predicate, 
          in.value.query
            .labels(path.predicate.element.label)
            .direction(Direction.IN)
        ).asInstanceOf[TitanVertexQuery[_]]
        .vertexIds
      )
    }
  }

  implicit def evalOutV[
    P <: AnyPredicate { type Element <: AnyEdge }, O
  ](implicit
    toBlueprintsQuery: ToBlueprintsPredicate[P],
    packValue: ValueContainer[OutV[P]#Out#Container, JIterable[TitanVertex]] { type Out = O }
  ):  EvalPathOn[TitanVertex, OutV[P], O] =
  new EvalPathOn[TitanVertex, OutV[P], O] {
    def apply(path: Path)(in: In): Out = {
      (path.out: Path#Out) := packValue(
        toBlueprintsQuery(path.predicate, 
          in.value.query
            .labels(path.predicate.element.label)
            .direction(Direction.OUT)
        ).asInstanceOf[TitanVertexQuery[_]]
        .vertexIds
      )
    }
  }

}
