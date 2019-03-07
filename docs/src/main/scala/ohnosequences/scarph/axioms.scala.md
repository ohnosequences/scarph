
```scala
package ohnosequences.scarph

case object axioms {

  trait AnyAxiom {

    type First <: AnyGraphMorphism

    type Second <: AnyGraphMorphism { type In = First#In; type Out = First#Out }
  }

  trait Axiom[
    F <: AnyGraphMorphism,
    S <: AnyGraphMorphism { type In = F#In; type Out = F#Out }
  ]
  extends AnyAxiom {

    type First = F
    type Second = S
  }

  type ≅[F <: AnyGraphMorphism,S <: AnyGraphMorphism { type In = F#In; type Out = F#Out }] =
    AnyAxiom { type First = F; type Second = S }

  // example: category axioms
  class CompositionIsAssociative[
    F <: AnyGraphMorphism,
    G <: AnyGraphMorphism { type In = F#Out },
    H <: AnyGraphMorphism { type In = G#Out }
  ]
  extends Axiom[F >=> (G >=> H), (F >=> G) >=> H]

  class RightIdentity[F <: AnyGraphMorphism]
  extends Axiom[F, F >=> id[F#Out]]

  class LeftIdentity[F <: AnyGraphMorphism]
  // NOTE needed due to the usual crap
  extends Axiom[F, Composition[id[F#In], F]]

  // after those: dagger category axioms
  // crappy bounds in dagger
  class DaggerIsInvolutive[
    F <: AnyGraphMorphism {
      type Dagger <: AnyGraphMorphism {
        type Dagger <: AnyGraphMorphism {
          type In = F#In;
          type Out = F#Out
        }
      }
    }
  ]
  extends Axiom[F, F#Dagger#Dagger]

  // works in some cases
  def buh[O <: AnyGraphObject]: DaggerIsInvolutive[id[O]] = new DaggerIsInvolutive[id[O]]
  // not in others
  // def uhoh[F <: AnyGraphMorphism, G <: AnyGraphMorphism { type In = F#Out}]: DaggerIsInvolutive[F >=> G] = ???

  // more ugliness here due to insufficient bounds
  class DaggerAntiComposition[
    F <: AnyGraphMorphism { type Dagger <: AnyGraphMorphism { type In = F#Out; type Out = F#In }},
    G <: AnyGraphMorphism { type In = F#Out; type Dagger <: AnyGraphMorphism { type In = G#Out; type Out = G#In } }
  ]
  extends Axiom[(F >=> G)#Dagger, G#Dagger >=> F#Dagger]

  class IdentitiesAreTheirDagger[O <: AnyGraphObject]
  extends Axiom[id[O]#Dagger, id[O]]

  // other non-generic axioms: dagger mono, dagger epi
  class DaggerMono[
    F <: AnyGraphMorphism {
      type Dagger <: AnyGraphMorphism { type In = F#Out; type Out = F#In}
    }
  ]
  extends Axiom[F >=> F#Dagger, id[F#In]]

  class DaggerEpi[
    F <: AnyGraphMorphism {
      type Dagger <: AnyGraphMorphism { type In = F#Out; type Out = F#In}
    }
  ]
  extends Axiom[Composition[F#Dagger, F], id[F#Out]]

  // dagger monoidal category axioms
  // see http://ncatlab.org/nlab/show/symmetric+monoidal+dagger-category
  // class AssociatorDagger ...

}

```




[main/scala/ohnosequences/scarph/axioms.scala]: axioms.scala.md
[main/scala/ohnosequences/scarph/tensor.scala]: tensor.scala.md
[main/scala/ohnosequences/scarph/predicates.scala]: predicates.scala.md
[main/scala/ohnosequences/scarph/impl/biproducts.scala]: impl/biproducts.scala.md
[main/scala/ohnosequences/scarph/impl/tensors.scala]: impl/tensors.scala.md
[main/scala/ohnosequences/scarph/impl/evals.scala]: impl/evals.scala.md
[main/scala/ohnosequences/scarph/impl/distributivity.scala]: impl/distributivity.scala.md
[main/scala/ohnosequences/scarph/impl/relations.scala]: impl/relations.scala.md
[main/scala/ohnosequences/scarph/impl/category.scala]: impl/category.scala.md
[main/scala/ohnosequences/scarph/rewrites.scala]: rewrites.scala.md
[main/scala/ohnosequences/scarph/package.scala]: package.scala.md
[main/scala/ohnosequences/scarph/arities.scala]: arities.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: objects.scala.md
[main/scala/ohnosequences/scarph/writes.scala]: writes.scala.md
[main/scala/ohnosequences/scarph/biproduct.scala]: biproduct.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: schemas.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/package.scala]: syntax/package.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: syntax/objects.scala.md
[main/scala/ohnosequences/scarph/syntax/writes.scala]: syntax/writes.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/isomorphisms.scala]: isomorphisms.scala.md
[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: ../../../../test/scala/ohnosequences/scarph/impl/dummy.scala.md
[test/scala/ohnosequences/scarph/impl/writes.scala]: ../../../../test/scala/ohnosequences/scarph/impl/writes.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: ../../../../test/scala/ohnosequences/scarph/impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[test/scala/ohnosequences/scarph/asserts.scala]: ../../../../test/scala/ohnosequences/scarph/asserts.scala.md
[test/scala/ohnosequences/scarph/SchemaCreation.scala]: ../../../../test/scala/ohnosequences/scarph/SchemaCreation.scala.md
[test/scala/ohnosequences/scarph/implicitSearch.scala]: ../../../../test/scala/ohnosequences/scarph/implicitSearch.scala.md