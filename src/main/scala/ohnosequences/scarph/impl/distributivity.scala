package ohnosequences.scarph.impl

import ohnosequences.scarph._

trait Distributivity extends Tensors with Biproducts {

  // NOTE: this won't be enough, because we want to use RawTensor in RawBiproduct and another way round
  // type DistributivityBound = TensorBound with BiproductBound

  type DistributivityBound
  type TensorBound    = DistributivityBound
  type BiproductBound = DistributivityBound

  def raw_distribute[
    X <: DistributivityBound,
    A <: DistributivityBound,
    B <: DistributivityBound
  ](xab: RawTensor[X, RawBiproduct[A, B]]):
    RawBiproduct[RawTensor[X, A], RawTensor[X, B]]

  def raw_undistribute[
    X <: DistributivityBound,
    A <: DistributivityBound,
    B <: DistributivityBound
  ](xab: RawBiproduct[RawTensor[X, A], RawTensor[X, B]]):
    RawTensor[X, RawBiproduct[A, B]]


  implicit final def eval_distribute[
    X <: AnyGraphObject, RX <: DistributivityBound,
    A <: AnyGraphObject, RA <: DistributivityBound,
    B <: AnyGraphObject, RB <: DistributivityBound
  ]: Eval[
    distribute[X, A, B],
    RawTensor[RX, RawBiproduct[RA, RB]],
    RawBiproduct[RawTensor[RX, RA], RawTensor[RX, RB]]
  ] = new Eval( morph => raw_distribute )


  implicit final def eval_undistribute[
    X <: AnyGraphObject, RX <: DistributivityBound,
    A <: AnyGraphObject, RA <: DistributivityBound,
    B <: AnyGraphObject, RB <: DistributivityBound
  ]: Eval[
    undistribute[X, A, B],
    RawBiproduct[RawTensor[RX, RA], RawTensor[RX, RB]],
    RawTensor[RX, RawBiproduct[RA, RB]]
  ] = new Eval( morph => raw_undistribute )


  implicit def fromUnitBiproduct[U, L <: BiproductBound, R <: BiproductBound]
  (implicit
    l: FromUnit[U, L],
    r: FromUnit[U, R]
  ):  FromUnit[U, RawBiproduct[L, R]] =
  new FromUnit[U, RawBiproduct[L, R]] {

    def fromUnit(u: U, o: AnyGraphObject): T = raw_biproduct(l.fromUnit(u, o), r.fromUnit(u, o))
  }

  implicit def fromZeroTensor[
    L <: AnyGraphObject, RL <: BiproductBound,
    R <: AnyGraphObject, RR <: BiproductBound
  ](implicit
    l_fromZero: RawFromZero[L, RL],
    r_fromZero: RawFromZero[R, RR]
  ):  RawFromZero[L ⊗ R, RawTensor[RL, RR]] =
  new RawFromZero[L ⊗ R, RawTensor[RL, RR]] {

    def apply(obj: L ⊗ R) = raw_tensor(
      l_fromZero(obj.left),
      r_fromZero(obj.right)
    )
  }
}
