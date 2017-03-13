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

case class  oneOrNone[GO <: AnyGraphObject](go: GO) extends Arity[GO](go)
case class atLeastOne[GO <: AnyGraphObject](go: GO) extends Arity[GO](go)
case class exactlyOne[GO <: AnyGraphObject](go: GO) extends Arity[GO](go)
case class manyOrNone[GO <: AnyGraphObject](go: GO) extends Arity[GO](go)
