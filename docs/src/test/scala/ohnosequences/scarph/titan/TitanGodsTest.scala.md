
```scala
package ohnosequences.scarph.titan.test

// import org.scalatest._

import com.thinkaurelius.titan.example.GraphOfTheGodsFactory
import com.thinkaurelius.titan.core._
import java.io.File

import GodsSchema._
import GodsImplementation._

import ohnosequences.scarph._
import ohnosequences.scarph.titan._

class TitanSuite extends org.scalatest.FunSuite with org.scalatest.BeforeAndAfterAll {

  val graphLocation = new File("/tmp/titanTest")
  var g: TitanGraph = null

  // Reusing the graph if possible, else cleaning the directory and creating graph
  override def beforeAll() {
    try { 
      g = TitanFactory.open(graphLocation.getAbsolutePath)
      // checking that the graph is there:
      g.getVertices("name", "saturn").iterator().next()
      println("Reusing Titan graph")
    } catch {
      case e: Exception => {
        def cleanDir(f: File) {
          if (f.isDirectory) f.listFiles.foreach(cleanDir(_))
          else { println(f.toString); f.delete }
        }
        cleanDir(graphLocation)
        g = GraphOfTheGodsFactory.create(graphLocation.getAbsolutePath)
        println("Created Titan graph")
      }
    }
  }

  override def afterAll() {
    if(g != null) {
      g.shutdown
      println("Shutdown Titan graph")
    }
  }

  implicit class graphOps(tg: TitanGraph) {
    // just a shortcut
    def getTagged[V <: AnyTVertex](vx: V)(k: String, v: String): vx.Rep = {
      vx ->> tg.getVertices(k, v).iterator().next().asInstanceOf[TitanVertex]
    }
  } 

  test("get vertex property") {

    val saturn = g.getTagged(GodsImplementation.titan)("name", "saturn")
```

pure blueprints with string keys and casting:

```scala
    assert(saturn.getProperty[Int]("age") === 10000)
```

safe and nifty:

```scala
    assert(saturn.get(age) === 10000)

  }

  test("get OUTgoing edges and their property") {

    val hercules = g.getTagged(demigod)("name", "hercules")
    
    assert(hercules.getProperty[Int]("age") === (hercules get age))

    val es: List[battled.Rep] = hercules out battled
    assert((hercules out battled map { _ get time }).toSet === Set(1, 12, 2))
  }

  ignore("get INcoming edges and their property") {

    val tartarus = g.getTagged(location)("name", "tartarus")

    // FIXME: godLives and monsterLives have the same label.
    // it should get only one edge (for pluto), but it gets both, because they have the same label:
    info((tartarus in godLives map { _ get reason }).mkString("['","', '","']"))
  }

  test("get target/source vertices of incoming/outgoing edges") {

    val pluto = g.getTagged(god)("name", "pluto")

    val pe: List[pet.Rep] = pluto out pet
    assert(pluto.out(pet).map{ _.target }.map{ _.get(name) } === List("cerberus"))

    assert(pluto.in(brother).map{ _.source }.map{ _.get(name) }.toSet === Set("neptune", "jupiter"))
    // symmetry:
    assert(pluto.in(brother).map{ _.source } 
      === pluto.out(brother).map{ _.target })
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