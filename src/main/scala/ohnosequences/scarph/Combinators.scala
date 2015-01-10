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
  trait AnyComposition extends CombinatorOf2Paths with AnySimpleGraphType {

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

    // not so important
    lazy val label: String = s"(${second.label} >=> ${first.label})"

    type First = F
    type Second = S

    type Inside = Composition[F,S]
  }

  type >=>[F <: AnyPath, S <: AnyPath] = Composition[F, S]


  /* Mapping a Path over a container */
  trait AnyMapOver extends CombinatorOf1Path with AnySimpleGraphType {

    type MappedOver <: AnyContainer
    val  mappedOver: MappedOver

    type     In = MappedOver#Of[Inner#In]
    lazy val in = mappedOver.of(inner.in): In

    type     Out = MappedOver#Of[Inner#Out]
    lazy val out = mappedOver.of(inner.out): Out
  }
  /* P MapOver C */
  case class MapOver[P <: AnyPath, C <: AnyContainer]
    (val inner: P, val mappedOver: C) extends AnyMapOver {

    lazy val label: String = s"${container.label}(${inner.label})"

    type Inner = P
    type MappedOver = C

    type Inside = MapOver[P,C]
  }


  /* Mapping a Path over a container */
  trait AnyFlatten extends CombinatorOf1Path with AnySimpleGraphType {

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

      lazy val label: String = s"Flatten(${inner.label})"
      type Inner = P 
      type OutC = C

      type Inside = Flatten[P,C]
    }


  /* Parallel composition of paths */
  trait AnyPar extends CombinatorOf2Paths with AnySimpleGraphType {

    type     In = ParType[First#In, Second#In]
    lazy val in = ParType(first.in, second.in): In

    type     Out = ParType[First#Out, Second#Out]
    lazy val out = ParType(first.out, second.out): Out

  }

  case class Par[F <: AnyPath, S <: AnyPath]
    (val first: F, val second: S) extends AnyPar {

    lazy val label: String = s"(${first.label} ⊗ ${second.label})"
    type First = F
    type Second = S

    type Inside = Par[F,S]
  }

  // \otimes symbol: F ⊗ S
  type ⊗[F <: AnyPath, S <: AnyPath] = Par[F, S]


  trait AnyFork extends CombinatorOf1Path with AnySimpleGraphType {

    type     In = Inner#In
    lazy val in = inner.in

    type     Out = ParType[Inner#Out, Inner#Out]
    lazy val out = ParType(inner.out, inner.out): Out
  }

  case class Fork[P <: AnyPath](val inner: P) extends AnyFork { 

    lazy val label: String = s"Fork(${inner.label})"
    type Inner = P
    type Inside = Fork[P]
  }


  /* Choice */
  trait AnyOr extends CombinatorOf2Paths with AnySimpleGraphType {
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

    lazy val label: String = s"(${first.label} ⊕ ${second.label})"
    type First = F
    type Second = S

    type Inside = Or[F,S]
  }

  // \oplus symbol:
  type ⊕[F <: AnyPath, S <: AnyPath] = Or[F, S]

}
