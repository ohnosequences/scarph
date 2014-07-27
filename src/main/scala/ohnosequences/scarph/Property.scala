package ohnosequences.scarph

import ohnosequences.typesets._
import scala.reflect._

/* Properties */
trait AnyProperty extends Representable { self =>
  val label: String
  val classTag: ClassTag[self.Raw]
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
trait CanGetProperties { self: AnyDenotation =>

  abstract class PropertyGetter[P <: AnyProperty](val p: P) {
    def apply(rep: self.Rep): p.Raw
  }
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















// copied from tabula, need to organize this








/* 
  This is a generic thing for dereriving the set of representations 
  from a set of representable singletons. For example:
  ```scala
  case object id extends Property[Int]
  case object name extends Property[String]

  implicitly[Represented.By[
    id.type :~: name.type :~: ∅,
    id.Rep  :~: name.Rep  :~: ∅
  ]]
  ```

  See examples of usage it for item properties in tests
*/
@annotation.implicitNotFound(msg = "Can't construct a set of representations for ${S}")
sealed class Represented[S <: TypeSet] { type Out <: TypeSet }

object Represented {
  type By[S <: TypeSet, O <: TypeSet] = Represented[S] { type Out = O }

  implicit val empty: ∅ By ∅ = new Represented[∅] { type Out = ∅ }

  implicit def cons[H <: Singleton with Representable, T <: TypeSet]
    (implicit t: Represented[T]): (H :~: T) By (H#Rep :~: t.Out) =
          new Represented[H :~: T] { type Out = H#Rep :~: t.Out }
}


/* Takes a set of Reps and returns the set of what they represent */
import shapeless._, poly._

trait TagsOf[S <: TypeSet] extends DepFn1[S] { type Out <: TypeSet }

object TagsOf {
  def apply[S <: TypeSet](implicit keys: TagsOf[S]): Aux[S, keys.Out] = keys

  type Aux[S <: TypeSet, O <: TypeSet] = TagsOf[S] { type Out = O }

  implicit val empty: Aux[∅, ∅] =
    new TagsOf[∅] {
      type Out = ∅
      def apply(s: ∅): Out = ∅
    }

  implicit def cons[H <: Singleton with Representable, T <: TypeSet]
    (implicit fromRep: H#Rep => H, t: TagsOf[T]): Aux[H#Rep :~: T, H :~: t.Out] =
      new TagsOf[H#Rep :~: T] {
        type Out = H :~: t.Out
        def apply(s: H#Rep :~: T): Out = fromRep(s.head) :~: t(s.tail)
      }
}

//////////////////////////////////////////////

trait ListLike[L] {
  type E // elements type

  val nil: L
  def cons(h: E, t: L): L

  def head(l: L): E
  def tail(l: L): L
}

object ListLike {
  type Of[L, T] = ListLike[L] { type E = T }
}

/* Transforms a representation of item to something else */
trait FromProperties[
    A <: TypeSet, // set of properties
    Out           // what we want to get
  ] {

  type Reps <: TypeSet            // representation of properties
  type Fun <: Singleton with Poly // transformation function

  def apply(r: Reps): Out
}

object FromProperties {
  def apply[A <: TypeSet, Reps <: TypeSet, F <: Singleton with Poly, Out](implicit tr: FromProperties.Aux[A, Reps, F, Out]):
    FromProperties.Aux[A, Reps, F, Out] = tr

  type Aux[A <: TypeSet, R <: TypeSet, F <: Singleton with Poly, Out] =
    FromProperties[A, Out] { 
      type Reps = R
      type Fun = F
    }

  type Anyhow[A <: TypeSet, R <: TypeSet, Out] =
    FromProperties[A, Out] { 
      type Reps = R
    }

  implicit def empty[Out, F <: Singleton with Poly]
    (implicit m: ListLike[Out]): FromProperties.Aux[∅, ∅, F, Out] = new FromProperties[∅, Out] {
      type Reps = ∅
      type Fun = F
      def apply(r: ∅): Out = m.nil
    }

  implicit def cons[
    F <: Singleton with Poly,
    AH <: Singleton with AnyProperty, AT <: TypeSet,
    RT <: TypeSet,
    E, Out
  ](implicit
    tagOf: AH#Rep => AH,
    listLike: ListLike.Of[Out, E], 
    transform: Case1.Aux[F, (AH, AH#Rep), E], 
    recOnTail: FromProperties.Aux[AT, RT, F, Out]
  ): FromProperties.Aux[AH :~: AT, AH#Rep :~: RT, F, Out] =
    new FromProperties[AH :~: AT, Out] {
      type Reps = AH#Rep :~: RT
      type Fun = F
      def apply(r: AH#Rep :~: RT): Out = {
        listLike.cons(
          transform((tagOf(r.head), r.head)),
          recOnTail(r.tail)
        )
      }
    }
}

///////////////////////////////////////////////////////////////

/* Transforms properties set representation from something else */
trait ToProperties[
    In,          // some other representation
    A <: TypeSet // set of corresponding properties
  ] {

  type Out <: TypeSet             // representation of properties
  type Fun <: Singleton with Poly // transformation function

  def apply(in: In, a: A): Out
}

object ToProperties {
  type Aux[In, A <: TypeSet, O <: TypeSet, F <: Singleton with Poly] = ToProperties[In, A] { type Out = O; type Fun = F } 

  def apply[In, A <: TypeSet, O <: TypeSet, F <: Singleton with Poly]
    (implicit form: ToProperties.Aux[In, A, O, F]): ToProperties.Aux[In, A, O, F] = form

  implicit def empty[In, F <: Singleton with Poly]: ToProperties.Aux[In, ∅, ∅, F] = new ToProperties[In, ∅] {
      type Out = ∅
      type Fun = F
      def apply(in: In, a: ∅): Out = ∅
    }

  implicit def cons[
    In,
    AH <: Singleton with AnyProperty, AT <: TypeSet,
    RH <: AH#Rep, RT <: TypeSet,
    F <: Singleton with Poly
  ](implicit
    f: Case1.Aux[F, (In, AH), RH], 
    t: ToProperties.Aux[In, AT, RT, F]
  ): ToProperties.Aux[In, AH :~: AT, RH :~: RT, F] =
    new  ToProperties[In, AH :~: AT] {
      type Out = RH :~: RT
      type Fun = F
      def apply(in: In, a: AH :~: AT): Out = f((in, a.head)) :~: t(in, a.tail)
    }
}