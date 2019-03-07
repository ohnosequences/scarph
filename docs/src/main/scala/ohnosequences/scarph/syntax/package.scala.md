
```scala
package ohnosequences.scarph

import ohnosequences.cosas.types._

package object syntax {

  /////////////////////////////////////////////////////////////////////////////////////////////////
  // L ⊗ R → R ⊗ L

  type SymmetryRefine[F <: AnyGraphMorphism { type Out <: AnyTensorObj }] =
    F with AnyGraphMorphism { type Out = F#Out#Left ⊗ F#Out#Right }

  implicit def rensorRefine[F <: AnyGraphMorphism { type Out <: AnyTensorObj }](f: F): SymmetryRefine[F] = f

  implicit def tensorSyntax[F <: AnyGraphMorphism { type Out <: AnyTensorObj }](f: F)
    (implicit refine: F => SymmetryRefine[F]):
        SymmetrySyntax[F#Out#Left, F#Out#Right, SymmetryRefine[F]] =
    new SymmetrySyntax[F#Out#Left, F#Out#Right, SymmetryRefine[F]](refine(f))


  /////////////////////////////////////////////////////////////////////////////////////////////////
  // X ⊗ X → X

  type MatchUpRefine[F <: AnyGraphMorphism { type Out <: AnyTensorObj }] =
    F with AnyGraphMorphism { type Out = F#Out#Left ⊗ F#Out#Left }

  implicit def matchUpRefine[F <: AnyGraphMorphism { type Out <: AnyTensorObj }](f: F)
    (implicit check: F#Out#Left =:= F#Out#Right): MatchUpRefine[F] = f

  implicit def matchUpSyntax[F <: AnyGraphMorphism { type Out <: AnyTensorObj }](f: F)
    (implicit refine: F => MatchUpRefine[F]):
        MatchUpSyntax[F#Out#Left, MatchUpRefine[F]] =
    new MatchUpSyntax[F#Out#Left, MatchUpRefine[F]](refine(f))


  /////////////////////////////////////////////////////////////////////////////////////////////////
  // X ⊗ (A ⊕ B) → (X ⊗ A) ⊕ (X ⊗ B)

  type DistributeRefine[
    F <: AnyGraphMorphism {
      type Out <: AnyTensorObj {
        type Right <: AnyBiproductObj
      }
    }
  ] = F with AnyGraphMorphism { type Out = F#Out#Left ⊗ (F#Out#Right#Left ⊕ F#Out#Right#Right) }

  implicit def distributeRefine[
    F <: AnyGraphMorphism {
      type Out <: AnyTensorObj {
        type Right <: AnyBiproductObj
      }
    }
  ](f: F): DistributeRefine[F] = f


  implicit def distributeSyntax[
    F <: AnyGraphMorphism {
      type Out <: AnyTensorObj {
        type Right <: AnyBiproductObj
      }
    }
  ](f: F)(implicit refine: F => DistributeRefine[F]):
    DistributeSyntax[
      F#Out#Left,        // X
      F#Out#Right#Left,  // A
      F#Out#Right#Right, // B
      DistributeRefine[F]
    ] =
    new DistributeSyntax[
      F#Out#Left,        // X
      F#Out#Right#Left,  // A
      F#Out#Right#Right, // B
      DistributeRefine[F]
    ](refine(f))


  /////////////////////////////////////////////////////////////////////////////////////////////////
  // (X ⊗ A) ⊕ (X ⊗ B) → X ⊗ (A ⊕ B)

  type UndistributeRefine[
    F <: AnyGraphMorphism {
      type Out <: AnyBiproductObj {
        type Left  <: AnyTensorObj
        type Right <: AnyTensorObj
      }
    }
  ] = F with AnyGraphMorphism { type Out = (F#Out#Left#Left ⊗ F#Out#Left#Right) ⊕ (F#Out#Left#Left ⊗ F#Out#Right#Right) }

  implicit def undistributeRefine[
    F <: AnyGraphMorphism {
      type Out <: AnyBiproductObj {
        type Left  <: AnyTensorObj
        type Right <: AnyTensorObj
      }
    }
  ](f: F)(implicit check: F#Out#Left#Left =:= F#Out#Right#Left): UndistributeRefine[F] = f


  implicit def undistributeSyntax[
    F <: AnyGraphMorphism {
      type Out <: AnyBiproductObj {
        type Left  <: AnyTensorObj
        type Right <: AnyTensorObj
      }
    }
  ](f: F)(implicit refine: F => UndistributeRefine[F]):
    UndistributeSyntax[
      F#Out#Left#Left,   // X
      F#Out#Left#Right,  // A
      F#Out#Right#Right, // B
      UndistributeRefine[F]
    ] =
    new UndistributeSyntax[
      F#Out#Left#Left,   // X
      F#Out#Left#Right,  // A
      F#Out#Right#Right, // B
      UndistributeRefine[F]
    ](refine(f))



  /////////////////////////////////////////////////////////////////////////////////////////////////
  // X ⊕ X → X

  type SameBiproductOut[F <: AnyGraphMorphism { type Out <: AnyBiproductObj }] =
    F with AnyGraphMorphism { type Out = F#Out#Left ⊕ F#Out#Left }

  implicit def sameBiproductOut[F <: AnyGraphMorphism { type Out <: AnyBiproductObj }](f: F)
    (implicit check: F#Out#Left =:= F#Out#Right): SameBiproductOut[F] = f

  implicit def mergeSyntax[F <: AnyGraphMorphism { type Out <: AnyBiproductObj }](f: F)
    (implicit refine: F => SameBiproductOut[F]):
        MergeSyntax[F#Out#Left, SameBiproductOut[F]] =
    new MergeSyntax[F#Out#Left, SameBiproductOut[F]](refine(f))


  /////////////////////////////////////////////////////////////////////////////////////////////////
  // A ⊕ (B ⊕ C) → (A ⊕ B) ⊕ C

  type AssociateBiproductLeftRefine[
    F <: AnyGraphMorphism {
      type Out <: AnyBiproductObj {
        type Right <: AnyBiproductObj
      }
    }
  ] = F with AnyGraphMorphism { type Out = F#Out#Left ⊕ (F#Out#Right#Left ⊕ F#Out#Right#Right) }

  implicit def associateBiproductLeftRefine[
    F <: AnyGraphMorphism {
      type Out <: AnyBiproductObj {
        type Right <: AnyBiproductObj
      }
    }
  ](f: F): AssociateBiproductLeftRefine[F] = f

  implicit def associateBiproductLeftSyntax[
    F <: AnyGraphMorphism {
      type Out <: AnyBiproductObj {
        type Right <: AnyBiproductObj
      }
    }
  ](f: F)(implicit refine: F => AssociateBiproductLeftRefine[F]):
    AssociateBiproductLeftSyntax[
      F#Out#Left,        // A
      F#Out#Right#Left,  // B
      F#Out#Right#Right, // C
      AssociateBiproductLeftRefine[F]
    ] =
    AssociateBiproductLeftSyntax[
      F#Out#Left,        // A
      F#Out#Right#Left,  // B
      F#Out#Right#Right, // C
      AssociateBiproductLeftRefine[F]
    ](refine(f))

  /////////////////////////////////////////////////////////////////////////////////////////////////
  // (A ⊕ B) ⊕ C → A ⊕ (B ⊕ C)

  type AssociateBiproductRightRefine[
    F <: AnyGraphMorphism {
      type Out <: AnyBiproductObj {
        type Left <: AnyBiproductObj
      }
    }
  ] = F with AnyGraphMorphism { type Out = (F#Out#Left#Left ⊕ F#Out#Left#Right) ⊕ F#Out#Right }

  implicit def associateBiproductRightRefine[
    F <: AnyGraphMorphism {
      type Out <: AnyBiproductObj {
        type Left <: AnyBiproductObj
      }
    }
  ](f: F): AssociateBiproductRightRefine[F] = f

  implicit def associateBiproductRightSyntax[
    F <: AnyGraphMorphism {
      type Out <: AnyBiproductObj {
        type Left <: AnyBiproductObj
      }
    }
  ](f: F)(implicit refine: F => AssociateBiproductRightRefine[F]):
    AssociateBiproductRightSyntax[
      F#Out#Left#Left,   // A
      F#Out#Left#Right,  // B
      F#Out#Right,       // C
      AssociateBiproductRightRefine[F]
    ] =
    AssociateBiproductRightSyntax[
      F#Out#Left#Left,   // A
      F#Out#Left#Right,  // B
      F#Out#Right,       // C
      AssociateBiproductRightRefine[F]
    ](refine(f))


  /////////////////////////////////////////////////////////////////////////////////////////////////
  // A ⊗ (B ⊗ C) → (A ⊗ B) ⊗ C

  type AssociateTensorLeftRefine[
    F <: AnyGraphMorphism {
      type Out <: AnyTensorObj {
        type Right <: AnyTensorObj
      }
    }
  ] = F with AnyGraphMorphism { type Out = F#Out#Left ⊗ (F#Out#Right#Left ⊗ F#Out#Right#Right) }

  implicit def associateTensorLeftRefine[
    F <: AnyGraphMorphism {
      type Out <: AnyTensorObj {
        type Right <: AnyTensorObj
      }
    }
  ](f: F): AssociateTensorLeftRefine[F] = f

  implicit def associateTensorLeftSyntax[
    F <: AnyGraphMorphism {
      type Out <: AnyTensorObj {
        type Right <: AnyTensorObj
      }
    }
  ](f: F)(implicit refine: F => AssociateTensorLeftRefine[F]):
    AssociateTensorLeftSyntax[
      F#Out#Left,        // A
      F#Out#Right#Left,  // B
      F#Out#Right#Right, // C
      AssociateTensorLeftRefine[F]
    ] =
    AssociateTensorLeftSyntax[
      F#Out#Left,        // A
      F#Out#Right#Left,  // B
      F#Out#Right#Right, // C
      AssociateTensorLeftRefine[F]
    ](refine(f))

  /////////////////////////////////////////////////////////////////////////////////////////////////
  // (A ⊗ B) ⊗ C → A ⊗ (B ⊗ C)

  type AssociateTensorRightRefine[
    F <: AnyGraphMorphism {
      type Out <: AnyTensorObj {
        type Left <: AnyTensorObj
      }
    }
  ] = F with AnyGraphMorphism { type Out = (F#Out#Left#Left ⊗ F#Out#Left#Right) ⊗ F#Out#Right }

  implicit def associateTensorRightRefine[
    F <: AnyGraphMorphism {
      type Out <: AnyTensorObj {
        type Left <: AnyTensorObj
      }
    }
  ](f: F): AssociateTensorRightRefine[F] = f

  implicit def associateTensorRightSyntax[
    F <: AnyGraphMorphism {
      type Out <: AnyTensorObj {
        type Left <: AnyTensorObj
      }
    }
  ](f: F)(implicit refine: F => AssociateTensorRightRefine[F]):
    AssociateTensorRightSyntax[
      F#Out#Left#Left,   // A
      F#Out#Left#Right,  // B
      F#Out#Right,       // C
      AssociateTensorRightRefine[F]
    ] =
    AssociateTensorRightSyntax[
      F#Out#Left#Left,   // A
      F#Out#Left#Right,  // B
      F#Out#Right,       // C
      AssociateTensorRightRefine[F]
    ](refine(f))

  /////////////////////////////////////////////////////////////////////////////////////////////////
  // Simple syntax

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

  implicit def biproductSyntax[F <: AnyGraphMorphism { type Out <: AnyBiproductObj }](f: F):
        BiproductSyntax[F] =
    new BiproductSyntax[F](f)


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

```




