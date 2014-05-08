package ohnosequences.scarph.titan.test

import ohnosequences.scarph._
import ohnosequences.scarph.titan._

import GodsSchema._

object expressions {


  val goduncle = Compose(Simple(Brother), Simple(GodFather))

  // surprisingly it works
  object buh extends Par(Simple(Brother), Simple(GodFather))

  import shapeless._
  import shapeless.ops._
  // but this not
  // val superBuh = Par(buh, Simple(Pet))
}
