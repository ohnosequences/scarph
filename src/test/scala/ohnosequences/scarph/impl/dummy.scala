package ohnosequences.scarph.test

object dummy {

  import ohnosequences.scarph._, implementations._

  implicit def tensorImpl:
        TensorImpl[Any] =
    new TensorImpl[Any] {
      type Left = Any
      type Right = Any

      def leftProj(i: Impl): Left = i
      def rightProj(i: Impl): Right = i
      def apply(l: Left, r: Right): Impl = s"($l, $r)"
    }

  implicit def zeroImpl:
        ZeroImpl[Any] =
    new ZeroImpl[Any] {
      type Inside = Any

      def apply(): Impl = ""
  }

  implicit def biproductImpl:
        BiproductImpl[Any] =
    new BiproductImpl[Any] {
      type Left = Any
      type Right = Any

      def leftProj(i: Impl): Left = i
      def rightProj(i: Impl): Right = i

      def leftInj(l: Left): Impl = l //(l, zeroR())
      def rightInj(r: Right): Impl = r //(zeroL(), r)

      def apply(l: Left, r: Right): Impl = s"($l, $r)"
    }

}

import ohnosequences.scarph._, graphTypes._, morphisms._, evals._

object dummyEvals extends dummyEvals2 {
/*
  // F >=> S
  implicit def eval_composition[
    X,
    F <: AnyGraphMorphism,
    S <: AnyGraphMorphism { type In = F#Out }
  ](implicit
    evalFirst:  EvalPathOn[String, F, X],
    evalSecond: EvalPathOn[X, S, String]
  ):  EvalPathOn[String, F >=> S, String] =
  new EvalPathOn[String, F >=> S, String] {

    def apply(morph: Morph)(input: Input): Output = {
      val f = evalFirst(morph.first)((morph.first.in: F#In) := "").value
      val s = evalSecond(morph.second)(morph.second.in := f).value
      morph.out := (f +" >=> "+ s)
    }
  }*/

}

trait dummyEvals2 extends DefaultEvals {

  implicit def eval_primitive[
    M <: AnyPrimitive
  ]:  EvalPathOn[M] =
  new EvalPathOn[M] {
    type InVal = String
    type OutVal = String

    def apply(morph: Morph)(input: Input): Output = (morph.out: M#Out) := (morph.out.label: OutVal)

    def present(morph: Morph): String = morph.label
  }
}
