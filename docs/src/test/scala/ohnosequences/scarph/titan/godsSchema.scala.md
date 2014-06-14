
```scala
package ohnosequences.scarph.titan.test

import ohnosequences.scarph._
import ohnosequences.typesets._
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
    implicit val _props = this has name :~: age :~: ∅
  }
  case object God extends VertexType("god")
    // or outside
    implicit val God_props = God has name :~: age :~: ∅
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
    implicit val Monster_props = Monster has name :~: ∅
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
  implicit val Battled_props = Battled has time :~: ∅
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
    dependencies = ∅,
    properties = name :~: age :~: time :~: reason :~: place :~: ∅,
    vertexTypes = Titan :~: God :~: Demigod :~: Human :~: Monster :~: Location :~: ∅,
    edgeTypes = TitanFather :~: GodFather :~: HumanMother :~: Brother :~: Pet :~: Battled :~: GodLives :~: MonsterLives :~: ∅
  )

}

```


------

### Index

+ src
  + test
    + scala
      + ohnosequences
        + scarph
          + [properties.scala][test/scala/ohnosequences/scarph/properties.scala]
          + restricted
            + [SimpleSchema.scala][test/scala/ohnosequences/scarph/restricted/SimpleSchema.scala]
            + [RestrictedSchemaTest.scala][test/scala/ohnosequences/scarph/restricted/RestrictedSchemaTest.scala]
            + [SimpleSchemaImplementation.scala][test/scala/ohnosequences/scarph/restricted/SimpleSchemaImplementation.scala]
          + [edges.scala][test/scala/ohnosequences/scarph/edges.scala]
          + [vertices.scala][test/scala/ohnosequences/scarph/vertices.scala]
          + titan
            + [expressions.scala][test/scala/ohnosequences/scarph/titan/expressions.scala]
            + [TitanSchemaTest.scala][test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]
            + [TitanGodsTest.scala][test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]
            + [godsImplementation.scala][test/scala/ohnosequences/scarph/titan/godsImplementation.scala]
            + [godsSchema.scala][test/scala/ohnosequences/scarph/titan/godsSchema.scala]
          + [vertexTypes.scala][test/scala/ohnosequences/scarph/vertexTypes.scala]
          + [edgeTypes.scala][test/scala/ohnosequences/scarph/edgeTypes.scala]
  + main
    + scala
      + ohnosequences
        + scarph
          + [Expressions.scala][main/scala/ohnosequences/scarph/Expressions.scala]
          + [Denotation.scala][main/scala/ohnosequences/scarph/Denotation.scala]
          + [EdgeType.scala][main/scala/ohnosequences/scarph/EdgeType.scala]
          + [VertexType.scala][main/scala/ohnosequences/scarph/VertexType.scala]
          + [Vertex.scala][main/scala/ohnosequences/scarph/Vertex.scala]
          + [Schema.scala][main/scala/ohnosequences/scarph/Schema.scala]
          + [Edge.scala][main/scala/ohnosequences/scarph/Edge.scala]
          + titan
            + [TEdge.scala][main/scala/ohnosequences/scarph/titan/TEdge.scala]
            + [TVertex.scala][main/scala/ohnosequences/scarph/titan/TVertex.scala]
            + [TSchema.scala][main/scala/ohnosequences/scarph/titan/TSchema.scala]
          + [Property.scala][main/scala/ohnosequences/scarph/Property.scala]

[test/scala/ohnosequences/scarph/properties.scala]: ../properties.scala.md
[test/scala/ohnosequences/scarph/restricted/SimpleSchema.scala]: ../restricted/SimpleSchema.scala.md
[test/scala/ohnosequences/scarph/restricted/RestrictedSchemaTest.scala]: ../restricted/RestrictedSchemaTest.scala.md
[test/scala/ohnosequences/scarph/restricted/SimpleSchemaImplementation.scala]: ../restricted/SimpleSchemaImplementation.scala.md
[test/scala/ohnosequences/scarph/edges.scala]: ../edges.scala.md
[test/scala/ohnosequences/scarph/vertices.scala]: ../vertices.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: expressions.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: TitanSchemaTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: godsSchema.scala.md
[test/scala/ohnosequences/scarph/vertexTypes.scala]: ../vertexTypes.scala.md
[test/scala/ohnosequences/scarph/edgeTypes.scala]: ../edgeTypes.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: ../../../../../main/scala/ohnosequences/scarph/Expressions.scala.md
[main/scala/ohnosequences/scarph/Denotation.scala]: ../../../../../main/scala/ohnosequences/scarph/Denotation.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: ../../../../../main/scala/ohnosequences/scarph/EdgeType.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: ../../../../../main/scala/ohnosequences/scarph/VertexType.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: ../../../../../main/scala/ohnosequences/scarph/Vertex.scala.md
[main/scala/ohnosequences/scarph/Schema.scala]: ../../../../../main/scala/ohnosequences/scarph/Schema.scala.md
[main/scala/ohnosequences/scarph/Edge.scala]: ../../../../../main/scala/ohnosequences/scarph/Edge.scala.md
[main/scala/ohnosequences/scarph/titan/TEdge.scala]: ../../../../../main/scala/ohnosequences/scarph/titan/TEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TVertex.scala]: ../../../../../main/scala/ohnosequences/scarph/titan/TVertex.scala.md
[main/scala/ohnosequences/scarph/titan/TSchema.scala]: ../../../../../main/scala/ohnosequences/scarph/titan/TSchema.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: ../../../../../main/scala/ohnosequences/scarph/Property.scala.md