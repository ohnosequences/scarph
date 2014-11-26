package ohnosequences.scarph

import ohnosequences.cosas._

trait AnyPath { path =>

  /* the wrapped type */
  type InT <: AnyLabelType
  val  inT: InT
  /* the container used */
  type InC <: AnyConstructor
  val inC: InC

  type In = inC.C[InT]
  val in: In = inC(inT)

  type OutT <: AnyLabelType
  val  outT: OutT
  type OutC <: AnyConstructor
  val outC: OutC

  type Out = outC.C[OutT]
  val out: Out = outC(outT)

  // no ops
  def andThen[S <: AnyPath { type In = path.Out }](s: S): AnyComposition = new AnyComposition {

    type First = path.type
    val first = path: path.type
    type Second = S
    val second = s
  }

  def map[F <: AnyPath { type In = path.OutT }](f: F): AnyMap = new AnyMap {

    type PrevPath = path.type
    val prevPath = path: path.type

    type MappedPath = F
    val mappedPath = f
  }
}

class Path[
  I <: AnyLabelType,
  InC0 <: AnyConstructor, 
  O <: AnyLabelType,
  OutC0 <: AnyConstructor
]
(
  val inC: InC0,
  val inT: I,
  val outC: OutC0,
  val outT: O
)
extends AnyPath {

  type InT = I
  type InC = InC0

  type OutT = O
  type OutC = OutC0
}

/* a composition of other paths */
trait AnyComposition extends AnyPath {

  type First <: AnyPath
  val  first: First

  type Second <: AnyPath { type In = first.Out }
  val  second: Second

  type InT = first.InT
  val inT = first.inT
  type InC = first.InC
  val inC = first.inC
  
  type OutT = second.OutT
  val outT = second.outT
  type OutC = second.OutC
  val outC = second.outC
}

/*
this represents mapping a Path over a container; the path should have InT/OutT matching what the container wraps.

This wraps two different paths

1. the first path 
*/
trait AnyMap extends AnyPath {

  type PrevPath <: AnyPath
  val prevPath: PrevPath
  // the path being mapped should have as In the wrapped type
  type MappedPath <: AnyPath { type In = prevPath.OutT }
  val mappedPath: MappedPath

  type InT = prevPath.InT
  val inT = prevPath.inT
  type InC = prevPath.InC
  val inC = prevPath.inC

  type OutT = mappedPath.Out
  val outT = mappedPath.out
  type OutC = prevPath.OutC
  val outC = prevPath.outC
}


// trait AnyMapExactlyOne extends AnyPath { memap =>

//   // the container is exactlyOne
//   type InC[X <: AnyLabelType] = exactlyOne[X]

//   // the path being mapped should have as In the wrapped type
//   type MappedPath <: AnyPath { type In = exactlyOne[memap.InT] }
//   val mappedPath: MappedPath

//   type OutT = mappedPath.Out
//   val outT = mappedPath.out
//   type OutC[X <: AnyLabelType] = exactlyOne[X]
//   // type Out = exactlyOne[MappedPath#Out]
//   val out = exactlyOne(mappedPath.out)
// }

/*
  here we need

  1. the container value
  2. the path that is being mapped
*/
// abstract class MapExactlyOne[X0 <: AnyLabelType](val in: exactlyOne[X0]) extends AnyMapExactlyOne {

//   type InT = X0
//   val inT = in.of
// }

object AnyPath {

  implicit def pathOps[T <: AnyPath](t: T): PathOps[T] = new PathOps(t)

  // implicit def mapExactlyOneOps[P <: AnyPath { type OutC[X <: AnyLabelType] = exactlyOne[X] }](p: P)
  // : exactlyOneMapOps[P] = exactlyOneMapOps[P](p)
}

class PathOps[F <: AnyPath](val f: F) {

  /* Just a synonym for composition */
  def >=>[S <: AnyPath { type In = f.Out }](s: S): AnyComposition = new AnyComposition {

    type First = f.type
    val first = f: f.type
    type Second = S
    val second = s
  }

  // def evalOn[I, O](i: I LabeledBy F#InT)
  //   (implicit 
  //     ev: EvalPath[I, F, O]
  //   ): O LabeledBy F#Out = ev(i, t)
}

// case class exactlyOneMapOps[P <: AnyPath { type OutC[X <: AnyLabelType] = exactlyOne[X] }](val p: P) {

//   def map[F <: AnyPath { type In = exactlyOne[p.OutT] }](f: F): MapExactlyOne[p.OutT] = new MapExactlyOne(p.out) {

//     type MappedPath = F
//     val mappedPath = f
//   }
// }
