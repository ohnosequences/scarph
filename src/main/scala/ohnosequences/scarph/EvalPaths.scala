package ohnosequences.scarph

import ohnosequences.cosas._

trait AnyEvalPath {

  type Path  <: AnyPath
  val path: Path

  type InVal
  type OutVal

  type In <: InVal LabeledBy path.In
  type Out = OutVal LabeledBy path.Out

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

  // abstract class EvalComposition[I, P <: AnyComposition, M, O](val composed: P) extends EvalPathOn[I,P,O](composed) { comp =>

  //   val evalFirst:  EvalPathOn[I,path.first.type,M] { type In = I LabeledBy comp.path.In }
  //   val evalSecond: EvalPathOn[M,path.second.type,O] { type In = M LabeledBy comp.path.first.Out }

  //   def apply(in: In): Out = {

  //     val firstResult = evalFirst(in)
      
  //     evalSecond(firstResult)
  //   }
  // }
}
