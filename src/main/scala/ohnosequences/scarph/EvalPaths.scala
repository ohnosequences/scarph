package ohnosequences.scarph

import ohnosequences.cosas._

trait AnyEvalPath {

  type Path  <: AnyPath
  val path: Path

  type InVal
  type OutVal

  type In <: InVal LabeledBy path.In
  type Out <: OutVal LabeledBy path.Out

  def apply(in: In): Out
}

trait AnyEvalPathOn[I, O] 
  extends AnyEvalPath {

  type InVal = I
  type OutVal = O
}

abstract class EvalPathOn[I,P <: AnyPath,O](val path: P) extends AnyEvalPathOn[I,O] {

  type Path = P
}

object AnyEvalPath {

  abstract class EvalComposition[I, P <: AnyComposition, M, O](val composed: P) extends EvalPathOn[I,P,O](composed) { comp =>

    // to be provided implicitly; maybe add types
    val evalFirst:  EvalPathOn[I,path.first.type,M] { type In = comp.In }
    val evalSecond: EvalPathOn[M,path.second.type,O] { type In = evalFirst.Out; type Out = comp.Out }

    type In = InVal LabeledBy path.In
    type Out = OutVal LabeledBy path.Out

    def apply(in: evalFirst.In): Out = {

      val firstResult: evalSecond.In = evalFirst(in)
      
      evalSecond(firstResult)
    }
  }
}
