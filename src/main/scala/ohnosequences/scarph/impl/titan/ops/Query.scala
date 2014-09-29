package ohnosequences.scarph.impl.titan.ops

import ohnosequences.pointless._, AnyTypeSet._, AnyWrap._
import ohnosequences.scarph._, AnyPropertiesHolder._, AnyEdge._
import ohnosequences.scarph.impl.titan._, AnyTitanVertex._

object query {
  import com.tinkerpop.blueprints.Direction

  implicit def getProperty[E <: AnyTitanElement, P <: AnyProperty, Q <: GetProperty[E#Tpe, P]]:
        EvalQuery[Q, E, P] =
    new EvalQuery[Q, E, P] {

      def apply(q: In1, es: In2): Out =
        es.map{ e => valueOf[P](e.raw.getProperty[P#Raw](q.p.label)) }
    }

  /* Getting source vertex */
  implicit def getSource[E <: AnyTitanEdge, Q <: GetSource[E#Tpe], S <: AnyTitanVertex with AnyVertex.ofType[E#Tpe#SourceType]]:
        EvalQuery[Q, E, S] =
    new EvalQuery[Q, E, S] {

      def apply(q: In1, es: In2): Out =
        es.map{ e => valueOf[S](e.raw.getVertex(Direction.OUT)) }
    }

  /* Getting target vertex */
  implicit def getTarget[E <: AnyTitanEdge, Q <: GetTarget[E#Tpe], T <: AnyTitanVertex with AnyVertex.ofType[E#Tpe#TargetType]]:
        EvalQuery[Q, E, T] =
    new EvalQuery[Q, E, T] {

      def apply(q: In1, es: In2): Out =
        es.map{ e => valueOf[T](e.raw.getVertex(Direction.IN)) }
    }

  import scala.collection.JavaConversions._

  implicit def getInEdges[E <: AnyTitanEdge, V <: AnyTitanVertex with AnyVertex.ofType[E#Tpe#TargetType], Q <: GetInEdges[E#Tpe]]:
        EvalQuery[Q, V, E] =
    new EvalQuery[Q, V, E] {

      def apply(q: In1, vs: In2): Out = {
        vs.flatMap{ v => v.raw.getEdges(Direction.IN, q.outT.label)
          .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]
          .toList.map{ valueOf[E](_) }
        }
      }
    }

  implicit def getOutEdges[E <: AnyTitanEdge, V <: AnyTitanVertex with AnyVertex.ofType[E#Tpe#SourceType], Q <: GetOutEdges[E#Tpe]]:
        EvalQuery[Q, V, E] =
    new EvalQuery[Q, V, E] {

      def apply(q: In1, vs: In2): Out = {
        vs.flatMap{ v => v.raw.getEdges(Direction.OUT, q.outT.label)
          .asInstanceOf[java.lang.Iterable[com.thinkaurelius.titan.core.TitanEdge]]
          .toList.map{ valueOf[E](_) }
        }
      }
    }

}
