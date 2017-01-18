package ohnosequences.scarph

trait AnyPredicate extends AnyGraphObject {

  type Element <: AnyGraphElement
  val  element: Element

  val  conditions: List[AnyCondition]

  lazy val label: String = s"(${element.label} ? ${conditions.toString})"
}

case object AnyPredicate {

  type On[E <: AnyGraphElement] = AnyPredicate { type Element = E }
}

/* Empty predicate doesn't have any restrictions */
trait AnyEmptyPredicate extends AnyPredicate {
  lazy val conditions: List[AnyCondition] = List[AnyCondition]()
}


case class EmptyPredicate[E <: AnyGraphElement](val element: E)
  extends AnyEmptyPredicate {

  type Element = E
}


/* This is just like cons, but controlling, that all conditions are on the same element type */
trait AnyAndPredicate extends AnyPredicate {

  type Body <: AnyPredicate
  val  body: Body

  type     Element = Body#Element
  lazy val element: Element = body.element

  type Condition <: AnyCondition.OnElement[Body#Element]
  val  condition: Condition

  lazy val conditions: List[AnyCondition] = condition :: body.conditions
}

case class AndPredicate[B <: AnyPredicate, C <: AnyCondition.OnElement[B#Element]]
  (val body: B, val condition: C) extends AnyAndPredicate {

  type Body = B
  type Condition = C
}


/* A condition is a restriction on the property values */
trait AnyCondition {

  type Property <: AnyProperty
  val  property: Property

  type     Element = Property#Source
  lazy val element: Element = property.source

  val label: String
  override final def toString: String = label
}

case object AnyCondition {

  type OnProperty[P <: AnyProperty] = AnyCondition { type Property = P }
  type OnElement[E <: AnyGraphElement] = AnyCondition { type Element = E }
}


/* Comparison conditions with **One** property value */
trait AnyCompareCondition extends AnyCondition {
  type Property <: AnyProperty //{ type Raw <: Comparable[_] }

  val value: Property#Target#Raw
}

trait CompareCondition[P <: AnyProperty]
  extends AnyCompareCondition { type Property = P }


trait AnyEqual extends AnyCompareCondition
case class Equal[P <: AnyProperty](
  val property: P,
  val value: P#Target#Raw
) extends AnyEqual with CompareCondition[P] {
  lazy val label: String = s"${property.label} = ${value.toString}"
}

trait AnyNotEqual extends AnyCompareCondition
case class NotEqual[P <: AnyProperty](
  val property: P,
  val value: P#Target#Raw
) extends AnyNotEqual with CompareCondition[P] {
  lazy val label: String = s"${property.label} ≠ ${value.toString}"
}

trait AnyLess extends AnyCompareCondition
case class Less[P <: AnyProperty](
  val property: P,
  val value: P#Target#Raw
) extends AnyLess with CompareCondition[P] {
  lazy val label: String = s"${property.label} < ${value.toString}"
}

trait AnyLessOrEqual extends AnyCompareCondition
case class LessOrEqual[P <: AnyProperty](
  val property: P,
  val value: P#Target#Raw
) extends AnyLessOrEqual with CompareCondition[P] {
  lazy val label: String = s"${property.label} ≤ ${value.toString}"
}

trait AnyGreater extends AnyCompareCondition
case class Greater[P <: AnyProperty](
  val property: P,
  val value: P#Target#Raw
) extends AnyGreater with CompareCondition[P] {
  lazy val label: String = s"${property.label} > ${value.toString}"
}

trait AnyGreaterOrEqual extends AnyCompareCondition
case class GreaterOrEqual[P <: AnyProperty](
  val property: P,
  val value: P#Target#Raw
) extends AnyGreaterOrEqual with CompareCondition[P] {
  lazy val label: String = s"${property.label} ≥ ${value.toString}"
}

trait AnyInterval extends AnyCondition {
  type Property <: AnyProperty
  val start: Property#Target#Raw
  val end: Property#Target#Raw
}

case class Interval[P <: AnyProperty](
  val property: P,
  val start: P#Target#Raw,
  val end: P#Target#Raw
) extends AnyInterval {
  type Property = P
  lazy val label: String = s"${start.toString} ≤ ${property.label} ≤ ${end.toString}"
}



/* ### Morphisms */

case class quantify[P <: AnyPredicate](val predicate: P) extends AnyPrimitiveMorph {

  type Predicate = P

  type     In = Predicate#Element
  lazy val in: In = predicate.element

  type     Out = Predicate
  lazy val out: Out = predicate

  type     Dagger = coerce[Predicate]
  lazy val dagger: Dagger = coerce(predicate)

  lazy val label: String = s"quantify(${predicate.label})"
}


case class coerce[P <: AnyPredicate](val predicate: P) extends AnyPrimitiveMorph {

  type Predicate = P

  type     Out = Predicate#Element
  lazy val out: Out = predicate.element

  type     In = Predicate
  lazy val in: In = predicate

  type     Dagger = quantify[Predicate]
  lazy val dagger: Dagger = quantify(predicate)

  lazy val label: String = s"coerce(${predicate.label})"
}
