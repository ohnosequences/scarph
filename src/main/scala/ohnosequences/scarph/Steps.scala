package ohnosequences.scarph

import AnyEvalPath._

/* Basic steps: */
case class get[P <: AnyProp](val property: P) extends AnyPath {

  type Property = P
  type InT = Property#Owner
  lazy val inT = property.owner
  type InC = ExactlyOne.type
  lazy val inC = ExactlyOne

  type In = Property#Owner
  lazy val in = property.owner

  type OutT = P
  lazy val outT = property
  type OutC = ExactlyOne.type
  lazy val outC = ExactlyOne

  type Out = P
  lazy val out = property

  def evalOn[I](input: I LabeledBy In)(implicit eval: EvalGet[I,P]): P#Raw LabeledBy Out = {

    eval(this)(input)
  }
}

case class in[E <: AnyEdgeType](val edge: E) extends AnyPath {

  type InT = edge.Target
  lazy val  inT = edge.target
  type InC = ExactlyOne.type
  lazy val inC = ExactlyOne
  type In = edge.Target
  lazy val in = edge.target

  type OutT = E
  lazy val  outT: OutT = edge
  type OutC = edge.inC.type
  lazy val outC: OutC  = edge.inC
  type Out = edge.inC.C[E]
  lazy val out: Out = edge.inC(edge)
}
case class inV[E <: AnyEdgeType](val edge: E) extends AnyPath {

  type InT = E#Target
  lazy val inT = edge.target
  type InC = ExactlyOne.type
  lazy val inC = ExactlyOne
  type In = E#Target
  lazy val in = edge.target

  type OutT = E#Source
  lazy val  outT: OutT = edge.source
  type OutC = E#InC
  lazy val outC: OutC = edge.inC
  type Out = E#InC#C[E#Source]
  lazy val out: Out = edge.inC(edge.source)
}
case class out[E <: AnyEdgeType](val edge: E) extends AnyPath {

  type InT = E#Source
  lazy val inT = edge.source
  type InC = ExactlyOne.type
  lazy val inC = ExactlyOne
  type In = E#Source
  lazy val in = edge.source

  type OutT = E
  lazy val outT: OutT = edge
  type OutC = edge.outC.type
  lazy val outC: OutC  = edge.outC
  type Out = E#OutC#C[E]
  lazy val out: Out = edge.outC(edge)
}
case class outV[E <: AnyEdgeType](val edge: E) extends AnyPath {

  type InT = E#Source
  lazy val  inT = edge.source
  type InC = ExactlyOne.type
  lazy val inC = ExactlyOne
  type In = E#Source
  lazy val in: In = edge.source

  type OutT = E#Target
  lazy val  outT = edge.target
  type OutC = E#OutC
  lazy val outC: OutC  = edge.outC
  type Out = E#OutC#C[E#Target]
  lazy val out: Out = edge.outC(edge.target)
}
case class src[E <: AnyEdgeType](val edge: E) extends AnyPath {

  type InT = E
  lazy val inT = edge
  type InC = ExactlyOne.type
  lazy val inC = ExactlyOne
  type In = E
  lazy val in: In = edge

  type OutT = E#Source
  lazy val outT = edge.source
  type OutC = ExactlyOne.type
  lazy val outC = ExactlyOne
  type Out = E#Source
  lazy val out: Out = edge.source

  def evalOn[I,O](input: I LabeledBy In)(implicit eval: EvalSource[I,E,O]): O LabeledBy Out = {
    
    eval(this)(input)
  }
}

case class target[E <: AnyEdgeType](val edge: E) extends AnyPath {

  type InT = E
  lazy val inT = edge
  type InC = ExactlyOne.type
  lazy val inC = ExactlyOne
  type In = E
  lazy val in: In = edge

  type OutT = edge.Target
  lazy val outT = edge.target
  type OutC = ExactlyOne.type
  lazy val outC = ExactlyOne
  type Out = edge.Target
  lazy val out: Out = edge.target
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
