package ohnosequences.scarph.impl.titan.ops

import ohnosequences.pointless._, AnyTypeSet._, AnyWrap._
import ohnosequences.scarph._, AnyPropertiesHolder._
import ohnosequences.scarph.impl.titan._, AnyTitanVertex._

object element {
  import ohnosequences.scarph.ops.element._

  // /* Getting a property from any TitanEdge */
  implicit def unsafeGetProperty[E <: AnyTitanElement, P <: AnyProperty]
    (implicit hasProp: E#DenotedType HasProperty P):
          GetProperty[E, P] = 
      new GetProperty[E, P] {

        def apply(raw: RawOf[E], prop: P): Out = prop(raw.getProperty[RawOf[P]](prop.label))
      }

}
