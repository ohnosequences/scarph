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
    import ohnosequences.typesets._
    val _Titan    = (GodsSchema.Titan, name :~: age :~: ∅)
    val _God      = (God,              name :~: age :~: ∅)
    val _Demigod  = (Demigod,          name :~: age :~: ∅)
    val _Human    = (Human,            name :~: age :~: ∅)
    val _Monster  = (Monster,          name :~: ∅)
    val _Location = (Location,         name :~: ∅)

    val _Pet      = (Pet, ∅)
    val _Battled  = (Battled, time :~: place :~: ∅)
    val _GodLives = (GodLives, reason :~: ∅)

    implicitly[HasProperties[GodsSchema.Titan.type, name.type :~: age.type :~: ∅]]
    implicitly[HasProperty[GodsSchema.Titan.type, name.type]]

    assert(godsGraphSchema.propertiesOfVertex[_Titan._1.type]    === _Titan._2)
    assert(godsGraphSchema.altPropertiesOfVertex(_Titan._1)    === _Titan._2)
    assert(godsGraphSchema.propertiesOfVertex[_God._1.type]      === _God._2)
    assert(godsGraphSchema.propertiesOfVertex[_Demigod._1.type]  === _Demigod._2)
    assert(godsGraphSchema.propertiesOfVertex[_Human._1.type]    === _Human._2)
    assert(godsGraphSchema.propertiesOfVertex[_Monster._1.type]  === _Monster._2)
    assert(godsGraphSchema.propertiesOfVertex[_Location._1.type] === _Location._2)

    assert(godsGraphSchema.propertiesOfEdge[_Pet._1.type]      === _Pet._2)
    assert(godsGraphSchema.propertiesOfEdge[_Battled._1.type]  === _Battled._2)
    assert(godsGraphSchema.propertiesOfEdge[_GodLives._1.type] === _GodLives._2)

    assert(godsGraphSchema.vTypesWithProps === _Titan :~: _God :~: _Demigod :~: _Human :~: _Monster :~: _Location :~: ∅)
    // println(godsGraphSchema.eTypesWithProps)
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
