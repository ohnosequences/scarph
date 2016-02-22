
```scala
package ohnosequences.scarph.syntax

case object morphisms {

  import ohnosequences.cosas.types._
  import ohnosequences.{ scarph => s }
  import s.objects._, s.morphisms._


  implicit final def graphMorphismValOps[F <: AnyGraphMorphism, VF](vt: F := VF):
    GraphMorphismValOps[F, VF] =
    GraphMorphismValOps[F, VF](vt)

  case class GraphMorphismValOps[F <: AnyGraphMorphism, VF](vf: F := VF) extends AnyVal {

    // (F := t) ⊗ (S := s) : (F ⊗ S) := (t, s)
    def ⊗[S <: AnyGraphMorphism, VS](vs: S := VS): TensorMorph[F, S] := (VF, VS) =
      TensorMorph(vf.tpe, vs.tpe) := ((vf.value, vs.value))

    // (F := t) ⊕ (S := s) : (F ⊕ S) := (t, s)
    def ⊕[S <: AnyGraphMorphism, VS](vs: S := VS): BiproductMorph[F, S] := (VF, VS) =
      BiproductMorph(vf.tpe, vs.tpe) := ((vf.value, vs.value))
  }

  implicit def graphMorphismSyntax[F <: AnyGraphMorphism](f: F):
    GraphMorphismSyntax[F] =
    GraphMorphismSyntax[F](f)

  case class GraphMorphismSyntax[F <: AnyGraphMorphism](f: F) extends AnyVal {

    // just an alias for >=> composition:
    def andThen[T <: AnyGraphMorphism { type In = F#Out }](t: T):
      F >=> T =
      f >=> t

    def duplicate:
      F >=> s.morphisms.duplicate[F#Out] =
      f >=> s.morphisms.duplicate(f.out)

    def fork:
      F >=> s.morphisms.fork[F#Out] =
      f >=> s.morphisms.fork(f.out)


    def toUnit:
      F >=> s.morphisms.toUnit[F#Out] =
      f >=> s.morphisms.toUnit(f.out)

    def toZero:
      F >=> s.morphisms.toZero[F#Out] =
      f >=> s.morphisms.toZero(f.out)


    def leftCounit:
      F >=> s.morphisms.leftCounit[F#Out] =
      f >=> s.morphisms.leftCounit(f.out)

    def rightCounit:
      F >=> s.morphisms.rightCounit[F#Out] =
      f >=> s.morphisms.rightCounit(f.out)


    def leftCozero:
      F >=> s.morphisms.leftCozero[F#Out] =
      f >=> s.morphisms.leftCozero(f.out)

    def rightCozero:
      F >=> s.morphisms.rightCozero[F#Out] =
      f >=> s.morphisms.rightCozero(f.out)


    // biproduct injections
    def leftInj[B <: AnyBiproductObj { type Left = F#Out }](b: B):
      F >=> s.morphisms.leftInj[B] =
      f >=> s.morphisms.leftInj(b)

    def rightInj[B <: AnyBiproductObj { type Right = F#Out }](b: B):
      F >=> s.morphisms.rightInj[B] =
      f >=> s.morphisms.rightInj(b)
  }

  type RefineTensorOut[F <: AnyGraphMorphism { type Out <: AnyTensorObj }] =
    F with AnyGraphMorphism { type Out = F#Out#Left ⊗ F#Out#Right }

  implicit def refineTensorOut[F <: AnyGraphMorphism { type Out <: AnyTensorObj }](f: F): RefineTensorOut[F] = f

  implicit def tensorSyntax[F <: AnyGraphMorphism { type Out <: AnyTensorObj }](f: F)
    (implicit refine: F => RefineTensorOut[F]):
        TensorSyntax[F#Out#Left, F#Out#Right, RefineTensorOut[F]] =
    new TensorSyntax[F#Out#Left, F#Out#Right, RefineTensorOut[F]](refine(f))

  case class TensorSyntax[
    L <: AnyGraphObject,
    R <: AnyGraphObject,
    F <: AnyGraphMorphism { type Out = L ⊗ R }
  ](f: RefineTensorOut[F]) extends AnyVal {

    def twist:
      F >=> s.morphisms.symmetry[F#Out#Left, F#Out#Right] =
      f >=> s.morphisms.symmetry(f.out.left, f.out.right)
  }


  type SameTensorOut[F <: AnyGraphMorphism { type Out <: AnyTensorObj }] =
    F with AnyGraphMorphism { type Out = F#Out#Left ⊗ F#Out#Left }

  implicit def sameTensorOut[F <: AnyGraphMorphism { type Out <: AnyTensorObj }](f: F)
    (implicit check: F#Out#Left =:= F#Out#Right): SameTensorOut[F] = f

  implicit def matchUpSyntax[F <: AnyGraphMorphism { type Out <: AnyTensorObj }](f: F)
    (implicit refine: F => SameTensorOut[F]):
        MatchUpSyntax[F#Out#Left, SameTensorOut[F]] =
    new MatchUpSyntax[F#Out#Left, SameTensorOut[F]](refine(f))

  case class MatchUpSyntax[T <: AnyGraphObject, F <: AnyGraphMorphism { type Out = T ⊗ T }](f: SameTensorOut[F]) extends AnyVal {

    def matchUp:
      F >=> s.morphisms.matchUp[F#Out#Left] =
      f >=> s.morphisms.matchUp(f.out.left)
  }

  type DistributableOut[
    F <: AnyGraphMorphism {
      type Out <: AnyTensorObj {
        type Right <: AnyBiproductObj
      }
    }
  ] = F with AnyGraphMorphism { type Out = F#Out#Left ⊗ (F#Out#Right#Left ⊕ F#Out#Right#Right) }

  implicit def distributableOut[
    F <: AnyGraphMorphism {
      type Out <: AnyTensorObj {
        type Right <: AnyBiproductObj
      }
    }
  ](f: F): DistributableOut[F] = f


  implicit def distributableSyntax[
    F <: AnyGraphMorphism {
      type Out <: AnyTensorObj {
        type Right <: AnyBiproductObj
      }
    }
  ](f: F)
    (implicit refine: F => DistributableOut[F]):
        DistributableSyntax[
          F#Out#Left,
          F#Out#Right#Left,
          F#Out#Right#Right,
          DistributableOut[F]
        ] =
          new DistributableSyntax[
            F#Out#Left,
            F#Out#Right#Left,
            F#Out#Right#Right,
            DistributableOut[F]
          ](refine(f))

  case class DistributableSyntax[
    X <: AnyGraphObject,
    A <: AnyGraphObject,
    B <: AnyGraphObject,
    F <: AnyGraphMorphism { type Out = X ⊗ (A ⊕ B) }
  ](val f: F) {

    def distribute:
      F >=> s.morphisms.distribute[X,A,B] =
      f >=> s.morphisms.distribute(f.out.left, f.out.right.left, f.out.right.right)
  }

  implicit def biproductSyntax[F <: AnyGraphMorphism { type Out <: AnyBiproductObj }](f: F):
        BiproductSyntax[F] =
    new BiproductSyntax[F](f)

  case class BiproductSyntax[F <: AnyGraphMorphism { type Out <: AnyBiproductObj }](f: F) extends AnyVal {

    def leftProj:
      F >=> s.morphisms.leftProj[F#Out] =
      f >=> s.morphisms.leftProj(f.out)

    def rightProj:
      F >=> s.morphisms.rightProj[F#Out] =
      f >=> s.morphisms.rightProj(f.out)
  }


  type SameBiproductOut[F <: AnyGraphMorphism { type Out <: AnyBiproductObj }] =
    F with AnyGraphMorphism { type Out = F#Out#Left ⊕ F#Out#Left }

  implicit def sameBiproductOut[F <: AnyGraphMorphism { type Out <: AnyBiproductObj }](f: F)
    (implicit check: F#Out#Left =:= F#Out#Right): SameBiproductOut[F] = f

  implicit def mergeSyntax[F <: AnyGraphMorphism { type Out <: AnyBiproductObj }](f: F)
    (implicit refine: F => SameBiproductOut[F]):
        MergeSyntax[F#Out#Left, SameBiproductOut[F]] =
    new MergeSyntax[F#Out#Left, SameBiproductOut[F]](refine(f))

  case class MergeSyntax[T <: AnyGraphObject, F <: AnyGraphMorphism { type Out = T ⊕ T }](f: SameBiproductOut[F]) extends AnyVal {

    def merge:
      F >=> s.morphisms.merge[F#Out#Left] =
      f >=> s.morphisms.merge(f.out.left)
  }
```

