package ohnosequences.scarph.titan.test

import ohnosequences.scarph._
import ohnosequences.scarph.titan._

import GodsSchema._

object expressions {

  import shapeless._
  import shapeless.ops._
  import Morphism._

  val buh = GodFather.asMorphism
  val oh = Brother.asMorphism

  // val heyBehave = implicitly[buh.Target =:= oh.Source]
  // val goduncle = compose(buh, oh)

  // val other = compose(goduncle, simple(TitanFather))

  // surprisingly it works
  // val buh = par(toSingl2(bro), toSingl2(gF))

  
  // but this not
  // val superBuh = Par(buh, Simple(Pet))

  // why not
  // val godUncle = Brother --> DemiGod --> GodFather
}
