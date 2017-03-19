package ohnosequences.scarph

import ohnosequences.cosas.types._

case class AddVertexSyntax[G](u: unit := G) extends AnyVal {

  def addVertex[V <: AnyVertex, RV](v: V)
    (implicit
      adder: CanAddVertices[G, V, RV]
    ): V := RV = {
      adder.addVertex(u.value)(v)
    }
}

case class AddEdgeSyntax[E <: AnyEdge](e: E) extends AnyVal {

  def addEdge[RE, RS, RT](
    src: E#Source := RS,
    tgt: E#Target := RT
  )(implicit
    adder: CanAddEdges[RS, E, RE, RT]
  ): E := RE = {
    adder.addEdge(e)(src, tgt)
  }
}

case class SetPropertySyntax[E <: AnyGraphElement, RE](e: E := RE) extends AnyVal {

  def setProperty[P <: AnyProperty { type Source = E }](
    p: P,
    v: P#Target#Val
  )(implicit
    adder: CanSetProperties[E, RE, P]
  ): E := RE = {
    adder.setProperty(e, p, v)
  }
}