Element types

```scala
  implicit def elementSyntax[F <: AnyGraphMorphism { type Out <: AnyGraphElement }](f: F):
        ElementSyntax[F] =
    new ElementSyntax[F](f)

  case class ElementSyntax[F <: AnyGraphMorphism { type Out <: AnyGraphElement }](f: F) extends AnyVal {

    def get[P <: AnyProperty { type Source = F#Out }](p: P):
      Composition[F, s.morphisms.get[P]] =
      Composition(f, s.morphisms.get(p))

    def quantify[P <: AnyPredicate.On[F#Out]](p: P):
      F >=> s.morphisms.quantify[P] =
      f >=> s.morphisms.quantify(p)

    def filter[P <: AnyPredicate.On[F#Out]](p: P):
      F >=> s.morphisms.quantify[P] >=> s.morphisms.coerce[P] =
      f >=> s.morphisms.quantify(p) >=> s.morphisms.coerce(p)
  }

  implicit def predicateSyntax[F <: AnyGraphMorphism { type Out <: AnyPredicate }](f: F):
        PredicateSyntax[F] =
    new PredicateSyntax[F](f)

  case class PredicateSyntax[F <: AnyGraphMorphism { type Out <: AnyPredicate }](f: F) extends AnyVal {

    def coerce:
      F >=> s.morphisms.coerce[F#Out] =
      f >=> s.morphisms.coerce(f.out)
  }

  implicit def zeroSyntax[F <: AnyGraphMorphism { type Out = zero }](f: F):
        ZeroSyntax[F] =
    new ZeroSyntax[F](f)

  case class ZeroSyntax[F <: AnyGraphMorphism { type Out = zero }](f: F) extends AnyVal {

    def fromZero[X <: AnyGraphObject](x: X):
      F >=> s.morphisms.fromZero[X] =
      f >=> s.morphisms.fromZero(x)
  }

  implicit def unitSyntax[F <: AnyGraphMorphism { type Out = unit }](f: F):
        UnitSyntax[F] =
    new UnitSyntax[F](f)

  case class UnitSyntax[F <: AnyGraphMorphism { type Out = unit }](f: F) extends AnyVal {

    def fromUnit[X <: AnyGraphObject](x: X):
      F >=> s.morphisms.fromUnit[X] =
      f >=> s.morphisms.fromUnit(x)
  }
```

