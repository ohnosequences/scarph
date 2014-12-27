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


  /* Parallel composition of paths */
  trait AnyPar extends CombinatorOf2Paths {

    type     In = ParType[First#In, Second#In]
    lazy val in = ParType(first.in, second.in): In

    type     Out = ParType[First#Out, Second#Out]
    lazy val out = ParType(first.out, second.out): Out
  }

  case class Par[F <: AnyPath, S <: AnyPath]
    (val first: F, val second: S) extends AnyPar {

    type First = F
    type Second = S
  }

  // \otimes symbol:
  type ⊗[F <: AnyPath, S <: AnyPath] = Par[F, S]


  trait AnyFork extends CombinatorOf1Path {

    type     In = Inner#In
    lazy val in = inner.in

    type     Out = ParType[Inner#Out, Inner#Out]
    lazy val out = ParType(inner.out, inner.out): Out
  }

  case class Fork[P <: AnyPath](val inner: P) extends AnyFork { type Inner = P }


  /* Choice */
  trait AnyOr extends CombinatorOf2Paths {
    type     Left = First
    lazy val left = first: Left

    type     Right = Second
    lazy val right = second: Right

    type     In = OrType[Left#In, Right#In]
    lazy val in = OrType(left.in, right.in): In

    type     Out = OrType[Left#Out, Right#Out]
    lazy val out = OrType(left.out, right.out): Out
  }

  case class Or[F <: AnyPath, S <: AnyPath]
    (val first: F, val second: S) extends AnyOr {

    type First = F
    type Second = S
  }

  // \oplus symbol:
  type ⊕[F <: AnyPath, S <: AnyPath] = Or[F, S]

}
