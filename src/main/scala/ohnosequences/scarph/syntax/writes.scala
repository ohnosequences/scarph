package ohnosequences.scarph

import ohnosequences.cosas.types._

case object writeSyntax {

  import syntax.SymmetryRefine

  /*

    Need to

    - organize conversions and names for refinements
    - simpler namespaces for syntax; shouldn't need to import one hundred objects
    - document syntax approach for types to work
  */
  implicit def writeSyntax[F <: AnyGraphMorphism { type Out <: AnyTensorObj }](f: F)
    (implicit refine: F => SymmetryRefine[F]):
        WriteRelationSyntax[F#Out#Left, F#Out#Right, SymmetryRefine[F]] =
    new WriteRelationSyntax[F#Out#Left, F#Out#Right, SymmetryRefine[F]](refine(f))

  final
  class WriteRelationSyntax[
    L0 <: AnyGraphObject, R0 <: AnyGraphObject,
    F0 <: AnyGraphMorphism { type Out = L0 ⊗ R0 }
  ](val f: SymmetryRefine[F0]) extends AnyVal {

    def write[
      E <: AnyRelation {
        type SourceArity <: AnyArity { type GraphObject = L0 };
        type TargetArity <: AnyArity { type GraphObject = R0 }
      }
    ](e: E): F0 >=> WriteRelation[L0,R0,E] =
      f >=> WriteRelation(e)
  }

  // implicit final class DeleteRelationSyntax[F <: AnyGraphMorphism { type Out <: AnyRelation }](val f: F) extends AnyVal {
  //
  //   def delete: F >=> DeleteRelation[F#Out] =
  //     f >=> DeleteRelation(f.out)
  // }

  implicit final class DeleteVertexSyntax[F <: AnyGraphMorphism { type Out <: AnyVertex }](val f: F) extends AnyVal {

    def delete: F >=> DeleteVertex[F#Out] =
      f >=> DeleteVertex(f.out)
  }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////

case class AddVertexSyntax[G](u: unit := G) extends AnyVal {

  def add[V <: AnyVertex, RV](v: V)
    (implicit
      adder: CanAddVertices[G, V, RV]
    ): V := RV = {
      adder.addVertex(u.value)(v)
    }
}

case class AddEdgeSyntax[E <: AnyEdge](e: E) {

  def add[RE, RS, RT](
    src: E#Source := RS,
    tgt: E#Target := RT
  )(implicit
    adder: CanAddEdges[RS, E, RE, RT]
  ): E := RE = {
    adder.addEdge(e)(src, tgt)
  }
}

case class SetPropertySyntax[E <: AnyGraphElement, RE](e: E := RE) {

  def set[P <: AnyProperty, V <: P#Target#Val](
    p: P,
    v: V
  )(implicit
    setter: CanSetProperties[E, RE, P, V]
  ): E := RE = {
    setter.setProperty(e, p, v)
  }
}
