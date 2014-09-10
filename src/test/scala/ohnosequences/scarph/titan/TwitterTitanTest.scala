package ohnosequences.scarph.test.titan

import com.thinkaurelius.titan.core._
import com.thinkaurelius.titan.core.Multiplicity._
import com.thinkaurelius.titan.core.schema._
import com.tinkerpop.blueprints.Edge
import com.tinkerpop.blueprints.Vertex
import java.io.File

import ohnosequences.pointless._, AnyTypeSet._, AnyWrap._, AnyTypeSet._
import ohnosequences.pointless.ops.typeSet._
import ohnosequences.scarph._, impl.titan._, TitanGraphSchema._
import ohnosequences.scarph.syntax.simple._
import ohnosequences.scarph.test._, TwitterSchema._, TwitterImpl._
import ohnosequences.scarph.impl.titan.ops.vertex._
import ohnosequences.scarph.impl.titan.ops.edge._

class TitanSuite extends org.scalatest.FunSuite with org.scalatest.BeforeAndAfterAll {

  val graphLocation = new File("/tmp/titanTest")
  var g: TitanGraph = null

  def createTitanTwitterInstance(g: TitanGraph): Unit = {

    g.createSchema(TwitterSchema.schema)

    /* Adding things */
    val edu = g.addVertexWithLabel(User.label)
    edu.setProperty(name.label, "@eparejatobes")
    edu.setProperty(age.label, 95)

    val alexey = g.addVertexWithLabel(User.label)
    alexey.setProperty(name.label, "@laughedelic")
    alexey.setProperty(age.label, 5)

    val kim = g.addVertexWithLabel(User.label)
    kim.setProperty(name.label, "@evdokim")
    kim.setProperty(age.label, 22)


    // everybody follows everybody — boring "/
    edu.addEdge(Follows.label, alexey)
    edu.addEdge(Follows.label, kim)

    alexey.addEdge(Follows.label, edu)
    alexey.addEdge(Follows.label, kim)

    kim.addEdge(Follows.label, alexey)
    kim.addEdge(Follows.label, edu)


    val eduTweet1 = g.addVertexWithLabel(Tweet.label) 
    eduTweet1.setProperty(text.label, 
      """#programming languages should be explicit (= code) about what they are and their needs: syntax, underlying platform, semantics""")

    val eduPosted1 = edu.addEdge(Posted.label, eduTweet1)
    eduPosted1.setProperty(time.label, "27.10.2013")
    eduPosted1.setProperty(url.label, "https://twitter.com/eparejatobes/status/394430900051927041")

    val eduTweet2 = g.addVertexWithLabel(Tweet.label) 
    eduTweet2.setProperty(text.label, 
      """type definitions in titan #graphdb https://github.com/thinkaurelius/titan/wiki/Type-Definition-Overview … killer feature! example of how type info can be used to get better performance""")

    val eduPosted2 = edu.addEdge(Posted.label, eduTweet2)
    eduPosted2.setProperty(time.label, "20.3.2013")
    eduPosted2.setProperty(url.label, "https://twitter.com/eparejatobes/status/314353912276738048")

    val eduTweet3 = g.addVertexWithLabel(Tweet.label) 
    eduTweet3.setProperty(text.label, 
      """just read "Categories for synchrony and asynchrony" http://bit.ly/15sBf22  nice paper with conditions for completeness of Kleisli cats""")

    val eduPosted3 = edu.addEdge(Posted.label, eduTweet3)
    eduPosted3.setProperty(time.label, "19.2.2013")
    eduPosted3.setProperty(url.label, "https://twitter.com/eparejatobes/status/303939214423236608")

    val eduTweet4 = g.addVertexWithLabel(Tweet.label) 
    eduTweet4.setProperty(text.label, 
      """back to twitter :)""")

    val eduPosted4 = edu.addEdge(Posted.label, eduTweet4)
    eduPosted4.setProperty(time.label, "13.11.2012")
    eduPosted4.setProperty(url.label, "https://twitter.com/eparejatobes/status/268324310614167552")


    val alexeyTweet1 = g.addVertexWithLabel(Tweet.label) 
    alexeyTweet1.setProperty(text.label, 
      """Spend the whole night trying to build Idris from sources. I lost any hope. Cabal is hell "(""")

    val alexeyPosted1 = alexey.addEdge(Posted.label, alexeyTweet1)
    alexeyPosted1.setProperty(time.label, "15.2.2014")
    alexeyPosted1.setProperty(url.label, "https://twitter.com/laughedelic/status/444717461150388224")

    val alexeyTweet2 = g.addVertexWithLabel(Tweet.label) 
    alexeyTweet2.setProperty(text.label, 
      """What I don’t like about going to bed at 8am is that the next morning there are no interesting news/notifications anywhere "/ boring morning""")

    val alexeyPosted2 = alexey.addEdge(Posted.label, alexeyTweet2)
    alexeyPosted2.setProperty(time.label, "7.2.2014")
    alexeyPosted2.setProperty(url.label, "https://twitter.com/laughedelic/status/441939905963622400")


    val kimTweet1 = g.addVertexWithLabel(Tweet.label) 
    kimTweet1.setProperty(text.label, 
      """больше недели искал нормальную jQuery-библиотеку для галереи - в итоге написал свою""")

    val kimPosted1 = kim.addEdge(Posted.label, kimTweet1)
    kimPosted1.setProperty(time.label, "23.2.2012")
    kimPosted1.setProperty(url.label, "https://twitter.com/evdokim/status/172712624931348480")

    val kimTweet2 = g.addVertexWithLabel(Tweet.label) 
    kimTweet2.setProperty(text.label, 
      """java.util.concurrent вообще круть!)))""")

    val kimPosted2 = kim.addEdge(Posted.label, kimTweet2)
    kimPosted2.setProperty(time.label, "7.7.2011")
    kimPosted2.setProperty(url.label, "https://twitter.com/evdokim/status/88926033750929409")

    val kimTweet3 = g.addVertexWithLabel(Tweet.label) 
    kimTweet3.setProperty(text.label, 
      """в раю все ездят на фиксах и смотрят арт-хаус @ ЛХ""")

    val kimPosted3 = kim.addEdge(Posted.label, kimTweet3)
    kimPosted3.setProperty(time.label, "22.6.2011")
    kimPosted3.setProperty(url.label, "https://twitter.com/evdokim/status/83373880454025216")

  }

