package ohnosequences.scarph

trait AnyArity {

  type GraphObject <: AnyGraphObject
  val  graphObject: GraphObject
}

case object AnyArity {

  type OfVertices   = AnyArity { type GraphObject <: AnyVertex }
  type OfElements   = AnyArity { type GraphObject <: AnyGraphElement }
  type OfValueTypes = AnyArity { type GraphObject <: AnyValueType }
}

abstract class Arity[GO <: AnyGraphObject](val graphObject: GO) extends AnyArity {

  type GraphObject = GO
}

case class  OneOrNone[GO <: AnyGraphObject](go: GO) extends Arity[GO](go)
case class AtLeastOne[GO <: AnyGraphObject](go: GO) extends Arity[GO](go)
case class ExactlyOne[GO <: AnyGraphObject](go: GO) extends Arity[GO](go)
case class ManyOrNone[GO <: AnyGraphObject](go: GO) extends Arity[GO](go)
