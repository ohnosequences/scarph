package ohnosequences.scarph

import ohnosequences.cosas._

trait AnyEvalPath {

  type Path  <: AnyPath

  type InVal
  type OutVal

  def apply[P <: Path](path: P)(in: InVal LabeledBy path.In): OutVal LabeledBy path.Out
}

trait AnyEvalPathOn[I, O] 
  extends AnyEvalPath {

  type InVal = I
  type OutVal = O
}

abstract class EvalPathOn[I,P <: AnyPath,O] extends AnyEvalPathOn[I,O] {

  type Path = P
}

object AnyEvalPath {

  abstract class EvalGet[I, P <: AnyProp] extends EvalPathOn[I, get[P], P#Raw] {

    // type Path = get[P]
  }

  abstract class EvalSource[I, E <: AnyEdgeType, O](val path: source[E]) extends EvalPathOn[I, source[E], O] {

    type In   = I LabeledBy E
    type Out  = O LabeledBy path.Out
  }

  abstract class EvalComposition[I, P <: AnyComposition, M, O] extends EvalPathOn[I,P,O] { comp =>

    // // to be provided implicitly; maybe add types
    // val evalFirst:  EvalPathOn[I,P#First,M] { type Path <: {type In = comp.Path#In } }
    // val evalSecond: EvalPathOn[M,P#Second,O]

    // override def apply(path: P)(in: I LabeledBy path.In): O LabeledBy path.Out = {

    //   val firstResult = evalFirst(path.first)(in)
      
    //   evalSecond(path.second)(firstResult)
    // }
  }
}
