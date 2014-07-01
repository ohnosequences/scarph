package ohnosequences.scarph

import ohnosequences.typesets._
import scala.reflect._

/* Properties */
trait AnyProperty extends Denotation[AnyProperty] { self =>
  val label: String
  val classTag: ClassTag[self.Raw]

  type Tpe = self.type
  val  tpe = self: self.type
}

/* Evidence that an arbitrary type `Smth` has property `P` */
sealed class HasProperty[S, P <: AnyProperty]
/* or a set of properties `Ps` */
sealed class HasProperties[S, Ps <: TypeSet : boundedBy[AnyProperty]#is] 

object AnyProperty {
  /* This implicit is a bridge from `HasProperties` to `HasProperty` */ 
  implicit def FromSetToAProperty[T, P <: AnyProperty, Ps <: TypeSet]
    (implicit ps: T HasProperties Ps, ep: P ∈ Ps): HasProperty[T, P] = new HasProperty[T, P]
}

/* Properties sould be defined as case objects: `case object Name extends Property[String]` */
class Property[V](implicit val classTag: ClassTag[V]) extends AnyProperty {
  val label = this.toString
  type Raw = V 
}

object Property {
  /* For context bounds: `P <: AnyProperty: Property.Of[X]#is` */
  type Of[S] = { type is[P <: AnyProperty] = S HasProperty P }
}

class HasPropertiesOps[T](t: T) {
  /* Handy way of creating an implicit evidence saying that this vertex type has that property */
  def has[P <: AnyProperty](p: P) = new (T HasProperty P)
  def has[Ps <: TypeSet : boundedBy[AnyProperty]#is](ps: Ps) = new (T HasProperties Ps)

  /* Takes a set of properties and filters out only those, which this vertex "has" */
  def filterMyProps[Ps <: TypeSet : boundedBy[AnyProperty]#is](ps: Ps)
    (implicit f: FilterProps[T, Ps]) = f(ps)
}


/* Read a property from a representation */
trait CanGetProperties { self: Representable =>

  type PropertiesOwner

  abstract class PropertyGetter[P <: AnyProperty](val p: P) {
    def apply(rep: self.Rep): p.Raw
  }

  implicit def propertyOps(rep: self.Rep): PropertyOps = PropertyOps(rep)
  case class   PropertyOps(rep: self.Rep) {
    def get[P <: Singleton with AnyProperty: Property.Of[PropertiesOwner]#is](p: P)
      (implicit mkGetter: p.type => PropertyGetter[p.type]): p.Raw = 
        mkGetter(p).apply(rep)
  }

  /* If we have just an independent getter for a particular property: */
  implicit def idGetter[P <: AnyProperty: Property.Of[PropertiesOwner]#is](p: P)
    (implicit getter: PropertyGetter[P]) = getter
}

trait CanGetPropertiesOfItself extends CanGetProperties { self: Representable =>
  type PropertiesOwner = self.type
}

trait CanGetPropertiesOfTpe extends CanGetProperties { self: AnyDenotation =>
  type PropertiesOwner = self.Tpe
}

import shapeless._, poly._
import ohnosequences.typesets._

/* 
  For a given arbitrary type `Smth`, filters any property set, 
  leaving only those which have the `Smth HasProperty _` evidence
*/
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
