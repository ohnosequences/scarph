package ohnosequences.scarph

case class IdStep[T <: AnyLabelType](t: T) extends Step[T, T](t, t) with OutArity[ExactlyOne]

/* Basic steps: */
case class GetProperty[P <: AnyProp](val prop: P) extends Step[P#Owner, P](prop.owner, prop) with OutArity[ExactlyOne]

case class GetSource[E <: AnyEdgeType](val edge: E) extends Step[E, E#SourceType](edge, edge.sourceType) with OutArity[ExactlyOne]
case class GetTarget[E <: AnyEdgeType](val edge: E) extends Step[E, E#TargetType](edge, edge.targetType) with OutArity[ExactlyOne]

case class  GetInEdges[E <: AnyEdgeType](val edge: E) extends Step[E#TargetType, E](edge.targetType, edge) with OutArity[E#InArity]
case class GetOutEdges[E <: AnyEdgeType](val edge: E) extends Step[E#SourceType, E](edge.sourceType, edge) with OutArity[E#OutArity]

case class GetOutE[E <: AnyEdgeType, P <: AnyPredicate.On[E]](val pred: P) 
  extends Step[E#SourceType, E](pred.elementType.sourceType, pred.elementType) with OutArity[E#OutArity]

/* This is just the same as `(GetInEdges(edge) >=> GetSource(edge))` in a query,
   but with the parenthesis, i.e. grouping this composition to be evaluated together.
   Therefore, you can write an evaluator for this composition as for one step if the
   backend allows you to optimize it this way.
*/
case class GetInVertices[E <: AnyEdgeType](val edge: E)
    extends Compose[GetInEdges[E], GetSource[E], E#InArity](GetInEdges(edge), GetSource(edge))
case class GetOutVertices[E <: AnyEdgeType](val edge: E)
    extends Compose[GetOutEdges[E], GetTarget[E], E#OutArity](GetOutEdges(edge), GetTarget(edge))


case class Query[E <: AnyElementType](val elem: E) extends 
  Step[PredicateType[E], E](PredicateType[E](elem), elem) with OutArity[ManyOrNone]
