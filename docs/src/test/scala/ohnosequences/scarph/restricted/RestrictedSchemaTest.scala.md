
```scala
package ohnosequences.scarph.restricted.test

import com.thinkaurelius.titan.core._
import java.io.File

import ohnosequences.scarph._
import ohnosequences.scarph.titan._, TSchema._

import SimpleSchema._
import SimpleSchemaImplementation._
```


The point of this test is to create an example of a graph where properties are used only in edges


```scala
class RestrictedSchemaSuite extends org.scalatest.FunSuite with org.scalatest.BeforeAndAfterAll {

  val graphLocation = new File("/tmp/restrictedTitanTest")
  var g: TitanGraph = null

  // Reusing the graph if possible, else cleaning the directory and creating graph
  override def beforeAll() {
    try { 
      g = TitanFactory.open(graphLocation.getAbsolutePath)
      // checking that the graph is there:
      // g.getVertices("name", "saturn").iterator().next()
      g.getEdges("name", "Edwin Brady").iterator.next
      println("Reusing Titan graph")
    } catch {
      case e: Exception => {
        def cleanDir(f: File) {
          if (f.isDirectory) f.listFiles.foreach(cleanDir(_))
          else { println(f.toString); f.delete }
        }
        cleanDir(graphLocation)
        g = TitanFactory.open(graphLocation.getAbsolutePath)

        // Defining property keys and edge labels
        g.addPropertyKey(name)
        g.addPropertyKey(phone)
        g.addPropertyKey(title)
        g.addPropertyKey(published)

        g.addEdgeLabel(humanProps.tpe)
        g.addEdgeLabel(articleProps.tpe)
        g.addEdgeLabel(author.tpe)
        g.addEdgeLabel(knows.tpe)

        // Adding actual vertices/edges
        val humans = g.addVertex(null)

        val edwin = g.addVertex(null)
        val edwin_human = g.addEdge(null, edwin, humans, humanProps.tpe.label)
        edwin_human.setProperty(name.label, "Edwin Brady")
        edwin_human.setProperty(phone.label, 1334463271)

        val kevin = g.addVertex(null)
        val kevin_human = g.addEdge(null, kevin, humans, humanProps.tpe.label)
        kevin_human.setProperty(name.label, "Kevin Hammond")
        kevin_human.setProperty(phone.label, 1334463241)

        g.addEdge(null, edwin, kevin, knows.tpe.label)
        g.addEdge(null, kevin, edwin, knows.tpe.label)


        val articles = g.addVertex(null)

        val impl = g.addVertex(null)
        val impl_article = g.addEdge(null, impl, articles, articleProps.tpe.label)
        impl_article.setProperty(title.label, "Resource-safe Systems Programming with Embedded Domain Specific Languages")
        impl_article.setProperty(published.label, true)

        g.addEdge(null, impl, edwin, author.tpe.label)
        g.addEdge(null, impl, kevin, author.tpe.label)

        val sysprog = g.addVertex(null)
        val sysprog_article = g.addEdge(null, sysprog, articles, articleProps.tpe.label)
        sysprog_article.setProperty(title.label, "Idris, a General Purpose Dependently Typed Programming Language: Design and Implementation ")
        sysprog_article.setProperty(published.label, false)

        g.addEdge(null, sysprog, edwin, author.tpe.label)

        g.commit
        println("Created a new Titan graph")
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
    def getTagged[V <: AnyTEdge](vx: V)(k: String, v: String): vx.Rep = {
      vx ->> tg.getEdges(k, v).iterator().next().asInstanceOf[TitanEdge]
    }
  } 

  test("get human's phone property") {

    val edwin = g.getTagged(humanProps)("name", "Edwin Brady")
    assert(edwin.get(phone) === 1334463271)

  }

  test("get names of authors of an article") {

    val sysprog = g.getTagged(articleProps)("title", "Idris, a General Purpose Dependently Typed Programming Language: Design and Implementation ")
    assert(sysprog.get(published) === false)
    
    // Vs - vertices, Es - edges
    val sysprogV = sysprog.source
    val authorEs = sysprogV out author
    val authorVs = authorEs map { _ target }
    val humanEs = authorVs flatMap { _ out humanProps }
    val names = humanEs map { _ get name }

    assert(names === List("Edwin Brady"))

    // same, but in one line:
    assert((sysprog.source out author map { _ target } flatMap { _ out humanProps } map { _ get name }) === List("Edwin Brady"))
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
[test/scala/ohnosequences/scarph/restricted/SimpleSchema.scala]: SimpleSchema.scala.md
[test/scala/ohnosequences/scarph/restricted/RestrictedSchemaTest.scala]: RestrictedSchemaTest.scala.md
[test/scala/ohnosequences/scarph/restricted/SimpleSchemaImplementation.scala]: SimpleSchemaImplementation.scala.md
[test/scala/ohnosequences/scarph/edges.scala]: ../edges.scala.md
[test/scala/ohnosequences/scarph/vertices.scala]: ../vertices.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: ../titan/expressions.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: ../titan/TitanSchemaTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: ../titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: ../titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: ../titan/godsSchema.scala.md
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