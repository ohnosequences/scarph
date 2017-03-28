
```scala
package ohnosequences.scarph.test

import ohnosequences.scarph._, impl._
import ohnosequences.cosas.types._
import scala.Function.const

case object dummy {

  trait Dummy

  trait DummyElement extends Dummy

  case object DummyEdge extends DummyElement
  type DummyEdge = DummyEdge.type

  case object DummyVertex extends DummyElement
  type DummyVertex = DummyVertex.type


  case object categoryStructure extends DaggerCategory

  case object graphStructure extends Relations {

    type RawEdge = DummyEdge
    type RawSource = DummyVertex
    type RawTarget = DummyVertex

    def raw_outV(edge: AnyEdge)(v: RawSource): RawTarget = DummyVertex
    def raw_inV(edge: AnyEdge)(v: RawTarget): RawSource = DummyVertex

    def raw_outE(edge: AnyEdge)(v: RawSource): RawEdge = DummyEdge
    def raw_source(edge: AnyEdge)(e: RawEdge): RawSource = DummyVertex

    def raw_inE(edge: AnyEdge)(v: RawTarget): RawEdge = DummyEdge
    def raw_target(edge: AnyEdge)(e: RawEdge): RawTarget = DummyVertex
  }


  case class DummyTensor[L <: Dummy, R <: Dummy](l: L, r: R) extends Dummy

  case object DummyUnit extends Dummy
  type DummyUnit = DummyUnit.type

  case object tensorStructure extends Tensors {

    type TensorBound = Dummy
    type RawTensor[L <: TensorBound, R <: TensorBound] = DummyTensor[L, R]
    type RawUnit = DummyUnit

    def raw_tensor[L <: TensorBound, R <: TensorBound](l: L, r: R): RawTensor[L, R] = DummyTensor(l, r)
    def raw_left[L <: TensorBound, R <: TensorBound](t: RawTensor[L, R]): L = t.l
    def raw_right[L <: TensorBound, R <: TensorBound](t: RawTensor[L, R]): R = t.r
    def raw_toUnit[X <: TensorBound](x: X): RawUnit = DummyUnit


    implicit def dummyMatch[T <: Dummy]:
        Matchable[T] =
    new Matchable[T] { def matchUp(l: T, r: T): T = l }

    // implicit def dummyUnitToEdge:
    //     FromUnit[DummyUnit, DummyEdge] =
    // new FromUnit[DummyUnit, DummyEdge] { def fromUnit(u: U, e: AnyGraphObject): T = DummyEdge }

    implicit def dummyUnitToVertex:
        FromUnit[DummyUnit, DummyVertex] =
    new FromUnit[DummyUnit, DummyVertex] { def fromUnit(u: U, e: AnyGraphObject): T = DummyVertex }

    // implicit def dummyUnitToTensor[U, L <: Dummy, R <: Dummy]
    // (implicit
    //   l: FromUnit[U, L],
    //   r: FromUnit[U, R]
    // ):  FromUnit[U, DummyTensor[L, R]] =
    // new FromUnit[U, DummyTensor[L, R]] {
    //
    //   def fromUnit(u: U, e: AnyGraphObject): T =
    //     DummyTensor(
    //       l.fromUnit(u, e),
    //       r.fromUnit(u, e)
    //     )
    // }

  }


  case class DummyBiproduct[L <: Dummy, R <: Dummy](l: L, r: R) extends Dummy

  case object DummyZero extends Dummy
  type DummyZero = DummyZero.type


  case object biproductStructure extends Biproducts {

    type BiproductBound = Dummy
    type RawBiproduct[L <: BiproductBound, R <: BiproductBound] = DummyBiproduct[L, R]
    type RawZero = DummyZero

    def raw_biproduct[L <: BiproductBound, R <: BiproductBound](l: L, r: R): RawBiproduct[L, R] =
      DummyBiproduct[L, R](l, r)

    def raw_leftProj[L <: BiproductBound, R <: BiproductBound](t: RawBiproduct[L, R]): L = t.l
    def raw_rightProj[L <: BiproductBound, R <: BiproductBound](t: RawBiproduct[L, R]): R = t.r

    def raw_toZero[X <: BiproductBound](x: X): RawZero = DummyZero


    implicit def dummyMerge[T <: Dummy]:
        RawMerge[T] =
    new RawMerge[T] { def apply(l: T, r: T): T = r }


    implicit def dummyZeroEdge[E <:AnyEdge]:
        RawFromZero[E, DummyEdge] =
    new RawFromZero[E, DummyEdge] { def apply(obj: E) = DummyEdge }

    implicit def dummyZeroVertex[V <: AnyVertex]:
        RawFromZero[V, DummyVertex] =
    new RawFromZero[V, DummyVertex] { def apply(obj: V) = DummyVertex }

  }


  case object propertyStructure {

    implicit def eval_getV[P <: AnyProperty { type Source <: AnyVertex }]:
        Eval[get[P], DummyVertex, P#Target#Raw] =
    new Eval( ??? )

    implicit def eval_getE[P <: AnyProperty { type Source <: AnyEdge }]:
        Eval[get[P], DummyEdge, P#Target#Raw] =
    new Eval( ??? )


    implicit def eval_lookupV[
      V,
      P <: AnyProperty { type Source <: AnyVertex; type Target <: AnyValueType { type Raw >: V }  }
    ]:  Eval[lookup[P], V, DummyVertex] =
    new Eval( _ => const(DummyVertex) )

    implicit def eval_lookupE[
      V,
      P <: AnyProperty { type Source <: AnyEdge; type Target <: AnyValueType { type Raw >: V }  }
    ]: Eval[lookup[P], V, DummyEdge] =
    new Eval( _ => const(DummyEdge) )

  }

  case object predicateStructure {

    implicit def eval_quantify[D <: Dummy, P <: AnyPredicate]:
        Eval[quantify[P], D, D] =
    new Eval( _ => identity[D] )

    implicit def eval_coerce[D <: Dummy, P <: AnyPredicate]:
        Eval[coerce[P], D, D] =
    new Eval( _ => identity[D] )

  }

  case object syntax {

    implicit def dummyObjectValOps[F <: AnyGraphObject { type Raw >: VF}, VF <: Dummy](vf: F := VF):
      DummyObjectValOps[F, VF] =
      DummyObjectValOps[F, VF](vf)

    case class DummyObjectValOps[F <: AnyGraphObject { type Raw >: VF }, VF <: Dummy](vf: F := VF) extends AnyVal {

      def ⊗[S <: AnyGraphObject, VS <: Dummy with S#Raw](vs: S := VS): (F ⊗ S) := DummyTensor[VF, VS] =
        (vf.tpe ⊗ vs.tpe) := DummyTensor(vf.value, vs.value)

      def ⊕[S <: AnyGraphObject, VS <: Dummy with S#Raw](vs: S := VS): (F ⊕ S) := DummyBiproduct[VF, VS] =
        (vf.tpe ⊕ vs.tpe) := DummyBiproduct(vf.value, vs.value)
    }
  }

}

```




