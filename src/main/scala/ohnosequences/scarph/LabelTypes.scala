package ohnosequences.scarph

// TODO: use this thing from cosas

trait AnyLabelType {
  val label: String

  implicit def meFrom[D <: AnyValueLabeledBy[this.type]](v: D): this.type = this
}

object AnyLabelType {

  implicit def labelTypeOps[T <: AnyLabelType](t: T): LabelTypeOps[T] = new LabelTypeOps[T](t)
}

class LabelTypeOps[ T <: AnyLabelType](t: T) {

  def apply[V](v: V): V LabeledBy T = new LabeledBy[V, T](v) {

    override def toString = s"${t.label}(${value.toString})"
  }
}


sealed trait AnyLabeledValue extends {

  type Tpe <: AnyLabelType
  // val  tpe: Tpe

  type Value
  val  value: Value
}

trait AnyValueLabeledBy[T <: AnyLabelType] extends AnyLabeledValue {
  type Tpe = T
}

trait LabeledValue[V, T <: AnyLabelType] extends AnyValueLabeledBy[T] {
  type Value = V
}

class LabeledBy[V, T <: AnyLabelType](val value: V) extends LabeledValue[V, T] {

  override def toString = s"labeled[ ${value.toString} ]"
}
