package ohnosequences.scarph


/* Basic steps: */
object steps {

  import graphTypes._, predicates._, schemas._, indexes._

  // △: T → T ⊗ T
  case class Fork[T <: AnyGraphType](t: T) extends AnyGraphMorphism {

    type     In = T
    lazy val in = t

    type     Out = Tensor[T, T]
    lazy val out = Tensor(t, t)

    type Dagger = Merge[T]
    lazy val dagger: Dagger = Merge(t)

    lazy val label = s"fork(${t.label})"
  }

  // ▽: T ⊗ T → T
  case class Merge[T <: AnyGraphType](t: T) extends AnyGraphMorphism {

    type     In = Tensor[T, T]
    lazy val in = Tensor(t, t)

    type     Out = T
    lazy val out = t

    type Dagger = Fork[T]
    lazy val dagger: Dagger = Fork(t)

    lazy val label = s"merge(${t.label} ⊗ ${t.label})"
  }

  // δ: T -> T ⊕ T
  case class either[T <: AnyGraphType](t: T) extends AnyGraphMorphism {

    type     In = T
    lazy val in = t

    type     Out = Biproduct[T, T]
    lazy val out = Biproduct(t, t)

    type Dagger = anyOf[T]
    lazy val dagger: Dagger = anyOf(t)

    lazy val label = s"either(${t.label})"
  }

  // ε: T ⊕ T -> T
  case class anyOf[T <: AnyGraphType](t: T) extends AnyGraphMorphism {

    type     In = Biproduct[T, T]
    lazy val in = Biproduct(t, t)

    type     Out = T
    lazy val out = t

    type Dagger = either[T]
    lazy val dagger: Dagger = either(t)

    lazy val label = s"anyOf(${t.label} ⊕ ${t.label})"
  }

  // projections and injections

  case class asLeft[B <: AnyBiproduct](b: B) extends AnyGraphMorphism {

    type     In = B#Left
    lazy val in = b.left

    type     Out = B
    lazy val out = b

    type Dagger = left[B]
    lazy val dagger: Dagger = left(b)

    lazy val label = s"${b.left.label} inL ${b.label}"
  }

  case class left[B <: AnyBiproduct](b: B) extends AnyGraphMorphism {

    type In = B
    lazy val in = b

    type Out = B#Left
    lazy val out: Out = b.left

    type Dagger = asLeft[B]
    lazy val dagger: Dagger = asLeft(b)

    lazy val label = s"(${b.label}).left"
  }

  case class asRight[B <: AnyBiproduct](b: B) extends AnyGraphMorphism {

    type     In = B#Right
    lazy val in = b.right

    type     Out = B
    lazy val out = b

    type Dagger = right[B]
    lazy val dagger: Dagger = right(b)

    lazy val label = s"${b.right.label} inL ${b.label}"
  }

  case class right[B <: AnyBiproduct](b: B) extends AnyGraphMorphism {

    type In = B
    lazy val in = b

    type Out = B#Right
    lazy val out: Out = b.right

    type Dagger = asRight[B]
    lazy val dagger: Dagger = asRight(b)

    lazy val label = s"(${b.label}).right"
  }

  case class InE[P <: AnyPredicate { type Element <: AnyEdge }](val predicate: P) extends AnyGraphMorphism {
    
    type Predicate = P

    type     Edge = Predicate#Element
    lazy val edge = predicate.element //: Edge

    type     In = Edge#Target
    lazy val in = edge.target

    type     Out = Edge
    lazy val out = edge

    // TODO: what about the predicate??
    type Dagger = Target[Edge]
    lazy val dagger: Dagger = Target(edge)

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

    // TODO: what about the predicate??
    type Dagger = Source[Edge]
    lazy val dagger: Dagger = Source(edge)

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

    type Dagger = OutV[P]
    lazy val dagger: Dagger = OutV(predicate)

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

    type Dagger = InV[P]
    lazy val dagger: Dagger = InV(predicate)

    lazy val label: String = s"outV(${predicate.label})"
  }

  case class Source[E <: AnyEdge](val edge: E) extends AnyGraphMorphism {
    
    type Edge = E

    type     In = Edge
    lazy val in = edge

    type     Out = Edge#Source
    lazy val out = edge.source

    type Dagger = OutE[EmptyPredicate[Edge]]
    lazy val dagger: Dagger = OutE[EmptyPredicate[Edge]](new EmptyPredicate(edge))

    lazy val label: String = s"source(${edge.label})"
  }

  case class Target[E <: AnyEdge](val edge: E) extends AnyGraphMorphism {
    type Edge = E

    type     In = Edge
    lazy val in = edge

    type     Out = Edge#Target
    lazy val out = edge.target

    // TODO: something like this
    type Dagger = InE[EmptyPredicate[Edge]]
    lazy val dagger: Dagger = InE[EmptyPredicate[Edge]](new EmptyPredicate(edge))

    lazy val label: String = s"target(${edge.label})"
  }

  case class Get[P <: AnyGraphProperty](val property: P) extends AnyGraphMorphism {
    type Property = P

    type     In = Property#Owner
    lazy val in = property.owner

    type     Out = Property
    lazy val out = property

    // TODO: lookup for a property value. Maybe a particular graph query?
    type Dagger = Nothing
    lazy val dagger: Dagger = ???

    lazy val label: String = s"get(${property.label})"
  }

  // I don't see the point of this; maybe the lookup property thing?
  case class GraphQuery[S <: AnyGraphSchema, P <: AnyPredicate](val graph: S, val predicate: P) extends AnyGraphMorphism {
    type Predicate = P

    type     In = S
    lazy val in = graph

    type     Out = Predicate#Element
    lazy val out = predicate.element

    // TODO: something like a quantified get?
    type Dagger = Nothing
    lazy val dagger: Dagger = ???

    lazy val label = s"query(${predicate.label})"
  }

}
