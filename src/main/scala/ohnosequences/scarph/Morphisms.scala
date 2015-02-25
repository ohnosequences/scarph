package ohnosequences.scarph


/* Basic set of morphisms: */
object morphisms {

  import graphTypes._, predicates._, monoidalStructures._

  trait AnyPrimitive extends AnyGraphMorphism { p =>

    type Dagger <: AnyPrimitive {
      type Dagger >: p.type <: AnyPrimitive

      type In = p.Out
      type Out = p.In
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
  case class asLeft[B <: AnyBiproductObj](val biproduct: B) extends AnyPrimitive {
    type Biproduct = B

    type     In = Biproduct#Left
    lazy val in = biproduct.left

    type     Out = Biproduct
    lazy val out = biproduct

    type     Dagger = left[Biproduct]
    lazy val dagger = left(biproduct)

    lazy val label = s"(${biproduct.left.label} asLeft ${biproduct.label})"
  }

  case class left[B <: AnyBiproductObj](val biproduct: B) extends AnyPrimitive {
    type Biproduct = B

    type     In = Biproduct
    lazy val in = biproduct

    type     Out = Biproduct#Left
    lazy val out = biproduct.left

    type     Dagger = asLeft[Biproduct]
    lazy val dagger = asLeft(biproduct)

    lazy val label = s"left(${biproduct.label})"
  }


  case class asRight[B <: AnyBiproductObj](val biproduct: B) extends AnyPrimitive {
    type Biproduct = B

    type     In = Biproduct#Right
    lazy val in = biproduct.right

    type     Out = Biproduct
    lazy val out = biproduct

    type     Dagger = right[Biproduct]
    lazy val dagger = right(biproduct)

    lazy val label = s"(${biproduct.right.label} asRight ${biproduct.label})"
  }

  case class right[B <: AnyBiproductObj](val biproduct: B) extends AnyPrimitive {
    type Biproduct = B

    type     In = Biproduct
    lazy val in = biproduct

    type     Out = Biproduct#Right
    lazy val out = biproduct.right

    type     Dagger = asRight[Biproduct]
    lazy val dagger = asRight(biproduct)

    lazy val label = s"$right({biproduct.label})"
  }


  case class inE[E <: AnyEdge](val edge: E) extends AnyPrimitive {
    type Edge = E
    
    type     In = Edge#TargetVertex
    lazy val in = edge.targetVertex

    type     Out = Edge
    lazy val out = edge

    type     Dagger = target[Edge]
    lazy val dagger = target(edge)

    lazy val label: String = s"inE(${edge.label})"
  }

  case class target[E <: AnyEdge](val edge: E) extends AnyPrimitive {
    type Edge = E

    type     In = Edge
    lazy val in = edge

    type     Out = Edge#TargetVertex
    lazy val out = edge.targetVertex

    type     Dagger = inE[Edge]
    lazy val dagger = inE(edge)

    lazy val label: String = s"target(${edge.label})"
  }


  case class outE[E <: AnyEdge](val edge: E) extends AnyPrimitive {
    type Edge = E
    
    type     In = Edge#SourceVertex
    lazy val in = edge.sourceVertex

    type     Out = Edge
    lazy val out = edge

    type     Dagger = source[Edge]
    lazy val dagger = source(edge)

    lazy val label: String = s"outE(${edge.label})"
  }

  case class source[E <: AnyEdge](val edge: E) extends AnyPrimitive {
    type Edge = E

    type     In = Edge
    lazy val in = edge

    type     Out = Edge#SourceVertex
    lazy val out = edge.sourceVertex

    type     Dagger = outE[Edge]
    lazy val dagger = outE(edge)

    lazy val label: String = s"source(${edge.label})"
  }


  case class inV[E <: AnyEdge](val edge: E) extends AnyPrimitive {
    type Edge = E

    type     In = Edge#TargetVertex
    lazy val in = edge.targetVertex

    type     Out = Edge#SourceVertex
    lazy val out = edge.sourceVertex

    type     Dagger = OutV[Edge]
    lazy val dagger = OutV(edge)

    lazy val label: String = s"inV(${edge.label})"
  }

  case class OutV[E <: AnyEdge](val edge: E) extends AnyPrimitive {
    type Edge = E

    type     In = Edge#SourceVertex
    lazy val in = edge.sourceVertex

    type     Out = Edge#TargetVertex
    lazy val out = edge.targetVertex

    type     Dagger = inV[Edge]
    lazy val dagger = inV(edge)

    lazy val label: String = s"outV(${edge.label})"
  }


  case class get[P <: AnyGraphProperty](val property: P) extends AnyPrimitive {
    type Property = P

    type     In = Property#Owner
    lazy val in = property.owner

    type     Out = Property#Value
    lazy val out = property.value

    type     Dagger = lookup[Property]
    lazy val dagger = lookup(property)

    lazy val label: String = s"get(${property.label})"
  }

  case class lookup[P <: AnyGraphProperty](val property: P) extends AnyPrimitive {
    type Property = P

    type     In = Property#Value
    lazy val in = property.value

    type     Out = Property#Owner
    lazy val out = property.owner

    type     Dagger = get[Property]
    lazy val dagger = get(property)

    lazy val label: String = s"lookup(${property.label})"
  }

}
