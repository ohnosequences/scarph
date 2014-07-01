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
  type Rep //= AnyTag.TaggedWith[self.type]

  /*
    `Raw` enters, `Rep` leaves
  */
  def ->>(r: Raw): self.Rep //= AnyTag.TagWith[self.type](self)(r)

  // implicit def fromRep(x: self.Rep): self.type = self
}


trait AnyDenotation extends Representable { self => 

  /* The base type for the types that this thing denotes */
  type TYPE
  type Tpe <: TYPE
  val  tpe: Tpe

  type Rep = Tagged.With[self.type]

  final def ->>(r: Raw): self.Rep = TagWith[self.type](self)(r)

  // implicit def fromRep(x: self.Rep): self.type = self
}

/*
  Bound the universe of types to be `T`s
*/
trait Denotation[T] extends AnyDenotation { type TYPE = T }

/*
  The companion object contains mainly tagging functionality.
*/
sealed trait Tag[T] 

case class TagWith[D <: Singleton with AnyDenotation](val d: D) {
  def apply(dr: d.Raw): Tagged.With[d.type] = dr.asInstanceOf[Tagged.With[d.type]]
}

object Tagged {

  type With[D <: Singleton with AnyDenotation] = D#Raw with Tag[D#Tpe]

  // Has to be empty! See http://www.scala-lang.org/old/node/11165.html#comment-49097
  // sealed trait AnyTag 
  //extends AnyTag //with KeyTag[D#Tpe, D#Raw]
  // sealed trait Tag[D <: Singleton with AnyDenotation] extends AnyTag with KeyTag[D#Tpe, D#Raw]

}
