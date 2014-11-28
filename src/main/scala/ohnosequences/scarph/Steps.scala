package ohnosequences.scarph

import AnyEvalPath._

/* Basic steps: */
case class get[P <: AnyProp](val property: P) extends AnyPath {

  type Property = P
  type InT = property.Owner
  lazy val inT = property.owner
  type InC = ExactlyOne.type
  lazy val inC = ExactlyOne

  type OutT = P
  lazy val outT = property
  type OutC = ExactlyOne.type
  lazy val outC = ExactlyOne

  def evalOn[I](input: I LabeledBy In)(implicit eval: EvalGet[I,P]): P#Raw LabeledBy Out = {

    eval(this)(input)
  }
}

case class in[E <: AnyEdgeType](val edge: E) extends AnyPath {

  type InT = edge.Target
  lazy val  inT = edge.target
  type InC = ExactlyOne.type
  lazy val inC = ExactlyOne

  type OutT = E
  lazy val  outT = edge
  type OutC = edge.InC
  lazy val outC  = edge.inC
}
case class inV[E <: AnyEdgeType](val edge: E) extends AnyPath {

  type InT = edge.Target
  lazy val inT = edge.target
  type InC = ExactlyOne.type
  lazy val inC = ExactlyOne

  type OutT = edge.Source
  val  outT = edge.source
  type OutC = edge.InC
  val outC  = edge.inC
}
case class out[E <: AnyEdgeType](val edge: E) extends AnyPath {

  type InT = edge.Source
  lazy val  inT = edge.source
  type InC = ExactlyOne.type
  lazy val inC = ExactlyOne

  type OutT = E
  lazy val  outT = edge
  type OutC = edge.OutC
  lazy val outC  = edge.outC
}
case class outV[E <: AnyEdgeType](val edge: E) extends AnyPath {

  type InT = edge.Source
  val  inT = edge.source
  type InC = ExactlyOne.type
  val inC = ExactlyOne

  type OutT = edge.Target
  val  outT = edge.target
  type OutC = edge.OutC
  val outC  = edge.outC
}
case class src[E <: AnyEdgeType](val edge: E) extends AnyPath {

  type InT = E
  lazy val inT = edge
  type InC = ExactlyOne.type
  lazy val inC = ExactlyOne

  type OutT = edge.Source
  lazy val outT = edge.source
  type OutC = ExactlyOne.type
  lazy val outC = ExactlyOne

  // def evalOn[I,O](input: I LabeledBy In)(implicit eval: EvalSource[I,E,O]): O LabeledBy Out = {

  //   eval(this)(input)
  // }
}

object src {

  implicit def srcOps[E <: AnyEdgeType](src: src[E]): sourceOps[E] = sourceOps(src)
}

case class sourceOps[E <: AnyEdgeType](src: src[E]) {

  def evalOn[I,O](input: I LabeledBy src.In)(implicit eval: EvalSource[I,E,O]): O LabeledBy src.Out = {

    eval(src)(input)
  }
}

case class target[E <: AnyEdgeType](val edge: E) extends AnyPath {

  type InT = E
  lazy val inT = edge
  type InC = ExactlyOne.type
  lazy val inC = ExactlyOne

  type OutT = edge.Target
  lazy val outT = edge.target
  type OutC = ExactlyOne.type
  lazy val outC = ExactlyOne
}

/* This is just the same as `(GetInEdges(edge) >=> GetSource(edge))` in a query,
   but with the parenthesis, i.e. grouping this composition to be evaluated together.
   Therefore, you can write an evaluator for this composition as for one step if the
   backend allows you to optimize it this way.
*/
// case class GetInVertices[E <: AnyEdgeType](val edge: E)
//     extends Compose[GetInEdges[E], GetSource[E], E#InArity](GetInEdges(edge), GetSource(edge))
// case class GetOutVertices[E <: AnyEdgeType](val edge: E)
//     extends Compose[GetOutEdges[E], GetTarget[E], E#OutArity](GetOutEdges(edge), GetTarget(edge))
