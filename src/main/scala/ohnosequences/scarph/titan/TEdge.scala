package ohnosequences.scarph.titan

import ohnosequences.scarph._

trait AnyTEdge extends AnyEdge { tedge =>

  final type Raw = com.thinkaurelius.titan.core.TitanEdge

  type Source <: AnyVertex.ofType[Tpe#SourceType] with AnyTVertex
  type Target <: AnyVertex.ofType[Tpe#TargetType] with AnyTVertex

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

class TEdge[
    S <: AnyVertex.ofType[ET#SourceType] with AnyTVertex, 
    ET <: AnyEdgeType, 
    T <: AnyVertex.ofType[ET#TargetType] with AnyTVertex
  ](val source: S, val tpe: ET, val target: T) extends AnyTEdge { 
    type Source = S
    type Tpe = ET 
    type Target = T
  }

object AnyTEdge {
  type ofType[ET <: AnyEdgeType] = AnyTEdge { type Tpe = ET }
}
