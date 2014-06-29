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
    def getTagged[VT <: Singleton with AnyVertexType, V <: Singleton with AnyTVertex.ofType[VT]](vt: VT)(k: String, s: String)
      (implicit toV: VT => V): V#Rep = {
      toV(vt) ->> tg.getVertices(k, s).iterator().next().asInstanceOf[TitanVertex]
    }
  } 

  test("testing titan implicit implementation") {

    import GodsImplementation._
    import ops.typelevel._
    import TitanImplementation._

    val pluto = g.getTagged(God)("name", "pluto")
    // shapeless.test.typed[god.Rep](pluto)

    // implicit val ppp = ETtoE.ettoe(Pet)
    // import ETtoE._

    // val pe = pluto.outT[Pet.type, pet.type](Pet)(ETtoE.ettoe(Pet), god.unsafeGetManyOutEdge)
    val pe = pluto.outT(Pet)
    // shapeless.test.typed[List[pet.Rep]](pe)

    assert(pe.map{ edgeOps(_).target }.map{ _.get(name) } === List("cerberus"))
    // assert(pluto.outV(Pet).map{ _.get(name) } === List("cerberus"))

  }

}
