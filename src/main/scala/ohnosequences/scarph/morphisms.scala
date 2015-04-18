package ohnosequences.scarph


/* Basic set of morphisms: */
object morphisms {

  import graphTypes._, predicates._, monoidalStructures._

  trait AnyPrimitive extends AnyGraphMorphism { morph =>

    type Dagger <: AnyPrimitive {
      type Dagger >: morph.type <: AnyPrimitive
    }
  }

  // id: X → X
  case class id[X <: AnyGraphObject](x: X) extends AnyPrimitive {

    type     In = X
    lazy val in = x

    type     Out = X
    lazy val out = x

    type     Dagger = id[X]
    lazy val dagger = id(x)

    lazy val label = s"id(${x.label})"
  }


  // I → X
  case class fromUnit[X <: AnyGraphObject](val obj: X) extends AnyPrimitive {
    
    type Obj = X

    type     In = unit
    lazy val in = unit

    type     Out = Obj
    lazy val out = obj

    type     Dagger = toUnit[Obj]
    lazy val dagger = toUnit(obj)

    lazy val label = s"fromUnit(${obj.label})"
  }

  // X → I
  case class toUnit[X <: AnyGraphObject](x: X) extends AnyPrimitive {

    type Obj = X

    type     Out = unit
    lazy val out = unit

    type     In = X
    lazy val in = x

    type     Dagger = fromUnit[X]
    lazy val dagger = fromUnit(x)

    lazy val label = s"toUnit(${x.label})" 
  }

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
  case class matchUp[X <: AnyGraphObject](x: X) extends AnyPrimitive {
    
    type     Out = X
    lazy val out = x

    type     In = X ⊗ X
    lazy val in = x ⊗ x

    type     Dagger = duplicate[X]
    lazy val dagger = duplicate(x)

    lazy val label = s"matchUp(${x.label} ⊗ ${x.label})"
  }


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
  case class toZero[X <: AnyGraphObject](x: X) extends AnyPrimitive {
    
    type     Out = zero
    lazy val out = zero

    type     In = X
    lazy val in = x

    type     Dagger = fromZero[X]
    lazy val dagger = fromZero(x)

    lazy val label = s"toZero(${x.label})" 
  }
  
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
  case class merge[X <: AnyGraphObject](x: X) extends AnyPrimitive {
    
    type     Out = X
    lazy val out = x

    type     In = BiproductObj[X, X]
    lazy val in = BiproductObj(x, x)

    type     Dagger = split[X]
    lazy val dagger = split(x)

    lazy val label = s"merge(${x.label} ⊕ ${x.label})"
  }


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
  case class leftProj[B <: AnyBiproductObj](val biproduct: B) extends AnyPrimitive {
    
    type Biproduct = B

    type     Out = Biproduct#Left
    lazy val out = biproduct.left

    type     In = Biproduct
    lazy val in = biproduct

    type     Dagger = leftInj[Biproduct]
    lazy val dagger = leftInj(biproduct)

    lazy val label = s"leftProj(${biproduct.label})" 
  }


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
  case class rightProj[B <: AnyBiproductObj](val biproduct: B) extends AnyPrimitive {
    
    type Biproduct = B

    type     Out = Biproduct#Right
    lazy val out = biproduct.right

    type     In = Biproduct
    lazy val in = biproduct

    type     Dagger = rightInj[Biproduct]
    lazy val dagger = rightInj(biproduct)

    lazy val label = s"leftProj(${biproduct.label})" 
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

  case class inE[E <: AnyEdge](val edge: E) extends AnyPrimitive {
    
    type Edge = E

    type     Out = Edge
    lazy val out = edge

    type     In = Edge#TargetVertex
    lazy val in = edge.targetVertex

    type     Dagger = target[Edge]
    lazy val dagger = target(edge)


    lazy val label = s"inE(${edge.label})"
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

  case class outE[E <: AnyEdge](val edge: E) extends AnyPrimitive {
    
    type Edge = E

    type     Out = Edge
    lazy val out = edge

    type     In = Edge#SourceVertex
    lazy val in = edge.sourceVertex

    type     Dagger = source[Edge]
    lazy val dagger = source(edge)

    lazy val label = s"outE(${edge.label})"
  }


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

  case class inV[E <: AnyEdge](val edge: E) extends AnyPrimitive {
    
    type Edge = E

    type     Out = Edge#SourceVertex
    lazy val out = edge.sourceVertex

    type     In = Edge#TargetVertex
    lazy val in = edge.targetVertex

    type     Dagger = outV[Edge]
    lazy val dagger = outV(edge)

    lazy val label = s"inV(${edge.label})"
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

    type     Out = Property#Owner
    lazy val out = property.owner

    type     In = Property#Value
    lazy val in = property.value

    type Dagger = get[Property]
    lazy val dagger = get(property)

    lazy val label = s"lookup(${property.label})"
  }


  case class quantify[P <: AnyPredicate](val predicate: P) extends AnyPrimitive {
    
    type Predicate = P

    type     In = Predicate#Element
    lazy val in = predicate.element

    type     Out = Predicate
    lazy val out = predicate

    type     Dagger = coerce[Predicate]
    lazy val dagger = coerce(predicate)

    lazy val label: String = s"quantify(${predicate.label})"
  }


  case class coerce[P <: AnyPredicate](val predicate: P) extends AnyPrimitive {
    
    type Predicate = P

    type     Out = Predicate#Element
    lazy val out = predicate.element

    type     In = Predicate
    lazy val in = predicate

    type     Dagger = quantify[Predicate]
    lazy val dagger = quantify(predicate)

    lazy val label: String = s"coerce(${predicate.label})"
  }

}
