
```scala
package ohnosequences.scarph.titan.test

import GodsSchema._
import ohnosequences.scarph.titan._

object GodsImplementation {
```


### Vertices


```scala
  case object titan    extends TVertex(Titan)
  case object god      extends TVertex(God)
  case object demigod  extends TVertex(Demigod)
  case object human    extends TVertex(Human)
  case object monster  extends TVertex(Monster)
  case object location extends TVertex(Location)

  case object alternativeTitan extends TitanImpl with AnyTVertex {

    import ohnosequences.scarph.AnyEdge

    implicit val _name: GetProperty[name.type] = unsafeGetProperty(name)
    implicit val _age:  GetProperty[age.type] = unsafeGetProperty(age)

    implicit def fatherIn[
      E <: Singleton with AnyEdge { type Tpe = TitanFather.type }
    ](e: E): RetrieveInEdge[E] = unsafeRetrieveManyInEdge[E](e)
  }
```


### Edges


```scala
  case object titanFather  extends TEdge(god,     TitanFather,  titan)
  case object godFather    extends TEdge(demigod, GodFather,    god)
  case object humanMother  extends TEdge(demigod, HumanMother,  human)
  case object brother      extends TEdge(god,     Brother,      god)
  case object pet          extends TEdge(god,     Pet,          monster)
  case object battled      extends TEdge(demigod, Battled,      monster)
  case object godLives     extends TEdge(god,     GodLives,     location)
  case object monsterLives extends TEdge(monster, MonsterLives, location)

}

```


------

### Index

+ src
  + main
    + scala
      + ohnosequences
        + scarph
          + titan
            + [TEdge.scala][main/scala/ohnosequences/scarph/titan/TEdge.scala]
            + [TitanGraphSchema.scala][main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]
            + [TVertex.scala][main/scala/ohnosequences/scarph/titan/TVertex.scala]
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

[main/scala/ohnosequences/scarph/titan/TEdge.scala]: ../../../../../main/scala/ohnosequences/scarph/titan/TEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]: ../../../../../main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala.md
[main/scala/ohnosequences/scarph/titan/TVertex.scala]: ../../../../../main/scala/ohnosequences/scarph/titan/TVertex.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: expressions.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: godsSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: TitanSchemaTest.scala.md