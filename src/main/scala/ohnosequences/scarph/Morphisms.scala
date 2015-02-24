package ohnosequences.scarph


/* Basic set of morphisms: */
object morphisms {

  import graphTypes._, predicates._, monoidalStructures._
  // , schemas._, indexes._

  trait AnyPrimitive extends AnyGraphMorphism { p =>

    type Dagger <: AnyPrimitive {
      type Dagger >: p.type <: AnyPrimitive
    }
  }

  // △: T → T ⊗ T
  case class fork[T <: AnyGraphObject](t: T) extends AnyPrimitive {

    type     In = T
    lazy val in = t

    type     Out = TensorObj[T, T]
    lazy val out = TensorObj(t, t)

    type     Dagger = merge[T]
    lazy val dagger = merge(t)

    lazy val label = s"fork(${t.label})"
  }

  // ▽: T ⊗ T → T
  case class merge[T <: AnyGraphObject](t: T) extends AnyPrimitive {

    type     In = TensorObj[T, T]
    lazy val in = TensorObj(t, t)

    type     Out = T
    lazy val out = t

    type     Dagger = fork[T]
    lazy val dagger = fork(t)

    lazy val label = s"merge(${t.label} ⊗ ${t.label})"
  }

  // δ: T -> T ⊕ T
  case class either[T <: AnyGraphObject](t: T) extends AnyPrimitive {

    type     In = T
    lazy val in = t

    type     Out = BiproductObj[T, T]
    lazy val out = BiproductObj(t, t)

    type     Dagger = anyOf[T]
    lazy val dagger = anyOf(t)

    lazy val label = s"either(${t.label})"
  }

  // ε: T ⊕ T -> T
  case class anyOf[T <: AnyGraphObject](t: T) extends AnyPrimitive {

    type     In = BiproductObj[T, T]
    lazy val in = BiproductObj(t, t)

    type     Out = T
    lazy val out = t

    type     Dagger = either[T]
    lazy val dagger = either(t)

    lazy val label = s"anyOf(${t.label} ⊕ ${t.label})"
  }


  /* Projections and injections */
  case class asLeft[B <: AnyBiproductObj](b: B) extends AnyPrimitive {

    type     In = B#Left
    lazy val in = b.left

    type     Out = B
    lazy val out = b

    type     Dagger = left[B]
    lazy val dagger = left(b)

    lazy val label = s"(${b.left.label} asLeft ${b.label})"
  }

  case class left[B <: AnyBiproductObj](b: B) extends AnyPrimitive {

    type     In = B
    lazy val in = b

    type     Out = B#Left
    lazy val out = b.left

    type     Dagger = asLeft[B]
    lazy val dagger = asLeft(b)

    lazy val label = s"left(${b.label})"
  }


  case class asRight[B <: AnyBiproductObj](b: B) extends AnyPrimitive {

    type     In = B#Right
    lazy val in = b.right

    type     Out = B
    lazy val out = b

    type     Dagger = right[B]
    lazy val dagger = right(b)

    lazy val label = s"(${b.right.label} asRight ${b.label})"
  }

  case class right[B <: AnyBiproductObj](b: B) extends AnyPrimitive {

    type     In = B
    lazy val in = b

    type     Out = B#Right
    lazy val out = b.right

    type     Dagger = asRight[B]
    lazy val dagger = asRight(b)

    lazy val label = s"$right({b.label})"
  }


  case class InE[E <: AnyEdge](val edge: E) extends AnyPrimitive {
    type Edge = E
    
    type     In = Edge#Target
    lazy val in = edge.target

    type     Out = Edge
    lazy val out = edge

    type     Dagger = Target[Edge]
    lazy val dagger = Target(edge)

    lazy val label: String = s"inE(${edge.label})"
  }

  case class Target[E <: AnyEdge](val edge: E) extends AnyPrimitive {
    type Edge = E

    type     In = Edge
    lazy val in = edge

    type     Out = Edge#Target
    lazy val out = edge.target

    type     Dagger = InE[Edge]
    lazy val dagger = InE(edge)

    lazy val label: String = s"target(${edge.label})"
  }


  case class OutE[E <: AnyEdge](val edge: E) extends AnyPrimitive {
    type Edge = E
    
    type     In = Edge#Source
    lazy val in = edge.source

    type     Out = Edge
    lazy val out = edge

    type     Dagger = Source[Edge]
    lazy val dagger = Source(edge)

    lazy val label: String = s"outE(${edge.label})"
  }

  case class Source[E <: AnyEdge](val edge: E) extends AnyPrimitive {
    type Edge = E

    type     In = Edge
    lazy val in = edge

    type     Out = Edge#Source
    lazy val out = edge.source

    type     Dagger = OutE[Edge]
    lazy val dagger = OutE(edge)

    lazy val label: String = s"source(${edge.label})"
  }


  case class InV[E <: AnyEdge](val edge: E) extends AnyPrimitive {
    type Edge = E

    type     In = Edge#Target
    lazy val in = edge.target

    type     Out = Edge#Source
    lazy val out = edge.source

    type     Dagger = OutV[Edge]
    lazy val dagger = OutV(edge)

    lazy val label: String = s"inV(${edge.label})"
  }

  case class OutV[E <: AnyEdge](val edge: E) extends AnyPrimitive {
    type Edge = E

    type     In = Edge#Source
    lazy val in = edge.source

    type     Out = Edge#Target
    lazy val out = edge.target

    type     Dagger = InV[Edge]
    lazy val dagger = InV(edge)

    lazy val label: String = s"outV(${edge.label})"
  }

}
