package ohnosequences.scarph

/* Basic steps: */
case class get[P <: AnyProp](val property: P) extends AnyPath {

  type InT = property.Owner
  val inT = property.owner
  type InC = ExactlyOne.type
  val inC = ExactlyOne

  type OutT = P
  val outT = property
  type OutC = ExactlyOne.type
  val outC = ExactlyOne
}

case class in[E <: AnyEdgeType](val edge: E) extends AnyPath {

  type InT = edge.Target
  val  inT = edge.target
  type InC = ExactlyOne.type
  val inC = ExactlyOne

  type OutT = E
  val  outT = edge
  type OutC = edge.InC
  val outC  = edge.inC
}
case class inV[E <: AnyEdgeType](val edge: E) extends AnyPath {

  type InT = edge.Target
  val  inT = edge.target
  type InC = ExactlyOne.type
  val inC = ExactlyOne

  type OutT = edge.Source
  val  outT = edge.source
  type OutC = edge.InC
  val outC  = edge.inC
}
case class out[E <: AnyEdgeType](val edge: E) extends AnyPath {

  type InT = edge.Source
  val  inT = edge.source
  type InC = ExactlyOne.type
  val inC = ExactlyOne

  type OutT = E
  val  outT = edge
  type OutC = edge.OutC
  val outC  = edge.outC
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
case class source[E <: AnyEdgeType](val edge: E) extends AnyPath {

  type InT = E
  val inT = edge
  type InC = ExactlyOne.type
  val inC = ExactlyOne

  type OutT = edge.Source
  val outT = edge.source
  type OutC = ExactlyOne.type
  val outC = ExactlyOne
}

case class target[E <: AnyEdgeType](val edge: E) extends AnyPath {

  type InT = E
  val inT = edge
  type InC = ExactlyOne.type
  val inC = ExactlyOne

  type OutT = edge.Target
  val outT = edge.target
  type OutC = ExactlyOne.type
  val outC = ExactlyOne
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
