package ohnosequences.scarph

object steps {

  case class IdStep[T <: AnyLabelType](t: T) extends Step[T, T](t, t) with OutArity[ExactlyOne]

  /* Basic steps: */
  case class Get[P <: AnyProp](val prop: P) extends Step[P#Owner, P](prop.owner, prop) with OutArity[ExactlyOne]

  case class Source[E <: AnyEdgeType](val edge: E) extends Step[E, E#SourceType](edge, edge.sourceType) with OutArity[ExactlyOne]
  case class Target[E <: AnyEdgeType](val edge: E) extends Step[E, E#TargetType](edge, edge.targetType) with OutArity[ExactlyOne]

  case class InE[P <: AnyPredicate { type ElementType <: AnyEdgeType }](val pred: P) 
    extends Step[P#ElementType#TargetType, P#ElementType](pred.elementType.targetType, pred.elementType) with OutArity[P#ElementType#InArity]
  case class OutE[P <: AnyPredicate { type ElementType <: AnyEdgeType }](val pred: P) 
    extends Step[P#ElementType#SourceType, P#ElementType](pred.elementType.sourceType, pred.elementType) with OutArity[P#ElementType#OutArity]

  // TODO: steps for in/out vertices

  case class Query[E <: AnyElementType](val elem: E)
    extends Step[PredicateType[E], E](PredicateType[E](elem), elem) with OutArity[ManyOrNone]

}
