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

    // type Second <: AnyPath { type In <: AnyGraphType.SameAs[First#Out] }

    type In = First#In
    lazy val  in = first.in

    type Out = Second#Out
    lazy val  out = second.out
  }

  case class Composition[F <: AnyPath, S <: AnyPath]
    (val first: F, val second: S)
    (implicit val composable: F#Out ≃ S#In) extends AnyComposition {

    type First = F
    type Second = S
  }

  type >=>[F <: AnyPath, S <: AnyPath] = Composition[F, S]


  /* Mapping a Path over a container */
  trait AnyMapOver extends CombinatorOf1Path {

    type Container <: AnyContainer
    val  container: Container

    type In = Container#Of[Inner#In]
    val  in: In = container.of(inner.in)

    type Out = Container#Of[Inner#Out]
    val  out: Out = container.of(inner.out)
  }

  case class MapOver[P <: AnyPath, C <: AnyContainer]
    (val inner: P, val container: C) extends AnyMapOver {

    type Inner = P
    type Container = C
  }


  /* Mapping a Path over a container */
  trait AnyFlatten extends CombinatorOf1Path {

    type In = Inner#In
    lazy val  in: In = inner.in

    type OutC <: AnyContainer
    val  outC: OutC
    // we will get OutC through this implicit on construction:
    val mul: (Inner#Out#Container × Inner#Out#Inside#Container) { type Out = OutC }
  }

  case class Flatten[P <: AnyPath, C <: AnyContainer]
    (val inner: P)
    (implicit val mul: (P#Out#Container × P#Out#Inside#Container) { type Out = C })
    extends AnyFlatten {

      type Inner = P 

      type OutC = C
      val  outC: OutC = mul(inner.out.container, inner.out.inside.container)

      type Out = OutC#Of[Inner#Out#Inside#Inside]
      val  out: Out = outC.of(inner.out.inside.inside)
    }


  // /* Parallel composition of paths */
  // trait AnyPar extends CombinatorOf2Paths {

  //   type InC = ExactlyOne
  //   val  inC = ExactlyOne 
  //   type InT = ParType[InOf[First], InOf[Second]]
  //   val  inT = ParType(inOf(first), inOf(second))

  //   type OutC = ExactlyOne
  //   val  outC = ExactlyOne
  //   type OutT = ParType[OutOf[First], OutOf[Second]]
  //   val  outT = ParType(outOf(first), outOf(second))
  // }

  // case class Par[F <: AnyPath, S <: AnyPath]
  //   (val first: F, val second: S) extends AnyPar {

  //   type First = F
  //   type Second = S
  // }

  // type ⨂[F <: AnyPath, S <: AnyPath] = Par[F, S]


  // /* Choice */
  // trait AnyOr extends CombinatorOf2Paths {

  //   type InC = ExactlyOne
  //   val  inC = ExactlyOne 
  //   type InT = OrType[InOf[First], InOf[Second]]
  //   val  inT = OrType(inOf(first), inOf(second))

  //   type OutC = ExactlyOne
  //   val  outC = ExactlyOne
  //   type OutT = OrType[OutOf[First], OutOf[Second]]
  //   val  outT = OrType(outOf(first), outOf(second))
  // }

  // case class Or[F <: AnyPath, S <: AnyPath]
  //   (val first: F, val second: S) extends AnyOr {

  //   type First = F
  //   type Second = S
  // }

  // type ⨁[F <: AnyPath, S <: AnyPath] = Or[F, S]

}
