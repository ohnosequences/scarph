package ohnosequences.scarph

object evals {

  import ohnosequences.cosas._, types._, fns._
  import graphTypes._, paths._, steps._, combinators._, containers._


  // NOTE: maybe this should be Fn2
  trait AnyEvalPath {

    type Path <: AnyPath

    type InVal
    type OutVal

    type In = InVal Denotes Path#In
    type Out = OutVal Denotes Path#Out

    def apply(path: Path)(in: In): Out
  }

  @annotation.implicitNotFound(msg = "Can't evaluate path ${P} with\n\tinput: ${I}\n\toutput: ${O}")
  trait EvalPathOn[I, P <: AnyPath, O] extends AnyEvalPath {

    type InVal = I
    type OutVal = O
    type Path = P
  }

  trait FlattenVals[F[_], G[_], X] extends Fn1[F[G[X]]]

  trait MergeVals[F, S] extends Fn2[F, S]

  object AnyEvalPath {

    implicit def evalComposition[
      I, 
      F <: AnyPath,
      G <: AnyPath,
      X, O
    ](implicit
      composable: F#Out ≃ G#In,
      evalFirst:  EvalPathOn[I, F, X],
      evalSecond: EvalPathOn[X, G, O]
    ):  EvalPathOn[I, Composition[F, G], O] = 
    new EvalPathOn[I, Composition[F, G], O] {
      def apply(path: Path)(in: In): Out = {
        val firstResult = evalFirst(path.first)(in)
        evalSecond(path.second)((path.second.in: G#In) := firstResult.value)
      }
    }


    implicit def evalMapOver[
      P <: AnyPath, C <: AnyContainer,
      I, F[_], O
    ](implicit
      evalInner: EvalPathOn[I, P, O],
      functor: scalaz.Functor[F]
    ):  EvalPathOn[F[I], P MapOver C, F[O]] = 
    new EvalPathOn[F[I], P MapOver C, F[O]] {
      def apply(path: Path)(in: In): Out = {
        path.out := (
          functor.map(in.value){ i => 
            evalInner(path.inner)( (path.inner.in: P#In) := i ).value 
          }
        )
      }
    }

    // trivial mapping over ExactlyOne:
    implicit def evalMapOverExactlyOne[
      P <: AnyPath, I, O
    ](implicit
      evalInner: EvalPathOn[I, P, O]
    ):  EvalPathOn[I, P MapOver ExactlyOne, O] = 
    new EvalPathOn[I, P MapOver ExactlyOne, O] {
      def apply(path: Path)(in: In): Out = evalInner(path.inner)(in)
    }


    implicit def evalFlatten[
      P <: AnyPath, 
      C <: AnyContainer,
      I, F[_], G[_], O, FGO
    ](implicit
      evalInner: EvalPathOn[I, P, F[G[O]]],
      // NOTE: this should be provided for particular F, G, H by implementation:
      flatten: FlattenVals[F, G, O] { type Out = FGO }
    ):  EvalPathOn[I, Flatten[P, C], FGO] = 
    new EvalPathOn[I, Flatten[P, C], FGO] {
      def apply(path: Path)(in: In): Out = {
        val nested = evalInner(path.inner)(in).value
        path.out := flatten(nested)
      }
    }

    // NOTE: this is the same a general thing, but then there is no secod container (i.e. it's virtual Id[])
    // it is an experiment, let's see how it works (we need more tests for flatten)
    implicit def evalFlattenWithOuterId[
      P <: AnyPath { 
        type Out <: AnyGraphType { 
          type Container = ExactlyOne 
        }
      }, I, O
    ](implicit
      evalInner: EvalPathOn[I, P, O]
    ):  EvalPathOn[I, Flatten[P, P#Out#Inside#Container], O] = 
    new EvalPathOn[I, Flatten[P, P#Out#Inside#Container], O] {
      def apply(path: Path)(in: In): Out = path.out := evalInner(path.inner)(in).value
    }

    implicit def evalFlattenWithInnerId[
      P <: AnyPath { 
        type Out <: AnyGraphType { 
          type Inside <: AnyGraphType { 
            type Container = ExactlyOne
          }
        }
      }, C <: AnyContainer, I, O
    ](implicit
      evalInner: EvalPathOn[I, P, O]
    ):  EvalPathOn[I, Flatten[P, C], O] = 
    new EvalPathOn[I, Flatten[P, C], O] {
      def apply(path: Path)(in: In): Out = path.out := evalInner(path.inner)(in).value
    }


    implicit def evalFork[
      I, P <: AnyPath, O
    ](implicit
      evalInner: EvalPathOn[I, P, O]
    ):  EvalPathOn[I, Fork[P], (O, O)] = 
    new EvalPathOn[I, Fork[P], (O, O)] {
      def apply(path: Path)(in: In): Out = {
        val outVal = evalInner(path.inner)(in).value
        path.out := ( (outVal, outVal) )
      }
    }


    implicit def evalMerge[
      First <: AnyGraphType,
      Second <: AnyGraphType,
      OutC <: AnyContainer,
      F, S, FS
    ](implicit
      merge: MergeVals[F, S] { type Out = FS }
    ):  EvalPathOn[(F, S), Merge[First, Second, OutC], FS] = 
    new EvalPathOn[(F, S), Merge[First, Second, OutC], FS] {
      def apply(path: Path)(in: In): Out = {
        path.out := merge(in.value._1, in.value._2)
      }
    }


    implicit def evalPar[
      FI, SI,
      F <: AnyPath, S <: AnyPath,
      FO, SO
    ](implicit
      eval1:  EvalPathOn[FI, F, FO], 
      eval2: EvalPathOn[SI, S, SO]
    ):  EvalPathOn[(FI, SI), Par[F, S], (FO, SO)] = 
    new EvalPathOn[(FI, SI), Par[F, S], (FO, SO)] {
      def apply(path: Path)(in: In): Out = {
        path.out := ((
          eval1(path.first) ( (path.first.in: F#In)  := in.value._1 ).value,
          eval2(path.second)( (path.second.in: S#In) := in.value._2 ).value
        ))
      }
    }


    import scalaz.\/
    implicit def evalOr[
      FI, SI,
      F <: AnyPath, S <: AnyPath,
      FO, SO
    ](implicit
      evalFirst:  EvalPathOn[FI, F, FO], 
      evalSecond: EvalPathOn[SI, S, SO]
    ):  EvalPathOn[FI \/ SI, Or[F, S], FO \/ SO] = 
    new EvalPathOn[FI \/ SI, Or[F, S], FO \/ SO] {
      def apply(path: Path)(in: In): Out = {
        path.out := ( in.value.bimap(
          fi => evalFirst(path.first)( (path.first.in: F#In) := fi ).value,
          si => evalSecond(path.second)( (path.second.in: S#In) := si ).value
        ))
      }
    }

  }

}
