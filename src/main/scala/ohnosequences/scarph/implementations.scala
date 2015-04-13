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


  trait AnyVertexImpl extends AnyImpl {

    type InEdges
    type OutEdges

    def inE(i: Impl, e: AnyEdge): InEdges
    def outE(i: Impl, e: AnyEdge): OutEdges

    //def inV(i: Impl, e: AnyEdge): InEdges#Source
    //def outV(i: Impl, e: AnyEdge): OutEdges#Target
  }

  trait VertexImpl[I, IE, OE] extends AnyVertexImpl {

    type Impl = I
    type InEdges = IE
    type OutEdges = OE
  }


  // TODO: unit, element, property (value)


}
