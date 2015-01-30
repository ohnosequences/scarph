package ohnosequences.scarph


/* Basic steps: */
object steps {

  import graphTypes._, predicates._, schemas._, indexes._

  // △: T → T ⊕ T
  case class Fork[T <: AnyGraphType](t: T) extends AnyGraphMorphism {

    type     In = T#In
    lazy val in = t.in

    type     Out = Biproduct[T#Out, T#Out]
    lazy val out = Biproduct(t.out, t.out): Out

    lazy val label = s"fork(${t.label})"
  }

  // ▽: T ⊕ T → T
  case class Merge[T <: AnyGraphType](t: T) extends AnyGraphMorphism {

    type     In = Biproduct[T#In, T#In]
    lazy val in = Biproduct(t.in, t.in): In

    type     Out = T#Out
    lazy val out = t.out

    lazy val label = s"merge(${t.label} ⊕ ${t.label})"
  }

  case class InE[P <: AnyPredicate { type Element <: AnyEdge }](val predicate: P) extends AnyGraphMorphism {
    type Predicate = P

    type     Edge = Predicate#Element
    lazy val edge = predicate.element //: Edge

    type     In = Edge#Target
    lazy val in = edge.target

    type     Out = Edge
    lazy val out = edge

    lazy val label: String = s"inE(${edge.label}, ${predicate.label})"
  }

  case class OutE[P <: AnyPredicate { type Element <: AnyEdge }](val predicate: P) extends AnyGraphMorphism {
    type Predicate = P

    type     Edge = Predicate#Element
    lazy val edge = predicate.element: Edge

    type     In = Edge#Source
    lazy val in = edge.source

    type     Out = Edge
    lazy val out = edge

    lazy val label: String = s"outE(${edge.label}, ${predicate.label})"
  }

  case class InV[P <: AnyPredicate { type Element <: AnyEdge }](val predicate: P) extends AnyGraphMorphism {
    type Predicate = P

    type     Edge = Predicate#Element
    lazy val edge = predicate.element: Edge

    type     In = Edge#Target
    lazy val in = edge.target

    type     Out = Edge#Source
    lazy val out = edge.source

    lazy val label: String = s"inV(${predicate.label})"
  }

  case class OutV[P <: AnyPredicate { type Element <: AnyEdge }](val predicate: P) extends AnyGraphMorphism {
    type Predicate = P

    type     Edge = Predicate#Element
    lazy val edge = predicate.element: Edge

    type     In = Edge#Source
    lazy val in = edge.source

    type     Out = Edge#Target
    lazy val out = edge.target

    lazy val label: String = s"outV(${predicate.label})"
  }

  case class Source[E <: AnyEdge](val edge: E) extends AnyGraphMorphism {
    type Edge = E

    type     In = Edge
    lazy val in = edge

    type     Out = Edge#Source
    lazy val out = edge.source

    lazy val label: String = s"source(${edge.label})"
  }

  case class Target[E <: AnyEdge](val edge: E) extends AnyGraphMorphism {
    type Edge = E

    type     In = Edge
    lazy val in = edge

    type     Out = Edge#Target
    lazy val out = edge.target

    lazy val label: String = s"target(${edge.label})"
  }

  case class Get[P <: AnyGraphProperty](val property: P) extends AnyGraphMorphism {
    type Property = P

    type     In = Property#Owner
    lazy val in = property.owner

    type     Out = Property
    lazy val out = property

    lazy val label: String = s"get(${property.label})"
  }

  case class GraphQuery[S <: AnyGraphSchema, P <: AnyPredicate](val graph: S, val predicate: P) extends AnyGraphMorphism {
    type Predicate = P

    type     In = S
    lazy val in = graph

    type     Out = Predicate#Element
    lazy val out = predicate.element

    lazy val label = s"query(${predicate.label})"
  }

}
