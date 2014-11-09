package ohnosequences.scarph

import ohnosequences.cosas._

// NOTE: maybe arity is not a proper name for what we are defining here...
sealed trait AnyArity {}

/* 4 things: one/many x non-empty/any */
// NOTE: don't know if the values of them are needed for anything
// TODO objects and aliases for their .type
trait OneOrNone extends AnyArity // Option
trait ExactlyOne extends AnyArity // Id
trait ManyOrNone extends AnyArity // List
trait AtLeastOne extends AnyArity // NEList

trait HasInArity  { type InArity  <: AnyArity }
trait HasOutArity { type OutArity <: AnyArity }

trait InArity[A <: AnyArity] extends HasInArity { type InArity = A }
trait OutArity[A <: AnyArity] extends HasOutArity { type OutArity = A }


/* Arities multiplication */
// not the best name, but it may look cool: A x B
trait x[A <: AnyArity, B <: AnyArity] extends AnyFn with OutBound[AnyArity]

object x extends x_2 {
  implicit def idemp[A <: AnyArity]: 
      (A x A) with Out[A] = 
  new (A x A) with Out[A]
}

trait x_2 extends x_3 {
  implicit def unitL[A <: AnyArity]: 
      (ExactlyOne x A) with Out[A] = 
  new (ExactlyOne x A) with Out[A]

  implicit def unitR[A <: AnyArity]: 
      (A x ExactlyOne) with Out[A] = 
  new (A x ExactlyOne) with Out[A]
}

trait x_3 extends x_4 {
  implicit def oneornoneL[A <: AnyArity]: 
      (OneOrNone x A) with Out[ManyOrNone] = 
  new (OneOrNone x A) with Out[ManyOrNone]

  implicit def oneornoneR[A <: AnyArity]: 
      (A x OneOrNone) with Out[ManyOrNone] = 
  new (A x OneOrNone) with Out[ManyOrNone]
}

trait x_4 {
  implicit def atleastoneL[A <: AnyArity]: 
      (AtLeastOne x A) with Out[AtLeastOne] = 
  new (AtLeastOne x A) with Out[AtLeastOne]

  implicit def atleastoneR[A <: AnyArity]: 
      (A x AtLeastOne) with Out[AtLeastOne] = 
  new (A x AtLeastOne) with Out[AtLeastOne]
}


trait Pack[X, A <: AnyArity] extends Fn1[List[X]]

// NOTE: these are example conversions. real ones should be provided by an implementation
object Pack {

  implicit def oneornone[X]: 
      Pack[X, OneOrNone] with Out[Option[X]] = 
  new Pack[X, OneOrNone] with Out[Option[X]] { def apply(list: In1): Out = list.headOption }

  // do we need a container here?
  implicit def exactlyone[X]: 
      Pack[X, ExactlyOne] with Out[X] = 
  new Pack[X, ExactlyOne] with Out[X] { def apply(list: In1): Out = list.head }

  implicit def manyornone[X]: 
      Pack[X, ManyOrNone] with Out[List[X]] = 
  new Pack[X, ManyOrNone] with Out[List[X]] { def apply(list: In1): Out = list }

  import scalaz._
  implicit def atleastone[X]: 
      Pack[X, AtLeastOne] with Out[NonEmptyList[X]] = 
  new Pack[X, AtLeastOne] with Out[NonEmptyList[X]] { def apply(list: In1): Out = NonEmptyList.nel(list.head, list.tail) }
}
