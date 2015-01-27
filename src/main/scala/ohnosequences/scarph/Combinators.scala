package ohnosequences.scarph

object combinators {

  import graphTypes._, paths._


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

    type Second <: AnyPath { type In = First#Out }

    type In <: First#In
    type Out <: Second#Out
  }

  case class Composition[F <: AnyPath, S <: AnyPath { type In = F#Out }]
    (val first: F, val second: S) extends AnyComposition {

    lazy val label: String = s"(${first.label} >=> ${second.label})"

    type First = F
    type Second = S

    type     In = First#In
    lazy val in = first.in: In

    type     Out = Second#Out
    lazy val out = second.out: Out
  }

  type >=>[F <: AnyPath, S <: AnyPath { type In = F#Out }] = Composition[F, S]

  implicit def CombinatorsSyntaxOps[F <: AnyPath](f: F):
        CombinatorsSyntaxOps[F] =
    new CombinatorsSyntaxOps[F](f)

  class CombinatorsSyntaxOps[F <: AnyPath](f: F) {

    def >=>[S <: AnyPath { type In = F#Out }](s: S): 
      Composition[F, S] = 
      Composition(f, s)
  }

}
