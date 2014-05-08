
```scala
package ohnosequences.scarph.titan.test

// import org.scalatest._

import com.thinkaurelius.titan.example.GraphOfTheGodsFactory
import com.thinkaurelius.titan.core._
import java.io.File

import ohnosequences.scarph._
import ohnosequences.scarph.titan._

import GodsSchema._
import GodsImplementation._
import MakeKeys._

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

  test("create property keys") {

    g.addPropertyKey(age)
    g.addPropertyKey(name)
    g.commit

    val ageType: TitanType = g.getType(age.label)
    val nameType: TitanType = g.getType(name.label)
    assert(nameType.getName === name.label)
    assert(nameType.isPropertyKey)

    // we checked that it's a property key, so we can cast:
    val ageKey: TitanKey = ageType.asInstanceOf[TitanKey]
    val nameKey: TitanKey = nameType.asInstanceOf[TitanKey]

    assert(ageKey.getDataType.getName === classOf[age.Rep].getName)
    assert(nameKey.getDataType.getName === classOf[name.Rep].getName)

  }

  test("create edge labels") {

    g.addEdgeLabel(pet)
    g.commit

    val petType: TitanType = g.getType(pet.tpe.label)
    assert(petType.getName === pet.tpe.label)
    assert(petType.isEdgeLabel)

    // we checked that it's an edge label, so we can cast:
    val petLabel: TitanLabel = petType.asInstanceOf[TitanLabel]
    assert(petLabel.isDirected)

    // Don't know how to check arity for a TitanLabel
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