package ohnosequences.scarph

trait Condition {
  
  type Property <: Singleton[AnyProperty]
  val property: Property
}

trait SimpleCondition[P <: Singleton[AnyProperty]] extends Condition {

  type Property = P
  val value: P#Raw
}

// example
case class EQ[P <: Singleton[AnyProperty]](
  val property: P,
  val value: P#Raw
) extends SimpleCondition[P]

trait VertexPredicate { 

  type VertexType <: Singleton[AnyVertexType]
  val vertexType: VertexType
}

