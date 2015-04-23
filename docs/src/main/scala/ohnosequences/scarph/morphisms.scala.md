
```scala
package ohnosequences.scarph

import ohnosequences.cosas.types._
```

Basic set of morphisms:

```scala
object morphisms {

  import objects._
```

Morphisms are spans

```scala
  trait AnyGraphMorphism extends AnyGraphType { morphism =>

    type In <: AnyGraphObject
    val  in: In

    type Out <: AnyGraphObject
    val  out: Out

    type Dagger <: AnyGraphMorphism
    val  dagger: Dagger
  }

  type -->[A <: AnyGraphObject, B <: AnyGraphObject] = AnyGraphMorphism { type In = A; type Out = B }
```

Sequential composition of two morphisms

```scala
  sealed trait AnyComposition extends AnyGraphMorphism { composition =>

    type First <: AnyGraphMorphism
    type Second <: AnyGraphMorphism //{ type In = First#Out }

    type In  <: First#In
    type Out <: Second#Out
  }

  case class Composition[
    F <: AnyGraphMorphism,
    S <: AnyGraphMorphism //{ type In = F#Out }
  ] (val first: F, val second: S) extends AnyComposition { cc =>

    type First = F
    type Second = S

    type     In = First#In
    lazy val in = first.in: In

    type     Out = Second#Out
    lazy val out = second.out: Out

    type     Dagger = Composition[Second#Dagger, First#Dagger]
    lazy val dagger: Dagger = Composition(second.dagger, first.dagger)

    lazy val label: String = s"(${first.label} >=> ${second.label})"
  }
```

Basic aliases

```scala
  type >=>[F <: AnyGraphMorphism, S <: AnyGraphMorphism { type In = F#Out }] = Composition[F, S]

  implicit def graphMorphismOps[F <: AnyGraphMorphism](f: F):
        GraphMorphismOps[F] =
    new GraphMorphismOps[F](f)

  case class GraphMorphismOps[F <: AnyGraphMorphism](val f: F) extends AnyVal {

    def >=>[S <: AnyGraphMorphism { type In = F#Out }](s: S): F >=> S = Composition(f, s)

    def ⊗[S <: AnyGraphMorphism](q: S): TensorMorph[F, S] = TensorMorph(f, q)
    def ⊕[S <: AnyGraphMorphism](q: S): BiproductMorph[F, S] = BiproductMorph(f, q)

    // TODO: remove this, use the eval-specific typeclass
    // import evals._

    // def evalOn[I, O](input: F#In := I)
    //   (implicit eval: EvalOn[I, F, O]): F#Out := O = eval(f)(input)

    // def present(implicit eval: Eval[F]): String = eval.present(f)
  }

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
  case class fork[X <: AnyGraphObject](x: X) extends AnyPrimitive {

    type     In = X
    lazy val in = x

    type     Out = BiproductObj[X, X]
    lazy val out = BiproductObj(x, x)

    type     Dagger = merge[X]
    lazy val dagger = merge(x)

    lazy val label = s"fork(${x.label})"
  }

  // X ⊕ X -> X
  case class merge[X <: AnyGraphObject](x: X) extends AnyPrimitive {

    type     Out = X
    lazy val out = x

    type     In = BiproductObj[X, X]
    lazy val in = BiproductObj(x, x)

    type     Dagger = fork[X]
    lazy val dagger = fork(x)

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



  sealed trait AnyTensorMorph extends AnyGraphMorphism { tensor =>

    type Left <: AnyGraphMorphism
    val  left: Left

    type Right <: AnyGraphMorphism
    val  right: Right

    type In  <: AnyTensorObj { type Left = tensor.Left#In; type Right = tensor.Right#In }
    type Out <: AnyTensorObj { type Left = tensor.Left#Out; type Right = tensor.Right#Out }

    type Dagger <: AnyTensorMorph { 
      type Left = tensor.Left#Dagger; 
      type Right = tensor.Right#Dagger
    }
  }

  case class TensorMorph[L <: AnyGraphMorphism, R <: AnyGraphMorphism]
    (val left: L, val right: R) extends AnyTensorMorph { tensor =>

    type Left = L
    type Right = R

    type     In = TensorObj[Left#In, Right#In]
    lazy val in = TensorObj(left.in, right.in): In

    type     Out = TensorObj[Left#Out, Right#Out]
    lazy val out = TensorObj(left.out, right.out): Out

    type     Dagger = TensorMorph[Left#Dagger, Right#Dagger]
    lazy val dagger = TensorMorph(left.dagger: Left#Dagger, right.dagger: Right#Dagger)

    lazy val label = s"(${left.label} ⊗ ${right.label})"
  }

  sealed trait AnyBiproductMorph extends AnyGraphMorphism { biprod =>

    type Left <: AnyGraphMorphism
    val  left: Left

    type Right <: AnyGraphMorphism
    val  right: Right

    type In  <: BiproductObj[Left#In, Right#In]
    type Out <: BiproductObj[Left#Out, Right#Out]

    type Dagger <: AnyBiproductMorph {
      type Left = biprod.Left#Dagger 
      type Right = biprod.Right#Dagger
    }
  }

  case class BiproductMorph[L <: AnyGraphMorphism, R <: AnyGraphMorphism]
    (val left: L, val right: R) extends AnyBiproductMorph { biprod =>

    type Left = L
    type Right = R

    type     In = BiproductObj[Left#In, Right#In]
    lazy val in = BiproductObj(left.in, right.in): In

    type     Out = BiproductObj[Left#Out, Right#Out]
    lazy val out = BiproductObj(left.out, right.out): Out

    type     Dagger = BiproductMorph[Left#Dagger, Right#Dagger]
    lazy val dagger = BiproductMorph(left.dagger: Left#Dagger, right.dagger: Right#Dagger)

    lazy val label = s"(${left.label} ⊕ ${right.label})"
  }


}

```


------

### Index

+ src
  + test
    + scala
      + ohnosequences
        + scarph
          + [TwitterQueries.scala][test/scala/ohnosequences/scarph/TwitterQueries.scala]
          + impl
            + [dummyTest.scala][test/scala/ohnosequences/scarph/impl/dummyTest.scala]
            + [dummy.scala][test/scala/ohnosequences/scarph/impl/dummy.scala]
          + [TwitterSchema.scala][test/scala/ohnosequences/scarph/TwitterSchema.scala]
  + main
    + scala
      + ohnosequences
        + scarph
          + [morphisms.scala][main/scala/ohnosequences/scarph/morphisms.scala]
          + [objects.scala][main/scala/ohnosequences/scarph/objects.scala]
          + [evals.scala][main/scala/ohnosequences/scarph/evals.scala]
          + [implementations.scala][main/scala/ohnosequences/scarph/implementations.scala]
          + [schemas.scala][main/scala/ohnosequences/scarph/schemas.scala]
          + [naturalIsomorphisms.scala][main/scala/ohnosequences/scarph/naturalIsomorphisms.scala]
          + syntax
            + [morphisms.scala][main/scala/ohnosequences/scarph/syntax/morphisms.scala]
            + [objects.scala][main/scala/ohnosequences/scarph/syntax/objects.scala]

[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: ../../../../test/scala/ohnosequences/scarph/impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: ../../../../test/scala/ohnosequences/scarph/impl/dummy.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: morphisms.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: objects.scala.md
[main/scala/ohnosequences/scarph/evals.scala]: evals.scala.md
[main/scala/ohnosequences/scarph/implementations.scala]: implementations.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: schemas.scala.md
[main/scala/ohnosequences/scarph/naturalIsomorphisms.scala]: naturalIsomorphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: syntax/objects.scala.md