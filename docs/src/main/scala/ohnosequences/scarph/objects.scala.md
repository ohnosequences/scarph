
```scala
package ohnosequences.scarph

object objects {

  import ohnosequences.cosas._, types._, properties._, typeSets._
  import java.lang.Comparable


  trait AnyGraphType extends AnyType
```

Graph objects are represented as their id-morphisms

```scala
  trait AnyGraphObject extends AnyGraphType
```

A graph element is either a vertex or an edge, only they can have properties

```scala
  sealed trait AnyGraphElement extends AnyGraphObject
```

Vertex type is very simple

```scala
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
```

Edges connect vertices and have in/out arities

```scala
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
```

This constructor encourages to use this syntax: Edge(user -> tweet)("tweeted")

```scala
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
```

Property values have raw types that are covered as graph objects

```scala
  trait AnyValueType extends AnyProperty with AnyGraphObject {

    def rawTag: ClassTag[Raw]
  }

  class ValueOfType[R](val label: String)(implicit val rawTag: ClassTag[R]) extends AnyValueType { 

    type Raw = R 
  }
```

This is like an edge between an element and a raw type

```scala
  trait AnyGraphProperty extends AnyGraphType {

    type Owner <: AnyGraphElement
    val  owner: Owner

    type Value <: AnyValueType
    val  value: Value
  }

  class Property[O <: AnyGraphElement, V <: AnyValueType](val st: (O,V))(val label: String)
    extends AnyGraphProperty
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
```

Empty predicate doesn't have any restrictions

```scala
  trait AnyEmptyPredicate extends AnyPredicate {
    type Conditions = ∅
    val  conditions = ∅
  }

  case class EmptyPredicate[E <: AnyGraphElement](val element: E)
    extends AnyEmptyPredicate {

    type Element = E
  }
```

This is just like cons, but controlling, that all conditions are on the same element type

```scala
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
```

A condition is a restriction on the property values

```scala
  trait AnyCondition {

    type Property <: AnyGraphProperty
    val  property: Property

    type     Element = Property#Owner
    lazy val element = property.owner

    val label: String
    override final def toString = label
  }

  object AnyCondition {

    type OnProperty[P <: AnyGraphProperty] = AnyCondition { type Property = P }
    type OnElement[E <: AnyGraphElement] = AnyCondition { type Element = E }
  }
```

Comparison conditions with **One** property value

```scala
  trait AnyCompareCondition extends AnyCondition {
    type Property <: AnyGraphProperty //{ type Raw <: Comparable[_] }

    val value: Property#Value#Raw
  }

  trait CompareCondition[P <: AnyGraphProperty]
    extends AnyCompareCondition { type Property = P }


  trait AnyEqual extends AnyCompareCondition
  case class Equal[P <: AnyGraphProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyEqual with CompareCondition[P] {
    lazy val label = s"${property.label} = ${value.toString}"
  }

  trait AnyNotEqual extends AnyCompareCondition
  case class NotEqual[P <: AnyGraphProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyNotEqual with CompareCondition[P] {
    lazy val label = s"${property.label} ≠ ${value.toString}"
  }


  trait AnyLess extends AnyCompareCondition
  case class Less[P <: AnyGraphProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyLess with CompareCondition[P] {
    lazy val label = s"${property.label} < ${value.toString}"
  }

  trait AnyLessOrEqual extends AnyCompareCondition
  case class LessOrEqual[P <: AnyGraphProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyLessOrEqual with CompareCondition[P] {
    lazy val label = s"${property.label} ≤ ${value.toString}"
  }


  trait AnyGreater extends AnyCompareCondition
  case class Greater[P <: AnyGraphProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyGreater with CompareCondition[P] {
    lazy val label = s"${property.label} > ${value.toString}"
  }

  trait AnyGreaterOrEqual extends AnyCompareCondition
  case class GreaterOrEqual[P <: AnyGraphProperty](
    val property: P,
    val value: P#Value#Raw
  ) extends AnyGreaterOrEqual with CompareCondition[P] {
    lazy val label = s"${property.label} ≥ ${value.toString}"
  }


  trait AnyInterval extends AnyCondition {
    type Property <: AnyGraphProperty

    val start: Property#Value#Raw
    val end: Property#Value#Raw
  }

  case class Interval[P <: AnyGraphProperty](
    val property: P,
    val start: P#Value#Raw,
    val end: P#Value#Raw
  ) extends AnyInterval {
    type Property = P
    lazy val label = s"${start.toString} ≤ ${property.label} ≤ ${end.toString}"
  }
```

## Tensor product

```scala
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
```

## Biproduct

```scala
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

  // \oplus symbol: f ⊕ s: F ⊕ S
  type ⊕[F <: AnyGraphObject, S <: AnyGraphObject] = BiproductObj[F, S]


  case object zero extends AnyGraphObject {

    lazy val label = this.toString
  }
  type zero = zero.type


  implicit def graphObjectOps[O <: AnyGraphObject](o: O):
    GraphObjectOps[O] =
    GraphObjectOps[O](o)

  case class GraphObjectOps[O <: AnyGraphObject](val obj: O) extends AnyVal {

    def ⊗[S <: AnyGraphObject](other: S): TensorObj[O, S] = TensorObj(obj, other)
    def ⊕[S <: AnyGraphObject](other: S): BiproductObj[O, S] = BiproductObj(obj, other)
  }

}

```


------

### Index

+ src
  + test
    + scala
      + ohnosequences
        + scarph
          + [TwitterQueries.scala][test/scala/ohnosequences/scarph/TwitterQueries.scala]
          + impl
            + [dummyTest.scala][test/scala/ohnosequences/scarph/impl/dummyTest.scala]
            + [dummy.scala][test/scala/ohnosequences/scarph/impl/dummy.scala]
          + [TwitterSchema.scala][test/scala/ohnosequences/scarph/TwitterSchema.scala]
  + main
    + scala
      + ohnosequences
        + scarph
          + [morphisms.scala][main/scala/ohnosequences/scarph/morphisms.scala]
          + [objects.scala][main/scala/ohnosequences/scarph/objects.scala]
          + [evals.scala][main/scala/ohnosequences/scarph/evals.scala]
          + [implementations.scala][main/scala/ohnosequences/scarph/implementations.scala]
          + [schemas.scala][main/scala/ohnosequences/scarph/schemas.scala]
          + [naturalIsomorphisms.scala][main/scala/ohnosequences/scarph/naturalIsomorphisms.scala]
          + syntax
            + [morphisms.scala][main/scala/ohnosequences/scarph/syntax/morphisms.scala]
            + [predicates.scala][main/scala/ohnosequences/scarph/syntax/predicates.scala]
            + [graphTypes.scala][main/scala/ohnosequences/scarph/syntax/graphTypes.scala]
            + [conditions.scala][main/scala/ohnosequences/scarph/syntax/conditions.scala]

[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: ../../../../test/scala/ohnosequences/scarph/impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: ../../../../test/scala/ohnosequences/scarph/impl/dummy.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: morphisms.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: objects.scala.md
[main/scala/ohnosequences/scarph/evals.scala]: evals.scala.md
[main/scala/ohnosequences/scarph/implementations.scala]: implementations.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: schemas.scala.md
[main/scala/ohnosequences/scarph/naturalIsomorphisms.scala]: naturalIsomorphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/predicates.scala]: syntax/predicates.scala.md
[main/scala/ohnosequences/scarph/syntax/graphTypes.scala]: syntax/graphTypes.scala.md
[main/scala/ohnosequences/scarph/syntax/conditions.scala]: syntax/conditions.scala.md