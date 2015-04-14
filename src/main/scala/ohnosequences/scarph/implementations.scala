package ohnosequences.scarph

object implementations {

  import graphTypes._


  trait AnyImpl {

    type Impl
  }


  trait AnyTensorImpl extends AnyImpl {

    type Tensor
    type Impl = Tensor
    def apply(l: Left, r: Right): Tensor

    type Left
    def leftProj(t: Tensor): Left

    type Right
    def rightProj(t: Tensor): Right
  }

  trait TensorImpl[T, L, R] extends AnyTensorImpl {

    type Tensor = T
    type Left = L
    type Right = R
  }


  trait AnyMatchUpImpl extends AnyImpl {

    def matchUp(l: Impl, r: Impl): Impl
  }

  trait MatchUpImpl[I] extends AnyMatchUpImpl { type Impl = I }


  trait AnyBiproductImpl extends AnyImpl {

    type Biproduct
    type Impl = Biproduct
    def apply(l: Left, r: Right): Biproduct

    type Left
    def leftProj(b: Biproduct): Left
    def leftInj(l: Left): Biproduct

    type Right
    def rightProj(b: Biproduct): Right
    def rightInj(r: Right): Biproduct
  }

  trait BiproductImpl[B, L, R] extends AnyBiproductImpl {

    type Biproduct = B
    type Left = L
    type Right = R
  }


  trait AnyMergeImpl extends AnyImpl {

    def merge(l: Impl, r: Impl): Impl
  }

  trait MergeImpl[I] extends AnyMergeImpl { type Impl = I }


  trait AnyZeroImpl extends AnyImpl {

    def apply(): Impl
  }

  trait ZeroImpl[Z] extends AnyZeroImpl { type Impl = Z }


  trait AnyEdgeImpl extends AnyImpl {

    type Edge
    type Impl = Edge

    type Source
    def source(e: Edge): Source

    type Target
    def target(e: Edge): Target
  }

  trait EdgeImpl[E, S, T] extends AnyEdgeImpl {

    type Edge = E
    type Source = S
    type Target = T
  }


  // TODO: probably it makes sense to separate it
  trait AnyVertexInImpl extends AnyImpl {

    type Vertex
    type Impl = Vertex

    type InEdges
    def inE(v: Vertex, e: AnyEdge): InEdges

    type InVertices
    def inV(v: Vertex, e: AnyEdge): InVertices
  }

  trait VertexInImpl[V, InE, InV] extends AnyVertexInImpl {

    type Vertex = V
    type InEdges = InE
    type InVertices = InV
  }


  // TODO: probably it makes sense to separate it
  trait AnyVertexOutImpl extends AnyImpl {

    type Vertex
    type Impl = Vertex

    type OutEdges
    def outE(v: Vertex, e: AnyEdge): OutEdges

    type OutVertices
    def outV(v: Vertex, e: AnyEdge): OutVertices
  }

  trait VertexOutImpl[V, OutE, OutV] extends AnyVertexOutImpl {

    type Vertex = V
    type OutEdges = OutE
    type OutVertices = OutV
  }


  trait AnyPropertyImpl extends AnyImpl {

    type Property
    type Impl = Property
    def get(e: Element, p: AnyGraphProperty): Property

    type Element
    def lookup(p: Property): Element
  }

  trait PropertyImpl[P, E] extends AnyPropertyImpl {

    type Property = P
    type Element = E
  }


  trait AnyUnitImpl extends AnyImpl {

    type UnitImpl
    type Impl = UnitImpl
    def toUnit(s: Obj): UnitImpl

    type Obj
    def fromUnit(u: UnitImpl): Obj
  }

  trait UnitImpl[U, S] extends AnyUnitImpl {

    type UnitImpl = U
    type Obj = S
  }

}
