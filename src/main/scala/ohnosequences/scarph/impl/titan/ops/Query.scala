package ohnosequences.scarph.impl.titan.ops

import ohnosequences.pointless._, AnyTypeSet._, AnyWrap._
import ohnosequences.scarph._, AnyPropertiesHolder._, AnyEdge._
import ohnosequences.scarph.impl.titan._, AnyTitanVertex._

object query {
  import com.tinkerpop.blueprints.Direction

  implicit def getProperty[E <: AnyTitanElement, P <: AnyProperty, Q <: GetProperty[E#Tpe, P]]:
        EvalQuery[Q, E, P] =
    new EvalQuery[Q, E, P] {

      def apply(q: In1, eraw: In2): Out = 
        valueOf[P](eraw.getProperty[P#Raw](q.p.label))
    }

  /* Getting source vertex */
  implicit def getSource[E <: AnyTitanEdge, Q <: GetSource[E#Tpe], S <: AnyTitanVertex with AnyVertex.ofType[E#Tpe#SourceType]]:
        EvalQuery[Q, E, S] =
    new EvalQuery[Q, E, S] {

      def apply(q: In1, eraw: In2): Out = 
        valueOf[S](eraw.getVertex(Direction.OUT))
    }

  // /* Getting target vertex */
  // implicit def targetGetter[E <: AnyTitanEdge]:
  //       GetTarget[E] =
  //   new GetTarget[E] {
  //     def apply(raw: RawOf[E]): Out = valueOf[TargetOf[E]](raw.getVertex(Direction.IN))
  //   }

}
