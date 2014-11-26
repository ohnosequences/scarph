package ohnosequences.scarph

import ohnosequences.cosas._

trait AnyPath { path =>

  type InT <: AnyLabelType
  val  inT: InT
  type InC[X <: AnyLabelType] <: Container[InC] with Of[X]
  type In = InC[InT]
  val in: In

  type OutT <: AnyLabelType
  val  outT: OutT
  type OutC[X <: AnyLabelType] <: Container[OutC] with Of[X]
  type Out = OutC[OutT]
  val out: Out

  // no ops
  def andThen[S <: AnyPath { type In = path.Out }](s: S): AnyComposition = new AnyComposition {

    type First = path.type
    val first = path: path.type
    type Second = S
    val second = s
  }

}

class Path[
  I <: AnyLabelType,
  InC0[X <: AnyLabelType] <: Container[InC0] with Of[X], 
  O <: AnyLabelType,
  OutC0[Z <: AnyLabelType] <: Container[OutC0] with Of[Z]
]
(
  val in: InC0[I],
  val out: OutC0[O]
)
extends AnyPath {

  type InT = I
  val inT = in.of
  type InC[X <: AnyLabelType] = InC0[X]

  type OutT = O
  val outT = out.of
  type OutC[X <: AnyLabelType] = OutC0[X]
}


/* A step is a minimal unit of path */
// trait AnyStep extends AnyPath

// abstract class Step[I <: AnyLabelType, O <: AnyLabelType](i: I, o: O) 
//   extends Path[I, O](i, o) with AnyStep

/* Path is a composition of other paths */
trait AnyComposition extends AnyPath {

  type First <: AnyPath
  val  first: First

  type Second <: AnyPath { type In = first.Out }
  val  second: Second

  type InT = first.InT
  val inT = first.inT
  type InC[X <: AnyLabelType] = first.InC[X]
  val in = first.in
  
  type OutT = second.OutT
  val outT = second.outT
  type OutC[X <: AnyLabelType] = second.OutC[X]
  val out = second.out
}

/*
this represents mapping a Path over a container; the path should have InT/OutT matching what the container wraps
*/
trait AnyMapExactlyOne extends AnyPath { map =>

  // the container is exactlyOne
  type InC[X <: AnyLabelType] = exactlyOne[X]

  // the path being mapped should have as In the wrapped type
  type MappedPath <: AnyPath { type In = exactlyOne[map.InT] }
  val mappedPath: MappedPath

  type OutT = mappedPath.Out
  val outT = mappedPath.out
  type OutC[X <: AnyLabelType] = exactlyOne[X]
  // type Out = exactlyOne[MappedPath#Out]
  val out = exactlyOne(mappedPath.out)
}

/*
  here we need

  1. the container value
  2. the path that is being mapped
*/
abstract class MapExactlyOne[X0 <: AnyLabelType](val in: exactlyOne[X0]) extends AnyMapExactlyOne {

  type InT = X0
  val inT = in.of
}

object AnyPath {

  implicit def pathOps[T <: AnyPath](t: T): PathOps[T] = new PathOps(t)

  implicit def mapExactlyOneOps[P <: AnyPath { type OutC[X <: AnyLabelType] = exactlyOne[X] }](p: P)
    : exactlyOneMapOps[P] = exactlyOneMapOps[P](p)
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

case class exactlyOneMapOps[P <: AnyPath { type OutC[X <: AnyLabelType] = exactlyOne[X] }](val p: P) {

  def map[F <: AnyPath { type In = exactlyOne[p.OutT] }](f: F): MapExactlyOne[p.OutT] = new MapExactlyOne(p.out) {

    type MappedPath = F
    val mappedPath = f
  }
}
