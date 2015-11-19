package ohnosequences.scarph

import objects._, morphisms._

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

}
