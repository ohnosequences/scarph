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

  implicit def simpleVertexQueryEval[
    V <: AnyTitanVertex,
    Q <: AnySimpleQuery with 
         AnyQuery.On[V#DenotedType] with 
         AnyQuery.HeadedBy[AnyEQ]
  ]: EvalQuery[V, Q] = new EvalQuery[V, Q] {

    def apply(e: V, query: Q): Q#Out[ValueOf[V]] = {
      e.graph.query()
        .has(query.head.property.label, EQUAL, query.head.value)
        .vertices.asInstanceOf[java.lang.Iterable[V#Raw]]
        .toList.map{ r => valueOf[V](r) }
    }
  }

  implicit def simpleEdgeQueryEval[
    E <: AnyTitanEdge,
    Q <: AnySimpleQuery with 
         AnyQuery.On[E#DenotedType] with 
         AnyQuery.HeadedBy[AnyEQ]
  ]: EvalQuery[E, Q] = new EvalQuery[E, Q] {

    def apply(e: E, query: Q): Q#Out[ValueOf[E]] = {
      e.graph.query()
        .has(query.head.property.label, EQUAL, query.head.value)
        .edges.asInstanceOf[java.lang.Iterable[E#Raw]]
        .toList.map{ r => valueOf[E](r) }
    }
  }

}
