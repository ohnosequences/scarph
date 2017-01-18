package ohnosequences.scarph

trait AnyProperty extends AnyRelation {

  type SourceArity <: AnyArity.OfElements
  type TargetArity <: AnyArity.OfValueTypes
}

case object AnyProperty {

  type withValue[V] = AnyProperty { type Target <: AnyValueType { type Val = V } }
}

abstract class Property[
  O <: AnyArity.OfElements,
  V <: AnyArity.OfValueTypes
](val st: (O,V))(val label: String) extends AnyProperty {

  type SourceArity = O
  lazy val sourceArity: SourceArity = st._1

  type TargetArity = V
  lazy val targetArity: TargetArity = st._2
}
