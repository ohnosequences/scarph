package ohnosequences.scarph


/* Basic steps: */
object steps {

  import graphTypes._, paths._, containers._, predicates._


  case class Get[P <: AnyGraphProperty](val property: P) 
    extends Step[P#Owner, P](property.owner, property)

  case class InE[P <: AnyPredicate { type ElementType <: AnyEdge }](val predicate: P) extends AnyStep {

      type Edge = P#ElementType
      val  edge = predicate.elementType: Edge

      type In = TargetV[Edge]
      val  in = targetV(edge)

      type Out = Edge#Source#Container#Of[Edge]
      val  out = edge.source.container.of(edge)
  }

  case class OutE[P <: AnyPredicate { type ElementType <: AnyEdge }](val predicate: P) extends AnyStep {

      type Edge = P#ElementType
      val  edge = predicate.elementType: Edge

      type In = SourceV[Edge]
      val  in = sourceV(edge)

      type Out = Edge#Target#Container#Of[Edge]
      val  out = edge.target.container.of(edge)
  }

  // TODO: inV/outV

  case class Source[E <: AnyEdge](val edge: E) extends AnyStep {

    type In = E
    val  in = edge

    type Out = SourceV[E]
    val  out = sourceV(edge)
  }

  case class Target[E <: AnyEdge](val edge: E) extends AnyStep {

    type In = E
    val  in = edge

    type Out = TargetV[E]
    val  out = targetV(edge)
  }

  case class Query[E <: AnyGraphElement](val elem: E)
    extends Step[PredicateType[E], ManyOrNone#Of[E]](PredicateType[E](elem), ManyOrNone.of(elem))

}
