package ohnosequences.scarph


/* Basic steps: */
object steps {

  import graphTypes._, paths._, containers._, predicates._


  case class Get[P <: AnyGraphProperty](val property: P) 
    extends Step[P#Owner, ExactlyOne, P](property.owner, ExactlyOne, property)

  case class InE[P <: AnyPredicate { type ElementType <: AnyEdge }](val pred: P) 
    extends Step[P#ElementType#OutT, P#ElementType#InC, P#ElementType](pred.elementType.outT, pred.elementType.inC, pred.elementType)

  // TODO: same as for InE
  // case class InV[E <: AnyEdge](val edge: E) 
  //   extends Step[E#OutT, E#InC, E#InT](edge.outT, edge.inC, edge.inT)

  case class OutE[P <: AnyPredicate { type ElementType <: AnyEdge }](val pred: P) 
    extends Step[P#ElementType#InT, P#ElementType#OutC, P#ElementType](pred.elementType.inT, pred.elementType.outC, pred.elementType)

  // TODO: same as for OutE
  // case class OutV[E <: AnyEdge](val edge: E) 
  //   extends Step[E#InT, E#OutC, E#OutT](edge.inT, edge.outC, edge.outT)

  case class Source[E <: AnyEdge](val edge: E)
    extends Step[E, ExactlyOne, E#InT](edge, ExactlyOne, edge.inT)

  case class Target[E <: AnyEdge](val edge: E)
    extends Step[E, ExactlyOne, E#OutT](edge, ExactlyOne, edge.outT)

  case class Query[E <: AnyGraphElement](val elem: E)
    extends Step[PredicateType[E], ManyOrNone, E](PredicateType[E](elem), ManyOrNone, elem)

}
