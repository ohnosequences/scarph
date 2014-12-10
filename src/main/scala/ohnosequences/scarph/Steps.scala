package ohnosequences.scarph

import AnyEvalPath._

/* Basic steps: */
object steps {

  case class IdStep[T <: AnyLabelType](t: T) 
    extends Path[ExactlyOne.type, T, ExactlyOne.type, T](ExactlyOne, t, ExactlyOne, t)

  case class Get[P <: AnyProp](val property: P) 
    extends Path[ExactlyOne.type, P#Owner, ExactlyOne.type, P](ExactlyOne, property.owner, ExactlyOne, property)

  case class InE[P <: AnyPredicate { type ElementType <: AnyEdgeType }](val pred: P) 
    extends Path[ExactlyOne.type, P#ElementType#Target, P#ElementType#InC, P#ElementType](ExactlyOne, pred.elementType.target, pred.elementType.inC, pred.elementType)

  // TODO: same as for InE
  // case class InV[E <: AnyEdgeType](val edge: E) 
  //   extends Path[ExactlyOne.type, E#Target, E#InC, E#Source](ExactlyOne, edge.target, edge.inC, edge.source)

  case class OutE[P <: AnyPredicate { type ElementType <: AnyEdgeType }](val pred: P) 
    extends Path[ExactlyOne.type, P#ElementType#Source, P#ElementType#OutC, P#ElementType](ExactlyOne, pred.elementType.source, pred.elementType.outC, pred.elementType)

  // TODO: same as for OutE
  // case class OutV[E <: AnyEdgeType](val edge: E) 
  //   extends Path[ExactlyOne.type, E#Source, E#OutC, E#Target](ExactlyOne, edge.source, edge.outC, edge.target)

  case class Source[E <: AnyEdgeType](val edge: E)
    extends Path[ExactlyOne.type, E, ExactlyOne.type, E#Source](ExactlyOne, edge, ExactlyOne, edge.source)

  case class Target[E <: AnyEdgeType](val edge: E)
    extends Path[ExactlyOne.type, E, ExactlyOne.type, E#Target](ExactlyOne, edge, ExactlyOne, edge.target)

  case class Query[E <: AnyElementType](val elem: E)
    extends Path[ExactlyOne.type, PredicateType[E], ManyOrNone.type, E](ExactlyOne, PredicateType[E](elem), ManyOrNone, elem)

}
