
```scala
package ohnosequences.scarph.titan.test

// import org.scalatest._

import com.thinkaurelius.titan.example.GraphOfTheGodsFactory
import com.thinkaurelius.titan.core._
import java.io.File

import ohnosequences.scarph._
import ohnosequences.scarph.titan._
import ohnosequences.typesets._

import GodsSchema._
import GodsImplementation._
import TSchema._

class TitanSchemaSuite extends org.scalatest.FunSuite with org.scalatest.BeforeAndAfterAll {

  val graphLocation = new File("/tmp/titanSchemaTest")
  var g: TitanGraph = null

  // Creating a new titan instance
  override def beforeAll() {
    def cleanDir(f: File) {
      if (f.isDirectory) f.listFiles.foreach(cleanDir(_))
      else { println(f.toString); f.delete }
    }
    cleanDir(graphLocation)

    g = TitanFactory.open(graphLocation.getAbsolutePath)
    println("Created Titan graph")
  }

  override def afterAll() {
    if(g != null) {
      g.shutdown
      println("Shutdown Titan graph")
    }
  }

  test("filter vertex types properties") {
    // VERTEX TYPES //
    val _Titan    = (GodsSchema.Titan, name :~: age :~: ∅)
    val _God      = (God,              name :~: age :~: ∅)
    val _Demigod  = (Demigod,          name :~: age :~: ∅)
    val _Human    = (Human,            name :~: age :~: ∅)
    val _Monster  = (Monster,          name :~: ∅)
    val _Location = (Location,         name :~: ∅)

    implicitly[HasProperties[GodsSchema.Titan.type, name.type :~: age.type :~: ∅]]
    implicitly[HasProperty[GodsSchema.Titan.type, name.type]]

    assert(godsGraphSchema.vertexProperties(_Titan._1)    === _Titan._2)
    assert(godsGraphSchema.vertexProperties(_God._1)      === _God._2)
    assert(godsGraphSchema.vertexProperties(_Demigod._1)  === _Demigod._2)
    assert(godsGraphSchema.vertexProperties(_Human._1)    === _Human._2)
    assert(godsGraphSchema.vertexProperties(_Monster._1)  === _Monster._2)
    assert(godsGraphSchema.vertexProperties(_Location._1) === _Location._2)

    // EDGE TYPES //
    val _TitanFather  = (TitanFather, ∅)
    val _GodFather    = (GodFather, ∅)
    val _HumanMother  = (HumanMother, ∅)
    val _Brother      = (Brother, ∅)
    val _Pet          = (Pet, ∅)
    val _Battled      = (Battled, time :~: place :~: ∅)
    val _GodLives     = (GodLives, reason :~: ∅)
    val _MonsterLives = (MonsterLives, ∅)

    assert(godsGraphSchema.edgeProperties(_Pet._1)      === _Pet._2)
    assert(godsGraphSchema.edgeProperties(_Battled._1)  === _Battled._2)
    assert(godsGraphSchema.edgeProperties(_GodLives._1) === _GodLives._2)

    assert(godsGraphSchema.verticesWithProperties === 
           _Titan :~: _God :~: _Demigod :~: _Human :~: _Monster :~: _Location :~: ∅)
    assert(godsGraphSchema.edgesWithProperties === 
           _TitanFather :~: _GodFather :~: _HumanMother :~: _Brother :~: _Pet :~: _Battled :~: _GodLives :~: _MonsterLives :~: ∅)
    println(godsGraphSchema)
  }

  test("create all keys and labels from a given schema") {
    g.createSchema(godsGraphSchema)
    g.commit

    // Checking all property keys:
    for { p <- List(name, age, time, reason, place) }
    yield {
      val pType: TitanType = g.getType(p.label)
      assert(pType.getName === p.label)
      assert(pType.isPropertyKey)
      // FIXME: can't check type, because `classOf` requires a stable identifier
      // assert(pType.asInstanceOf[TitanKey].getDataType.getName === classOf[p.Raw].getName)
    }

    // Checking all edge labels:
    // FIXME: Don't know how to check arity for a TitanLabel
    for { e <- List(titanFather, godFather, humanMother, brother, pet, battled, godLives, monsterLives) }
    yield {
      val eType: TitanType = g.getType(e.tpe.label)
      assert(eType.getName === e.tpe.label)
      assert(eType.isEdgeLabel)
      assert(eType.asInstanceOf[TitanLabel].isDirected)
    }

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
          + [properties.scala][test/scala/ohnosequences/scarph/properties.scala]
          + [edges.scala][test/scala/ohnosequences/scarph/edges.scala]
          + [vertices.scala][test/scala/ohnosequences/scarph/vertices.scala]
          + titan
            + [expressions.scala][test/scala/ohnosequences/scarph/titan/expressions.scala]
            + [TitanSchemaTest.scala][test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]
            + [TitanOtherOpsTest.scala][test/scala/ohnosequences/scarph/titan/TitanOtherOpsTest.scala]
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
          + ops
            + [typelevel.scala][main/scala/ohnosequences/scarph/ops/typelevel.scala]
            + [default.scala][main/scala/ohnosequences/scarph/ops/default.scala]
          + [Denotation.scala][main/scala/ohnosequences/scarph/Denotation.scala]
          + [EdgeType.scala][main/scala/ohnosequences/scarph/EdgeType.scala]
          + [VertexType.scala][main/scala/ohnosequences/scarph/VertexType.scala]
          + [Vertex.scala][main/scala/ohnosequences/scarph/Vertex.scala]
          + [Edge.scala][main/scala/ohnosequences/scarph/Edge.scala]
          + titan
            + [TitanImplementation.scala][main/scala/ohnosequences/scarph/titan/TitanImplementation.scala]
            + [TEdge.scala][main/scala/ohnosequences/scarph/titan/TEdge.scala]
            + [TVertex.scala][main/scala/ohnosequences/scarph/titan/TVertex.scala]
            + [TSchema.scala][main/scala/ohnosequences/scarph/titan/TSchema.scala]
          + [Property.scala][main/scala/ohnosequences/scarph/Property.scala]
          + [GraphSchema.scala][main/scala/ohnosequences/scarph/GraphSchema.scala]

[test/scala/ohnosequences/scarph/properties.scala]: ../properties.scala.md
[test/scala/ohnosequences/scarph/edges.scala]: ../edges.scala.md
[test/scala/ohnosequences/scarph/vertices.scala]: ../vertices.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: expressions.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: TitanSchemaTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanOtherOpsTest.scala]: TitanOtherOpsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: godsSchema.scala.md
[test/scala/ohnosequences/scarph/vertexTypes.scala]: ../vertexTypes.scala.md
[test/scala/ohnosequences/scarph/edgeTypes.scala]: ../edgeTypes.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: ../../../../../main/scala/ohnosequences/scarph/Expressions.scala.md
[main/scala/ohnosequences/scarph/ops/typelevel.scala]: ../../../../../main/scala/ohnosequences/scarph/ops/typelevel.scala.md
[main/scala/ohnosequences/scarph/ops/default.scala]: ../../../../../main/scala/ohnosequences/scarph/ops/default.scala.md
[main/scala/ohnosequences/scarph/Denotation.scala]: ../../../../../main/scala/ohnosequences/scarph/Denotation.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: ../../../../../main/scala/ohnosequences/scarph/EdgeType.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: ../../../../../main/scala/ohnosequences/scarph/VertexType.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: ../../../../../main/scala/ohnosequences/scarph/Vertex.scala.md
[main/scala/ohnosequences/scarph/Edge.scala]: ../../../../../main/scala/ohnosequences/scarph/Edge.scala.md
[main/scala/ohnosequences/scarph/titan/TitanImplementation.scala]: ../../../../../main/scala/ohnosequences/scarph/titan/TitanImplementation.scala.md
[main/scala/ohnosequences/scarph/titan/TEdge.scala]: ../../../../../main/scala/ohnosequences/scarph/titan/TEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TVertex.scala]: ../../../../../main/scala/ohnosequences/scarph/titan/TVertex.scala.md
[main/scala/ohnosequences/scarph/titan/TSchema.scala]: ../../../../../main/scala/ohnosequences/scarph/titan/TSchema.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: ../../../../../main/scala/ohnosequences/scarph/Property.scala.md
[main/scala/ohnosequences/scarph/GraphSchema.scala]: ../../../../../main/scala/ohnosequences/scarph/GraphSchema.scala.md