package ohnosequences.scarph

import AnyEvalPath._

/* Basic steps: */
object steps {

  // case class IdStep[T <: AnyGraphType](t: T) 
  //   extends Step[T, ExactlyOne.type, T](t, ExactlyOne, t)

  case class Get[P <: AnyGraphProperty](val property: P) 
    extends Step[P#Owner, ExactlyOne.type, P](property.owner, ExactlyOne, property)

  case class InE[P <: AnyPredicate { type ElementType <: AnyEdgeType }](val pred: P) 
    extends Step[P#ElementType#OutT, P#ElementType#InC, P#ElementType](pred.elementType.outT, pred.elementType.inC, pred.elementType)

  // TODO: same as for InE
  // case class InV[E <: AnyEdgeType](val edge: E) 
  //   extends Step[E#OutT, E#InC, E#InT](edge.outT, edge.inC, edge.inT)

  case class OutE[P <: AnyPredicate { type ElementType <: AnyEdgeType }](val pred: P) 
    extends Step[P#ElementType#InT, P#ElementType#OutC, P#ElementType](pred.elementType.inT, pred.elementType.outC, pred.elementType)

  // TODO: same as for OutE
  // case class OutV[E <: AnyEdgeType](val edge: E) 
  //   extends Step[E#InT, E#OutC, E#OutT](edge.inT, edge.outC, edge.outT)

  case class Source[E <: AnyEdgeType](val edge: E)
    extends Step[E, ExactlyOne.type, E#InT](edge, ExactlyOne, edge.inT)

  case class Target[E <: AnyEdgeType](val edge: E)
    extends Step[E, ExactlyOne.type, E#OutT](edge, ExactlyOne, edge.outT)

  case class Query[E <: AnyElementType](val elem: E)
    extends Step[PredicateType[E], ManyOrNone.type, E](PredicateType[E](elem), ManyOrNone, elem)

}
