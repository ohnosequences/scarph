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

    // should be provided implicitly:
    val composable: First#Out ≃ Second#In

    type     In = First#In
    lazy val in = first.in

    type     Out = Second#Out
    lazy val out = second.out
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

    type     In = Container#Of[Inner#In]
    lazy val in = container.of(inner.in): In

    type     Out = Container#Of[Inner#Out]
    lazy val out = container.of(inner.out): Out
  }

  case class MapOver[P <: AnyPath, C <: AnyContainer]
    (val inner: P, val container: C) extends AnyMapOver {

    type Inner = P
    type Container = C
  }


  /* Mapping a Path over a container */
  trait AnyFlatten extends CombinatorOf1Path {

    type     In = Inner#In
    lazy val in = inner.in: In

    // container that we get after flattening:
    type OutC <: AnyContainer

    // we will get OutC through this implicit on construction:
    val mul: (Inner#Out#Container × Inner#Out#Inside#Container) { type Out = OutC }

    lazy val outC = mul(inner.out.container, inner.out.inside.container): OutC

    type     Out = OutC#Of[Inner#Out#Inside#Inside]
    lazy val out = outC.of(inner.out.inside.inside): Out
  }

  case class Flatten[P <: AnyPath, C <: AnyContainer]
    (val inner: P)
    (implicit val mul: (P#Out#Container × P#Out#Inside#Container) { type Out = C })
    extends AnyFlatten {

      type Inner = P 
      type OutC = C
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

  trait AnyMerge extends CombinatorOf2Paths {

    type OutT <: AnyGraphType

    type First <: AnyPath { type Out = OutT }
    type Second <: AnyPath { type Out = OutT }

    // container that we get after flattening:
    type OutC <: AnyContainer

    // we will get OutC through this implicit on construction:
    val sum: (First#Out#Container + Second#Out#Container) { type Out = OutC }

    lazy val outC = sum(first.out.container, second.out.container): OutC

    type     In = ParType[First#in, Second#In]
    lazy val in = ParType(first.in, second.in)

    type     Out = OutC#Of[OutT]
    lazy val out = outC.of(outT): Out
  }

  case class Merge[
    C <: AnyContainer, 
    T <: AnyGraphType, 
    F <: AnyPath { type Out = T },
    S <: AnyPath { type Out = T }
  ](val first: F, val second: S)
    (implicit val mul: (F#Out#Container + S#Out#Container) { type Out = C })
    extends AnyFlatten {

      type OutC = C
      type OutT = T

      type First = F
      type Second = S
    }

}
