package ohnosequences.scarph

import AnyEvalPath._

/* Basic steps: */
object steps {

  case class IdStep[T <: AnyGraphType](t: T) 
    extends Step[T, ExactlyOne.type, T](t, ExactlyOne, t)

  case class Get[P <: AnyProp](val property: P) 
    extends Step[P#Owner, ExactlyOne.type, P](property.owner, ExactlyOne, property)

  case class InE[P <: AnyPredicate { type ElementType <: AnyEdgeType }](val pred: P) 
    extends Step[P#ElementType#Target, P#ElementType#InC, P#ElementType](pred.elementType.target, pred.elementType.inC, pred.elementType)

  // TODO: same as for InE
  // case class InV[E <: AnyEdgeType](val edge: E) 
  //   extends Step[E#Target, E#InC, E#Source](edge.target, edge.inC, edge.source)

  case class OutE[P <: AnyPredicate { type ElementType <: AnyEdgeType }](val pred: P) 
    extends Step[P#ElementType#Source, P#ElementType#OutC, P#ElementType](pred.elementType.source, pred.elementType.outC, pred.elementType)

  // TODO: same as for OutE
  // case class OutV[E <: AnyEdgeType](val edge: E) 
  //   extends Step[E#Source, E#OutC, E#Target](edge.source, edge.outC, edge.target)

  case class Source[E <: AnyEdgeType](val edge: E)
    extends Step[E, ExactlyOne.type, E#Source](edge, ExactlyOne, edge.source)

  case class Target[E <: AnyEdgeType](val edge: E)
    extends Step[E, ExactlyOne.type, E#Target](edge, ExactlyOne, edge.target)

  case class Query[E <: AnyElementType](val elem: E)
    extends Step[PredicateType[E], ManyOrNone.type, E](PredicateType[E](elem), ManyOrNone, elem)

}
