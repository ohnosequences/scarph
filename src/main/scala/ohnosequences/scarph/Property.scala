package ohnosequences.scarph

/*
  Properties
*/


// LiteralType has a label!
// TODO move to AnyDenotation
trait AnyProperty extends LiteralType {
  // NOTE: should this go somewhere else?
  type Raw
}

import scala.reflect._

class Property[V](implicit c: ClassTag[V]) extends AnyProperty with shapeless.FieldOf[V] { 

  type Raw = V 
}

object AnyProperty {

  import SmthHasProperty._

  type VertexTag = AnyDenotationTag { type Denotation <: AnyVertex }

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
  val smth: Smth

  type Property <: AnyProperty
  val property: Property
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
}
