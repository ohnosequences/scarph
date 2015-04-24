package ohnosequences.scarph

object objects {

  import ohnosequences.cosas._, types._, typeSets._


  trait AnyGraphType extends AnyType

  /* Graph objects are represented as their id-morphisms */
  trait AnyGraphObject extends AnyGraphType

  /* A graph element is either a vertex or an edge, only they can have properties */
  sealed trait AnyGraphElement extends AnyGraphObject

  /* Vertex type is very simple */
  trait AnyVertex extends AnyGraphElement
  class Vertex(val label: String) extends AnyVertex


  trait AnyArity {

    type Vertex <: AnyVertex
    val  vertex: Vertex
  }

  abstract class Arity[V <:AnyVertex](val vertex: V) extends AnyArity { type Vertex = V }

  case class  OneOrNone[V <: AnyVertex](v: V) extends Arity[V](v)
  case class AtLeastOne[V <: AnyVertex](v: V) extends Arity[V](v)
  case class ExactlyOne[V <: AnyVertex](v: V) extends Arity[V](v)
  case class ManyOrNone[V <: AnyVertex](v: V) extends Arity[V](v)


  /* Edges connect vertices and have in/out arities */
  trait AnyEdge extends AnyGraphElement {

    type SourceArity <: AnyArity
    val  sourceArity: SourceArity

    type SourceVertex <: SourceArity#Vertex
    val  sourceVertex: SourceVertex


    type TargetArity <: AnyArity
    val  targetArity: TargetArity

    type TargetVertex <: TargetArity#Vertex
    val  targetVertex: TargetVertex
  }

  /* This constructor encourages to use this syntax: Edge(user -> tweet)("tweeted") */
  class Edge[S <: AnyArity, T <: AnyArity]( st: (S, T))(val label: String) extends AnyEdge {

    type SourceArity = S
    lazy val sourceArity = st._1
    type SourceVertex = SourceArity#Vertex
    lazy val sourceVertex = sourceArity.vertex

    type TargetArity = T
    lazy val targetArity = st._2
    type TargetVertex = TargetArity#Vertex
    lazy val targetVertex = targetArity.vertex
  }

  object AnyEdge {

    type From[S <: AnyVertex] = AnyEdge { type SourceVertex = S }
    type   To[T <: AnyVertex] = AnyEdge { type TargetVertex = T }
  }

  import scala.reflect.ClassTag
  /* Property values have raw types that are covered as graph objects */
  trait AnyValueType extends properties.AnyProperty with AnyGraphObject {

    def rawTag: ClassTag[Raw]
  }

  class ValueOfType[R](val label: String)(implicit val rawTag: ClassTag[R]) extends AnyValueType {

    type Raw = R
  }

  /* This is like an edge between an element and a raw type */
  trait AnyProperty extends AnyGraphType {

    type Owner <: AnyGraphElement
    val  owner: Owner

    type Value <: AnyValueType
    val  value: Value
  }

  class Property[O <: AnyGraphElement, V <: AnyValueType](val st: (O,V))(val label: String)
    extends AnyProperty
  {

    type Owner = O
    val owner: O = st._1
    type Value = V
    val value: V = st._2
  }

  trait AnyPredicate extends AnyGraphObject {

    type Element <: AnyGraphElement
    val  element: Element

    type Conditions <: AnyTypeSet //.Of[AnyCondition]
    val  conditions: Conditions

    lazy val label: String = s"(${element.label} ? ${conditions.toString})"
  }

  object AnyPredicate {

    type On[E <: AnyGraphElement] = AnyPredicate { type Element = E }
  }

  /* Empty predicate doesn't have any restrictions */
  trait AnyEmptyPredicate extends AnyPredicate {
    type Conditions = ∅
    val  conditions = ∅
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

    type     Conditions = Condition :~: Body#Conditions
    lazy val conditions = condition :~: (body.conditions: Body#Conditions)
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

    type     Element = Property#Owner
    lazy val element = property.owner

    val label: String
    override final def toString = label
  }

  object AnyCondition {

    type OnProperty[P <: AnyProperty] = AnyCondition { type Property = P }
    type OnElement[E <: AnyGraphElement] = AnyCondition { type Element = E }
  }


  /* Comparison conditions with **One** property value */
  trait AnyCompareCondition extends AnyCondition {
    type Property <: AnyProperty //{ type Raw <: Comparable[_] }

    val value: Property#Value#Raw
  }

  trait CompareCondition[P <: AnyProperty]
    extends AnyCompareCondition { type Property = P }


  trait AnyEqual extends AnyCompareCondition
  case class Equal[P <: AnyProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyEqual with CompareCondition[P] {
    lazy val label = s"${property.label} = ${value.toString}"
  }

  trait AnyNotEqual extends AnyCompareCondition
  case class NotEqual[P <: AnyProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyNotEqual with CompareCondition[P] {
    lazy val label = s"${property.label} ≠ ${value.toString}"
  }


  trait AnyLess extends AnyCompareCondition
  case class Less[P <: AnyProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyLess with CompareCondition[P] {
    lazy val label = s"${property.label} < ${value.toString}"
  }

  trait AnyLessOrEqual extends AnyCompareCondition
  case class LessOrEqual[P <: AnyProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyLessOrEqual with CompareCondition[P] {
    lazy val label = s"${property.label} ≤ ${value.toString}"
  }


  trait AnyGreater extends AnyCompareCondition
  case class Greater[P <: AnyProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyGreater with CompareCondition[P] {
    lazy val label = s"${property.label} > ${value.toString}"
  }

  trait AnyGreaterOrEqual extends AnyCompareCondition
  case class GreaterOrEqual[P <: AnyProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyGreaterOrEqual with CompareCondition[P] {
    lazy val label = s"${property.label} ≥ ${value.toString}"
  }


  trait AnyInterval extends AnyCondition {
    type Property <: AnyProperty

    val start: Property#Value#Raw
    val end: Property#Value#Raw
  }

  case class Interval[P <: AnyProperty](
    val property: P,
    val start: P#Value#Raw,
    val end: P#Value#Raw
  ) extends AnyInterval {
    type Property = P
    lazy val label = s"${start.toString} ≤ ${property.label} ≤ ${end.toString}"
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

    lazy val label = s"(${left.label} ⊗ ${right.label})"
  }

  // \otimes symbol: f ⊗ s: F ⊗ S
  type ⊗[F <: AnyGraphObject, S <: AnyGraphObject] = TensorObj[F, S]


  case object unit extends AnyGraphObject {

    lazy val label = this.toString
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

    lazy val label = s"(${left.label} ⊕ ${right.label})"
  }


  case object zero extends AnyGraphObject {

    lazy val label = this.toString
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
