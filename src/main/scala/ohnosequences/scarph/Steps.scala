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

  case class GraphQuery[S <: AnySchema, C <: AnyContainer, P <: AnyPredicate]
    (val graph: S, val container: C, val predicate: P)
      extends Step[S, C#Of[P#Element]](graph, container.of(predicate.element))


  case class Merge[
    First <: AnyPath, // { type Out <: AnyGraphType { type Insinde = T } },
    Second <: AnyPath { type Out <: AnyGraphType { type Insinde = First#Out#Inside } },
    OutC <: AnyContainer
  ](first: First, second: Second)
    (implicit val sum: (First#Out#Container + Second#Out#Container) { type Out = OutC }) extends AnyStep {

    lazy val outC = sum(first.out.container, second.out.container): OutC

    type     In = ParType[First#In, Second#In]
    lazy val in = ParType(first.in, second.in): In

    type     Out = OutC#Of[First#Out#Inside]
    lazy val out = outC.of(first.out.inside): Out
  }

}
