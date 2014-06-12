package ohnosequences.scarph

/*
  Properties
*/


trait AnyProperty extends AnyDenotation {
  val label: String

  type TYPE <: AnyProperty
}

import scala.reflect._

/* 
  Properties sould be defined as case objects:

  ``` scala
  case object Name extends Property[String]
  ```
*/
class Property[V](implicit c: ClassTag[V]) extends AnyProperty with Denotation[AnyProperty] {
  val label = this.toString

  type Tpe = this.type
  val  tpe = this: Tpe

  type Raw = V 
}

object AnyProperty {

  import SmthHasProperty._

  type VertexTag = AnyDenotation.AnyTag { type Denotation <: AnyVertex }

  /* Right associative property getter for vertices */
  implicit class PropertyOps[P <: AnyProperty](val p: P) {

    def %:[VT <: VertexTag](vr: VT)
      (implicit
        ev: PropertyOf[vr.DenotedType]#is[P],
        mkReader: VT => ReadFrom[VT]
      ): p.Raw = mkReader(vr).apply(p)

  }

  /* For using `%:` you have to provide an implicit val of `ReadFrom` */
  abstract class ReadFrom[VT <: VertexTag](val vt: VT) {
    // NOTE: can't add `PropertyOf[vt.DenotedType]#is` requirement here
    def apply[P <: AnyProperty](p: P): p.Raw
  }

}

/* Evidence that an arbitrary type `Smth` has property `Property` */
trait SmthHasProperty {
  type Smth
  type Property <: AnyProperty
}

case class HasProperty[S, P <: AnyProperty]
  (val smth: S, val property: P) extends SmthHasProperty {
    type Smth = S
    type Property = P
}

object SmthHasProperty {

  type PropertyOf[S] = { 
    type is[P <: AnyProperty] = SmthHasProperty { type Smth = S; type Property = P }
  }

  import ohnosequences.typesets._

  implicit def FinalVertexTypeHasProperty[VT <: AnyVertexType, P <: AnyProperty]
    (implicit e: P ∈ VT#Props): PropertyOf[VT]#is[P] =
      new SmthHasProperty { type Smth = VT; type Property = P }
}


import shapeless._, poly._
import ohnosequences.typesets._

/* For a given arbitrary type `Smth`, filters any property set, 
   leaving only those which have the `Smth HasProperty _` evidence */
trait FilterProps[Smth, Ps <: TypeSet] extends DepFn1[Ps] {
  type Out <: TypeSet
}

object FilterProps extends FilterProps2 {
  // the case when there is this evidence (leaving the head)
  implicit def consFilter[Smth, H <: AnyProperty, T <: TypeSet, OutT <: TypeSet]
    (implicit
      h: Smth HasProperty H,
      t: Aux[Smth, T, OutT]
    ): Aux[Smth, H :~: T, H :~: OutT] =
      new FilterProps[Smth, H :~: T] { type Out = H :~: OutT
        def apply(s: H :~: T): Out = s.head :~: t(s.tail)
      }
}

trait FilterProps2 {
  def apply[Smth, Ps <: TypeSet](implicit filt: FilterProps[Smth, Ps]): Aux[Smth, Ps, filt.Out] = filt

  type Aux[Smth, In <: TypeSet, O <: TypeSet] = FilterProps[Smth, In] { type Out = O }
  
  implicit def emptyFilter[Smth]: Aux[Smth, ∅, ∅] =
  new FilterProps[Smth, ∅] {
    type Out = ∅
    def apply(s: ∅): Out = ∅
  }

  // the low-priority case when there is no evidence (just skipping head)
  implicit def skipFilter[Smth, H <: AnyProperty, T <: TypeSet, OutT <: TypeSet]
  (implicit t: Aux[Smth, T, OutT]): Aux[Smth, H :~: T, OutT] =
    new FilterProps[Smth, H :~: T] { type Out = OutT
      def apply(s: H :~: T): Out = t(s.tail)
    }
}


/* This applies `FilterProps` to a list of `Smth`s (`Ts` here) */
trait ZipWithProps[Ts <: TypeSet, Ps <: TypeSet] extends DepFn2[Ts, Ps] {
  type Out <: TypeSet
}

object ZipWithProps {
  def apply[Ts <: TypeSet, Ps <: TypeSet]
    (implicit z: ZipWithProps[Ts, Ps]): Aux[Ts, Ps, z.Out] = z

  type Aux[Ts <: TypeSet, Ps <: TypeSet, O <: TypeSet] = ZipWithProps[Ts, Ps] { type Out = O }
  
  implicit def emptyZipWithProps[Ps <: TypeSet]: Aux[∅, Ps, ∅] =
    new ZipWithProps[∅, Ps] {
      type Out = ∅
      def apply(s: ∅, ps: Ps): Out = ∅
    }

  implicit def consZipWithProps[H, T <: TypeSet, Ps <: TypeSet, OutT <: TypeSet]
    (implicit 
      h: FilterProps[H, Ps],
      t: Aux[T, Ps, OutT]
    ): Aux[H :~: T, Ps, (H, h.Out) :~: OutT] =
      new ZipWithProps[H :~: T, Ps] { type Out = (H, h.Out) :~: OutT
        def apply(s: H :~: T, ps: Ps): Out = (s.head, h(ps)) :~: t(s.tail, ps)
      }
}
