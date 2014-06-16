package ohnosequences.scarph.titan.test

// import org.scalatest._

import com.thinkaurelius.titan.example.GraphOfTheGodsFactory
import com.thinkaurelius.titan.core._
import java.io.File

import GodsSchema._
// import GodsImplementation._

import ohnosequences.scarph._, titan._, TSchema._

class TitanImplementationSuite extends org.scalatest.FunSuite with org.scalatest.BeforeAndAfterAll {

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
    def getTagged[V <: AnyTVertex](vx: V)(k: String, v: String): vx.Rep = {
      vx ->> tg.getVertices(k, v).iterator().next().asInstanceOf[TitanVertex]
    }
    // def getTagged[VT <: AnyVertexType](vt: VT)(k: String, v: String//)(
    //   // implicit toTVertex: VT => TVertex[VT]
    // ): TitanVertex with AnyDenotation.Tag[TVertex[VT]] = {
    //   // case object vx extends TVertex(vt)
    //   // val vx = toTVertex(vt)
    //   val vx = TitanImplementation.VTtoV(vt)
    //   (vx: TVertex[VT]) ->> tg.getVertices(k, v).iterator().next().asInstanceOf[TitanVertex]
    // }
  } 

  test("testing titan implicit implementation") {

    import TitanImplementation._

    object vGod extends TVertex(GodsSchema.God)
    val pluto = g.getTagged(vGod)("name", "pluto")

    // val pe: List[pet.Rep] = pluto out pet
    assert(pluto.out(ETtoE(Pet)).map{ _.target }.map{ _.get(name) } === List("cerberus"))

    // assert(saturn.getProperty[Int]("age") === 10000)
    // assert(saturn.get(age) === 10000)

    // println(saturn in )

  }

}
