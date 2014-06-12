package ohnosequences.scarph.titan

import ohnosequences.scarph._

trait AnyTEdge extends AnyEdge { tedge =>

  final type Raw = com.thinkaurelius.titan.core.TitanEdge

  type Source <: AnyVertex.ofType[Tpe#SourceType] with AnyTVertex
  val source: Source

  type Target <: AnyVertex.ofType[Tpe#TargetType] with AnyTVertex
  val target: Target

  /* Getting a property from any TitanEdge */
  implicit def unsafeGetProperty[P <: AnyProperty: Property.Of[this.Tpe]#is](p: P) = 
    new GetProperty[P](p) {
      def apply(rep: tedge.Rep): p.Raw = rep.getProperty[p.Raw](p.label)
    }

  import com.tinkerpop.blueprints.Direction

  /* Getting source vertex */
  implicit object sourceGetter extends GetSource[Source](source) {
    def apply(rep: tedge.Rep): source.Rep = 
      source ->> rep.getVertex(Direction.OUT)
  }

  /* Getting target vertex */
  implicit object targetGetter extends GetTarget[Target](target) {
    def apply(rep: tedge.Rep): target.Rep = 
      target ->> rep.getVertex(Direction.IN)
  }

}

class TEdge[
    ET <: AnyEdgeType, 
    S <: AnyVertex.ofType[ET#SourceType] with AnyTVertex, 
    T <: AnyVertex.ofType[ET#TargetType] with AnyTVertex
  ](val source: S, val tpe: ET, val target: T) extends AnyTEdge { 
    type Source = S
    type Tpe = ET 
    type Target = T
  }