Edge types

```scala
  implicit def edgeSyntax[F <: AnyGraphMorphism { type Out <: AnyEdge }](f: F):
        EdgeSyntax[F] =
    new EdgeSyntax[F](f)

  case class EdgeSyntax[F <: AnyGraphMorphism { type Out <: AnyEdge }](f: F) extends AnyVal {

    // NOTE: in gremlin this is called .outV
    def source: F >=> s.morphisms.source[F#Out] =
                f >=> s.morphisms.source(f.out)

    // NOTE: in gremlin this is called .inV
    def target: F >=> s.morphisms.target[F#Out] =
                f >=> s.morphisms.target(f.out)
  }
```

Vertex types

```scala
  implicit def vertexSyntax[F <: AnyGraphMorphism { type Out <: AnyVertex }](f: F):
        VertexSyntax[F] =
    new VertexSyntax[F](f)

  case class VertexSyntax[F <: AnyGraphMorphism { type Out <: AnyVertex }](f: F) extends AnyVal {

    def inE[E <: AnyEdge.To[F#Out]](e: E):
      Composition[F, s.morphisms.inE[E]] =
      Composition(f, s.morphisms.inE(e))

    def inV[E <: AnyEdge.To[F#Out]](e: E):
      Composition[F, s.morphisms.inV[E]] =
      Composition(f, s.morphisms.inV(e))


    def outE[E <: AnyEdge with AnyEdge.From[F#Out]](e: E):
      Composition[F, s.morphisms.outE[E]] =
      Composition(f, s.morphisms.outE(e))

    def outV[E <: AnyEdge with AnyEdge.From[F#Out]](e: E):
      Composition[F, s.morphisms.outV[E]] =
      Composition(f, s.morphisms.outV(e))
  }

}

```




[main/scala/ohnosequences/scarph/axioms.scala]: ../axioms.scala.md
[main/scala/ohnosequences/scarph/evals.scala]: ../evals.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: ../morphisms.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: ../objects.scala.md
[main/scala/ohnosequences/scarph/rewrites.scala]: ../rewrites.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: ../schemas.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: objects.scala.md
[test/scala/ohnosequences/scarph/asserts.scala]: ../../../../../test/scala/ohnosequences/scarph/asserts.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummy.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/implicitSearch.scala]: ../../../../../test/scala/ohnosequences/scarph/implicitSearch.scala.md
[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md