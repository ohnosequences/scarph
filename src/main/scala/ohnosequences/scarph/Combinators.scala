package ohnosequences.scarph

object combinators {

  import graphTypes._, paths._, containers._


  trait CombinatorOf1Path extends AnyPathCombinator {

    type Inner <: AnyPath
    val  inner: Inner
  }

  trait CombinatorOf2Paths extends AnyPathCombinator {

    type First <: AnyPath
    val  first: First

    type Second <: AnyPath
    val  second: Second
  }


  /* Sequential composition of two paths */
  trait AnyComposition extends CombinatorOf2Paths {

    type Second <: AnyPath { 
      type InC = First#OutC
      type InT = First#OutT 
    }

    type InT = First#InT
    val  inT = first.inT
    type InC = First#InC
    val  inC = first.inC

    type OutT = Second#OutT
    val  outT = second.outT 
    type OutC = Second#OutC
    val  outC = second.outC
  }

  case class Composition[F <: AnyPath, S <: AnyPath { type InC = F#OutC; type InT = F#OutT }]
    (val first: F, val second: S) extends AnyComposition {

    type First = F
    type Second = S
  }

  type >=>[F <: AnyPath, S <: AnyPath { type InC = F#OutC; type InT = F#OutT }] = Composition[F, S]


  /* Mapping a Path over a container */
  trait AnyMapOver extends CombinatorOf1Path {

    type Inner <: AnyPath {
      type InC = ExactlyOne
    }

    type Container <: AnyContainer
    val  container: Container

    type InT = Inner#InT
    val  inT = inner.inT
    type InC = Container
    val  inC = container

    type OutT = OutOf[Inner]
    val  outT = outOf(inner)
    type OutC = Container
    val  outC = container
  }

  case class MapOver[P <: AnyPath { type InC = ExactlyOne }, C <: AnyContainer]
    (val inner: P, val container: C) extends AnyMapOver {

    type Inner = P
    type Container = C
  }


  /* Mapping a Path over a container */
  trait AnyFlatten extends CombinatorOf1Path {

    type Inner <: AnyPath {
      type OutT <: AnyNestedGraphType
    }

    type InT = Inner#InT
    val  inT = inner.inT
    type InC = Inner#InC
    val  inC = inner.inC

    type OutT = Inner#OutT#Inside
    val  outT = inner.outT.inside

    // we will get OutC through this implicit on construction:
    val mul: Inner#OutC × Inner#OutT#Container
  }

  case class Flatten[P <: AnyPath { type OutT <: AnyNestedGraphType }, C <: AnyContainer]
    (val inner: P)
    (implicit val mul: (P#OutC × P#OutT#Container) { type Out = C })
    extends AnyFlatten {

      type Inner = P 

      type OutC = C
      val  outC = mul(inner.outC, inner.outT.container)
    }


  /* Parallel composition of paths */
  trait AnyPar extends CombinatorOf2Paths {

    type InT = ParV[InOf[First], InOf[Second]]
    val  inT = ParV(inOf(first), inOf(second))
    type InC = ExactlyOne
    val  inC = ExactlyOne 

    type OutT = ParV[OutOf[First], OutOf[Second]]
    val  outT = ParV(outOf(first), outOf(second))
    type OutC = ExactlyOne
    val  outC = ExactlyOne
  }

  case class Par[F <: AnyPath, S <: AnyPath]
    (val first: F, val second: S) extends AnyPar {

    type First = F
    type Second = S
  }

  type ⨁[F <: AnyPath, S <: AnyPath] = Or[F, S]


  /* Choice */
  trait AnyOr extends CombinatorOf2Paths {

    type InT = OrV[InOf[First], InOf[Second]]
    val  inT = OrV(inOf(first), inOf(second))
    type InC = ExactlyOne
    val  inC = ExactlyOne 

    type OutT = OrV[OutOf[First], OutOf[Second]]
    val  outT = OrV(outOf(first), outOf(second))
    type OutC = ExactlyOne
    val  outC = ExactlyOne
  }

  case class Or[F <: AnyPath, S <: AnyPath]
    (val first: F, val second: S) extends AnyOr {

    type First = F
    type Second = S
  }

  type ⨂[F <: AnyPath, S <: AnyPath] = Par[F, S]

}
