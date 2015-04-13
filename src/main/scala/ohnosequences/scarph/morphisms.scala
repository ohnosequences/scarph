package ohnosequences.scarph


/* Basic set of morphisms: */
object morphisms {

  import graphTypes._, predicates._, monoidalStructures._

  trait AnyPrimitive extends AnyGraphMorphism

  trait AnyStraightPrimitive extends AnyPrimitive { morph =>

    type Dagger <: AnyDaggerPrimitive {
      type Dagger >: morph.type <: AnyStraightPrimitive
    }
  }

  trait AnyDaggerPrimitive extends AnyPrimitive {

    type Dagger <: AnyStraightPrimitive
    val  dagger: Dagger
  }

  abstract class DaggerOf[M <: AnyStraightPrimitive](val m: M) extends AnyDaggerPrimitive {

    type     Dagger = M
    lazy val dagger = m

    type     In = Dagger#Out
    lazy val in = dagger.out

    type     Out = Dagger#In
    lazy val out = dagger.in
  }


  // id: X → X
  case class id[X <: AnyGraphObject](x: X) extends AnyStraightPrimitive with AnyDaggerPrimitive {

    type     In = X
    lazy val in = x

    type     Out = X
    lazy val out = x

    type     Dagger = id[X]
    lazy val dagger = id(x)

    lazy val label = s"id(${x.label})"
  }


  // I → X
  case class fromUnit[X <: AnyGraphObject](x: X) extends AnyStraightPrimitive {

    type     In = unit
    lazy val in = unit

    type     Out = X
    lazy val out = x

    type     Dagger = toUnit[X]
    lazy val dagger = toUnit(x)

    lazy val label = s"fromUnit(${x.label})"
  }

  // X → I
  case class toUnit[X <: AnyGraphObject](x: X)
    extends DaggerOf(fromUnit[X](x)) { lazy val label = s"toUnit(${x.label})" }


  // △: X → X ⊗ X
  case class duplicate[X <: AnyGraphObject](x: X) extends AnyStraightPrimitive {

    type     In = X
    lazy val in = x

    type     Out = X ⊗ X
    lazy val out = x ⊗ x

    type     Dagger = matchUp[X]
    lazy val dagger = matchUp(x)

    lazy val label = s"duplicate(${x.label})"
  }

  // ▽: X ⊗ X → X
  case class matchUp[X <: AnyGraphObject](x: X)
    extends DaggerOf(duplicate[X](x)) { lazy val label = s"matchUp(${x.label} ⊗ ${x.label})" }


  // 0 → X
  case class fromZero[X <: AnyGraphObject](x: X) extends AnyStraightPrimitive {

    type     In = zero
    lazy val in = zero

    type     Out = X
    lazy val out = x

    type     Dagger = toZero[X]
    lazy val dagger = toZero(x)

    lazy val label = s"fromZero(${x.label})"
  }

  // X → 0
  case class toZero[X <: AnyGraphObject](x: X)
    extends DaggerOf(fromZero[X](x)) { lazy val label = s"toZero(${x.label})" }


  // X -> X ⊕ X
  case class split[X <: AnyGraphObject](x: X) extends AnyStraightPrimitive {

    type     In = X
    lazy val in = x

    type     Out = BiproductObj[X, X]
    lazy val out = BiproductObj(x, x)

    type     Dagger = merge[X]
    lazy val dagger = merge(x)

    lazy val label = s"split(${x.label})"
  }

  // X ⊕ X -> X
  case class merge[X <: AnyGraphObject](x: X)
    extends DaggerOf(split[X](x)) { lazy val label = s"merge(${x.label} ⊕ ${x.label})" }


  // L → L ⊕ R
  case class leftInj[B <: AnyBiproductObj](val biproduct: B) extends AnyStraightPrimitive {
    type Biproduct = B

    type     In = Biproduct#Left
    lazy val in = biproduct.left

    type     Out = Biproduct
    lazy val out = biproduct

    type     Dagger = leftProj[Biproduct]
    lazy val dagger = leftProj(biproduct)

    lazy val label = s"(${biproduct.left.label} leftInj ${biproduct.label})"
  }

