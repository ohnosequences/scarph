
```scala
package ohnosequences.scarph

import shapeless._
import shapeless.ops.hlist._

sealed trait Morphism {

  type Source <: HList
  val source: Source

  type Target <: HList
  val target: Target
}

  case class Simple[E <: AnyEdgeType with Singleton](val edge: E)

  extends Morphism {

    type Source = edge.SourceType :: HNil
    val source  = edge.sourceType :: HNil

    type Target = edge.TargetType :: HNil
    val target  = edge.targetType :: HNil
  }

  case class Par[F <: Morphism, G <: Morphism](val f: F, val g: G)
  (implicit
    val prependSource: Prepend[F#Source, G#Source],
    val prependTarget: Prepend[F#Target, G#Target]
  )

  extends Morphism {

    // need Prepend for both sources and targets
    type Source = prependSource.Out
    val  source = (f.source:F#Source) ::: (g.source:G#Source)
    
    type Target = prependTarget.Out
    val  target = (f.target:F#Target) ::: (g.target:G#Target)
  }

  case class Compose[G <: Morphism, F <: Morphism](val g: G, val f: F)
  (implicit
    val eqSrcTgt: (G#Source =:= F#Target)
  )

  extends Morphism {

    type Source = f.Source
    val  source = f.source

    type Target = g.Target
    val  target = g.target
  }

```


------

### Index

+ src
  + main
    + scala
      + ohnosequences
        + scarph
          + [Denotation.scala][main/scala/ohnosequences/scarph/Denotation.scala]
          + [Edge.scala][main/scala/ohnosequences/scarph/Edge.scala]
          + [EdgeType.scala][main/scala/ohnosequences/scarph/EdgeType.scala]
          + [Expressions.scala][main/scala/ohnosequences/scarph/Expressions.scala]
          + [GraphSchema.scala][main/scala/ohnosequences/scarph/GraphSchema.scala]
          + ops
            + [default.scala][main/scala/ohnosequences/scarph/ops/default.scala]
            + [typelevel.scala][main/scala/ohnosequences/scarph/ops/typelevel.scala]
          + [Property.scala][main/scala/ohnosequences/scarph/Property.scala]
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
          + titan
            + [expressions.scala][test/scala/ohnosequences/scarph/titan/expressions.scala]
            + [godsImplementation.scala][test/scala/ohnosequences/scarph/titan/godsImplementation.scala]
            + [godsSchema.scala][test/scala/ohnosequences/scarph/titan/godsSchema.scala]
            + [TitanGodsTest.scala][test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]
            + [TitanSchemaTest.scala][test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]

[main/scala/ohnosequences/scarph/Denotation.scala]: Denotation.scala.md
[main/scala/ohnosequences/scarph/Edge.scala]: Edge.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: EdgeType.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: Expressions.scala.md
[main/scala/ohnosequences/scarph/GraphSchema.scala]: GraphSchema.scala.md
[main/scala/ohnosequences/scarph/ops/default.scala]: ops/default.scala.md
[main/scala/ohnosequences/scarph/ops/typelevel.scala]: ops/typelevel.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: Property.scala.md
[main/scala/ohnosequences/scarph/titan/TitanEdge.scala]: titan/TitanEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]: titan/TitanGraphSchema.scala.md
[main/scala/ohnosequences/scarph/titan/TitanVertex.scala]: titan/TitanVertex.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: Vertex.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: VertexType.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: ../../../../test/scala/ohnosequences/scarph/titan/expressions.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: ../../../../test/scala/ohnosequences/scarph/titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: ../../../../test/scala/ohnosequences/scarph/titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: ../../../../test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala.md