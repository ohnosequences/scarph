
```scala
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

```




[test/scala/ohnosequences/scarph/asserts.scala]: ../../../../../test/scala/ohnosequences/scarph/asserts.scala.md
[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummy.scala.md
[test/scala/ohnosequences/scarph/impl/writes.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/writes.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[test/scala/ohnosequences/scarph/implicitSearch.scala]: ../../../../../test/scala/ohnosequences/scarph/implicitSearch.scala.md
[test/scala/ohnosequences/scarph/SchemaCreation.scala]: ../../../../../test/scala/ohnosequences/scarph/SchemaCreation.scala.md
[main/scala/ohnosequences/scarph/arities.scala]: ../arities.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: ../schemas.scala.md
[main/scala/ohnosequences/scarph/predicates.scala]: ../predicates.scala.md
[main/scala/ohnosequences/scarph/package.scala]: ../package.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: ../objects.scala.md
[main/scala/ohnosequences/scarph/impl/distributivity.scala]: distributivity.scala.md
[main/scala/ohnosequences/scarph/impl/tensors.scala]: tensors.scala.md
[main/scala/ohnosequences/scarph/impl/evals.scala]: evals.scala.md
[main/scala/ohnosequences/scarph/impl/category.scala]: category.scala.md
[main/scala/ohnosequences/scarph/impl/biproducts.scala]: biproducts.scala.md
[main/scala/ohnosequences/scarph/impl/relations.scala]: relations.scala.md
[main/scala/ohnosequences/scarph/syntax/package.scala]: ../syntax/package.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: ../syntax/objects.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: ../syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/writes.scala]: ../syntax/writes.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: ../morphisms.scala.md
[main/scala/ohnosequences/scarph/tensor.scala]: ../tensor.scala.md
[main/scala/ohnosequences/scarph/axioms.scala]: ../axioms.scala.md
[main/scala/ohnosequences/scarph/isomorphisms.scala]: ../isomorphisms.scala.md
[main/scala/ohnosequences/scarph/writes.scala]: ../writes.scala.md
[main/scala/ohnosequences/scarph/rewrites.scala]: ../rewrites.scala.md
[main/scala/ohnosequences/scarph/biproduct.scala]: ../biproduct.scala.md