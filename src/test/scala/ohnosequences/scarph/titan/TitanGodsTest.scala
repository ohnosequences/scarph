package ohnosequences.scarph.titan.test

// import org.scalatest._

import com.thinkaurelius.titan.example.GraphOfTheGodsFactory
import com.thinkaurelius.titan.core.TitanGraph
import com.thinkaurelius.titan.core.TitanFactory
import java.io.File

import GodsSchema._

import ohnosequences.scarph._, ops.default._, AnyQuery._, AnyCondition._
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
    def getTagged[V <: AnyTitanVertex](vx: V)(k: String, v: String): vx.Rep = {
      vx ->> tg.getVertices(k, v).iterator().next().asInstanceOf[com.thinkaurelius.titan.core.TitanVertex]
    }
  } 

  test("get vertex property") {
    val impl = GodsImplementation(g); import impl._

    // getting saturn directly
    val saturn = g.getTagged(titan)(name.label, "saturn")
    // getting it through a query to the standard index over the name property
    val sat = titan.query(Titan ? (name === "saturn"))
    assert(sat.nonEmpty)

    // check the they are the same
    assert(sat.head === saturn)

    /* pure blueprints with string keys and casting: */
    assert(saturn.getProperty[Int]("age") === 10000)
    /* safe and nifty: */
    assert(saturn.get(age) === 10000)

  }

  test("get OUTgoing edges and their property") {
    val impl = GodsImplementation(g); import impl._

    val hercules = g.getTagged(demigod)("name", "hercules")
    
    assert(hercules.getProperty[Int]("age") === (hercules get age))

    val es: List[battled.Rep] = hercules out battled
    assert((hercules out battled map { _ get time }).toSet === Set(1, 12, 2))
  }

  ignore("get INcoming edges and their property") {
    val impl = GodsImplementation(g); import impl._

    val tartarus = g.getTagged(location)("name", "tartarus")

    // FIXME: godLives and monsterLives have the same label.
    // it should get only one edge (for pluto), but it gets both, because they have the same label:
    info((tartarus in godLives map { _ get reason }).mkString("['","', '","']"))
  }

  test("get target/source vertices of incoming/outgoing edges") {
    val impl = GodsImplementation(g); import impl._

    val pluto = g.getTagged(god)("name", "pluto")

    val pe: List[pet.Rep] = pluto out pet
    assert(pluto.out(pet).map{ _.target }.map{ _.get(name) } === List("cerberus"))
    // same but using .outV
    assert(pluto.outV(pet).map{ _.get(name) } === List("cerberus"))

    assert(pluto.in(brother).map{ _.source }.map{ _.get(name) }.toSet === Set("neptune", "jupiter"))
    // symmetry:
    assert(pluto.in(brother).map{ _.source } 
      === pluto.out(brother).map{ _.target })

    assert(pluto.inV(brother) === pluto.in(brother).map{ _.source })
    assert(pluto.inV(brother) === pluto.outV(brother))

    // FIXME: this doesn't work on the first flatMap
    // assert(pluto.in(brother).flatMap{ _.out(godLives) }.map{ _.get(name) }.toSet === Set("sea", "sky"))
    assert(pluto.inV(brother).map{ _.outV(godLives) }.flatten.map{ _.get(name) }.toSet === Set("sea", "sky"))
  }


  test("testing titan implicit implementation") {
    val impl = GodsImplementation(g); import impl._

    implicit class graphOps(tg: TitanGraph) {
      // just a shortcut
      def getTagged[VT <: AnyVertexType, V <: AnyTitanVertex.ofType[VT]](vt: VT)(k: String, s: String)
        (implicit v: V): v.Rep = {
        v ->> tg.getVertices(k, s).iterator().next().asInstanceOf[com.thinkaurelius.titan.core.TitanVertex]
      }
    } 

    import ops.typelevel._

    val plutos = query(God ? (name === "pluto"))
    assert(plutos.nonEmpty)
    val pluto = plutos.head

    val pe: List[pet.Rep] = pluto out Pet

    assert(pluto.out(Pet).map{ _.target }.map{ _.get(name) } === List("cerberus"))
    assert(pluto.outV(Pet).map{ _.get(name) } === List("cerberus"))

  }

}
