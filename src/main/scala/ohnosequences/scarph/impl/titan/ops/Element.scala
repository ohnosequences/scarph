package ohnosequences.scarph.impl.titan.ops

import ohnosequences.pointless._, AnyTypeSet._, AnyWrap._
import ohnosequences.scarph._, AnyPropertiesHolder._
import ohnosequences.scarph.impl.titan._, AnyTitanVertex._
import com.thinkaurelius.titan.core.attribute.Cmp.EQUAL
import scala.collection.JavaConversions._

object element {
  import ohnosequences.scarph.ops.element._

  /* Getting a property from any TitanElement (it works the same for vertex and edge) */
  implicit def unsafeGetProperty[E <: AnyTitanElement, P <: AnyProperty]
    (implicit hasProp: E#DenotedType HasProperty P):
          GetProperty[E, P] = 
      new GetProperty[E, P] {

        def apply(raw: E#Raw, prop: P): Out = prop(raw.getProperty[RawOf[P]](prop.label))
      }

  implicit def simpleQueryEval[
    E <: AnyTitanElement,
    Q <: AnySimpleQuery with AnyQuery.On[E#DenotedType] with AnyQuery.HeadedBy[AnyEQ],
    // I <: AnyStandardIndex.For[E#DenotedType, Q#Head#Property]
    I <: AnyStandardIndex { type IndexedType = E#DenotedType }
  ](implicit index: I): EvalQuery[E, Q] = new EvalQuery[E, Q] {

    def apply(e: E, query: Q): Q#Out[ValueOf[E]] = {
      e.graph.query()
        .has(query.head.property.label, EQUAL, query.head.value)
        .vertices.asInstanceOf[java.lang.Iterable[E#Raw]]
        .toList.map{ r => valueOf[E](r) }
    }
  }

}
