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

/*
object dummyEvals extends dummyEvals2 {
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

}
  }*/

object dummyEvals {
  import dummy._

  implicit def eval_primitive[
    M <: AnyPrimitive
  ]:  EvalOn[Dummy, M, Dummy] =
  new EvalOn[Dummy, M, Dummy] {

    def apply(morph: Morph)(input: Input): Output = (morph.out: M#Out) := Dummy

    def present(morph: Morph): String = morph.label
  }
}