[test/scala/ohnosequences/scarph/asserts.scala]: ../asserts.scala.md
[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: dummyTest.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: dummy.scala.md
[test/scala/ohnosequences/scarph/impl/writes.scala]: writes.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../TwitterSchema.scala.md
[test/scala/ohnosequences/scarph/implicitSearch.scala]: ../implicitSearch.scala.md
[test/scala/ohnosequences/scarph/SchemaCreation.scala]: ../SchemaCreation.scala.md
[main/scala/ohnosequences/scarph/arities.scala]: ../../../../../main/scala/ohnosequences/scarph/arities.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: ../../../../../main/scala/ohnosequences/scarph/schemas.scala.md
[main/scala/ohnosequences/scarph/predicates.scala]: ../../../../../main/scala/ohnosequences/scarph/predicates.scala.md
[main/scala/ohnosequences/scarph/package.scala]: ../../../../../main/scala/ohnosequences/scarph/package.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: ../../../../../main/scala/ohnosequences/scarph/objects.scala.md
[main/scala/ohnosequences/scarph/impl/distributivity.scala]: ../../../../../main/scala/ohnosequences/scarph/impl/distributivity.scala.md
[main/scala/ohnosequences/scarph/impl/tensors.scala]: ../../../../../main/scala/ohnosequences/scarph/impl/tensors.scala.md
[main/scala/ohnosequences/scarph/impl/evals.scala]: ../../../../../main/scala/ohnosequences/scarph/impl/evals.scala.md
[main/scala/ohnosequences/scarph/impl/category.scala]: ../../../../../main/scala/ohnosequences/scarph/impl/category.scala.md
[main/scala/ohnosequences/scarph/impl/biproducts.scala]: ../../../../../main/scala/ohnosequences/scarph/impl/biproducts.scala.md
[main/scala/ohnosequences/scarph/impl/relations.scala]: ../../../../../main/scala/ohnosequences/scarph/impl/relations.scala.md
[main/scala/ohnosequences/scarph/syntax/package.scala]: ../../../../../main/scala/ohnosequences/scarph/syntax/package.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: ../../../../../main/scala/ohnosequences/scarph/syntax/objects.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: ../../../../../main/scala/ohnosequences/scarph/syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/writes.scala]: ../../../../../main/scala/ohnosequences/scarph/syntax/writes.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: ../../../../../main/scala/ohnosequences/scarph/morphisms.scala.md
[main/scala/ohnosequences/scarph/tensor.scala]: ../../../../../main/scala/ohnosequences/scarph/tensor.scala.md
[main/scala/ohnosequences/scarph/axioms.scala]: ../../../../../main/scala/ohnosequences/scarph/axioms.scala.md
[main/scala/ohnosequences/scarph/isomorphisms.scala]: ../../../../../main/scala/ohnosequences/scarph/isomorphisms.scala.md
[main/scala/ohnosequences/scarph/writes.scala]: ../../../../../main/scala/ohnosequences/scarph/writes.scala.md
[main/scala/ohnosequences/scarph/rewrites.scala]: ../../../../../main/scala/ohnosequences/scarph/rewrites.scala.md
[main/scala/ohnosequences/scarph/biproduct.scala]: ../../../../../main/scala/ohnosequences/scarph/biproduct.scala.md