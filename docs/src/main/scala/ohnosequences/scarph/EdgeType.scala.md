
```scala
package ohnosequences.scarph

import ohnosequences.pointless._
import scalaz._, std.option._, std.list._
```


Declares an edge type. it is determined my a label, source/target vertex types and in/out arities


```scala
trait AnyEdgeType {

  val label: String

  // TODO add an applicative/monad requirement here
  type In[X]
  type Out[X]
  implicit val inFunctor: Functor[In]
  implicit val outFunctor: Functor[Out]

  type SourceType <: AnyVertexType
  val sourceType: SourceType

  type TargetType <: AnyVertexType
  val targetType: TargetType
}

object AnyEdgeType {
```

Additional methods

```scala
  implicit def edgeTypeOps[ET <: AnyEdgeType](et: ET) = EdgeTypeOps(et)
  case class   EdgeTypeOps[ET <: AnyEdgeType](et: ET) 
    extends HasPropertiesOps(et) {}

  type ==>[S <: AnyVertexType, T <: AnyVertexType] = AnyEdgeType {
    type SourceType = S
    type TargetType = T
  }
}

trait AnySealedEdgeType extends AnyEdgeType {

  // type SourceType <: AnySealedVertexType
  // type TargetType <: AnySealedVertexType

  type Record <: Singleton with AnyRecord
  val record: Record
}

abstract class SealedEdgeType [
  S <: AnyVertexType,
  R <: Singleton with AnyRecord,
  T <: AnyVertexType
](
  val sourceType: S,
  val label: String,
  val record: R,
  val targetType: T
) 
extends AnySealedEdgeType with From[S] with To[T] {

  type Record = R
}
```

Source/Target

```scala
trait From[S <: AnyVertexType] extends AnyEdgeType { type SourceType = S }
trait   To[T <: AnyVertexType] extends AnyEdgeType { type TargetType = T }
```

Arities

```scala
trait ManyOut extends AnyEdgeType { type Out[X] =   List[X]; val outFunctor = implicitly[Functor[Out]] }
trait  OneOut extends AnyEdgeType { type Out[X] = Option[X]; val outFunctor = implicitly[Functor[Out]] }
trait ManyIn  extends AnyEdgeType { type  In[X] =   List[X]; val  inFunctor = implicitly[Functor[In]] }
trait  OneIn  extends AnyEdgeType { type  In[X] = Option[X]; val  inFunctor = implicitly[Functor[In]] }

class ManyToMany[S <: AnyVertexType, T <: AnyVertexType]
  (val sourceType: S, val label: String, val targetType: T) 
    extends From[S] with To[T] with ManyIn with ManyOut

class OneToMany[S <: AnyVertexType, T <: AnyVertexType]
  (val sourceType: S, val label: String, val targetType: T) 
    extends From[S] with To[T] with OneIn with ManyOut

class ManyToOne[S <: AnyVertexType, T <: AnyVertexType]
  (val sourceType: S, val label: String, val targetType: T) 
    extends From[S] with To[T] with ManyIn with OneOut

class OneToOne[S <: AnyVertexType, T <: AnyVertexType]
  (val sourceType: S, val label: String, val targetType: T) 
    extends From[S] with To[T] with OneIn with OneOut

```


------

### Index

+ src
  + main
    + scala
      + ohnosequences
        + scarph
          + [Edge.scala][main/scala/ohnosequences/scarph/Edge.scala]
          + [EdgeType.scala][main/scala/ohnosequences/scarph/EdgeType.scala]
          + [Expressions.scala][main/scala/ohnosequences/scarph/Expressions.scala]
          + [GraphSchema.scala][main/scala/ohnosequences/scarph/GraphSchema.scala]
          + ops
            + [default.scala][main/scala/ohnosequences/scarph/ops/default.scala]
            + [typelevel.scala][main/scala/ohnosequences/scarph/ops/typelevel.scala]
          + titan
            + [TitanEdge.scala][main/scala/ohnosequences/scarph/titan/TitanEdge.scala]
            + [TitanGraphSchema.scala][main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]
            + [TitanVertex.scala][main/scala/ohnosequences/scarph/titan/TitanVertex.scala]
          + [Vertex.scala][main/scala/ohnosequences/scarph/Vertex.scala]
          + [VertexType.scala][main/scala/ohnosequences/scarph/VertexType.scala]
  + test
    + scala
      + ohnosequences
        + scarph
          + [sealedStuff.scala][test/scala/ohnosequences/scarph/sealedStuff.scala]
          + titan
            + [expressions.scala][test/scala/ohnosequences/scarph/titan/expressions.scala]
            + [godsImplementation.scala][test/scala/ohnosequences/scarph/titan/godsImplementation.scala]
            + [godsSchema.scala][test/scala/ohnosequences/scarph/titan/godsSchema.scala]
            + [TitanGodsTest.scala][test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]
            + [TitanSchemaTest.scala][test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]

[main/scala/ohnosequences/scarph/Edge.scala]: Edge.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: EdgeType.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: Expressions.scala.md
[main/scala/ohnosequences/scarph/GraphSchema.scala]: GraphSchema.scala.md
[main/scala/ohnosequences/scarph/ops/default.scala]: ops/default.scala.md
[main/scala/ohnosequences/scarph/ops/typelevel.scala]: ops/typelevel.scala.md
[main/scala/ohnosequences/scarph/titan/TitanEdge.scala]: titan/TitanEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]: titan/TitanGraphSchema.scala.md
[main/scala/ohnosequences/scarph/titan/TitanVertex.scala]: titan/TitanVertex.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: Vertex.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: VertexType.scala.md
[test/scala/ohnosequences/scarph/sealedStuff.scala]: ../../../../test/scala/ohnosequences/scarph/sealedStuff.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: ../../../../test/scala/ohnosequences/scarph/titan/expressions.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: ../../../../test/scala/ohnosequences/scarph/titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: ../../../../test/scala/ohnosequences/scarph/titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala.md