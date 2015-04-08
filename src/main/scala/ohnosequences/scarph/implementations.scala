package ohnosequences.scarph

object implementations {

  import monoidalStructures._
  import ohnosequences.cosas._, types._, fns._
  import graphTypes._, morphisms._


  trait AnyEvalPath {

    type Morph <: AnyGraphMorphism

    type InVal
    type OutVal

    type Input = Morph#In := InVal
    type Output = Morph#Out := OutVal

    def apply(morph: Morph)(input: Input): Output
  }

  @annotation.implicitNotFound(msg = "Can't evaluate morph ${P} with\n\tinput: ${I}\n\toutput: ${O}")
  trait EvalPathOn[I, P <: AnyGraphMorphism, O] extends AnyEvalPath {

    type InVal = I
    type OutVal = O
    type Morph = P
  }


  trait AnyImpl {

    type Impl
  }

  //trait FlattenVals[F[_], G[_], X] extends Fn1[F[G[X]]]*/

  //trait MergeVals[F, S] extends Fn2[F, S]*/


  trait AnyTensorImpl extends AnyImpl {

    type Left
    def leftProj(i: Impl): Left

    type Right
    def rightProj(i: Impl): Right

    def apply(l: Left, r: Right): Impl
  }

  abstract class TensorImpl[L, R] extends AnyTensorImpl {

    type Left = L
    type Right = R
  }


  trait AnyBiproductImpl extends AnyImpl {

    type Left
    def leftProj(i: Impl): Left

    type Right
    def rightProj(i: Impl): Right

    def apply(l: Left, r: Right): Impl

    def leftInj(l: Left): Impl
    def rightInj(r: Right): Impl
  }

  abstract class BiproductImpl[L, R] extends AnyBiproductImpl {

    type Left = L
    type Right = R
  }


  trait AnyZeroImpl extends AnyImpl {

    type Inside

    def apply(): Impl
  }

  abstract class ZeroImpl[T] extends AnyZeroImpl {

    type Inside = T
  }

}
