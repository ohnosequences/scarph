package ohnosequences.scarph

import shapeless.record._

/*
  This trait represents a mapping between 

  - members `Tpe` of a universe of types `TYPE`
  - and `Raw` a type meant to be a denotation of `Tpe` thus the name

  Tagging is used for being able to operate on `Raw` values knowing what they are denotating; `Rep` is just `Raw` tagged with the `.type` of this denotation. So, summarizing

  - `Tpe` is the denotated type
  - `Raw` is its denotation
  - `Rep <: Raw` is just `Raw` tagged with `this.type`
*/

trait AnyDenotationLike {

 type Raw
 type Rep <: Raw
}
trait AnyDenotation extends AnyDenotationLike { self =>

  /* The base type for the types that this thing denotes */
  type TYPE
  type Tpe <: TYPE
  // TODO what about a version without this val?
  val tpe: Tpe

  /*
    The type used to denotate `Tpe`.
  */
  type Raw
 
  /*
    `Raw` tagged with `self.type`; this lets you recognize a denotation while being able to operate on it as `Raw`.
  */
  final type Rep = AnyDenotation.TaggedWith[self.type]

  /*
    `Raw` enters, `Rep` leaves
  */
  final def ->>(r: Raw): self.Rep = AnyDenotation.tagWith[self.type](r)
}

/*
  Bound the universe of types to be `T`s
*/
trait Denotation[T] extends AnyDenotation { 

  type TYPE = T
}

/*
  The companion object contains mainly tagging functionality.
*/
object AnyDenotation {

  type TaggedWith[D <: AnyDenotation] = D#Raw with Tag[D]

  def tagWith[D <: AnyDenotation with Singleton] = new TagBuilder[D]

  class TagBuilder[D <: AnyDenotation] {
    def apply(dr : D#Raw): TaggedWith[D] = dr.asInstanceOf[TaggedWith[D]]
  }

  trait AnyTag {

    type Denotation <: AnyDenotation
    type DenotedType = Denotation#Tpe
  }

  trait Tag[D <: AnyDenotation] extends AnyTag with KeyTag[D, D#Raw] {

    type Denotation = D
  }

}
