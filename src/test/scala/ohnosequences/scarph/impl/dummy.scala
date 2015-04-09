package ohnosequences.scarph.test

object dummy {

  import ohnosequences.scarph._, implementations._

  implicit def tensorImpl[L, R]:
        TensorImpl[(L, R)] =
    new TensorImpl[(L, R)] {
      type Left = L
      type Right = R

      def leftProj(i: Impl): Left = i._1
      def rightProj(i: Impl): Right = i._2
      def apply(l: Left, r: Right): Impl = (l, r)
    }

  implicit def biproductImpl[T]:
        ZeroImpl[List[T]] =
    new ZeroImpl[List[T]] {
      type Inside = T

      def apply(): Impl = List[T]()
  }

  implicit def biproductImpl[L, R](
    implicit
      zeroL: ZeroImpl[L],
      zeroR: ZeroImpl[R]
    ):  BiproductImpl[(L, R)] =
    new BiproductImpl[(L, R)] {
      type Left = L
      type Right = R

      def leftProj(i: Impl): Left = i._1
      def rightProj(i: Impl): Right = i._2

      def leftInj(l: Left): Impl = (l, zeroR())
      def rightInj(r: Right): Impl = (zeroL(), r)

      def apply(l: Left, r: Right): Impl = (l, r)
    }

}

import ohnosequences.scarph._, graphTypes._, morphisms._, evals._

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

}

trait dummyEvals2 extends DefaultEvals {

  implicit def eval_Primitives[
    M <: AnyPrimitive
  ]:  EvalPathOn[String, M, String] =
  new EvalPathOn[String, M, String] {
    def apply(morph: Morph)(input: Input): Output = (morph.out: M#Out) := morph.label
  }
}
