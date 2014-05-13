package ohnosequences.scarph.titan

import ohnosequences.scarph._

trait AnyTEdge extends AnyEdge { tedge =>

  type Raw = com.thinkaurelius.titan.core.TitanEdge

  type Source <: AnyVertex.ofType[Tpe#SourceType]
  val source: Source

  type Target <: AnyVertex.ofType[Tpe#TargetType]
  val target: Target

  /* Getting a property from any TitanEdge */
  import SmthHasProperty._
  implicit def unsafeGetProperty[P <: AnyProperty: PropertyOf[this.Tpe]#is](p: P) = 
    new GetProperty[P](p) {
      def apply(rep: tedge.Rep): p.Raw = rep.getProperty[p.Raw](p.label)
    }

  import com.tinkerpop.blueprints.Direction

  /* Getting source vertex */
  implicit object sourceGetter extends GetSource[Source](source) {
    def apply(rep: tedge.Rep): source.Rep = 
      source ->> rep.getVertex(Direction.OUT).asInstanceOf[source.Raw]
  }

  /* Getting target vertex */
  implicit object targetGetter extends GetTarget[Target](target) {
    def apply(rep: tedge.Rep): target.Rep = 
      target ->> rep.getVertex(Direction.IN).asInstanceOf[target.Raw]
  }

}

class TEdge[
    ET <: AnyEdgeType, 
    S <: AnyVertex.ofType[ET#SourceType], 
    T <: AnyVertex.ofType[ET#TargetType]
  ](val source: S, val tpe: ET, val target: T) extends AnyTEdge { 
    type Source = S
    type Tpe = ET 
    type Target = T
  }