  // L ⊕ R → L
  case class leftProj[B <: AnyBiproductObj](b: B)
    extends DaggerOf(leftInj[B](b)) { lazy val label = s"leftProj(${b.label})" }


  // R → L ⊕ R
  case class rightInj[B <: AnyBiproductObj](val biproduct: B) extends AnyStraightPrimitive {
    type Biproduct = B

    type     In = Biproduct#Right
    lazy val in = biproduct.right

    type     Out = Biproduct
    lazy val out = biproduct

    type     Dagger = rightProj[Biproduct]
    lazy val dagger = rightProj(biproduct)

    lazy val label = s"(${biproduct.right.label} rightInj ${biproduct.label})"
  }

  // L ⊕ R → R
  case class rightProj[B <: AnyBiproductObj](b: B)
    extends DaggerOf(rightInj[B](b)) { lazy val label = s"rightProj(${b.label})" }


  case class target[E <: AnyEdge](val edge: E) extends AnyStraightPrimitive {
    type Edge = E

    type     In = Edge
    lazy val in = edge

    type     Out = Edge#TargetVertex
    lazy val out = edge.targetVertex

    type     Dagger = inE[Edge]
    lazy val dagger = inE(edge)

    lazy val label: String = s"target(${edge.label})"
  }

  case class inE[E <: AnyEdge](val edge: E) extends DaggerOf(target[E](edge)) {
    type Edge = E

    lazy val label = s"inE(${edge.label})"
  }


  case class source[E <: AnyEdge](val edge: E) extends AnyStraightPrimitive {
    type Edge = E

    type     In = Edge
    lazy val in = edge

    type     Out = Edge#SourceVertex
    lazy val out = edge.sourceVertex

    type     Dagger = outE[Edge]
    lazy val dagger = outE(edge)

    lazy val label: String = s"source(${edge.label})"
  }

  case class outE[E <: AnyEdge](val edge: E) extends DaggerOf(source[E](edge)) {
    type Edge = E

    lazy val label = s"outE(${edge.label})"
  }


  case class outV[E <: AnyEdge](val edge: E) extends AnyStraightPrimitive {
    type Edge = E

    type     In = Edge#SourceVertex
    lazy val in = edge.sourceVertex

    type     Out = Edge#TargetVertex
    lazy val out = edge.targetVertex

    type     Dagger = inV[Edge]
    lazy val dagger = inV(edge)

    lazy val label: String = s"outV(${edge.label})"
  }

  case class inV[E <: AnyEdge](val edge: E) extends DaggerOf(outV[E](edge)) {
    type Edge = E

    lazy val label = s"inV(${edge.label})"
  }


  case class get[P <: AnyGraphProperty](val property: P) extends AnyStraightPrimitive {
    type Property = P

    type     In = Property#Owner
    lazy val in = property.owner

    type     Out = Property#Value
    lazy val out = property.value

    type     Dagger = lookup[Property]
    lazy val dagger = lookup(property)

    lazy val label: String = s"get(${property.label})"
  }

  case class lookup[P <: AnyGraphProperty](p: P)
    extends DaggerOf(get[P](p)) { lazy val label = s"lookup(${p.label})" }


  case class quantify[P <: AnyPredicate](val predicate: P) extends AnyGraphMorphism {
    type Predicate = P

    type In = Predicate#Element
    lazy val in: In = predicate.element

    type Out = Predicate
    lazy val out: Out = predicate

    type Dagger = coerce[Predicate]
    lazy val dagger: Dagger = coerce(predicate)

    lazy val label: String = s"quantify ${out.label}"
  }


  case class coerce[P <: AnyPredicate](val predicate: P) extends AnyGraphMorphism {
    type Predicate = P

    type In = Predicate
    lazy val in: In = predicate

    type Out = Predicate#Element
    lazy val out: Out = predicate.element

    type Dagger = quantify[Predicate]
    lazy val dagger: Dagger = quantify(predicate)

    lazy val label: String = s"${in.label} as {out.label}"
  }


}
