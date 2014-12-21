package ohnosequences.scarph


/* Basic steps: */
object steps {

  import graphTypes._, paths._, containers._, predicates._


  case class Get[P <: AnyGraphProperty](val property: P) 
    extends Step[P#Owner, P](property.owner, property)

  case class InE[P <: AnyPredicate { type ElementType <: AnyEdge }](val pred: P) 
    extends Step[P#ElementType#Target, P#ElementType](pred.elementType.target, pred.elementType)

  // TODO: same as for InE
  // case class InV[E <: AnyEdge](val edge: E) 
  //   extends Step[E#OutT, E#InC, E#InT](edge.outT, edge.inC, edge.inT)

  case class OutE[P <: AnyPredicate { type ElementType <: AnyEdge }](val pred: P) 
    extends Step[P#ElementType#Source, P#ElementType](pred.elementType.source, pred.elementType)

  // TODO: same as for OutE
  // case class OutV[E <: AnyEdge](val edge: E) 
  //   extends Step[E#InT, E#OutC, E#OutT](edge.inT, edge.outC, edge.outT)

  case class Source[E <: AnyEdge](val edge: E)
    extends Step[E, E#Source](edge, edge.source)

  case class Target[E <: AnyEdge](val edge: E)
    extends Step[E, E#Target](edge, edge.target)

  case class Query[E <: AnyGraphElement](val elem: E)
    extends Step[PredicateType[E], ManyOrNone#Of[E]](PredicateType[E](elem), ManyOrNone.of(elem))

}
