package ohnosequences.scarph.impl.titan.ops

import ohnosequences.pointless._, AnyTypeSet._, AnyWrap._
import ohnosequences.scarph._, AnyPropertiesHolder._, AnyEdge._
import ohnosequences.scarph.impl.titan._, AnyTitanVertex._

object edge {
  import ohnosequences.scarph.ops.edge._

  // /* Getting a property from any TitanEdge */
  implicit def unsafeGetEdgeProperty[E <: AnyTitanEdge, P <: AnyProperty]
    (implicit hasProp: EdgeTypeOf[E] HasProperty P):
          GetEdgeProperty[E, P] = 
      new GetEdgeProperty[E, P] {

        def apply(raw: RawOf[E], prop: P): Out = prop(raw.getProperty[RawOf[P]](prop.label))
      }

  import com.tinkerpop.blueprints.Direction

  /* Getting source vertex */
  implicit def sourceGetter[E <: AnyTitanEdge]:
        GetSource[E] =
    new GetSource[E] {
      def apply(raw: RawOf[E]): Out = valueOf[SourceOf[E]](raw.getVertex(Direction.OUT))
    }

  /* Getting target vertex */
  implicit def targetGetter[E <: AnyTitanEdge]:
        GetTarget[E] =
    new GetTarget[E] {
      def apply(raw: RawOf[E]): Out = valueOf[TargetOf[E]](raw.getVertex(Direction.IN))
    }

}
