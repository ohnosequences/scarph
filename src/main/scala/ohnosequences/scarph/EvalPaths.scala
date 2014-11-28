package ohnosequences.scarph

import ohnosequences.cosas._

trait AnyEvalPath {

  type Path  <: AnyPath

  type InVal
  type OutVal

  def apply(path: Path)(in: InVal LabeledBy Path#In): OutVal LabeledBy Path#Out
}

trait AnyEvalPathOn[I, O] extends AnyEvalPath {

  type InVal = I
  type OutVal = O

  override def apply(path: Path)(in: InVal LabeledBy Path#In): OutVal LabeledBy Path#Out
}

trait EvalPathOn[I,P <: AnyPath,O] extends AnyEvalPathOn[I,O] {

  type Path = P

  override def apply(path: Path)(in: InVal LabeledBy Path#In): OutVal LabeledBy Path#Out
}

abstract class EvalPathOnInOut[
  I, In0 <: P#InC#C[P#InT],
  P <: AnyPath,
  O, Out0 <: P#OutC#C[P#OutT]
] extends EvalPathOn[I,P,O] {

  type In = In0
  type Out = Out0
}

object AnyEvalPath {

  // helpers

  trait EvalGet[I, P <: AnyProp] extends EvalPathOn[I, get[P], P#Raw] {

    override def apply(path: get[P])(in: I LabeledBy Path#In): OutVal LabeledBy get[P]#Out
  }
  trait EvalSource[I, E <: AnyEdgeType, O] extends EvalPathOn[I, src[E], O] {

    // I need this here, I don't know why
    override def apply(path: src[E])(in: I LabeledBy Path#In): O LabeledBy src[E]#Out
  }

  trait AnyEvalComposition[
    I, 
    F <: AnyPath,
    G <: AnyPath { type In = F#Out },
    X, 
    O
  ] extends EvalPathOn[I,Composition[F,G],O] {

    // // to be provided implicitly; maybe add types
   val evalFirst:  EvalPathOn[I,F,X]
   val evalSecond: EvalPathOn[X,G,O]

    override def apply(path: Composition[F,G])(in: I LabeledBy Composition[F,G]#In): O LabeledBy Composition[F,G]#Out = {

      val evFirst: EvalPathOn[I,F,X] = evalFirst
      val firstResult = evFirst(path.first)(in)
      
      evalSecond(path.second)(firstResult)
    }
  }

  case class EvalComposition[
    I, 
    F <: AnyPath,
    G <: AnyPath { type In = F#Out },
    X, 
    O
  ](val evalFirst: EvalPathOn[I,F,X], val evalSecond: EvalPathOn[X,G,O]) extends AnyEvalComposition[I,F,G,X,O] {

    override def apply(path: Composition[F,G])(in: I LabeledBy Composition[F,G]#In): O LabeledBy Composition[F,G]#Out = {

      val firstResult = evalFirst(path.first)(in)
      
      evalSecond(path.second)(firstResult)
    }
  }
}
