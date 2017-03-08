package ohnosequences.scarph.test

import ohnosequences.scarph._
import ohnosequences.scarph.impl._
import ohnosequences.cosas.types._
import scala.Function.const

case object dummy {

  trait Dummy

  case object DummyEdge extends Dummy
  type DummyEdge = DummyEdge.type

  case object DummyVertex extends Dummy
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


    implicit val dummyZeroEdge:   RawFromZero[DummyEdge]   = new RawFromZero[DummyEdge]   { def apply() = DummyEdge }
    implicit val dummyZeroVertex: RawFromZero[DummyVertex] = new RawFromZero[DummyVertex] { def apply() = DummyVertex }

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
