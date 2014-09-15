
```scala
package ohnosequences.scarph.titan.test

// import org.scalatest._

import com.thinkaurelius.titan.example.GraphOfTheGodsFactory
import com.thinkaurelius.titan.core._
import java.io.File

import GodsSchema._
import GodsImplementation._

import ohnosequences.scarph._, ops.default._
import ohnosequences.scarph.titan._
import ohnosequences.typesets.Property._

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
    def getTagged[V <: AnyTitanVertex](vx: V)(k: String, v: String): vx.Rep = {
      vx ->> tg.getVertices(k, v).iterator().next().asInstanceOf[com.thinkaurelius.titan.core.TitanVertex]
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
    assert(pluto.out(pet).map{ _.tgt }.map{ _.get(name) } === List("cerberus"))
    // same but using .outV
    assert(pluto.outV(pet).map{ _.get(name) } === List("cerberus"))

    assert(pluto.in(brother).map{ _.src }.map{ _.get(name) }.toSet === Set("neptune", "jupiter"))
    // symmetry:
    assert(pluto.in(brother).map{ _.tgt } 
      === pluto.out(brother).map{ _.src })

    assert(pluto.inV(brother) === pluto.in(brother).map{ _.src })
    assert(pluto.inV(brother) === pluto.outV(brother))

    // FIXME: this doesn't work on the first flatMap
    // assert(pluto.in(brother).flatMap{ _.out(godLives) }.map{ _.get(name) }.toSet === Set("sea", "sky"))
    assert(pluto.inV(brother).map{ _.outV(godLives) }.flatten.map{ _.get(name) }.toSet === Set("sea", "sky"))
  }


  test("testing titan implicit implementation") {

    implicit class graphOps(tg: TitanGraph) {
      // just a shortcut
      def getTagged[VT <: AnyVertexType, V <: AnyTitanVertex.ofType[VT]](vt: VT)(k: String, s: String)
        (implicit v: V): v.Rep = {
        v ->> tg.getVertices(k, s).iterator().next().asInstanceOf[com.thinkaurelius.titan.core.TitanVertex]
      }
    } 

    import GodsImplementation._
    import ops.typelevel._

    val pluto = g.getTagged(God)("name", "pluto")
    // shapeless.test.typed[god.Rep](pluto)

    val pe: List[pet.Rep] = pluto out Pet

    assert(pluto.out(Pet).map{ _.tgt }.map{ _.get(name) } === List("cerberus"))
    assert(pluto.outV(Pet).map{ _.get(name) } === List("cerberus"))

  }

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