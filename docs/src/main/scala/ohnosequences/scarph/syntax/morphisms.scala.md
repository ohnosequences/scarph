
```scala
package ohnosequences.scarph.syntax

import ohnosequences.cosas.types._
import ohnosequences.scarph, scarph._


case class GraphMorphismValOps[F <: AnyGraphMorphism, VF](vf: F := VF) extends AnyVal {

  // (F := t) ⊗ (S := s) : (F ⊗ S) := (t, s)
  def ⊗[S <: AnyGraphMorphism, VS](vs: S := VS): TensorMorph[F, S] := (VF, VS) =
    TensorMorph(vf.tpe, vs.tpe) := ((vf.value, vs.value))

  // (F := t) ⊕ (S := s) : (F ⊕ S) := (t, s)
  def ⊕[S <: AnyGraphMorphism, VS](vs: S := VS): BiproductMorph[F, S] := (VF, VS) =
    BiproductMorph(vf.tpe, vs.tpe) := ((vf.value, vs.value))
}

case class GraphMorphismSyntax[F <: AnyGraphMorphism](f: F) extends AnyVal {

  // just an alias for >=> composition:
  def andThen[T <: AnyGraphMorphism { type In = F#Out }](t: T):
    F >=> T =
    f >=> t

  def duplicate:
    F >=> scarph.duplicate[F#Out] =
    f >=> scarph.duplicate(f.out)

  def fork:
    F >=> scarph.fork[F#Out] =
    f >=> scarph.fork(f.out)


  def toUnit:
    F >=> scarph.toUnit[F#Out] =
    f >=> scarph.toUnit(f.out)

  def toZero:
    F >=> scarph.toZero[F#Out] =
    f >=> scarph.toZero(f.out)


  def leftCounit:
    F >=> scarph.leftCounit[F#Out] =
    f >=> scarph.leftCounit(f.out)

  def rightCounit:
    F >=> scarph.rightCounit[F#Out] =
    f >=> scarph.rightCounit(f.out)


  def leftCozero:
    F >=> scarph.leftCozero[F#Out] =
    f >=> scarph.leftCozero(f.out)

  def rightCozero:
    F >=> scarph.rightCozero[F#Out] =
    f >=> scarph.rightCozero(f.out)


  // biproduct injections
  def leftInj[B <: AnyBiproductObj { type Left = F#Out }](b: B):
    F >=> scarph.leftInj[B] =
    f >=> scarph.leftInj(b)

  def rightInj[B <: AnyBiproductObj { type Right = F#Out }](b: B):
    F >=> scarph.rightInj[B] =
    f >=> scarph.rightInj(b)
}

case class SymmetrySyntax[
  L <: AnyGraphObject,
  R <: AnyGraphObject,
  F <: AnyGraphMorphism { type Out = L ⊗ R }
](val f: SymmetryRefine[F]) extends AnyVal {

  def twist:
    F >=> scarph.symmetry[L, R] =
    f >=> scarph.symmetry(f.out.left, f.out.right)
}


case class MatchUpSyntax[
  X <: AnyGraphObject,
  F <: AnyGraphMorphism { type Out = X ⊗ X }
](val f: MatchUpRefine[F]) extends AnyVal {

  def matchUp:
    F >=> scarph.matchUp[X] =
    f >=> scarph.matchUp(f.out.left)
}

case class DistributeSyntax[
  X <: AnyGraphObject,
  A <: AnyGraphObject,
  B <: AnyGraphObject,
  F <: AnyGraphMorphism { type Out = X ⊗ (A ⊕ B) }
](val f: F) {

  def distribute:
    F >=> scarph.distribute[X, A, B] =
    f >=> scarph.distribute(f.out.left, f.out.right.left, f.out.right.right)
}

case class UndistributeSyntax[
  X <: AnyGraphObject,
  A <: AnyGraphObject,
  B <: AnyGraphObject,
  F <: AnyGraphMorphism { type Out = (X ⊗ A) ⊕ (X ⊗ B) }
](val f: F) {

  def distribute:
    F >=> scarph.undistribute[X, A, B] =
    f >=> scarph.undistribute(f.out.left.left, f.out.left.right, f.out.right.right)
}

case class BiproductSyntax[F <: AnyGraphMorphism { type Out <: AnyBiproductObj }](f: F) extends AnyVal {

  def leftProj:
    F >=> scarph.leftProj[F#Out] =
    f >=> scarph.leftProj(f.out)

  def rightProj:
    F >=> scarph.rightProj[F#Out] =
    f >=> scarph.rightProj(f.out)
}

case class AssociateBiproductLeftSyntax[
  A <: AnyGraphObject,
  B <: AnyGraphObject,
  C <: AnyGraphObject,
  F <: AnyGraphMorphism { type Out = A ⊕ (B ⊕ C) }
](val f: F) {

  def associateLeft:
    F >=> scarph.associateBiproductLeft[A, B, C] =
    f >=> scarph.associateBiproductLeft(f.out.left, f.out.right.left, f.out.right.right)
}

case class AssociateBiproductRightSyntax[
  A <: AnyGraphObject,
  B <: AnyGraphObject,
  C <: AnyGraphObject,
  F <: AnyGraphMorphism { type Out = (A ⊕ B) ⊕ C }
](val f: F) {

  def associateRight:
    F >=> scarph.associateBiproductRight[A, B, C] =
    f >=> scarph.associateBiproductRight(f.out.left.left, f.out.left.right, f.out.right)
}

case class AssociateTensorLeftSyntax[
  A <: AnyGraphObject,
  B <: AnyGraphObject,
  C <: AnyGraphObject,
  F <: AnyGraphMorphism { type Out = A ⊗ (B ⊗ C) }
](val f: F) {

  def associateLeft:
    F >=> scarph.associateTensorLeft[A, B, C] =
    f >=> scarph.associateTensorLeft(f.out.left, f.out.right.left, f.out.right.right)
}

case class AssociateTensorRightSyntax[
  A <: AnyGraphObject,
  B <: AnyGraphObject,
  C <: AnyGraphObject,
  F <: AnyGraphMorphism { type Out = (A ⊗ B) ⊗ C }
](val f: F) {

  def associateRight:
    F >=> scarph.associateTensorRight[A, B, C] =
    f >=> scarph.associateTensorRight(f.out.left.left, f.out.left.right, f.out.right)
}


case class MergeSyntax[T <: AnyGraphObject, F <: AnyGraphMorphism { type Out = T ⊕ T }](f: SameBiproductOut[F]) extends AnyVal {

  def merge:
    F >=> scarph.merge[F#Out#Left] =
    f >=> scarph.merge(f.out.left)
}
```

