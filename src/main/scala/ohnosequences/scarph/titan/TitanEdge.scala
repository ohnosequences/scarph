package ohnosequences.scarph.titan

import ohnosequences.scarph._
import com.thinkaurelius.titan.core.{TitanGraph => TGraph}
import com.thinkaurelius.titan.core.{TitanEdge => TEdge}

trait AnyTitanEdge extends AnyEdge { tedge =>

  final type Graph = TGraph
  final type Raw = TEdge

  type Source <: AnyVertex.ofType[Tpe#SourceType] with AnyTitanVertex
  type Target <: AnyVertex.ofType[Tpe#TargetType] with AnyTitanVertex

  /* Getting a property from any TitanEdge */
  implicit def unsafeGetProperty[P <: AnyProperty: Property.Of[this.Tpe]#is](p: P) = 
    new PropertyGetter[P](p) {
      def apply(rep: tedge.Rep): p.Raw = rep.getProperty[p.Raw](p.label)
    }

  import com.tinkerpop.blueprints.Direction

  /* Getting source vertex */
  implicit val sourceGetter = new GetSource {
    def apply(rep: tedge.Rep): Out = source ->> rep.getVertex(Direction.OUT)
  }

  /* Getting target vertex */
  implicit val targetGetter = new GetTarget {
    def apply(rep: tedge.Rep): Out = target ->> rep.getVertex(Direction.IN)
  }

}

class TitanEdge[
    S <: AnyVertex.ofType[ET#SourceType] with AnyTitanVertex, 
    ET <: AnyEdgeType, 
    T <: AnyVertex.ofType[ET#TargetType] with AnyTitanVertex
  ](val graph: TGraph, val source: S, val tpe: ET, val target: T) extends AnyTitanEdge { 
    type Source = S
    type Tpe = ET 
    type Target = T
  }

object AnyTitanEdge {
  type ofType[ET <: AnyEdgeType] = AnyTitanEdge { type Tpe = ET }
}
