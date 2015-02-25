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

  // η: I → X
  case class unitor[X <: AnyGraphObject](x: X) extends AnyPrimitive {

    type     In = unit
    lazy val in = unit

    type     Out = X
    lazy val out = x

    type     Dagger = counitor[X]
    lazy val dagger = counitor(x)

    lazy val label = s"unitor(${x.label})"
  }

  // ε: X → I
  case class counitor[X <: AnyGraphObject](x: X) extends AnyPrimitive {

    type     In = X
    lazy val in = x

    type     Out = unit
    lazy val out = unit

    type     Dagger = unitor[X]
    lazy val dagger = unitor(x)

    lazy val label = s"counitor(${x.label})"
  }


  // △: X → X ⊗ X
  case class fork[X <: AnyGraphObject](x: X) extends AnyPrimitive {

    type     In = X
    lazy val in = x

    type     Out = X ⊗ X
    lazy val out = x ⊗ x

    type     Dagger = merge[X]
    lazy val dagger = merge(x)

    lazy val label = s"fork(${x.label})"
  }

  // ▽: X ⊗ X → X
  case class merge[X <: AnyGraphObject](x: X) extends AnyPrimitive {

    type     In = X ⊗ X
    lazy val in = x ⊗ x

    type     Out = X
    lazy val out = x

    type     Dagger = fork[X]
    lazy val dagger = fork(x)

    lazy val label = s"merge(${x.label} ⊗ ${x.label})"
  }


  // λ: I ⊗ X → X
  case class leftUnitor[X <: AnyGraphObject](x: X) extends AnyPrimitive {

    type     In = unit ⊗ X
    lazy val in = unit ⊗ x

    type     Out = X
    lazy val out = x

    type     Dagger = leftCounitor[X]
    lazy val dagger = leftCounitor(x)

    lazy val label = s"leftUnitor(I ⊗ ${x.label})"
  }

  // λ: I ⊗ X → X
  case class leftCounitor[X <: AnyGraphObject](x: X) extends AnyPrimitive {

    type     In = X
    lazy val in = x

    type     Out = unit ⊗ X
    lazy val out = unit ⊗ x

    type     Dagger = leftUnitor[X]
    lazy val dagger = leftUnitor(x)

    lazy val label = s"leftCounitor(${x.label})"
  }


  // ρ: X ⊗ I → X
  // TODO: ...


  // δ: X -> X ⊕ X
  case class either[X <: AnyGraphObject](x: X) extends AnyPrimitive {

    type     In = X
    lazy val in = x

    type     Out = BiproductObj[X, X]
    lazy val out = BiproductObj(x, x)

    type     Dagger = anyOf[X]
    lazy val dagger = anyOf(x)

    lazy val label = s"either(${x.label})"
  }

  // ε: X ⊕ X -> X
  case class anyOf[X <: AnyGraphObject](x: X) extends AnyPrimitive {

    type     In = BiproductObj[X, X]
    lazy val in = BiproductObj(x, x)

    type     Out = X
    lazy val out = x

    type     Dagger = either[X]
    lazy val dagger = either(x)

    lazy val label = s"anyOf(${x.label} ⊕ ${x.label})"
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


  trait AnyIsomorphism extends AnyPrimitive

  case class symmetry[L <: AnyGraphObject, R <: AnyGraphObject](l: L, r: R) extends AnyIsomorphism {

    type     In = L ⊗ R
    lazy val in = l ⊗ r

    type     Out = R ⊗ L
    lazy val out = r ⊗ l

    type     Dagger = symmetry[R, L]
    lazy val dagger = symmetry(r, l)

    lazy val label: String = s"symmetry(${l.label}, ${r.label})"
  }

  case class distribute[U <: AnyGraphObject, A <: AnyGraphObject, B <: AnyGraphObject]
    (u: U, a: A, b: B) extends AnyIsomorphism {

    type     In = U ⊗ (A ⊕ B)
    lazy val in = u ⊗ (a ⊕ b)

    type     Out = (U ⊗ A) ⊕ (U ⊗ B)
    lazy val out = (u ⊗ a) ⊕ (u ⊗ b)

    type     Dagger = undistribute[U, A, B]
    lazy val dagger = undistribute(u, a, b)

    lazy val label: String = s"distribute(${u.label} ⊗ (${a.label} ⊕ ${b.label}))"
  }

  case class undistribute[U <: AnyGraphObject, A <: AnyGraphObject, B <: AnyGraphObject]
    (u: U, a: A, b: B) extends AnyIsomorphism {

    type     In = (U ⊗ A) ⊕ (U ⊗ B)
    lazy val in = (u ⊗ a) ⊕ (u ⊗ b)

    type     Out = U ⊗ (A ⊕ B)
    lazy val out = u ⊗ (a ⊕ b)

    type     Dagger = distribute[U, A, B]
    lazy val dagger = distribute(u, a, b)

    lazy val label: String = s"undistribute((${u.label} ⊗ ${a.label}) ⊕ (${u.label} ⊗ ${b.label}))"
  }

}
