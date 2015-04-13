package ohnosequences.scarph.test

object dummy {

  import ohnosequences.scarph._, implementations._, graphTypes._

  case object Dummy
  type Dummy = Dummy.type

  implicit def tensorImpl:
      TensorImpl[Dummy, Dummy, Dummy] =
  new TensorImpl[Dummy, Dummy, Dummy] {

    def leftProj(i: Impl): Left = Dummy
    def rightProj(i: Impl): Right = Dummy
    def apply(l: Left, r: Right): Impl = Dummy
  }

  implicit def zeroImpl:
      ZeroImpl[Dummy] =
  new ZeroImpl[Dummy] {
    //type Inside = Dummy*/

    def apply(): Impl = Dummy
  }

  implicit def biproductImpl:
      BiproductImpl[Dummy, Dummy, Dummy] =
  new BiproductImpl[Dummy, Dummy, Dummy] {

    def leftProj(i: Impl): Left = Dummy
    def rightProj(i: Impl): Right = Dummy

    def leftInj(l: Left): Impl = l
    def rightInj(r: Right): Impl = r

    def apply(l: Left, r: Right): Impl = Dummy
  }

  implicit def edgeImpl:
      EdgeImpl[Dummy, Dummy, Dummy] =
  new EdgeImpl[Dummy, Dummy, Dummy] {

    def source(i: Impl): Source = Dummy
    def target(i: Impl): Target = Dummy
  }

  implicit def vertexImpl:
      VertexImpl[Dummy, Dummy, Dummy] =
  new VertexImpl[Dummy, Dummy, Dummy] {

    def inE(i: Impl, e: AnyEdge): InEdges = Dummy
    def outE(i: Impl, e: AnyEdge): OutEdges = Dummy
  }

}

import ohnosequences.scarph._, graphTypes._, morphisms._, evals._

object dummyEvals {
  import dummy._
/*
  implicit def eval_primitive[
    M <: AnyPrimitive
  ]:  EvalOn[Dummy, M, Dummy] =
  new EvalOn[Dummy, M, Dummy] {

    def present(morph: Morph): String = morph.label
    def apply(morph: Morph)(input: Input): Output = (morph.out: M#Out) := Dummy
  }
*/
}