[main/scala/ohnosequences/scarph/axioms.scala]: ../axioms.scala.md
[main/scala/ohnosequences/scarph/tensor.scala]: ../tensor.scala.md
[main/scala/ohnosequences/scarph/predicates.scala]: ../predicates.scala.md
[main/scala/ohnosequences/scarph/impl/biproducts.scala]: ../impl/biproducts.scala.md
[main/scala/ohnosequences/scarph/impl/tensors.scala]: ../impl/tensors.scala.md
[main/scala/ohnosequences/scarph/impl/evals.scala]: ../impl/evals.scala.md
[main/scala/ohnosequences/scarph/impl/distributivity.scala]: ../impl/distributivity.scala.md
[main/scala/ohnosequences/scarph/impl/relations.scala]: ../impl/relations.scala.md
[main/scala/ohnosequences/scarph/impl/category.scala]: ../impl/category.scala.md
[main/scala/ohnosequences/scarph/rewrites.scala]: ../rewrites.scala.md
[main/scala/ohnosequences/scarph/package.scala]: ../package.scala.md
[main/scala/ohnosequences/scarph/arities.scala]: ../arities.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: ../objects.scala.md
[main/scala/ohnosequences/scarph/writes.scala]: ../writes.scala.md
[main/scala/ohnosequences/scarph/biproduct.scala]: ../biproduct.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: ../schemas.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: ../morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/package.scala]: package.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: objects.scala.md
[main/scala/ohnosequences/scarph/syntax/writes.scala]: writes.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: morphisms.scala.md
[main/scala/ohnosequences/scarph/isomorphisms.scala]: ../isomorphisms.scala.md
[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummy.scala.md
[test/scala/ohnosequences/scarph/impl/writes.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/writes.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[test/scala/ohnosequences/scarph/asserts.scala]: ../../../../../test/scala/ohnosequences/scarph/asserts.scala.md
[test/scala/ohnosequences/scarph/SchemaCreation.scala]: ../../../../../test/scala/ohnosequences/scarph/SchemaCreation.scala.md
[test/scala/ohnosequences/scarph/implicitSearch.scala]: ../../../../../test/scala/ohnosequences/scarph/implicitSearch.scala.md