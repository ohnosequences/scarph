package ohnosequences.scarph


/* Basic steps: */
object steps {

  import graphTypes._, paths._, containers._, predicates._, schemas._


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

  case class GraphQuery[S <: AnySchema, P <: AnyPredicate](val graph: S, val predicate: P)
    extends Step[S, ManyOrNone#Of[P#Element]](graph, ManyOrNone.of(predicate.element))

}
