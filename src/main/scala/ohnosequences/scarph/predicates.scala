  package ohnosequences.scarph

object predicates {

  import ohnosequences.cosas._, typeSets._
  import graphTypes._, conditions._, monoidalStructures._

  val  GraphBoolean = unit ⊕ unit
  type GraphBoolean = GraphBoolean.type

  trait AnyQuantifiedObject extends AnyGraphObject {

    type Predicate <: AnyPredicate
    val predicate: Predicate
    type OnObject = Predicate#Element
    lazy val onObject: OnObject = predicate.element
  }

  case class those[P <: AnyPredicate](val predicate: P) extends AnyQuantifiedObject {

    type Predicate = P

    lazy val label: String = predicate.label
  }

  trait AnyCheck extends AnyGraphMorphism {

    type QObject <: AnyQuantifiedObject
    val qObject: QObject

    type In = QObject#OnObject
    lazy val in: In = qObject.onObject

    type Out = QObject
    lazy val out: Out = qObject

    type Dagger = coerce[QObject]
    lazy val dagger: Dagger = coerce(qObject)

    lazy val label: String = s"check ${out.label}"
  }

  case class check[QO <: AnyQuantifiedObject](val qObject: QO) extends AnyCheck {

    type QObject = QO
  }

  trait AnyCoerce extends AnyGraphMorphism {

    type QObject <: AnyQuantifiedObject
    val qObject: QObject

    type In = QObject
    lazy val in: In = qObject

    type Out = QObject#OnObject
    lazy val out: Out = qObject.onObject

    type Dagger = check[QObject]
    lazy val dagger: Dagger = check(qObject)

    lazy val label: String = s"${in.label} as {out.label}"
  }

  case class coerce[QO <: AnyQuantifiedObject](val qObject: QO) extends AnyCoerce {

    type QObject = QO
  }

  trait AnyPredicate extends AnyGraphMorphism {

    type Element <: AnyGraphElement
    val  element: Element

    type Conditions <: AnyTypeSet //.Of[AnyCondition]
    val  conditions: Conditions

    type     In = Element
    lazy val in = element

    type     Out = GraphBoolean
    lazy val out = GraphBoolean: Out

    type     Dagger = PredicateDagger[this.type]
    lazy val dagger = PredicateDagger(this): Dagger

    lazy val label: String = s"(${element.label} ? ${conditions.toString})"
  }

  // FIXME: this should be something else
  case class PredicateDagger[P <: AnyPredicate](p: P) extends AnyGraphMorphism {
    type     In = P#Out
    lazy val in = p.out

    type     Out = P#In
    lazy val out = p.in

    type     Dagger = P
    lazy val dagger = p

    lazy val label = "predicate dagger"
  }

  object AnyPredicate {

    type On[E <: AnyGraphElement] = AnyPredicate { type Element = E }
  }

  /* Empty predicate doesn't have any restrictions */
  trait AnyEmptyPredicate extends AnyPredicate {
    type Conditions = ∅
    val  conditions = ∅
  }

  class EmptyPredicate[E <: AnyGraphElement](val element: E)
    extends AnyEmptyPredicate {

    type Element = E
  }


  /* This is just like cons, but controlling, that all conditions are on the same element type */
  trait AnyAndPredicate extends AnyPredicate {

    type Body <: AnyPredicate
    val  body: Body

    type     Element = Body#Element
    lazy val element = body.element

    type Condition <: AnyCondition.OnElement[Body#Element]
    val  condition: Condition

    type     Conditions = Condition :~: Body#Conditions
    lazy val conditions = condition :~: (body.conditions: Body#Conditions)
  }

  case class AndPredicate[B <: AnyPredicate, C <: AnyCondition.OnElement[B#Element]]
    (val body: B, val condition: C) extends AnyAndPredicate {

    type Body = B
    type Condition = C
  }

}
