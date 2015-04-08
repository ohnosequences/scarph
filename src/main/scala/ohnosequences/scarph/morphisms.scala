package ohnosequences.scarph


/* Basic set of morphisms: */
object morphisms {

  import graphTypes._, predicates._, monoidalStructures._

  trait AnyPrimitive extends AnyGraphMorphism { morph =>

    type Dagger <: AnyDaggerPrimitive {
      type Dagger >: morph.type <: AnyPrimitive
    }
  }

  trait AnyDaggerPrimitive extends AnyGraphMorphism {

    type Dagger <: AnyPrimitive
    val  dagger: Dagger
  }

  abstract class DaggerOf[M <: AnyPrimitive](val m: M) extends AnyDaggerPrimitive {

    type     Dagger = M
    lazy val dagger = m

    type     In = Dagger#Out
    lazy val in = dagger.out

    type     Out = Dagger#In
    lazy val out = dagger.in
  }


  // id: X → X
  case class id[X <: AnyGraphObject](x: X) extends AnyPrimitive with AnyDaggerPrimitive {

    type     In = X
    lazy val in = x

    type     Out = X
    lazy val out = x

    type     Dagger = id[X]
    lazy val dagger = id(x)

    lazy val label = s"id(${x.label})"
  }


  // I → X
  case class fromUnit[X <: AnyGraphObject](x: X) extends AnyPrimitive {

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


//  // A → I → B
//  case class unitMorph[A <: AnyGraphObject, B <: AnyGraphObject](a: A, b: B)
//    extends AnyPrimitive with AnyDaggerPrimitive {
//
//    type     In = A
//    lazy val in = a
//
//    type     Out = B
//    lazy val out = b
//
//    type     Dagger = unitMorph[B, A]
//    lazy val dagger = unitMorph(b, a)
//
//    lazy val label = s"unitMorph(${a.label}, ${b.label})"
//  }


  // △: X → X ⊗ X
  case class duplicate[X <: AnyGraphObject](x: X) extends AnyPrimitive {

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
  case class fromZero[X <: AnyGraphObject](x: X) extends AnyPrimitive {

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


//  // A → 0 → B
//  case class zeroMorph[A <: AnyGraphObject, B <: AnyGraphObject](a: A, b: B)
//    extends AnyPrimitive with AnyDaggerPrimitive {
//
//    type     In = A
//    lazy val in = a
//
//    type     Out = B
//    lazy val out = b
//
//    type     Dagger = zeroMorph[B, A]
//    lazy val dagger = zeroMorph(b, a)
//
//    lazy val label = s"zeroMorph(${a.label}, ${b.label})"
//  }


  // X -> X ⊕ X
  case class split[X <: AnyGraphObject](x: X) extends AnyPrimitive {

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
  case class leftInj[B <: AnyBiproductObj](val biproduct: B) extends AnyPrimitive {
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
  case class rightInj[B <: AnyBiproductObj](val biproduct: B) extends AnyPrimitive {
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

  case class inE[E <: AnyEdge](e: E)
    extends DaggerOf(target[E](e)) { lazy val label = s"inE(${e.label})" }


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

  case class outE[E <: AnyEdge](e: E)
    extends DaggerOf(source[E](e)) { lazy val label = s"outE(${e.label})" }


  case class outV[E <: AnyEdge](val edge: E) extends AnyPrimitive {
    type Edge = E

    type     In = Edge#SourceVertex
    lazy val in = edge.sourceVertex

    type     Out = Edge#TargetVertex
    lazy val out = edge.targetVertex

    type     Dagger = inV[Edge]
    lazy val dagger = inV(edge)

    lazy val label: String = s"outV(${edge.label})"
  }

  case class inV[E <: AnyEdge](e: E)
    extends DaggerOf(outV[E](e)) { lazy val label = s"inV(${e.label})" }


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

  case class lookup[P <: AnyGraphProperty](p: P)
    extends DaggerOf(get[P](p)) { lazy val label = s"lookup(${p.label})" }


}
