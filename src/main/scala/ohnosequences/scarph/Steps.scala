package ohnosequences.scarph


/* Basic steps: */
object steps {

  import graphTypes._, paths._, containers._, predicates._, schemas._, indexes._


  case class Get[P <: AnyGraphProperty](val property: P) 
    extends Step[P#Owner, P](property.owner, property)

  case class InE[P <: AnyPredicate { type Element <: AnyEdge }](val predicate: P) extends AnyStep {

      type     Edge = P#Element
      lazy val edge = predicate.element: Edge

      type     In = Edge#TargetV
      lazy val in = edge.targetV

      type     Out = Edge#Source#Container#Of[Edge]
      lazy val out = edge.source.container.of(edge)
  }

  case class OutE[P <: AnyPredicate { type Element <: AnyEdge }](val predicate: P) extends AnyStep {

      type     Edge = P#Element
      lazy val edge = predicate.element: Edge

      type     In = Edge#SourceV
      lazy val in = edge.sourceV

      type     Out = Edge#Target#Container#Of[Edge]
      lazy val out = edge.target.container.of(edge)
  }


  case class InV[P <: AnyPredicate { type Element <: AnyEdge }](val predicate: P) extends AnyStep {

      type     Edge = P#Element
      lazy val edge = predicate.element: Edge

      type     In = Edge#TargetV
      lazy val in = edge.targetV

      type     Out = Edge#SourceV
      lazy val out = edge.sourceV
  }

  case class OutV[P <: AnyPredicate { type Element <: AnyEdge }](val predicate: P) extends AnyStep {

      type     Edge = P#Element
      lazy val edge = predicate.element: Edge

      type     In = Edge#SourceV
      lazy val in = edge.sourceV

      type     Out = Edge#Target
      lazy val out = edge.target
  }

  // TODO: inV/outV

  case class Source[E <: AnyEdge](val edge: E) extends AnyStep {

    type In = E
    val  in = edge

    type     Out = E#SourceV
    lazy val out = edge.sourceV
  }

  case class Target[E <: AnyEdge](val edge: E) extends AnyStep {

    type In = E
    val  in = edge

    type     Out = E#TargetV
    lazy val out = edge.targetV
  }

  case class GraphQuery[S <: AnySchema, P <: AnyPredicate, C <: AnyContainer](val graph: S, val predicate: P, val container: C)
    extends Step[S, C#Of[P#Element]](graph, container.of(predicate.element))

  // case class GraphQuery[S <: AnySchema, P <: AnyPredicate](s: S, p: P)
  //   extends Query[S, P, ManyOrNone](s, p, ManyOrNone)

  // case class IndexQuery[S <: AnySchema, I <: AnyIndex, P <: AnyPredicate, C <: AnyContainer](s: S, i: I, p: P)
  //   (implicit 
  //     check: I#PredicateRestriction[P],
  //     cont: IndexContainer[I] { type Out = C }
  //   ) extends Query[S, P, C](s, p, cont.apply)

}
