
```scala
package ohnosequences.scarph.titan.test

import GodsSchema._
import ohnosequences.scarph.titan._

object GodsImplementation {
```


### Vertices


```scala
  implicit case object titan    extends TitanVertex(Titan)
  implicit case object god      extends TitanVertex(God)
  implicit case object demigod  extends TitanVertex(Demigod)
  implicit case object human    extends TitanVertex(Human)
  implicit case object monster  extends TitanVertex(Monster)
  implicit case object location extends TitanVertex(Location)
```


### Edges


```scala
  implicit case object titanFather  extends TitanEdge(god,     TitanFather,  titan)
  implicit case object godFather    extends TitanEdge(demigod, GodFather,    god)
  implicit case object humanMother  extends TitanEdge(demigod, HumanMother,  human)
  implicit case object brother      extends TitanEdge(god,     Brother,      god)
  implicit case object pet          extends TitanEdge(god,     Pet,          monster)
  implicit case object battled      extends TitanEdge(demigod, Battled,      monster)
  implicit case object godLives     extends TitanEdge(god,     GodLives,     location)
  implicit case object monsterLives extends TitanEdge(monster, MonsterLives, location)

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

[main/scala/ohnosequences/scarph/Denotation.scala]: ../../../../../main/scala/ohnosequences/scarph/Denotation.scala.md
[main/scala/ohnosequences/scarph/Edge.scala]: ../../../../../main/scala/ohnosequences/scarph/Edge.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: ../../../../../main/scala/ohnosequences/scarph/EdgeType.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: ../../../../../main/scala/ohnosequences/scarph/Expressions.scala.md
[main/scala/ohnosequences/scarph/GraphSchema.scala]: ../../../../../main/scala/ohnosequences/scarph/GraphSchema.scala.md
[main/scala/ohnosequences/scarph/ops/default.scala]: ../../../../../main/scala/ohnosequences/scarph/ops/default.scala.md
[main/scala/ohnosequences/scarph/ops/typelevel.scala]: ../../../../../main/scala/ohnosequences/scarph/ops/typelevel.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: ../../../../../main/scala/ohnosequences/scarph/Property.scala.md
[main/scala/ohnosequences/scarph/titan/TitanEdge.scala]: ../../../../../main/scala/ohnosequences/scarph/titan/TitanEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]: ../../../../../main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala.md
[main/scala/ohnosequences/scarph/titan/TitanVertex.scala]: ../../../../../main/scala/ohnosequences/scarph/titan/TitanVertex.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: ../../../../../main/scala/ohnosequences/scarph/Vertex.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: ../../../../../main/scala/ohnosequences/scarph/VertexType.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: expressions.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: godsSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: TitanSchemaTest.scala.md