package ohnosequences.scarph.syntax

object morphisms {

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.monoidalStructures._, s.morphisms._

  implicit def graphMorphismSyntax[F <: AnyGraphMorphism](f: F):
        GraphMorphismSyntax[F] =
    new GraphMorphismSyntax[F](f)

  class GraphMorphismSyntax[F <: AnyGraphMorphism](f: F) {

    def duplicate:
      F >=> s.morphisms.duplicate[F#Out] =
      f >=> s.morphisms.duplicate(f.out)
  }

  implicit def tensorSyntax[F <: AnyGraphMorphism { type Out <: AnyTensorObj }](f: F):
        TensorSyntax[F] =
    new TensorSyntax[F](f)

  class TensorSyntax[F <: AnyGraphMorphism { type Out <: AnyTensorObj }](f: F) {

    // def matchUp:
    //   Tensor[F, F] >=> matchUp[F#Out] =
    //   ff >=> matchUp(ff.left.out)
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


  /* Element types */
  implicit def elementSyntax[F <: AnyGraphMorphism { type Out <: AnyGraphElement }](f: F):
        ElementSyntax[F] =
    new ElementSyntax[F](f)

  class ElementSyntax[F <: AnyGraphMorphism { type Out <: AnyGraphElement }](f: F) {

    def get[P <: AnyGraphProperty { type Owner = F#Out }](p: P):
      F >=> s.morphisms.get[P] =
      f >=> s.morphisms.get(p)
  }

  /* Edge types */
  implicit def edgeSyntax[F <: AnyGraphMorphism { type Out <: AnyEdge }](f: F):
        EdgeSyntax[F] =
    new EdgeSyntax[F](f)

  class EdgeSyntax[F <: AnyGraphMorphism { type Out <: AnyEdge }](f: F) {

    // NOTE: in gremlin this is called .outV
    def src: F >=> source[F#Out] =
             f >=> source(f.out)

    // NOTE: in gremlin this is called .inV
    def tgt: F >=> target[F#Out] =
             f >=> target(f.out)
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
