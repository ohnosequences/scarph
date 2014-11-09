package ohnosequences.scarph.impl

import ohnosequences.scarph._
import com.thinkaurelius.titan.core._ 

object titan {

  import com.tinkerpop.blueprints.Direction

  implicit def evalGetVertexProperty[P <: AnyProp { type Owner <: AnyVertexType }]:
      EvalPath[TitanVertex, GetProperty[P], P#Raw] =
  new EvalPath[TitanVertex, GetProperty[P], P#Raw] {
    def apply(in: In, t: Path): Out = List(t.prop( in.value.getProperty[P#Raw](t.prop.label) ))
  }

  implicit def evalGetEdgeProperty[P <: AnyProp { type Owner <: AnyEdgeType }]:
      EvalPath[TitanEdge, GetProperty[P], P#Raw] =
  new EvalPath[TitanEdge, GetProperty[P], P#Raw] {
    def apply(in: In, t: Path): Out = List(t.prop( in.value.getProperty[P#Raw](t.prop.label) ))
  }

  implicit def evalGetSource[E <: AnyEdgeType]:
      EvalPath[TitanEdge, GetSource[E], TitanVertex] =
  new EvalPath[TitanEdge, GetSource[E], TitanVertex] {
    def apply(in: In, t: Path): Out = List(new LabeledBy[TitanVertex, E#Source]( in.value.getVertex(Direction.OUT) ))
  }

  implicit def evalGetTarget[E <: AnyEdgeType]:
      EvalPath[TitanEdge, GetTarget[E], TitanVertex] =
  new EvalPath[TitanEdge, GetTarget[E], TitanVertex] {
    def apply(in: In, t: Path): Out = List(new LabeledBy[TitanVertex, E#Target]( in.value.getVertex(Direction.IN) ))
  }

  import scala.collection.JavaConversions._

  implicit def evalGetOutEdges[E <: AnyEdgeType]:
      EvalPath[TitanVertex, GetOutEdges[E], TitanEdge] =
  new EvalPath[TitanVertex, GetOutEdges[E], TitanEdge] {
    def apply(in: In, t: Path): Out = {
      in.value
        .getEdges(Direction.OUT, t.edge.label)
        .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]
        .toList.map{ new LabeledBy[TitanEdge, E]( _ ) }
    }
  }

  implicit def evalGetInEdges[E <: AnyEdgeType]:
      EvalPath[TitanVertex, GetInEdges[E], TitanEdge] =
  new EvalPath[TitanVertex, GetInEdges[E], TitanEdge] {
    def apply(in: In, t: Path): Out = {
      in.value
        .getEdges(Direction.IN, t.edge.label)
        .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]
        .toList.map{ new LabeledBy[TitanEdge, E]( _ ) }
      // FIXME: to avoid casting here, we should use getTitanEdges instead of getEdges,
      // but it requires having an EdgeLabel, which we can get only from TitanGraph#TitanManagement,
      // so maybe we can have it as a common evaluation context
    }
  }

  implicit def evalGetOutVertices[E <: AnyEdgeType]:
      EvalPath[TitanVertex, GetOutVertices[E], TitanVertex] =
  new EvalPath[TitanVertex, GetOutVertices[E], TitanVertex] {
    def apply(in: In, t: Path): Out = {
      in.value
        .getVertices(Direction.OUT, t.edge.label)
        .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanVertex]]
        .toList.map{ new LabeledBy[TitanVertex, E#Target]( _ ) }
    }
  }

  implicit def evalGetInVertices[E <: AnyEdgeType]:
      EvalPath[TitanVertex, GetInVertices[E], TitanVertex] =
  new EvalPath[TitanVertex, GetInVertices[E], TitanVertex] {
    def apply(in: In, t: Path): Out = {
      in.value
        .getVertices(Direction.IN, t.edge.label)
        .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanVertex]]
        .toList.map{ new LabeledBy[TitanVertex, E#Source]( _ ) }
    }
  }

}
