
```scala
package ohnosequences.scarph.test.titan

import ohnosequences.scarph._
```


## The Graph of the Gods Schema

See [Titan tutorial](https://github.com/thinkaurelius/titan/wiki/Getting-Started) and
[the code](https://github.com/thinkaurelius/titan/blob/master/titan-core/src/main/java/com/thinkaurelius/titan/example/GraphOfTheGodsFactory.java) for the reference.


```scala
object GodsSchema {
```


### Properties


```scala
  case object name extends Property[String]
  case object age extends Property[Int]
  // instead of this property we will create REAL types (because we are cool)
  // case object `type` extends Property[String]
  case object time extends Property[Int]
  case object reason extends Property[String]
  // This is something advanced:
  import com.thinkaurelius.titan.core.attribute.Geoshape
  case object place extends Property[Geoshape]
```


### Vertices


```scala
  case object Titan extends VertexType("titan")
  implicit val Titan_name = Titan has name
  implicit val Titan_age  = Titan has age

  // example
  // force the user to provide the right set of implicits for this schema
  abstract class TitanImpl extends Vertex[Titan.type](Titan) {

    implicit val _name: GetProperty[name.type]
    implicit val _age:  GetProperty[age.type]

    implicit def fatherIn[
      E <: Singleton with AnyEdge {type Tpe = TitanFather.type }
    ](e: E): RetrieveInEdge[E]
  }

  case object God extends VertexType("god")
  implicit val God_name = God has name
  implicit val God_age  = God has age

  case object Demigod extends VertexType("demigod")
  implicit val Demigod_name = Demigod has name
  implicit val Demigod_age  = Demigod has age

  case object Human extends VertexType("human")
  implicit val Human_name = Human has name
  implicit val Human_age  = Human has age

  case object Monster extends VertexType("monster")
  implicit val Monster_name = Monster has name
  
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

  // TODO: other combinations are also possible, but are just not needed for the example

```

#### Other relationships
a God can have moster pets

```scala
  case object Pet extends OneToMany(God, "pet", Monster)
```

Demigods battle with Monsters

```scala
  case object Battled extends ManyToMany(Demigod, "battled", Monster)
  implicit val Battled_time  = Battled has time
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
          + [HasProperties.scala][main/scala/ohnosequences/scarph/HasProperties.scala]
          + [Property.scala][main/scala/ohnosequences/scarph/Property.scala]
          + [Vertex.scala][main/scala/ohnosequences/scarph/Vertex.scala]
          + [VertexType.scala][main/scala/ohnosequences/scarph/VertexType.scala]
  + test
    + scala
      + ohnosequences
        + scarph
          + [edges.scala][test/scala/ohnosequences/scarph/edges.scala]
          + [edgeTypes.scala][test/scala/ohnosequences/scarph/edgeTypes.scala]
          + [expressions.scala][test/scala/ohnosequences/scarph/expressions.scala]
          + [properties.scala][test/scala/ohnosequences/scarph/properties.scala]
          + titan
            + [godsImplementation.scala][test/scala/ohnosequences/scarph/titan/godsImplementation.scala]
            + [godsSchema.scala][test/scala/ohnosequences/scarph/titan/godsSchema.scala]
            + [TEdge.scala][test/scala/ohnosequences/scarph/titan/TEdge.scala]
            + [TitanGodsTest.scala][test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]
            + [TitanGraphSchema.scala][test/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]
            + [TitanSchemaTest.scala][test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]
            + [TVertex.scala][test/scala/ohnosequences/scarph/titan/TVertex.scala]
          + [vertexTypes.scala][test/scala/ohnosequences/scarph/vertexTypes.scala]
          + [vertices.scala][test/scala/ohnosequences/scarph/vertices.scala]

[main/scala/ohnosequences/scarph/Denotation.scala]: ../../../../../main/scala/ohnosequences/scarph/Denotation.scala.md
[main/scala/ohnosequences/scarph/Edge.scala]: ../../../../../main/scala/ohnosequences/scarph/Edge.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: ../../../../../main/scala/ohnosequences/scarph/EdgeType.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: ../../../../../main/scala/ohnosequences/scarph/Expressions.scala.md
[main/scala/ohnosequences/scarph/HasProperties.scala]: ../../../../../main/scala/ohnosequences/scarph/HasProperties.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: ../../../../../main/scala/ohnosequences/scarph/Property.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: ../../../../../main/scala/ohnosequences/scarph/Vertex.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: ../../../../../main/scala/ohnosequences/scarph/VertexType.scala.md
[test/scala/ohnosequences/scarph/edges.scala]: ../edges.scala.md
[test/scala/ohnosequences/scarph/edgeTypes.scala]: ../edgeTypes.scala.md
[test/scala/ohnosequences/scarph/expressions.scala]: ../expressions.scala.md
[test/scala/ohnosequences/scarph/properties.scala]: ../properties.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: godsSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TEdge.scala]: TEdge.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]: TitanGraphSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: TitanSchemaTest.scala.md
[test/scala/ohnosequences/scarph/titan/TVertex.scala]: TVertex.scala.md
[test/scala/ohnosequences/scarph/vertexTypes.scala]: ../vertexTypes.scala.md
[test/scala/ohnosequences/scarph/vertices.scala]: ../vertices.scala.md