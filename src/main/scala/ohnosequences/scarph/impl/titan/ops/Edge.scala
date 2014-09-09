package ohnosequences.scarph.impl.titan.ops

import ohnosequences.pointless._, AnyTypeSet._, AnyWrap._
import ohnosequences.scarph._, AnyPropertiesHolder._, AnyEdge._
import ohnosequences.scarph.impl.titan._, AnyTitanVertex._

object edge {
  import ohnosequences.scarph.ops.edge._

  // /* Getting a property from any TitanEdge */
  implicit def unsafeGetProperty[E <: AnyTitanEdge, P <: AnyProperty]
    (implicit hasProp: P ∈ PropertiesOf[EdgeTypeOf[E]]):
          GetProperty[E, P] = 
      new GetProperty[E, P] {

        def apply(rep: ValueOf[E], prop: P): Out = prop(rep.raw.getProperty[RawOf[P]](prop.label))
      }

  import com.tinkerpop.blueprints.Direction

  /* Getting source vertex */
  implicit def sourceGetter[E <: AnyTitanEdge]:
        GetSource[E] =
    new GetSource[E] {
      def apply(rep: ValueOf[E]): Out = valueOf[SourceOf[E]](rep.raw.getVertex(Direction.OUT))
    }

  /* Getting target vertex */
  implicit def targetGetter[E <: AnyTitanEdge]:
        GetTarget[E] =
    new GetTarget[E] {
      def apply(rep: ValueOf[E]): Out = valueOf[TargetOf[E]](rep.raw.getVertex(Direction.IN))
    }

}
