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

  // FIXME: this test _sometimes_ fails
  ignore("create property keys") {

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

    assert(ageKey.getDataType.getName === classOf[age.Raw].getName)
    assert(nameKey.getDataType.getName === classOf[name.Raw].getName)

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

    assert(godsGraphSchema.vTypeProps[_Titan._1.type]    === _Titan._2)
    assert(godsGraphSchema.vTypeProps[_God._1.type]      === _God._2)
    assert(godsGraphSchema.vTypeProps[_Demigod._1.type]  === _Demigod._2)
    assert(godsGraphSchema.vTypeProps[_Human._1.type]    === _Human._2)
    assert(godsGraphSchema.vTypeProps[_Monster._1.type]  === _Monster._2)
    assert(godsGraphSchema.vTypeProps[_Location._1.type] === _Location._2)

    assert(godsGraphSchema.eTypeProps[_Pet._1.type]      === _Pet._2)
    assert(godsGraphSchema.eTypeProps[_Battled._1.type]  === _Battled._2)
    assert(godsGraphSchema.eTypeProps[_GodLives._1.type] === _GodLives._2)

    assert(godsGraphSchema.vTypesWithProps === _Titan :~: _God :~: _Demigod :~: _Human :~: _Monster :~: _Location :~: ∅)
    // println(godsGraphSchema.eTypesWithProps)
    println(godsGraphSchema)
  }

  test("create a whole schema") {
    // FIXME: doesn't work (no SetMapper)
    import ohnosequences.typesets._
    import shapeless._, poly._
    // g.createSchema(godsGraphSchema) //(SetMapper.consMapper(g.mkKey.default, SetMapper.consMapper), SetMapper.emptyMapper)
    // g.commit
  }

}
