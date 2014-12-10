package ohnosequences.scarph

import ohnosequences.cosas._
import ohnosequences.scarph.steps._
import combinators._

// NOTE: maybe this should be Fn2
// trait AnyTraverser[P <: AnyPath] {
trait AnyEvalPath {

  type Path <: AnyPath

  type InVal
  type OutVal

  type In = InVal LabeledBy Path#In
  type Out = OutVal LabeledBy Path#Out

  def apply(path: Path)(in: In): Out
}

trait AnyEvalPathOn[I, O] extends AnyEvalPath {

  type InVal = I
  type OutVal = O
}

trait EvalPathOn[I, P <: AnyPath, O] extends AnyEvalPathOn[I, O] {

  type Path = P
}

object AnyEvalPath {

  implicit def evalIdStep[T <: AnyLabelType, X]:
      EvalPathOn[X, IdStep[T], X] =
  new EvalPathOn[X, IdStep[T], X] { def apply(path: Path)(in: In): Out = in }

  implicit def evalComposition[
    I, 
    F <: AnyPath,
    G <: AnyPath { type In = F#Out },
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

  // TODO: how?
  // we need to have a map between containers and labels, so that each container is assigned a LabeledBy
  // with LabeledBys of something inside. Having that, this should be easy (assuming you can map over it)
  // this is where we would need monads and dist laws for them. PMO should be P#OutC[M#OutC[M#Out]], 
  // everything labeledby
  case class EvalMap[
    I,
    P <: AnyPath, M <: AnyPath { type In = P#OutT },
    O, PO, PMO
  ](val evalPrevPath: EvalPathOn[I, P, PO], val evalMappedPath: EvalPathOn[O, M, PMO]) 
  extends EvalPathOn[I, Map[P, M], PMO] {

    def apply(path: Path)(in: InVal LabeledBy Path#In): OutVal LabeledBy Path#Out = ???
  }

  // TODO: how?
  // we need to know something about how to go form labelings on two things to a labeling of one
  // maybe it could be fixed so that I,O are derived from FI,SI and FO,SO respectively
  // for our use, adding to labels ops for doing so sounds reasonable (making LabeledBy lax monoidal)
  // all this applies to Or, but there is a bit more complicated due to the unnaturalness of coproducts in Scala
  case class EvalPar[
    I, FI, SI,
    F <: AnyPath, S <: AnyPath,
    O, FO, SO
  ](val evalFirst: EvalPathOn[FI, F, FO], val evalSecond: EvalPathOn[SI, S, SO])
  extends EvalPathOn[I, Par[F, S], O] {

    def apply(path: Path)(in: InVal LabeledBy Path#In): OutVal LabeledBy Path#Out = ???
  }

  // TODO: how??!?!
  // The only possible way looks to be adding rev as a primitive so to say
  // each path knows how to reverse itself and return something of type rev
  case class EvalRev[I, P <: AnyPath, O]() extends EvalPathOn[I, rev[P], O] {

    def apply(path: rev[P])(in: I LabeledBy rev[P]#In): O LabeledBy rev[P]#Out = ???
  }
}
