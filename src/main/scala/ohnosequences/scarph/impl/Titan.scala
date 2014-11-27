package ohnosequences.scarph.impl

import ohnosequences.scarph._, AnyEvalPath._
import com.thinkaurelius.titan.core._ 

object titan {

  import com.tinkerpop.blueprints.Direction

  implicit def evalGetVertexProperty[P <: AnyProp { type Owner <: AnyVertexType }]:
      EvalGet[TitanVertex, P] = new EvalGet[TitanVertex, P] {

    def apply[P <: Path](path: P)(in: InVal LabeledBy path.In): OutVal LabeledBy path.Out = {

      path.property( in.value.getProperty[path.property.Raw](path.property.label) )
    }
  }

  // implicit def evalGetEdgeProperty[P <: AnyProp { type Owner <: AnyEdgeType }](getP: get[P]):
  //     EvalGet[TitanEdge, P] = new EvalGet[TitanEdge, P](getP) {

  //   def apply(in: In): Out = getP.property( in.value.getProperty[P#Raw](getP.property.label) )
  // }

  // implicit def evalGetSource[E <: AnyEdgeType](src: source[E]):
  //     EvalSource[TitanEdge, E, TitanVertex] =
  // new EvalSource[TitanEdge, E, TitanVertex](src) {
  //   def apply(in: In): Out = new LabeledBy[TitanVertex, path.edge.Source]( in.value.getVertex(Direction.OUT) )
  // }

  // implicit def evalGetTarget[E <: AnyEdgeType]:
  //     EvalPath[TitanEdge, GetTarget[E], TitanVertex] =
  // new EvalPath[TitanEdge, GetTarget[E], TitanVertex] {
  //   def apply(in: In, t: Path): Out = List(new LabeledBy[TitanVertex, E#Target]( in.value.getVertex(Direction.IN) ))
  // }

  // import scala.collection.JavaConversions._

  // implicit def evalGetOutEdges[E <: AnyEdgeType]:
  //     EvalPath[TitanVertex, GetOutEdges[E], TitanEdge] =
  // new EvalPath[TitanVertex, GetOutEdges[E], TitanEdge] {
  //   def apply(in: In, t: Path): Out = {
  //     in.value
  //       .getEdges(Direction.OUT, t.edge.label)
  //       .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]
  //       .toList.map{ new LabeledBy[TitanEdge, E]( _ ) }
  //   }
  // }

  // implicit def evalGetInEdges[E <: AnyEdgeType]:
  //     EvalPath[TitanVertex, GetInEdges[E], TitanEdge] =
  // new EvalPath[TitanVertex, GetInEdges[E], TitanEdge] {
  //   def apply(in: In, t: Path): Out = {
  //     in.value
  //       .getEdges(Direction.IN, t.edge.label)
  //       .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]
  //       .toList.map{ new LabeledBy[TitanEdge, E]( _ ) }
  //     // FIXME: to avoid casting here, we should use getTitanEdges instead of getEdges,
  //     // but it requires having an EdgeLabel, which we can get only from TitanGraph#TitanManagement,
  //     // so maybe we can have it as a common evaluation context
  //   }
  // }

  // implicit def evalGetOutVertices[E <: AnyEdgeType]:
  //     EvalPath[TitanVertex, GetOutVertices[E], TitanVertex] =
  // new EvalPath[TitanVertex, GetOutVertices[E], TitanVertex] {
  //   def apply(in: In, t: Path): Out = {
  //     in.value
  //       .getVertices(Direction.OUT, t.edge.label)
  //       .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanVertex]]
  //       .toList.map{ new LabeledBy[TitanVertex, E#Target]( _ ) }
  //   }
  // }

  // implicit def evalGetInVertices[E <: AnyEdgeType]:
  //     EvalPath[TitanVertex, GetInVertices[E], TitanVertex] =
  // new EvalPath[TitanVertex, GetInVertices[E], TitanVertex] {
  //   def apply(in: In, t: Path): Out = {
  //     in.value
  //       .getVertices(Direction.IN, t.edge.label)
  //       .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanVertex]]
  //       .toList.map{ new LabeledBy[TitanVertex, E#Source]( _ ) }
  //   }
  // }

}