Element types

```scala
case class ElementSyntax[F <: AnyGraphMorphism { type Out <: AnyGraphElement }](f: F) extends AnyVal {

  def get[P <: AnyProperty { type Source = F#Out }](p: P):
    Composition[F, scarph.get[P]] =
    Composition(f, scarph.get(p))

  def quantify[P <: AnyPredicate.On[F#Out]](p: P):
    F >=> scarph.quantify[P] =
    f >=> scarph.quantify(p)

  def filter[P <: AnyPredicate.On[F#Out]](p: P):
    F >=> scarph.quantify[P] >=> scarph.coerce[P] =
    f >=> scarph.quantify(p) >=> scarph.coerce(p)
}

case class PredicateSyntax[F <: AnyGraphMorphism { type Out <: AnyPredicate }](f: F) extends AnyVal {

  def coerce:
    F >=> scarph.coerce[F#Out] =
    f >=> scarph.coerce(f.out)
}


case class ZeroSyntax[F <: AnyGraphMorphism { type Out = zero }](f: F) extends AnyVal {

  def fromZero[X <: AnyGraphObject](x: X):
    F >=> scarph.fromZero[X] =
    f >=> scarph.fromZero(x)
}

case class UnitSyntax[F <: AnyGraphMorphism { type Out = unit }](f: F) extends AnyVal {

  def fromUnit[X <: AnyGraphObject](x: X):
    F >=> scarph.fromUnit[X] =
    f >=> scarph.fromUnit(x)
}
```

