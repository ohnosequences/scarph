package ohnosequences.scarph

/* Basic steps: */
case class GetProperty[P <: AnyProp](val prop: P) extends Step[P#Owner, P](prop.owner, prop) with OutArity[ExactlyOne]

case class GetSource[E <: AnyEdgeType](val edge: E) extends Step[E, E#Source](edge, edge.source) with OutArity[ExactlyOne]
case class GetTarget[E <: AnyEdgeType](val edge: E) extends Step[E, E#Target](edge, edge.target) with OutArity[ExactlyOne]

case class  GetInEdges[E <: AnyEdgeType](val edge: E) extends Step[E#Target, E](edge.target, edge) with OutArity[E#InArity]
case class GetOutEdges[E <: AnyEdgeType](val edge: E) extends Step[E#Source, E](edge.source, edge) with OutArity[E#OutArity]




case class In[E <: AnyEdgeType](val edge: E) extends Step[E#Target, E#InC[E]](edge.target, edge.inV(edge)) with AnyWrappedPath {
}



case class InV[E <: AnyEdgeType](val edge: E) extends Step[E#Target, E#InC[E#Source]](edge.target, edge.inV(edge.source))
case class Src[E <: AnyEdgeType](val edge: E) extends Step[E, E#Source](edge, edge.source)

case class vertexOps[V <: AnyVertexType](v: V) {

  def in[E <: AnyEdgeType { type Target = V }](edge: E) = In(edge)
}

case class edgeOps[E <: AnyEdgeType](e: E) {

  def src: Src[E] = Src(e)
}

/* This is just the same as `(GetInEdges(edge) >=> GetSource(edge))` in a query,
   but with the parenthesis, i.e. grouping this composition to be evaluated together.
   Therefore, you can write an evaluator for this composition as for one step if the
   backend allows you to optimize it this way.
*/
case class GetInVertices[E <: AnyEdgeType](val edge: E)
    extends Compose[GetInEdges[E], GetSource[E], E#InArity](GetInEdges(edge), GetSource(edge))
case class GetOutVertices[E <: AnyEdgeType](val edge: E)
    extends Compose[GetOutEdges[E], GetTarget[E], E#OutArity](GetOutEdges(edge), GetTarget(edge))
