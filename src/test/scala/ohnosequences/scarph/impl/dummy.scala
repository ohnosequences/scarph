package ohnosequences.scarph.test

import ohnosequences.scarph._, implementations._, graphTypes._, evals._

object dummy extends DefaultEvals {

  case object Dummy
  type Dummy = Dummy.type

  implicit def tensorImpl:
      TensorImpl[Dummy, Dummy, Dummy] =
  new TensorImpl[Dummy, Dummy, Dummy] {

    def leftProj(i: Impl): Left = Dummy
    def rightProj(i: Impl): Right = Dummy
    def apply(l: Left, r: Right): Impl = Dummy
  }

  implicit def zeroImpl:
      ZeroImpl[Dummy] =
  new ZeroImpl[Dummy] {
    //type Inside = Dummy*/

    def apply(): Impl = Dummy
  }

  implicit def biproductImpl:
      BiproductImpl[Dummy, Dummy, Dummy] =
  new BiproductImpl[Dummy, Dummy, Dummy] {

    def leftProj(i: Impl): Left = Dummy
    def rightProj(i: Impl): Right = Dummy

    def leftInj(l: Left): Impl = l
    def rightInj(r: Right): Impl = r

    def apply(l: Left, r: Right): Impl = Dummy
  }

  implicit def edgeImpl:
      EdgeImpl[Dummy, Dummy, Dummy] =
  new EdgeImpl[Dummy, Dummy, Dummy] {

    def source(i: Impl): Source = Dummy
    def target(i: Impl): Target = Dummy
  }

  implicit def vertexInImpl:
      VertexInImpl[Dummy, Dummy, Dummy] =
  new VertexInImpl[Dummy, Dummy, Dummy] {

    def inE(i: Impl, e: AnyEdge): InEdges = Dummy
    def inV(i: Impl, e: AnyEdge): InVertices = Dummy
  }

  implicit def vertexOutImpl:
      VertexOutImpl[Dummy, Dummy, Dummy] =
  new VertexOutImpl[Dummy, Dummy, Dummy] {

    def outE(i: Impl, e: AnyEdge): OutEdges = Dummy
    def outV(i: Impl, e: AnyEdge): OutVertices = Dummy
  }

  implicit def dummyPropertyImpl:
      PropertyImpl[Dummy, Dummy] =
  new PropertyImpl[Dummy, Dummy] {

    def lookup(i: Impl): Element = Dummy
    def get(e: Element, p: AnyGraphProperty): Impl = Dummy
  }

  implicit def unitImpl:
      UnitImpl[Dummy, Dummy] =
  new UnitImpl[Dummy, Dummy] {

    def fromUnit(u: Impl): Smth = Dummy
    def toUnit(s: Smth): Impl = Dummy
  }

}
