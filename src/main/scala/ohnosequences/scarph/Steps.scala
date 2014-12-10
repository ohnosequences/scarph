package ohnosequences.scarph

import AnyEvalPath._

/* Basic steps: */
object steps {

  case class IdStep[T <: AnyLabelType](t: T) extends AnyPath {

    type InT = T
    lazy val inT = t
    type InC = ExactlyOne.type
    lazy val inC = ExactlyOne
    type In = T
    lazy val in = t

    type OutT = T
    lazy val outT = t
    type OutC = ExactlyOne.type
    lazy val outC = ExactlyOne
    type Out = T
    lazy val out = t
  }

  case class Get[P <: AnyProp](val property: P) extends AnyPath {

    type Property = P
    type InT = Property#Owner
    lazy val inT = property.owner
    type InC = ExactlyOne.type
    lazy val inC = ExactlyOne
    type In = Property#Owner
    lazy val in = property.owner

    type OutT = P
    lazy val outT = property
    type OutC = ExactlyOne.type
    lazy val outC = ExactlyOne
    type Out = P
    lazy val out = property
  }

  case class InE[E <: AnyEdgeType](val edge: E) extends AnyPath {

    type InT = edge.Target
    lazy val  inT = edge.target
    type InC = ExactlyOne.type
    lazy val inC = ExactlyOne
    type In = edge.Target
    lazy val in = edge.target

    type OutT = E
    lazy val outT = edge
    type OutC = E#InC
    lazy val outC = edge.inC
    type Out = E#InC#C[E]
    lazy val out = edge.inC(edge)

    // type Rev =? Map source
  }

  case class InV[E <: AnyEdgeType](val edge: E) extends AnyPath {

    type InT = E#Target
    lazy val inT = edge.target
    type InC = ExactlyOne.type
    lazy val inC = ExactlyOne
    type In = E#Target
    lazy val in = edge.target

    type OutT = E#Source
    lazy val  outT = edge.source
    type OutC = E#InC
    lazy val outC = edge.inC
    type Out = E#InC#C[E#Source]
    lazy val out: Out = edge.inC(edge.source)
  }

  case class OutE[E <: AnyEdgeType](val edge: E) extends AnyPath {

    type InT = E#Source
    lazy val inT = edge.source
    type InC = ExactlyOne.type
    lazy val inC = ExactlyOne
    type In = E#Source
    lazy val in = edge.source

    type OutT = E
    lazy val outT = edge
    type OutC = E#OutC
    lazy val outC = edge.outC
    type Out = E#OutC#C[E]
    lazy val out = edge.outC(edge)
  }

  case class OutV[E <: AnyEdgeType](val edge: E) extends AnyPath {

    type InT = E#Source
    lazy val  inT = edge.source
    type InC = ExactlyOne.type
    lazy val inC = ExactlyOne
    type In = E#Source
    lazy val in: In = edge.source

    type OutT = E#Target
    lazy val outT = edge.target
    type OutC = E#OutC
    lazy val outC = edge.outC
    type Out = E#OutC#C[E#Target]
    lazy val out: Out = edge.outC(edge.target)
  }

  case class Source[E <: AnyEdgeType](val edge: E) extends AnyPath {

    type InT = E
    lazy val inT = edge
    type InC = ExactlyOne.type
    lazy val inC = ExactlyOne
    type In = E
    lazy val in: In = edge

    type OutT = E#Source
    lazy val outT = edge.source
    type OutC = ExactlyOne.type
    lazy val outC = ExactlyOne
    type Out = E#Source
    lazy val out = edge.source
  }

  case class Target[E <: AnyEdgeType](val edge: E) extends AnyPath {

    type InT = E
    lazy val inT = edge
    type InC = ExactlyOne.type
    lazy val inC = ExactlyOne
    type In = E
    lazy val in: In = edge

    type OutT = E#Target
    lazy val outT = edge.target
    type OutC = ExactlyOne.type
    lazy val outC = ExactlyOne
    type Out = E#Target
    lazy val out = edge.target
  }

  case class Query[E <: AnyElementType](val elem: E) extends AnyPath {

    type InT = PredicateType[E]
    lazy val inT = PredicateType[E](elem)
    type InC = ExactlyOne.type
    lazy val inC = ExactlyOne
    type In = PredicateType[E]
    lazy val in: In = PredicateType[E](elem)

    type OutT = E
    lazy val outT = elem
    type OutC = ManyOrNone.type
    lazy val outC = ManyOrNone
    type Out = ManyOrNoneOf[E]
    lazy val out: Out = ManyOrNone(elem)
  }

}

//   case class IdStep[T <: AnyLabelType](t: T) extends Step[T, T](t, t) with OutArity[ExactlyOne]

//   /* Basic steps: */
//   case class Get[P <: AnyProp](val prop: P) extends Step[P#Owner, P](prop.owner, prop) with OutArity[ExactlyOne]

//   case class Source[E <: AnyEdgeType](val edge: E) extends Step[E, E#SourceType](edge, edge.sourceType) with OutArity[ExactlyOne]
//   case class Target[E <: AnyEdgeType](val edge: E) extends Step[E, E#TargetType](edge, edge.targetType) with OutArity[ExactlyOne]

//   case class InE[P <: AnyPredicate { type ElementType <: AnyEdgeType }](val pred: P) 
//     extends Step[P#ElementType#TargetType, P#ElementType](pred.elementType.targetType, pred.elementType) with OutArity[P#ElementType#InArity]
//   case class OutE[P <: AnyPredicate { type ElementType <: AnyEdgeType }](val pred: P) 
//     extends Step[P#ElementType#SourceType, P#ElementType](pred.elementType.sourceType, pred.elementType) with OutArity[P#ElementType#OutArity]

//   // TODO: steps for in/out vertices

//   case class Query[E <: AnyElementType](val elem: E)
//     extends Step[PredicateType[E], E](PredicateType[E](elem), elem) with OutArity[ManyOrNone]