  // Reusing the graph if possible, else cleaning the directory and creating graph
  override def beforeAll() {
    g = TitanFactory.open("berkeleyje:" + graphLocation.getAbsolutePath)
    // checking that the graph is there:
    // FIXME: it doesn't reuse the graph
    if (g.getManagementSystem.containsRelationType("posted")) { 
      println("Reusing Titan graph")
    } else {
      def cleanDir(f: File) {
        if (f.isDirectory) f.listFiles.foreach(cleanDir(_))
        else { println(f.toString); f.delete }
      }
      cleanDir(graphLocation)
      g = TitanFactory.open("berkeleyje:" + graphLocation.getAbsolutePath)
      createTitanTwitterInstance(g)
      println("Created Titan graph")
    }
  }

  override def afterAll() {
    if(g != null) {
      g.shutdown
      println("Shutdown Titan graph")
    }
  }

  object TestContext {

    implicit class graphOps(tg: TitanGraph) {
      // just a shortcut
      def retrieve[V <: AnyTitanVertex, P <: AnyProperty](v: V)(p: P)(pval: RawOf[P]): ValueOf[V] = {
        v( tg.getVertices(p.label, pval).iterator.next.asInstanceOf[com.thinkaurelius.titan.core.TitanVertex] )
      }
    } 

    val edu = g.retrieve(user)(name)("@eparejatobes")
    val laughedelic = g.retrieve(user)(name)("@laughedelic")
    val kim = g.retrieve(user)(name)("@evdokim")

    val twt = g.retrieve(tweet)(text)("back to twitter :)")

  }
  import TestContext._

  // checks existence and arity
  def checkEdgeLabel[ET <: AnyEdgeType](mgmt: TitanManagement, et: ET) = {

    assert{ mgmt.containsRelationType(et.label) }

    assertResult(multiplicity(et)) {
      mgmt.getEdgeLabel(et.label).getMultiplicity
    }
  }

  // checks existence and dataType
  def checkPropertyKey[P <: AnyProperty](mgmt: TitanManagement, p: P) = {

    assert{ mgmt.containsRelationType(p.label) }

    import scala.reflect._
    assertResult(p.classTag.runtimeClass.asInstanceOf[Class[RawOf[P]]]) {
      mgmt.getPropertyKey(p.label).getDataType
    }
  }

  test("check schema keys/labels") {
    val mgmt = g.getManagementSystem

    checkPropertyKey(mgmt, name)
    checkPropertyKey(mgmt, age)
    checkPropertyKey(mgmt, text)
    checkPropertyKey(mgmt, url)
    checkPropertyKey(mgmt, time)

    checkEdgeLabel(mgmt, Posted)
    checkEdgeLabel(mgmt, Follows)

    assert{ mgmt.containsVertexLabel(User.label) }
    assert{ mgmt.containsVertexLabel(Tweet.label) }

    mgmt.commit
  }

  test("get vertex property") {

    // pure blueprints with string keys and casting: 
    assert(edu.raw.getProperty[Int]("age") == 95)
    // safe and nifty: 
    assert(edu.get(age).raw == 95)
    // and it's the same thing
    assert(edu.raw.getProperty[Int]("age") == edu.get(age).raw)
  }

  test("get OUTgoing edges and their property") {

    assertResult(List(time("15.2.2014"), time("7.2.2014"))) {
      laughedelic out posted map { _ get time }
    }

  }

  test("get INcoming edges and their property") {

    assertResult(Some(time("13.11.2012"))) {
      twt in posted map { _ get time }
    }
  }

  test("get target/source vertices of incoming/outgoing edges") {

    assertResult( Some(name("@eparejatobes")) ) {
      twt in posted map { _.src } map { _ get name }
    }

    assert {
      (edu out follows map { _.tgt }) ==
      (edu  in follows map { _.src })
    }

    assertResult( Set(name("@eparejatobes"), name("@laughedelic"), name("@evdokim")) ) {
      (edu out follows 
        map { _.tgt } 
        flatMap { _ out follows } 
        map { _.tgt } 
        map { _ get name }
      ).toSet
    }

  }

  // test("out + target vs. outV") {

  //   assert {
  //     (edu out  follows map { _.tgt }) ==
  //     (edu outV follows)
  //   }
  // }

}
