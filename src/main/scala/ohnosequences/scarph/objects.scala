package ohnosequences.scarph

case object objects {

  import scala.reflect.ClassTag
  import ohnosequences.cosas._, types._, typeSets._

  trait AnyGraphType extends AnyType { type Raw = Any }

  trait AnyGraphObject extends AnyGraphType

  trait AnyRelation extends AnyGraphObject {

    type SourceArity <: AnyArity
    val  sourceArity: SourceArity

    type Source >: SourceArity#GraphObject <: SourceArity#GraphObject
    lazy val  source: Source = sourceArity.graphObject


    type TargetArity <: AnyArity
    val  targetArity: TargetArity

    type Target >: TargetArity#GraphObject <: TargetArity#GraphObject
    lazy val  target: Target = targetArity.graphObject
  }


  // stuff that normally can have properties
  sealed trait AnyGraphElement extends AnyGraphObject

  trait AnyVertex extends AnyGraphElement
  class Vertex(val label: String) extends AnyVertex

  trait AnyEdge extends AnyRelation with AnyGraphElement {

    type SourceArity <: AnyArity.OfVertices
    type TargetArity <: AnyArity.OfVertices
  }
  case object AnyEdge {

    type From[S <: AnyVertex] = AnyEdge { type Source = S }
    type   To[T <: AnyVertex] = AnyEdge { type Target = T }
  }
  /* This constructor encourages to use this syntax: Edge(user -> tweet)("tweeted") */
  class Edge[
    S <: AnyArity.OfVertices,
    T <: AnyArity.OfVertices
  ](st: (S, T))(val label: String) extends AnyEdge {

    type SourceArity = S
    lazy val sourceArity = st._1
    type Source = SourceArity#GraphObject

    type TargetArity = T
    lazy val targetArity = st._2
    type Target = TargetArity#GraphObject
  }

  /* Property values have raw types that are covered as graph objects */
  trait AnyValueType extends AnyGraphObject {

    type Value
    def valueTag: ClassTag[Value]
  }
  class ValueOfType[V](val label: String)(implicit val valueTag: ClassTag[V]) extends AnyValueType {

    type Value = V
  }




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

  trait AnyProperty extends AnyRelation {

    type SourceArity <: AnyArity.OfElements
    type TargetArity <: AnyArity.OfValueTypes
  }
  case object AnyProperty {

    type withValue[V] = AnyProperty { type Target <: AnyValueType { type Value = V } }
  }
  class Property[
    O <: AnyArity.OfElements,
    V <: AnyArity.OfValueTypes
  ]
  (val st: (O,V))(val label: String)
  extends AnyProperty
  {
    type SourceArity = O
    lazy val sourceArity = st._1
    type Source = SourceArity#GraphObject

    type TargetArity = V
    lazy val targetArity = st._2
    type Target = TargetArity#GraphObject
  }

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
    lazy val conditions = List[AnyCondition]()
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
    lazy val element = body.element

    type Condition <: AnyCondition.OnElement[Body#Element]
    val  condition: Condition

    lazy val conditions = condition :: body.conditions
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
    lazy val element = property.source

    val label: String
    override final def toString = label
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



  /* ## Tensor product */
  sealed trait AnyTensorObj extends AnyGraphObject {

    type Left <: AnyGraphObject
    val  left: Left

    type Right <: AnyGraphObject
    val  right: Right
  }

  case class TensorObj[L <: AnyGraphObject, R <: AnyGraphObject]
    (val left: L, val right: R) extends AnyTensorObj {

    type Left = L
    type Right = R

    lazy val label: String = s"(${left.label} ⊗ ${right.label})"
  }

  // \otimes symbol: f ⊗ s: F ⊗ S
  type ⊗[F <: AnyGraphObject, S <: AnyGraphObject] = TensorObj[F, S]


  case object unit extends AnyGraphObject {

    lazy val label: String = this.toString
  }
  type unit = unit.type



  /* ## Biproduct */
  sealed trait AnyBiproductObj extends AnyGraphObject {

    type Left <: AnyGraphObject
    val  left: Left

    type Right <: AnyGraphObject
    val  right: Right
  }

  case class BiproductObj[L <: AnyGraphObject, R <: AnyGraphObject]
    (val left: L, val right: R) extends AnyBiproductObj {

    type Left = L
    type Right = R

    lazy val label: String = s"(${left.label} ⊕ ${right.label})"
  }

  case object zero extends AnyGraphObject {

    lazy val label: String = this.toString
  }
  type zero = zero.type

  // \oplus symbol: f ⊕ s: F ⊕ S
  type ⊕[F <: AnyGraphObject, S <: AnyGraphObject] = BiproductObj[F, S]

  implicit def graphObjectOps[O <: AnyGraphObject](o: O):
    GraphObjectOps[O] =
    GraphObjectOps[O](o)

  case class GraphObjectOps[O <: AnyGraphObject](val obj: O) extends AnyVal {

    def ⊗[S <: AnyGraphObject](other: S): TensorObj[O, S] = TensorObj(obj, other)
    def ⊕[S <: AnyGraphObject](other: S): BiproductObj[O, S] = BiproductObj(obj, other)
  }

}
