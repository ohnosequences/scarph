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

  // import com.tinkerpop.blueprints.Direction

  // /* Getting source vertex */
  // implicit val sourceGetter = new GetSource {
  //   def apply(rep: tedge.Rep): Out = source ->> rep.getVertex(Direction.OUT)
  // }

  // /* Getting target vertex */
  // implicit val targetGetter = new GetTarget {
  //   def apply(rep: tedge.Rep): Out = target ->> rep.getVertex(Direction.IN)
  // }

}
