package ohnosequences.scarph

object implementations {

  import graphTypes._


  trait AnyImpl {

    type Impl
  }


  trait AnyTensorImpl extends AnyImpl {

    type Left
    def leftProj(i: Impl): Left

    type Right
    def rightProj(i: Impl): Right

    def apply(l: Left, r: Right): Impl
  }

  trait TensorImpl[I, L, R] extends AnyTensorImpl {

    type Impl = I
    type Left = L
    type Right = R
  }


  trait AnyBiproductImpl extends AnyImpl {

    type Left
    def leftProj(i: Impl): Left

    type Right
    def rightProj(i: Impl): Right

    def apply(l: Left, r: Right): Impl

    def leftInj(l: Left): Impl
    def rightInj(r: Right): Impl
  }

  trait BiproductImpl[I, L, R] extends AnyBiproductImpl {

    type Impl = I
    type Left = L
    type Right = R
  }


  trait AnyZeroImpl extends AnyImpl {

    //type Inside*/

    def apply(): Impl
  }

  trait ZeroImpl[I] extends AnyZeroImpl { type Impl = I }


  trait AnyEdgeImpl extends AnyImpl {

    type Source
    type Target

    def source(i: Impl): Source
    def target(i: Impl): Target
  }

  trait EdgeImpl[I, S, T] extends AnyEdgeImpl {

    type Impl = I
    type Source = S
    type Target = T
  }


  // TODO: probably it makes sense to separate it
  trait AnyVertexInImpl extends AnyImpl {

    type InEdges
    def inE(i: Impl, e: AnyEdge): InEdges

    type InVertices
    def inV(i: Impl, e: AnyEdge): InVertices
  }

  trait VertexInImpl[I, IE, IV] extends AnyVertexInImpl {

    type Impl = I
    type InEdges = IE
    type InVertices = IV
  }


  // TODO: probably it makes sense to separate it
  trait AnyVertexOutImpl extends AnyImpl {

    type OutEdges
    def outE(i: Impl, e: AnyEdge): OutEdges

    type OutVertices
    def outV(i: Impl, e: AnyEdge): OutVertices
  }

  trait VertexOutImpl[I, OE, OV] extends AnyVertexOutImpl {

    type Impl = I
    type OutEdges = OE
    type OutVertices = OV
  }


  // TODO: unit, element, property (value)


}
