
```scala
package ohnosequences.scarph.titan.test

// import org.scalatest._

import com.thinkaurelius.titan.example.GraphOfTheGodsFactory
import com.thinkaurelius.titan.core._
import java.io.File

import GodsSchema._

import ohnosequences.scarph._, titan._, TSchema._

class TitanOtherOpsSuite extends org.scalatest.FunSuite with org.scalatest.BeforeAndAfterAll {

  val graphLocation = new File("/tmp/titanImplementationTest")
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
        g.createSchema(godsGraphSchema)
        g.commit
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
    def getTagged[VT <: AnyVertexType, V <: AnyTVertex.ofType[VT]](vt: VT)(k: String, s: String)
      (implicit v: V): v.Rep = {
      v ->> tg.getVertices(k, s).iterator().next().asInstanceOf[TitanVertex]
    }
  } 

  test("testing titan implicit implementation") {

    import GodsImplementation._
    import ops.typelevel._

    // val pluto = g.getTagged(God)("name", "pluto")
    // shapeless.test.typed[god.Rep](pluto)

    // val pe: List[pet.Rep] = pluto out Pet

    // assert(pluto.out(Pet).map{ _.target }.map{ _.get(name) } === List("cerberus"))
    // assert(pluto.outV(Pet).map{ _.get(name) } === List("cerberus"))

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