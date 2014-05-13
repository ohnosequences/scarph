package ohnosequences.scarph

import shapeless.record._

/*
  This trait represents a mapping between 

  - members `Tpe` of a universe of types `TYPE`
  - and `Rep` a type meant to be a denotation of `Tpe` thus the name

  Tagging is used for being able to operate on `Rep` values knowing what they are denotating.
*/
trait AnyDenotation { self =>

  /* The base type for the types that this thing denotes */
  type TYPE
  type Tpe <: TYPE
  val tpe: Tpe

  /*
    Why not `Raw` or something like that?
  */
  type Raw

  import Tagged._
  /*
    This could be called just `Rep` instead; then you'd do for `buh` extending `Buh` something like 

    - `buh ->> buh.Raw(args)` for building it
    - `buh.Rep` for requiring it
  */
  final type Rep = TaggedWith[self.type]
  /*
    `Raw` enters, `Rep` leaves
  */
  final def ->>(r: Raw): TaggedWith[self.type] = tagWith[self.type](r)
  // def ->>(r: Raw): self.Rep = tagWith[self.type](r)
}

trait Denotation[T] extends AnyDenotation { 

  type TYPE = T
}

trait AnyDenotationTag {

  type Denotation <: AnyDenotation
  type DenotedType = Denotation#Tpe
}

trait DenotationTag[D <: AnyDenotation] extends AnyDenotationTag with KeyTag[D, D#Raw] {

  type Denotation = D
}

/*
  tagging functionality
*/
object Tagged {

  type TaggedWith[D <: AnyDenotation] = D#Raw with DenotationTag[D]

  def tagWith[D <: AnyDenotation with Singleton] = new TagBuilder[D]

  class TagBuilder[D <: AnyDenotation] {
    def apply(dr : D#Raw): TaggedWith[D] = dr.asInstanceOf[TaggedWith[D]]
  }

}
