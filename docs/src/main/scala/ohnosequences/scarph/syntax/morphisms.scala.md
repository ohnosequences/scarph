
```scala
package ohnosequences.scarph.syntax

object morphisms {

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.monoidalStructures._, s.morphisms._, s.predicates._

  implicit def graphMorphismSyntax[F <: AnyGraphMorphism](f: F):
        GraphMorphismSyntax[F] =
    new GraphMorphismSyntax[F](f)

  class GraphMorphismSyntax[F <: AnyGraphMorphism](f: F) {

    // just an alias for >=> composition:
    def andThen[T <: AnyGraphMorphism { type In = F#Out }](t: T):
      F >=> T =
      f >=> t

    def duplicate:
      F >=> s.morphisms.duplicate[F#Out] =
      f >=> s.morphisms.duplicate(f.out)

    def split:
      F >=> s.morphisms.split[F#Out] =
      f >=> s.morphisms.split(f.out)


    def toUnit:
      F >=> s.morphisms.toUnit[F#Out] =
      f >=> s.morphisms.toUnit(f.out)

    def toZero:
      F >=> s.morphisms.toZero[F#Out] =
      f >=> s.morphisms.toZero(f.out)


    def leftCounit:
      F >=> s.naturalIsomorphisms.leftCounit[F#Out] =
      f >=> s.naturalIsomorphisms.leftCounit(f.out)

    def rightCounit:
      F >=> s.naturalIsomorphisms.rightCounit[F#Out] =
      f >=> s.naturalIsomorphisms.rightCounit(f.out)


    def leftCozero:
      F >=> s.naturalIsomorphisms.leftCozero[F#Out] =
      f >=> s.naturalIsomorphisms.leftCozero(f.out)

    def rightCozero:
      F >=> s.naturalIsomorphisms.rightCozero[F#Out] =
      f >=> s.naturalIsomorphisms.rightCozero(f.out)


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

  class TensorSyntax[
    L <: AnyGraphObject,
    R <: AnyGraphObject,
    F <: AnyGraphMorphism { type Out = L ⊗ R }
  ](val f: F) {

    def twist:
      F >=> s.naturalIsomorphisms.symmetry[F#Out#Left, F#Out#Right] =
      f >=> s.naturalIsomorphisms.symmetry(f.out.left, f.out.right)
  }


  type SameTensorOut[F <: AnyGraphMorphism { type Out <: AnyTensorObj }] =
    F with AnyGraphMorphism { type Out = F#Out#Left ⊗ F#Out#Left }

  implicit def sameTensorOut[F <: AnyGraphMorphism { type Out <: AnyTensorObj }](f: F)
    (implicit check: F#Out#Left =:= F#Out#Right): SameTensorOut[F] = f

  implicit def matchUpSyntax[F <: AnyGraphMorphism { type Out <: AnyTensorObj }](f: F)
    (implicit refine: F => SameTensorOut[F]):
        MatchUpSyntax[F#Out#Left, SameTensorOut[F]] =
    new MatchUpSyntax[F#Out#Left, SameTensorOut[F]](refine(f))

  class MatchUpSyntax[T <: AnyGraphObject, F <: AnyGraphMorphism { type Out = T ⊗ T }](f: F) {

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

  class DistributableSyntax[
    X <: AnyGraphObject,
    A <: AnyGraphObject,
    B <: AnyGraphObject,
    F <: AnyGraphMorphism { type Out = X ⊗ (A ⊕ B) }
  ](val f: F) {

    def distribute:
      F >=> s.naturalIsomorphisms.distribute[X,A,B] =
      f >=> s.naturalIsomorphisms.distribute(f.out.left, f.out.right.left, f.out.right.right)
  }

  implicit def biproductSyntax[F <: AnyGraphMorphism { type Out <: AnyBiproductObj }](f: F):
        BiproductSyntax[F] =
    new BiproductSyntax[F](f)

  class BiproductSyntax[F <: AnyGraphMorphism { type Out <: AnyBiproductObj }](f: F) {

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

  class MergeSyntax[T <: AnyGraphObject, F <: AnyGraphMorphism { type Out = T ⊕ T }](f: F) {

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

  class ElementSyntax[F <: AnyGraphMorphism { type Out <: AnyGraphElement }](f: F) {

    def get[P <: AnyGraphProperty { type Owner = F#Out }](p: P):
      F >=> s.morphisms.get[P] =
      f >=> s.morphisms.get(p)

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

  class PredicateSyntax[F <: AnyGraphMorphism { type Out <: AnyPredicate }](f: F) {

    def coerce:
      F >=> s.morphisms.coerce[F#Out] =
      f >=> s.morphisms.coerce(f.out)
  }

  implicit def zeroSyntax[F <: AnyGraphMorphism { type Out = zero }](f: F):
        ZeroSyntax[F] =
    new ZeroSyntax[F](f)

  class ZeroSyntax[F <: AnyGraphMorphism { type Out = zero }](f: F) {

    def fromZero[X <: AnyGraphObject](x: X):
      F >=> s.morphisms.fromZero[X] =
      f >=> s.morphisms.fromZero(x)
  }

  implicit def unitSyntax[F <: AnyGraphMorphism { type Out = unit }](f: F):
        UnitSyntax[F] =
    new UnitSyntax[F](f)

  class UnitSyntax[F <: AnyGraphMorphism { type Out = unit }](f: F) {

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

  class EdgeSyntax[F <: AnyGraphMorphism { type Out <: AnyEdge }](f: F) {

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

  class VertexSyntax[F <: AnyGraphMorphism { type Out <: AnyVertex }](f: F) {

    def inE[E <: AnyEdge.To[F#Out]](e: E):
      F >=> s.morphisms.inE[E] =
      f >=> s.morphisms.inE(e)

    def inV[E <: AnyEdge.To[F#Out]](e: E):
      F >=> s.morphisms.inV[E] =
      f >=> s.morphisms.inV(e)


    def outE[E <: AnyEdge.From[F#Out]](e: E):
      F >=> s.morphisms.outE[E] =
      f >=> s.morphisms.outE(e)

    def outV[E <: AnyEdge.From[F#Out]](e: E):
      F >=> s.morphisms.outV[E] =
      f >=> s.morphisms.outV(e)
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
          + [predicates.scala][main/scala/ohnosequences/scarph/predicates.scala]
          + [monoidalStructures.scala][main/scala/ohnosequences/scarph/monoidalStructures.scala]
          + [evals.scala][main/scala/ohnosequences/scarph/evals.scala]
          + [implementations.scala][main/scala/ohnosequences/scarph/implementations.scala]
          + [schemas.scala][main/scala/ohnosequences/scarph/schemas.scala]
          + [naturalIsomorphisms.scala][main/scala/ohnosequences/scarph/naturalIsomorphisms.scala]
          + [graphTypes.scala][main/scala/ohnosequences/scarph/graphTypes.scala]
          + syntax
            + [morphisms.scala][main/scala/ohnosequences/scarph/syntax/morphisms.scala]
            + [predicates.scala][main/scala/ohnosequences/scarph/syntax/predicates.scala]
            + [graphTypes.scala][main/scala/ohnosequences/scarph/syntax/graphTypes.scala]
            + [conditions.scala][main/scala/ohnosequences/scarph/syntax/conditions.scala]
          + [conditions.scala][main/scala/ohnosequences/scarph/conditions.scala]

[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummy.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: ../morphisms.scala.md
[main/scala/ohnosequences/scarph/predicates.scala]: ../predicates.scala.md
[main/scala/ohnosequences/scarph/monoidalStructures.scala]: ../monoidalStructures.scala.md
[main/scala/ohnosequences/scarph/evals.scala]: ../evals.scala.md
[main/scala/ohnosequences/scarph/implementations.scala]: ../implementations.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: ../schemas.scala.md
[main/scala/ohnosequences/scarph/naturalIsomorphisms.scala]: ../naturalIsomorphisms.scala.md
[main/scala/ohnosequences/scarph/graphTypes.scala]: ../graphTypes.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/predicates.scala]: predicates.scala.md
[main/scala/ohnosequences/scarph/syntax/graphTypes.scala]: graphTypes.scala.md
[main/scala/ohnosequences/scarph/syntax/conditions.scala]: conditions.scala.md
[main/scala/ohnosequences/scarph/conditions.scala]: ../conditions.scala.md