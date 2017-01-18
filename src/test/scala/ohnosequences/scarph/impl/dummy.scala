package ohnosequences.scarph.test

import ohnosequences.scarph._, evals._
import scala.Function.const

case object dummy {

  trait Dummy

  case object DummyEdge extends Dummy
  type DummyEdge = DummyEdge.type

  case object DummyVertex extends Dummy
  type DummyVertex = DummyVertex.type


  case object categoryStructure extends CategoryStructure

  case object graphStructure extends GraphStructure {

    type RawEdge = DummyEdge
    type RawSource = DummyVertex
    type RawTarget = DummyVertex

    def outVRaw(edge: AnyEdge)(v: RawSource): RawTarget = DummyVertex
    def inVRaw(edge: AnyEdge)(v: RawTarget): RawSource = DummyVertex

    def outERaw(edge: AnyEdge)(v: RawSource): RawEdge = DummyEdge
    def sourceRaw(edge: AnyEdge)(e: RawEdge): RawSource = DummyVertex

    def inERaw(edge: AnyEdge)(v: RawTarget): RawEdge = DummyEdge
    def targetRaw(edge: AnyEdge)(e: RawEdge): RawTarget = DummyVertex
  }


  case class DummyTensor[L <: Dummy, R <: Dummy](l: L, r: R) extends Dummy

  case object DummyUnit extends Dummy
  type DummyUnit = DummyUnit.type

  case object tensorStructure extends TensorStructure {

    type TensorBound = Dummy
    type RawTensor[L <: TensorBound, R <: TensorBound] = DummyTensor[L, R]
    type RawUnit = DummyUnit

    def tensorRaw[L <: TensorBound, R <: TensorBound](l: L, r: R): RawTensor[L, R] = DummyTensor(l, r)
    def leftRaw[L <: TensorBound, R <: TensorBound](t: RawTensor[L, R]): L = t.l
    def rightRaw[L <: TensorBound, R <: TensorBound](t: RawTensor[L, R]): R = t.r
    def toUnitRaw[X <: TensorBound](x: X): RawUnit = DummyUnit


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


  case object biproductStructure extends BiproductStructure {

    type BiproductBound = Dummy
    type RawBiproduct[L <: BiproductBound, R <: BiproductBound] = DummyBiproduct[L, R]
    type RawZero = DummyZero

    def biproductRaw[L <: BiproductBound, R <: BiproductBound](l: L, r: R): RawBiproduct[L, R] =
      DummyBiproduct[L, R](l, r)

    def leftProjRaw[L <: BiproductBound, R <: BiproductBound](t: RawBiproduct[L, R]): L = t.l
    def rightProjRaw[L <: BiproductBound, R <: BiproductBound](t: RawBiproduct[L, R]): R = t.r

    def toZeroRaw[X <: BiproductBound](x: X): RawZero = DummyZero


    implicit def dummyMerge[T <: Dummy]:
        Mergeable[T] =
    new Mergeable[T] { def merge(l: T, r: T): T = r }


    implicit def dummyZeroEdge[E <: AnyEdge]:
        ZeroFor[E, DummyEdge] =
    new ZeroFor[E, DummyEdge] { def zero(o: Obj): T = DummyEdge }

    implicit def dummyZeroVertex[V <: AnyVertex]:
        ZeroFor[V, DummyVertex] =
    new ZeroFor[V, DummyVertex] { def zero(o: Obj): T = DummyVertex }

  }


  case object propertyStructure {

    implicit def eval_getV[P <: AnyProperty { type Source <: AnyVertex }]:
        Eval[DummyVertex, get[P], P#Target#Raw] =
    new Eval[DummyVertex, get[P], P#Target#Raw] {

      def rawApply(morph: InMorph): InVal => OutVal = ???

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

    implicit def eval_getE[P <: AnyProperty { type Source <: AnyEdge }]:
        Eval[DummyEdge, get[P], P#Target#Raw] =
    new Eval[DummyEdge, get[P], P#Target#Raw] {

      def rawApply(morph: InMorph): InVal => OutVal = ???

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }


    implicit def eval_lookupV[
      V,
      P <: AnyProperty { type Source <: AnyVertex; type Target <: AnyValueType { type Raw >: V }  }
    ]
    : Eval[V, lookup[P], DummyVertex] =
    new Eval[V, lookup[P], DummyVertex] {

      def rawApply(morph: InMorph): InVal => OutVal = const(DummyVertex)

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

    implicit def eval_lookupE[
      V,
      P <: AnyProperty { type Source <: AnyEdge; type Target <: AnyValueType { type Raw >: V }  }
    ]
    : Eval[V, lookup[P], DummyEdge] =
    new Eval[V, lookup[P], DummyEdge] {

      def rawApply(morph: InMorph): InVal => OutVal = const(DummyEdge)

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

  }

  case object predicateStructure {

    implicit def eval_quantify[D <: Dummy, P <: AnyPredicate]:
        Eval[D, quantify[P], D] =
    new Eval[D, quantify[P], D] {

      def rawApply(morph: InMorph): InVal => OutVal = identity[D]

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

    implicit def eval_coerce[D <: Dummy, P <: AnyPredicate]:
        Eval[D, coerce[P], D] =
    new Eval[D, coerce[P], D] {

      def rawApply(morph: InMorph): InVal => OutVal = identity[D]

      def present(morph: InMorph): Seq[String] = Seq(morph.label)
    }

  }

  case object syntax {
    import ohnosequences.cosas.types._

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
