package ohnosequences.scarph

object evals {

  import ohnosequences.cosas._, types._, fns._
  import graphTypes._, paths._, steps._, combinators._, containers._


  // NOTE: maybe this should be Fn2
  trait AnyEvalPath {

    type Path <: AnyPath

    type InVal
    type OutVal

    type In = InVal Denotes InOf[Path]
    type Out = OutVal Denotes OutOf[Path]

    def apply(path: Path)(in: In): Out
  }

  trait EvalPathOn[I, P <: AnyPath, O] extends AnyEvalPath {

    type InVal = I
    type OutVal = O
    type Path = P
  }

  trait FlattenVals[F[_], G[_], X] extends Fn1[F[G[X]]]

  object AnyEvalPath {

    implicit def evalComposition[
      I, 
      F <: AnyPath,
      G <: AnyPath { type InC = F#OutC; type InT = F#OutT },
      X, O
    ](implicit
      evalFirst:  EvalPathOn[I, F, X],
      evalSecond: EvalPathOn[X, G, O]
    ):  EvalPathOn[I, Composition[F, G], O] = 
    new EvalPathOn[I, Composition[F, G], O] {
      def apply(path: Path)(in: In): Out = {
        val firstResult = evalFirst(path.first)(in)
        evalSecond(path.second)(firstResult)
      }
    }


    implicit def evalMapOver[
      P <: AnyPath { type InC = ExactlyOne.type }, C <: AnyContainer,
      I, F[_], O
    ](implicit
      evalInner: EvalPathOn[I, P, O],
      functor: scalaz.Functor[F]
    ):  EvalPathOn[F[I], P MapOver C, F[O]] = 
    new EvalPathOn[F[I], P MapOver C, F[O]] {
      def apply(path: Path)(in: In): Out = {
        val inner = path.path

        outOf(path) := (
          functor.map(in.value){ i => 
            evalInner(inner)( inOf(inner) := i ).value 
          }
        )
      }
    }


    implicit def evalFlatten[
      P <: AnyPath { type OutT <: AnyContainerType }, 
      C <: AnyContainer,
      I, F[_], G[_], O, FGO
    ](implicit
      evalInner: EvalPathOn[I, P, F[G[O]]],
      // NOTE: this should be provided for particular F, G, H by implementation:
      flatten: FlattenVals[F, G, O] { type Out = FGO }
    ):  EvalPathOn[I, Flatten[P, C], FGO] = 
    new EvalPathOn[I, Flatten[P, C], FGO] {
      def apply(path: Path)(in: In): Out = {
        val nested = evalInner(path.path)(in).value
        outOf(path) := flatten(nested)
      }
    }

    // NOTE: this is the same a general thing, but then there is no secod container (i.e. it's virtual Id[])
    // it is an experiment, let's see how it works (we need more tests for flatten)
    implicit def evalFlattenWithOuterId[
      P <: AnyPath { type OutC = ExactlyOne.type; type OutT <: AnyContainerType }, 
      I, O
    ](implicit
      evalInner: EvalPathOn[I, P, O]
    ):  EvalPathOn[I, Flatten[P, P#OutT#Container], O] = 
    new EvalPathOn[I, Flatten[P, P#OutT#Container], O] {
      def apply(path: Path)(in: In): Out = outOf(path) := evalInner(path.path)(in).value
    }

    implicit def evalFlattenWithInnerId[
      P <: AnyPath { type OutT <: AnyContainerType { type Container = ExactlyOne.type } }, 
      C <: AnyContainer,
      I, O
    ](implicit
      evalInner: EvalPathOn[I, P, O]
    ):  EvalPathOn[I, Flatten[P, C], O] = 
    new EvalPathOn[I, Flatten[P, C], O] {
      def apply(path: Path)(in: In): Out = outOf(path) := evalInner(path.path)(in).value
    }


    implicit def evalPar[
      FI, SI,
      F <: AnyPath, S <: AnyPath,
      FO, SO
    ](implicit
      evalFirst:  EvalPathOn[FI, F, FO], 
      evalSecond: EvalPathOn[SI, S, SO]
    ):  EvalPathOn[(FI, SI), F ⨂ S, (FO, SO)] = 
    new EvalPathOn[(FI, SI), F ⨂ S, (FO, SO)] {
      def apply(path: Path)(in: In): Out = {
        outOf(path) := ((
          evalFirst(path.first)( inOf(path.first) := (in.value._1) ).value,
          evalSecond(path.second)( inOf(path.second) := (in.value._2) ).value
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
    ):  EvalPathOn[FI \/ SI, F ⨁ S, FO \/ SO] = 
    new EvalPathOn[FI \/ SI, F ⨁ S, FO \/ SO] {
      def apply(path: Path)(in: In): Out = {
        outOf(path) := ( in.value.bimap(
          fi => evalFirst(path.first)( inOf(path.first) := (fi) ).value,
          si => evalSecond(path.second)( inOf(path.second) := (si) ).value
        ))
      }
    }

  }

}