Edge types

```scala
case class EdgeSyntax[F <: AnyGraphMorphism { type Out <: AnyEdge }](f: F) extends AnyVal {

  // NOTE: in gremlin this is called .outV
  def source: F >=> scarph.source[F#Out] =
              f >=> scarph.source(f.out)

  // NOTE: in gremlin this is called .inV
  def target: F >=> scarph.target[F#Out] =
              f >=> scarph.target(f.out)
}
```

Vertex types

```scala
case class VertexSyntax[F <: AnyGraphMorphism { type Out <: AnyVertex }](f: F) extends AnyVal {

  def inE[E <: AnyEdge.To[F#Out]](e: E):
    Composition[F, scarph.inE[E]] =
    Composition(f, scarph.inE(e))

  def inV[E <: AnyEdge.To[F#Out]](e: E):
    Composition[F, scarph.inV[E]] =
    Composition(f, scarph.inV(e))


  def outE[E <: AnyEdge with AnyEdge.From[F#Out]](e: E):
    Composition[F, scarph.outE[E]] =
    Composition(f, scarph.outE(e))

  def outV[E <: AnyEdge with AnyEdge.From[F#Out]](e: E):
    Composition[F, scarph.outV[E]] =
    Composition(f, scarph.outV(e))
}

```




[test/scala/ohnosequences/scarph/asserts.scala]: ../../../../../test/scala/ohnosequences/scarph/asserts.scala.md
[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummy.scala.md
[test/scala/ohnosequences/scarph/impl/writes.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/writes.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[test/scala/ohnosequences/scarph/implicitSearch.scala]: ../../../../../test/scala/ohnosequences/scarph/implicitSearch.scala.md
[test/scala/ohnosequences/scarph/SchemaCreation.scala]: ../../../../../test/scala/ohnosequences/scarph/SchemaCreation.scala.md
[main/scala/ohnosequences/scarph/arities.scala]: ../arities.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: ../schemas.scala.md
[main/scala/ohnosequences/scarph/predicates.scala]: ../predicates.scala.md
[main/scala/ohnosequences/scarph/package.scala]: ../package.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: ../objects.scala.md
[main/scala/ohnosequences/scarph/impl/distributivity.scala]: ../impl/distributivity.scala.md
[main/scala/ohnosequences/scarph/impl/tensors.scala]: ../impl/tensors.scala.md
[main/scala/ohnosequences/scarph/impl/evals.scala]: ../impl/evals.scala.md
[main/scala/ohnosequences/scarph/impl/category.scala]: ../impl/category.scala.md
[main/scala/ohnosequences/scarph/impl/biproducts.scala]: ../impl/biproducts.scala.md
[main/scala/ohnosequences/scarph/impl/relations.scala]: ../impl/relations.scala.md
[main/scala/ohnosequences/scarph/syntax/package.scala]: package.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: objects.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/writes.scala]: writes.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: ../morphisms.scala.md
[main/scala/ohnosequences/scarph/tensor.scala]: ../tensor.scala.md
[main/scala/ohnosequences/scarph/axioms.scala]: ../axioms.scala.md
[main/scala/ohnosequences/scarph/isomorphisms.scala]: ../isomorphisms.scala.md
[main/scala/ohnosequences/scarph/writes.scala]: ../writes.scala.md
[main/scala/ohnosequences/scarph/rewrites.scala]: ../rewrites.scala.md
[main/scala/ohnosequences/scarph/biproduct.scala]: ../biproduct.scala.md