package ohnosequences.scarph


/* Basic steps: */
object steps {

  import graphTypes._, paths._, containers._, predicates._, schemas._, indexes._


  case class Get[P <: AnyGraphProperty](val property: P) 
    extends Step[P#Owner, P](property.owner, property) {

      lazy val label: String = s"get(${property.label})"
      type Inside = Get[P]
    }

  case class InE[P <: AnyPredicate { type Element <: AnyEdge }](val predicate: P) extends AnyStep {

      type     Edge = P#Element
      lazy val edge = predicate.element: Edge

      type     In = Edge#TargetV
      lazy val in = edge.targetV

      type     Out = Edge#Source#Container#Of[Edge]
      lazy val out = edge.source.container.of(edge)

      lazy val label: String = s"inE(${edge.label}, ${predicate.label})"
      type Inside = InE[P]
  }

  case class OutE[P <: AnyPredicate { type Element <: AnyEdge }](val predicate: P) extends AnyStep {

      type     Edge = P#Element
      lazy val edge = predicate.element: Edge

      type     In = Edge#SourceV
      lazy val in = edge.sourceV

      type     Out = Edge#Target#Container#Of[Edge]
      lazy val out = edge.target.container.of(edge)

      lazy val label: String = s"outE(${edge.label}, ${predicate.label})"
      type Inside = OutE[P]
  }


  case class InV[P <: AnyPredicate { type Element <: AnyEdge }](val predicate: P) extends AnyStep {

      type     Edge = P#Element
      lazy val edge = predicate.element: Edge

      type     In = Edge#TargetV
      lazy val in = edge.targetV

      type     Out = Edge#SourceV
      lazy val out = edge.sourceV

      lazy val label: String = s"inV(${edge.label}, ${predicate.label})"
      type Inside = InV[P]
  }

  case class OutV[P <: AnyPredicate { type Element <: AnyEdge }](val predicate: P) extends AnyStep {

      type     Edge = P#Element
      lazy val edge = predicate.element: Edge

      type     In = Edge#SourceV
      lazy val in = edge.sourceV

      type     Out = Edge#Target
      lazy val out = edge.target

      lazy val label: String = s"outV(${edge.label}, ${predicate.label})"
      type Inside = OutV[P]
  }

  // TODO: inV/outV

  case class Source[E <: AnyEdge](val edge: E) extends AnyStep {

    type In = E
    val  in = edge

    type     Out = E#SourceV
    lazy val out = edge.sourceV

    lazy val label: String = s"source(${edge.label})"
    type Inside = Source[E]
  }

  case class Target[E <: AnyEdge](val edge: E) extends AnyStep {

    type In = E
    val  in = edge

    type     Out = E#TargetV
    lazy val out = edge.targetV

    lazy val label: String = s"target(${edge.label})"
    type Inside = Target[E]
  }

  // TODO: what's this??
  case class GraphQuery[S <: AnySchema, C <: AnyContainer, P <: AnyPredicate]
    (val graph: S, val c: C, val predicate: P)
      extends Step[S, C#Of[P#Element]](graph, c.of(predicate.element)) {

        // TODO: implement this
        lazy val label: String = toString
        type Inside = GraphQuery[S,C,P]
      }
}
