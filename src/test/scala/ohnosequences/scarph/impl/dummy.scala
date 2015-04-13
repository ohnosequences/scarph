package ohnosequences.scarph.test

object dummy {

  import ohnosequences.scarph._, implementations._

  case object Dummy
  type Dummy = Dummy.type

  implicit def tensorImpl:
        TensorImpl[Dummy] =
    new TensorImpl[Dummy] {
      type Left = Dummy
      type Right = Dummy

      def leftProj(i: Impl): Left = Dummy
      def rightProj(i: Impl): Right = Dummy
      def apply(l: Left, r: Right): Impl = Dummy
    }

  implicit def zeroImpl:
        ZeroImpl[Dummy] =
    new ZeroImpl[Dummy] {
      type Inside = Dummy

      def apply(): Impl = Dummy
  }

  implicit def biproductImpl:
        BiproductImpl[Dummy] =
    new BiproductImpl[Dummy] {
      type Left = Dummy
      type Right = Dummy

      def leftProj(i: Impl): Left = Dummy
      def rightProj(i: Impl): Right = Dummy

      def leftInj(l: Left): Impl = l
      def rightInj(r: Right): Impl = r

      def apply(l: Left, r: Right): Impl = Dummy
    }

}

import ohnosequences.scarph._, graphTypes._, morphisms._, evals._

object dummyEvals {
  import dummy._

  class DummyEvalOn[M <: AnyGraphMorphism] extends EvalOn[Dummy, M, Dummy] {

    def present(morph: Morph): String = morph.label
    def apply(morph: Morph)(input: Input): Output = (morph.out: M#Out) := Dummy
  }

  implicit def eval_primitive[
    M <: AnyPrimitive
  ]:  DummyEvalOn[M] = new DummyEvalOn[M]

}
