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
      F >=> s.isomorphisms.leftCounit[F#Out] =
      f >=> s.isomorphisms.leftCounit(f.out)

    def rightCounit:
      F >=> s.isomorphisms.rightCounit[F#Out] =
      f >=> s.isomorphisms.rightCounit(f.out)


    def leftCozero:
      F >=> s.isomorphisms.leftCozero[F#Out] =
      f >=> s.isomorphisms.leftCozero(f.out)

    def rightCozero:
      F >=> s.isomorphisms.rightCozero[F#Out] =
      f >=> s.isomorphisms.rightCozero(f.out)
  }

  implicit def isTensorReally[F <: AnyGraphMorphism { type Out <: AnyTensorObj }](f: F): AnyGraphMorphism.isTensorOut[F] = f
  implicit def tensorSyntax[F <: AnyGraphMorphism { type Out <: AnyTensorObj }, F0 <% AnyGraphMorphism.isTensorOut[F]](f: F0):
        TensorSyntax[F] =
    new TensorSyntax[F](f)

  class TensorSyntax[F <: AnyGraphMorphism { type Out <: AnyTensorObj }](val f: AnyGraphMorphism.isTensorOut[F]) {

    def twist:
    AnyGraphMorphism.isTensorOut[F] >=> s.isomorphisms.symmetry[F#Out#Left, F#Out#Right] =
        f >=> s.isomorphisms.symmetry(f.out.left, f.out.right)
  }


  implicit def matchUpSyntax[T <: AnyGraphObject, F <: AnyGraphMorphism { type Out = T ⊗ T }](f: F):
        MatchUpSyntax[T, F] =
    new MatchUpSyntax[T, F](f)

  class MatchUpSyntax[T <: AnyGraphObject, F <: AnyGraphMorphism { type Out = T ⊗ T }](f: F) {

    def matchUp:
      F >=> s.morphisms.matchUp[T] =
      f >=> s.morphisms.matchUp(f.out.left)
  }


  implicit def BiproductSyntax[F <: AnyGraphMorphism { type Out <: AnyBiproductObj }](f: F):
        BiproductSyntax[F] =
    new BiproductSyntax[F](f)

  class BiproductSyntax[F <: AnyGraphMorphism { type Out <: AnyBiproductObj }](f: F) {

    def left:
      F >=> leftProj[F#Out] =
      f >=> leftProj(f.out)

    def right:
      F >=> rightProj[F#Out] =
      f >=> rightProj(f.out)
  }


  implicit def mergeSyntax[T <: AnyGraphObject, F <: AnyGraphMorphism { type Out = T ⊕ T }](f: F):
        MergeSyntax[T, F] =
    new MergeSyntax[T, F](f)

  class MergeSyntax[T <: AnyGraphObject, F <: AnyGraphMorphism { type Out = T ⊕ T }](f: F) {

    def merge:
      F >=> s.morphisms.merge[T] =
      f >=> s.morphisms.merge(f.out.left)
  }


  /* Element types */
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

  /* Edge types */
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

  /* Vertex types */
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
