package ohnosequences.scarph

import ohnosequences.cosas._

trait AnyEvalPath {

  type Path  <: AnyPath

  type InVal
  type OutVal

  def apply(path: Path)(in: InVal LabeledBy path.In): OutVal LabeledBy path.Out
}

trait AnyEvalPathOn[I, O] extends AnyEvalPath {

  type InVal = I
  type OutVal = O
}

abstract class EvalPathOn[I,P <: AnyPath,O] extends AnyEvalPathOn[I,O] {

  type Path = P
}

object AnyEvalPath {

  // helpers

  abstract class EvalGet[I, P <: AnyProp] extends EvalPathOn[I, get[P], P#Raw] {}
  trait EvalSource[I, E <: AnyEdgeType, O] extends EvalPathOn[I, src[E], O] {

    // I need this here, I don't know why
    override def apply(path: src[E])(in: I LabeledBy path.In): O LabeledBy path.Out
  }
  abstract class EvalComposition[I, P <: AnyComposition, M, O] extends EvalPathOn[I,P,O] {

    // // to be provided implicitly; maybe add types
    // val evalFirst:  EvalPathOn[I,P#First,M] { type Path <: {type In = comp.Path#In } }
    // val evalSecond: EvalPathOn[M,P#Second,O]

    // override def apply(path: P)(in: I LabeledBy path.In): O LabeledBy path.Out = {

    //   val firstResult = evalFirst(path.first)(in)
      
    //   evalSecond(path.second)(firstResult)
    // }
  }
}
