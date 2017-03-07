package ohnosequences.scarph

import ohnosequences.cosas.types._

package object syntax {

  /* ## Type aliases */

  type RefineTensorOut[F <: AnyGraphMorphism { type Out <: AnyTensorObj }] =
    F with AnyGraphMorphism { type Out = F#Out#Left ⊗ F#Out#Right }

  type SameTensorOut[F <: AnyGraphMorphism { type Out <: AnyTensorObj }] =
    F with AnyGraphMorphism { type Out = F#Out#Left ⊗ F#Out#Left }


  type DistributableOut[
    F <: AnyGraphMorphism {
      type Out <: AnyTensorObj {
        type Right <: AnyBiproductObj
      }
    }
  ] = F with AnyGraphMorphism { type Out = F#Out#Left ⊗ (F#Out#Right#Left ⊕ F#Out#Right#Right) }


  type SameBiproductOut[F <: AnyGraphMorphism { type Out <: AnyBiproductObj }] =
    F with AnyGraphMorphism { type Out = F#Out#Left ⊕ F#Out#Left }



  implicit def elementPredicateOps[E <: AnyGraphElement](e: E):
      ElementPredicateOps[E] =
      ElementPredicateOps[E](e)

  implicit def predicateOps[P <: AnyPredicate](p: P):
      PredicateOps[P] =
      PredicateOps[P](p)

  implicit final def conditionOps[P <: AnyProperty](property: P):
    ConditionOps[P] =
    ConditionOps[P](property)


  implicit final def graphMorphismValOps[F <: AnyGraphMorphism, VF](vt: F := VF):
    GraphMorphismValOps[F, VF] =
    GraphMorphismValOps[F, VF](vt)

  implicit def graphMorphismSyntax[F <: AnyGraphMorphism](f: F):
    GraphMorphismSyntax[F] =
    GraphMorphismSyntax[F](f)


  implicit def refineTensorOut[F <: AnyGraphMorphism { type Out <: AnyTensorObj }](f: F): RefineTensorOut[F] = f

  implicit def tensorSyntax[F <: AnyGraphMorphism { type Out <: AnyTensorObj }](f: F)
    (implicit refine: F => RefineTensorOut[F]):
        TensorSyntax[F#Out#Left, F#Out#Right, RefineTensorOut[F]] =
    new TensorSyntax[F#Out#Left, F#Out#Right, RefineTensorOut[F]](refine(f))


  implicit def sameTensorOut[F <: AnyGraphMorphism { type Out <: AnyTensorObj }](f: F)
    (implicit check: F#Out#Left =:= F#Out#Right): SameTensorOut[F] = f

  implicit def matchUpSyntax[F <: AnyGraphMorphism { type Out <: AnyTensorObj }](f: F)
    (implicit refine: F => SameTensorOut[F]):
        MatchUpSyntax[F#Out#Left, SameTensorOut[F]] =
    new MatchUpSyntax[F#Out#Left, SameTensorOut[F]](refine(f))


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


  implicit def biproductSyntax[F <: AnyGraphMorphism { type Out <: AnyBiproductObj }](f: F):
        BiproductSyntax[F] =
    new BiproductSyntax[F](f)


  implicit def sameBiproductOut[F <: AnyGraphMorphism { type Out <: AnyBiproductObj }](f: F)
    (implicit check: F#Out#Left =:= F#Out#Right): SameBiproductOut[F] = f

  implicit def mergeSyntax[F <: AnyGraphMorphism { type Out <: AnyBiproductObj }](f: F)
    (implicit refine: F => SameBiproductOut[F]):
        MergeSyntax[F#Out#Left, SameBiproductOut[F]] =
    new MergeSyntax[F#Out#Left, SameBiproductOut[F]](refine(f))


  implicit def elementSyntax[F <: AnyGraphMorphism { type Out <: AnyGraphElement }](f: F):
        ElementSyntax[F] =
    new ElementSyntax[F](f)

  implicit def predicateSyntax[F <: AnyGraphMorphism { type Out <: AnyPredicate }](f: F):
        PredicateSyntax[F] =
    new PredicateSyntax[F](f)

  implicit def zeroSyntax[F <: AnyGraphMorphism { type Out = zero }](f: F):
        ZeroSyntax[F] =
    new ZeroSyntax[F](f)

  implicit def unitSyntax[F <: AnyGraphMorphism { type Out = unit }](f: F):
        UnitSyntax[F] =
    new UnitSyntax[F](f)

  implicit def edgeSyntax[F <: AnyGraphMorphism { type Out <: AnyEdge }](f: F):
        EdgeSyntax[F] =
    new EdgeSyntax[F](f)

  implicit def vertexSyntax[F <: AnyGraphMorphism { type Out <: AnyVertex }](f: F):
        VertexSyntax[F] =
    new VertexSyntax[F](f)


  implicit def addVertexSyntax[G](u: unit := G):
        AddVertexSyntax[G] =
    new AddVertexSyntax[G](u)

  implicit def addEdgeSyntax[E <: AnyEdge](e: E):
        AddEdgeSyntax[E] =
    new AddEdgeSyntax[E](e)

  implicit def setPropertySyntax[E <: AnyGraphElement, RE](e: E := RE):
        SetPropertySyntax[E, RE] =
    new SetPropertySyntax[E, RE](e)

}
