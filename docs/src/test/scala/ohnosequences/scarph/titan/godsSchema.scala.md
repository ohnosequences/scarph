
```scala
package ohnosequences.scarph.titan.test

import ohnosequences.scarph._
import ohnosequences.pointless._
```


## The Graph of the Gods GraphSchema

See [Titan tutorial](https://github.com/thinkaurelius/titan/wiki/Getting-Started) and
[the code](https://github.com/thinkaurelius/titan/blob/master/titan-core/src/main/java/com/thinkaurelius/titan/example/GraphOfTheGodsFactory.java) for the reference.


```scala
object GodsSchema {
```


### Properties


```scala
  case object name extends Property[String]
  case object age extends Property[Integer]
  // instead of this property we will create REAL types (because we are cool)
  // case object `type` extends Property[String]
  case object time extends Property[Integer]
  case object reason extends Property[String]
  // This is something advanced:
  import com.thinkaurelius.titan.core.attribute.Geoshape
  case object place extends Property[Geoshape]
```


### Vertices


```scala
  case object Titan extends VertexType("titan") {
    // you can define properties inside of the type
    implicit val _props = this has name :~: age :~: ?
  }
  case object God extends VertexType("god")
    // or outside
    implicit val God_props = God has name :~: age :~: ?
  case object Demigod extends VertexType("demigod")
    // or one by one in any place
    implicit val Demigod_name = Demigod has name
    implicit val Demigod_age  = Demigod has age
  case object Human extends VertexType("human")
    implicit val Human_name = Human has name
    implicit val Human_age  = Human has age
  case object Monster extends VertexType("monster")
    // both ways are fine
    implicit val Monster_name = Monster has name
    implicit val Monster_props = Monster has name :~: ?
  case object Location extends VertexType("location")
    implicit val Location_name = Location has name
```


### Edges

#### Family relationships
Gods can have a Titan father

```scala
  case object TitanFather extends ManyToOne(God, "father", Titan)
```

Demigods can have a God father

```scala
  case object GodFather extends ManyToOne(Demigod, "father", God)
```

Note, that GodFather and TitanFather have the same label, but the two things are needed,
because of different source/target types
Demigods can have a Human mother

```scala
  case object HumanMother extends ManyToOne(Demigod, "mother", Human)
```

Gods can be brothers with Gods

```scala
  case object Brother extends ManyToMany(God, "brother", God)
```

#### Other relationships
a God can have moster pets

```scala
  case object Pet extends OneToMany(God, "pet", Monster)
```

Demigods battle with Monsters

```scala
  case object Battled extends ManyToMany(Demigod, "battled", Monster)
  implicit val Battled_props = Battled has time :~: ?
  implicit val Battled_place = Battled has place
```

Gods live in some Location and they have a reason for that

```scala
  case object GodLives extends ManyToOne(God, "lives", Location)
  implicit val GodLives_reason = GodLives has reason
```

Monsters live in some Location (without any reason)

```scala
  case object MonsterLives extends ManyToOne(Monster, "lives", Location)
```


### Fixed GraphSchema


```scala
  object godsGraphSchema extends GraphSchema("godsGraphSchema",
    dependencies = ?,
    properties = name :~: age :~: time :~: reason :~: place :~: ?,
    vertexTypes = Titan :~: God :~: Demigod :~: Human :~: Monster :~: Location :~: ?,
    edgeTypes = TitanFather :~: GodFather :~: HumanMother :~: Brother :~: Pet :~: Battled :~: GodLives :~: MonsterLives :~: ?
  )

}

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

[main/scala/ohnosequences/scarph/Edge.scala]: ../../../../../main/scala/ohnosequences/scarph/Edge.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: ../../../../../main/scala/ohnosequences/scarph/EdgeType.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: ../../../../../main/scala/ohnosequences/scarph/Expressions.scala.md
[main/scala/ohnosequences/scarph/GraphSchema.scala]: ../../../../../main/scala/ohnosequences/scarph/GraphSchema.scala.md
[main/scala/ohnosequences/scarph/ops/default.scala]: ../../../../../main/scala/ohnosequences/scarph/ops/default.scala.md
[main/scala/ohnosequences/scarph/ops/typelevel.scala]: ../../../../../main/scala/ohnosequences/scarph/ops/typelevel.scala.md
[main/scala/ohnosequences/scarph/titan/TitanEdge.scala]: ../../../../../main/scala/ohnosequences/scarph/titan/TitanEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]: ../../../../../main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala.md
[main/scala/ohnosequences/scarph/titan/TitanVertex.scala]: ../../../../../main/scala/ohnosequences/scarph/titan/TitanVertex.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: ../../../../../main/scala/ohnosequences/scarph/Vertex.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: ../../../../../main/scala/ohnosequences/scarph/VertexType.scala.md
[test/scala/ohnosequences/scarph/sealedStuff.scala]: ../sealedStuff.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: expressions.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: godsSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: TitanSchemaTest.scala.md