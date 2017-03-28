
```scala
package ohnosequences.scarph

import scala.reflect.ClassTag
import ohnosequences.cosas._, types._

trait AnyGraphType extends AnyType {

  type Raw = Any
}



// FIXME: should be sealed
trait AnyGraphObject extends AnyGraphType



trait AnyRelation extends AnyGraphObject {

  type SourceArity <: AnyArity
  val  sourceArity: SourceArity

  type Source = SourceArity#GraphObject
  lazy val source: Source = sourceArity.graphObject

  type TargetArity <: AnyArity
  val  targetArity: TargetArity

  type Target = TargetArity#GraphObject
  lazy val target: Target = targetArity.graphObject
}

abstract class Relation[
  S <: AnyArity,
  T <: AnyArity
](st: (S, T)) extends AnyRelation {

  type SourceArity = S
  lazy val sourceArity: SourceArity = st._1

  type TargetArity = T
  lazy val targetArity: TargetArity = st._2
}


sealed trait GraphElementType
case object VertexElement extends GraphElementType
case object   EdgeElement extends GraphElementType

// NOTE in tradititional graph data models, only "elements" can have properties
sealed trait AnyGraphElement extends AnyGraphObject {

  val elementType: GraphElementType
}
```

A vertex is a simple graph object representing some type of entities

```scala
trait AnyVertex extends AnyGraphElement { val elementType = VertexElement }

abstract class Vertex(val label: String) extends AnyVertex
```

An edge is an object representing relation between vertex-objects

```scala
trait AnyEdge extends AnyRelation with AnyGraphElement {
  val elementType = EdgeElement

  type SourceArity <: AnyArity.OfVertices
  type TargetArity <: AnyArity.OfVertices
}

case object AnyEdge {

  type From[S <: AnyVertex] = AnyEdge { type Source = S }
  type   To[T <: AnyVertex] = AnyEdge { type Target = T }
}
```

This constructor encourages to use this syntax:
 `case class posted extends Edge(ExactlyOne(user) -> ManyOrNone(tweet))("posted")`

  Take a look at the GraphSchema trait for more convenient constructors


```scala
abstract class Edge[
  S <: AnyArity.OfVertices,
  T <: AnyArity.OfVertices
](st: (S, T))(val label: String)
extends Relation[S, T](st)
with AnyEdge
```

Properties are relations connecting element-objects (vertices or edges) with value-objects (scalar types)

```scala
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
](val ov: (O,V))(val label: String)
extends Relation[O, V](ov)
with AnyProperty
```

Property values have scalar types wrapped as graph objects

```scala
trait AnyValueType extends AnyGraphObject {

  type Val
  def valueTag: ClassTag[Val]
}

abstract class ValueOfType[V](val label: String)(implicit val valueTag: ClassTag[V]) extends AnyValueType {

  type Val = V
}

```




[test/scala/ohnosequences/scarph/asserts.scala]: ../../../../test/scala/ohnosequences/scarph/asserts.scala.md
[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: ../../../../test/scala/ohnosequences/scarph/impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: ../../../../test/scala/ohnosequences/scarph/impl/dummy.scala.md
[test/scala/ohnosequences/scarph/impl/writes.scala]: ../../../../test/scala/ohnosequences/scarph/impl/writes.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[test/scala/ohnosequences/scarph/implicitSearch.scala]: ../../../../test/scala/ohnosequences/scarph/implicitSearch.scala.md
[test/scala/ohnosequences/scarph/SchemaCreation.scala]: ../../../../test/scala/ohnosequences/scarph/SchemaCreation.scala.md
[main/scala/ohnosequences/scarph/arities.scala]: arities.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: schemas.scala.md
[main/scala/ohnosequences/scarph/predicates.scala]: predicates.scala.md
[main/scala/ohnosequences/scarph/package.scala]: package.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: objects.scala.md
[main/scala/ohnosequences/scarph/impl/distributivity.scala]: impl/distributivity.scala.md
[main/scala/ohnosequences/scarph/impl/tensors.scala]: impl/tensors.scala.md
[main/scala/ohnosequences/scarph/impl/evals.scala]: impl/evals.scala.md
[main/scala/ohnosequences/scarph/impl/category.scala]: impl/category.scala.md
[main/scala/ohnosequences/scarph/impl/biproducts.scala]: impl/biproducts.scala.md
[main/scala/ohnosequences/scarph/impl/relations.scala]: impl/relations.scala.md
[main/scala/ohnosequences/scarph/syntax/package.scala]: syntax/package.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: syntax/objects.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/writes.scala]: syntax/writes.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: morphisms.scala.md
[main/scala/ohnosequences/scarph/tensor.scala]: tensor.scala.md
[main/scala/ohnosequences/scarph/axioms.scala]: axioms.scala.md
[main/scala/ohnosequences/scarph/isomorphisms.scala]: isomorphisms.scala.md
[main/scala/ohnosequences/scarph/writes.scala]: writes.scala.md
[main/scala/ohnosequences/scarph/rewrites.scala]: rewrites.scala.md
[main/scala/ohnosequences/scarph/biproduct.scala]: biproduct.scala.md