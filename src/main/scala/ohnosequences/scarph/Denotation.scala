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

trait Representable { self =>

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


trait AnyDenotation extends Representable {

  /* The base type for the types that this thing denotes */
  type TYPE
  type Tpe <: TYPE
  // TODO what about a version without this val?
  val tpe: Tpe
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

  type TaggedWith[D <: Singleton with Representable] = D#Raw with Tag[D]

  def     tagWith[D <: Singleton with Representable] = new TagBuilder[D]

  class TagBuilder[D <: Singleton with Representable] {
    def apply(dr : D#Raw): TaggedWith[D] = dr.asInstanceOf[TaggedWith[D]]
  }

  trait AnyTag {
    type Denotation <: Representable
    // type DenotedType = Denotation#Tpe
  }

  trait Tag[D <: Singleton with Representable] extends AnyTag with KeyTag[D, D#Raw] {

    type Denotation = D
  }

}