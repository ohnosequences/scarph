package ohnosequences.scarph

trait Condition {
  
  type Property <: Singleton with AnyProperty
  val property: Property
}

trait SimpleCondition[P <: Singleton with AnyProperty] extends Condition {

  type Property = P
  val value: P#Raw
}

// example
case class EQ[P <: Singleton with AnyProperty](
  val property: P,
  val value: P#Raw
) extends SimpleCondition[P]

trait VertexPredicate { 

  type VertexType <: Singleton with AnyVertexType
  val vertexType: VertexType
}

