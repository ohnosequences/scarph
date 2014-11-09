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

}
