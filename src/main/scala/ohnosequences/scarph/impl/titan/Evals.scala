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
    S <: AnySchema,
    P <: AnyPredicate { type ElementType <: AnyVertex } 
  ](implicit transform: ToBlueprintsPredicate[P]): 
      EvalPathOn[TitanGraph, Query[S, P], Stream[TitanVertex]] =
  new EvalPathOn[TitanGraph, Query[S, P], Stream[TitanVertex]] {
    def apply(path: Path)(in: In): Out = {
      path.out := (
        transform(path.predicate, in.value.query).vertices
          .asInstanceOf[JIterable[com.thinkaurelius.titan.core.TitanVertex]].toStream
      )
    }
  }

  implicit def evalEdgeQuery[
    S <: AnySchema,
    P <: AnyPredicate { type ElementType <: AnyEdge }
  ](implicit transform: ToBlueprintsPredicate[P]): 
      EvalPathOn[TitanGraph, Query[S, P], Stream[TitanEdge]] =
  new EvalPathOn[TitanGraph, Query[S, P], Stream[TitanEdge]] {
    def apply(path: Path)(in: In): Out = {
      path.out := (
        transform(path.predicate, in.value.query).edges
          .asInstanceOf[JIterable[com.thinkaurelius.titan.core.TitanEdge]].toStream
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
    def apply(path: Path)(in: In): Out = (path.out: E#SourceV) := in.value.getVertex(Direction.OUT)
  }

  implicit def evalTarget[E <: AnyEdge]:
      EvalPathOn[TitanEdge, Target[E], TitanVertex] =
  new EvalPathOn[TitanEdge, Target[E], TitanVertex] {
    def apply(path: Path)(in: In): Out = (path.out: E#TargetV) := in.value.getVertex(Direction.IN)
  }

  implicit def evalInE[
    P <: AnyPredicate { type ElementType <: AnyEdge }, O
  ](implicit 
    transform: ToBlueprintsPredicate[P],
    containerVal: ValueContainer[InE[P]#Out#Container, JIterable[TitanEdge]] { type Out = O }
  ):  EvalPathOn[TitanVertex, InE[P], O] =
  new EvalPathOn[TitanVertex, InE[P], O] {
    def apply(path: Path)(in: In): Out = {
      (path.out: InE[P]#Out) := containerVal(
        transform(path.predicate, 
          in.value.query
            .labels(path.predicate.elementType.label)
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
    containerVal: ValueContainer[OutE[P]#Out#Container, JIterable[TitanEdge]] { type Out = O }
  ):  EvalPathOn[TitanVertex, OutE[P], O] =
  new EvalPathOn[TitanVertex, OutE[P], O] {
    def apply(path: Path)(in: In): Out = {
      (path.out: OutE[P]#Out) := containerVal(
        transform(path.predicate, 
          in.value.query
            .labels(path.predicate.elementType.label)
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

