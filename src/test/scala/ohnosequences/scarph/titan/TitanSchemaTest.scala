package ohnosequences.scarph.titan.test

// import org.scalatest._

import com.thinkaurelius.titan.example.GraphOfTheGodsFactory
import com.thinkaurelius.titan.core._
import java.io.File

import ohnosequences.scarph._
import ohnosequences.scarph.titan._
import ohnosequences.pointless._

import GodsSchema._
import GodsImplementation._
import TitanGraphSchema._

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
