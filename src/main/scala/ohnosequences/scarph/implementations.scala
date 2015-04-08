package ohnosequences.scarph

object implementations {


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

  abstract class TensorImpl[I] extends AnyTensorImpl { type Impl = I }


  trait AnyBiproductImpl extends AnyImpl {

    type Left
    def leftProj(i: Impl): Left

    type Right
    def rightProj(i: Impl): Right

    def apply(l: Left, r: Right): Impl

    def leftInj(l: Left): Impl
    def rightInj(r: Right): Impl
  }

  abstract class BiproductImpl[I] extends AnyBiproductImpl { type Impl = I }


  trait AnyZeroImpl extends AnyImpl {

    type Inside

    def apply(): Impl
  }

  abstract class ZeroImpl[I] extends AnyZeroImpl { type Impl = I }


  // TODO: unit, edge, vertex, element, property (value)
}
