package ohnosequences.scarph.test.titan

import com.thinkaurelius.titan.core._
import com.thinkaurelius.titan.core.Multiplicity._
import com.thinkaurelius.titan.core.schema._

import com.tinkerpop.blueprints.Edge
import com.tinkerpop.blueprints.Vertex

import java.io.File

import ohnosequences.cosas._

import ohnosequences.scarph._
import ohnosequences.scarph.test._, TwitterSchema._
import ohnosequences.scarph.impl.titan._

class TitanSuite extends org.scalatest.FunSuite with org.scalatest.BeforeAndAfterAll {

  val graphLocation = new File("/tmp/titanTest")
  var g: TitanGraph = null

  def createTitanTwitterInstance(g: TitanGraph): Unit = {

    // g.createSchema(TwitterSchema.schemaType)

    /* Adding things */
    val edu = g.addVertexWithLabel(user.label)
    edu.setProperty(name.label, "@eparejatobes")
    edu.setProperty(age.label, 95)

    val alexey = g.addVertexWithLabel(user.label)
    alexey.setProperty(name.label, "@laughedelic")
    alexey.setProperty(age.label, 5)

    val kim = g.addVertexWithLabel(user.label)
    kim.setProperty(name.label, "@evdokim")
    kim.setProperty(age.label, 22)


    // everybody follows everybody — boring "/
    edu.addEdge(follows.label, alexey)
    edu.addEdge(follows.label, kim)

    alexey.addEdge(follows.label, edu)
    alexey.addEdge(follows.label, kim)

    kim.addEdge(follows.label, alexey)
    kim.addEdge(follows.label, edu)


    val eduTweet1 = g.addVertexWithLabel(tweet.label)
    eduTweet1.setProperty(text.label,
      """#programming languages should be explicit (= code) about what they are and their needs: syntax, underlying platform, semantics""")

    val eduPosted1 = edu.addEdge(posted.label, eduTweet1)
    eduPosted1.setProperty(time.label, "27.10.2013")
    eduPosted1.setProperty(url.label, "https://twitter.com/eparejatobes/status/394430900051927041")

    val eduTweet2 = g.addVertexWithLabel(tweet.label)
    eduTweet2.setProperty(text.label,
      """type definitions in titan #graphdb https://github.com/thinkaurelius/titan/wiki/Type-Definition-Overview … killer feature! example of how type info can be used to get better performance""")

    val eduPosted2 = edu.addEdge(posted.label, eduTweet2)
    eduPosted2.setProperty(time.label, "20.3.2013")
    eduPosted2.setProperty(url.label, "https://twitter.com/eparejatobes/status/314353912276738048")

    val eduTweet3 = g.addVertexWithLabel(tweet.label)
    eduTweet3.setProperty(text.label,
      """just read "Categories for synchrony and asynchrony" http://bit.ly/15sBf22  nice paper with conditions for completeness of Kleisli cats""")

    val eduPosted3 = edu.addEdge(posted.label, eduTweet3)
    eduPosted3.setProperty(time.label, "19.2.2013")
    eduPosted3.setProperty(url.label, "https://twitter.com/eparejatobes/status/303939214423236608")

    val eduTweet4 = g.addVertexWithLabel(tweet.label)
    eduTweet4.setProperty(text.label,
      """back to twitter :)""")

    val eduPosted4 = edu.addEdge(posted.label, eduTweet4)
    eduPosted4.setProperty(time.label, "13.11.2012")
    eduPosted4.setProperty(url.label, "https://twitter.com/eparejatobes/status/268324310614167552")


    val alexeyTweet1 = g.addVertexWithLabel(tweet.label)
    alexeyTweet1.setProperty(text.label,
      """Spend the whole night trying to build Idris from sources. I lost any hope. Cabal is hell "(""")

    val alexeyPosted1 = alexey.addEdge(posted.label, alexeyTweet1)
    alexeyPosted1.setProperty(time.label, "15.2.2014")
    alexeyPosted1.setProperty(url.label, "https://twitter.com/laughedelic/status/444717461150388224")

    val alexeyTweet2 = g.addVertexWithLabel(tweet.label)
    alexeyTweet2.setProperty(text.label,
      """What I don’t like about going to bed at 8am is that the next morning there are no interesting news/notifications anywhere "/ boring morning""")

    val alexeyPosted2 = alexey.addEdge(posted.label, alexeyTweet2)
    alexeyPosted2.setProperty(time.label, "7.2.2014")
    alexeyPosted2.setProperty(url.label, "https://twitter.com/laughedelic/status/441939905963622400")


    val kimTweet1 = g.addVertexWithLabel(tweet.label)
    kimTweet1.setProperty(text.label,
      """больше недели искал нормальную jQuery-библиотеку для галереи - в итоге написал свою""")

    val kimPosted1 = kim.addEdge(posted.label, kimTweet1)
    kimPosted1.setProperty(time.label, "23.2.2012")
    kimPosted1.setProperty(url.label, "https://twitter.com/evdokim/status/172712624931348480")

    val kimTweet2 = g.addVertexWithLabel(tweet.label)
    kimTweet2.setProperty(text.label,
      """java.util.concurrent вообще круть!)))""")

    val kimPosted2 = kim.addEdge(posted.label, kimTweet2)
    kimPosted2.setProperty(time.label, "7.7.2011")
    kimPosted2.setProperty(url.label, "https://twitter.com/evdokim/status/88926033750929409")

    val kimTweet3 = g.addVertexWithLabel(tweet.label)
    kimTweet3.setProperty(text.label,
      """в раю все ездят на фиксах и смотрят арт-хаус @ ЛХ""")

    val kimPosted3 = kim.addEdge(posted.label, kimTweet3)
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
    // val impl = TwitterImpl(g); import impl._

    // quering vertices
    // val edu = user.query(user ? (name === "@eparejatobes")).head
    // val alexey = user.query(user ? (name === "@laughedelic")).head
    // val kim = user.query(user ? (name === "@evdokim")).head
    // val twt = tweet.query(tweet ? (text === "back to twitter :)")).head

    // // quering edges
    // val post = posted.query(posted ? (time === "13.11.2012")).head
  }

  // checks existence and arity
  def checkEdgeLabel[ET <: AnyEdgeType](mgmt: TitanManagement, et: ET) = {

    assert{ mgmt.containsRelationType(et.label) }

    // assertResult(multi(et)) {
    //   mgmt.getEdgeLabel(et.label).getMultiplicity
    // }
  }

  // checks existence and dataType
  def checkPropertyKey[P <: AnyProperty](mgmt: TitanManagement, p: P) = {

    assert{ mgmt.containsRelationType(p.label) }

    // import scala.reflect._
    // assertResult(p.classTag.runtimeClass.asInstanceOf[Class[P#Raw]]) {
    //   mgmt.getPropertyKey(p.label).getDataType
    // }
  }

  // checks existence, type and the indexed property
  // def checkIndex[Ix <: AnyCompositeIndex](mgmt: TitanManagement, ix: Ix) = {

  //   assert{ mgmt.containsGraphIndex(ix.label) }

  //   val index = mgmt.getGraphIndex(ix.label)
  //   // TODO: check for mixed indexes and any other stuff
  //   assert{ index.isCompositeIndex }
  //   assert{ index.getFieldKeys.toSet == Set(mgmt.getPropertyKey(ix.property.label)) }
  // }

  // TODO: make it a graph op: checkSchema
  // test("check schema keys/labels") {
  //   // import TestContext._, impl._

  //   val mgmt = g.getManagementSystem

  //   checkPropertyKey(mgmt, name)
  //   checkPropertyKey(mgmt, age)
  //   checkPropertyKey(mgmt, text)
  //   checkPropertyKey(mgmt, url)
  //   checkPropertyKey(mgmt, time)

  //   checkEdgeLabel(mgmt, posted)
  //   checkEdgeLabel(mgmt, follows)

  //   assert{ mgmt.containsVertexLabel(user.label) }
  //   assert{ mgmt.containsVertexLabel(tweet.label) }

  //   checkIndex(mgmt, UserNameIx)
  //   checkIndex(mgmt, TweetTextIx)
  //   checkIndex(mgmt, PostedTimeIx)

  //   mgmt.commit
  // }

  test("check what we got from the index queries") {
    import TestContext._, impl._

    // just shortcuts
    implicit class graphOps(tg: TitanGraph) {
      def vertex[V <: AnyVertexType, P <: AnyProp](v: V)(p: P)(pval: P#Raw): TitanVertex LabeledBy V = {
        v( tg.getVertices(p.label, pval).iterator.next.asInstanceOf[TitanVertex] )
      }
      def edge[E <: AnyEdgeType, P <: AnyProp](e: E)(p: P)(pval: P#Raw): TitanEdge LabeledBy E = {
        e( tg.getEdges(p.label, pval).iterator.next.asInstanceOf[TitanEdge] )
      }
    }

    // assert{ edu == graph.vertex(user)(name)("@eparejatobes") }
    // assert{ alexey == graph.vertex(user)(name)("@laughedelic") }
    // assert{ kim == graph.vertex(user)(name)("@evdokim") }
    // assert{ twt == graph.vertex(tweet)(text)("back to twitter :)") }

    // assert{ post == graph.edge(posted)(time)("13.11.2012") }

    val edu = g.vertex(user)(name)("@eparejatobes")
    val alexey = g.vertex(user)(name)("@laughedelic")
    val kim = g.vertex(user)(name)("@evdokim")
    val twt = g.vertex(tweet)(text)("back to twitter :)")
    val post = g.edge(posted)(time)("13.11.2012")

    /* Evaluating steps: */
    assert{ get(name).evalOn(edu) == name("@eparejatobes") }
    // assert{ GetSource(posted).evalOn(post) == edu }

//     /* Composing steps: */
//     val posterName = GetSource(posted) >=> GetProperty(name)

//     import AnyPath._, AnyVertexType._, AnyEdgeType._
//     // oh yeah

//     val p = tweet in posted
//     val zzz = p map posted.src

//     val pp = tweet in posted map posted.src
    
    
//     assert{ posterName.evalOn(post) == name("@eparejatobes") }

//     // this query returns a list of 4 Edus, so we comare it as a set
//     assert{ (GetOutEdges(posted) >=> posterName).evalOn(edu).toSet == Set(name("@eparejatobes")) }

//     // testing evaluation of getInVertices as one step
//     // FIXME: it works ONLY if we have eval for exactly GetInVertices (should work without it too)
//     assert{ (GetInVertices(posted) >=> GetProperty(name)).evalOn(twt) == name("@eparejatobes") }
  }

//   // test("get vertex property") {
//   //   import TestContext._, impl._

//   //   // pure blueprints with string keys and casting:
//   //   assert(edu.raw.getProperty[Int]("age") == 95)
//   //   // safe and nifty:
//   //   assert(edu.get(age).raw == 95)
//   //   // and it's the same thing
//   //   assert(edu.raw.getProperty[Int]("age") == edu.get(age).raw)
//   // }

//   // test("get OUTgoing edges and their property") {
//   //   import TestContext._, impl._

//   //   assertResult(List(time("15.2.2014"), time("7.2.2014"))) {
//   //     alexey out posted map { _ get time }
//   //   }

//   // }

//   // test("get INcoming edges and their property") {
//   //   import TestContext._, impl._

//   //   assertResult(Some(time("13.11.2012"))) {
//   //     twt in posted map { _ get time }
//   //   }
//   // }

//   // test("get target/source vertices of incoming/outgoing edges") {
//   //   import TestContext._, impl._

//   //   assert{ post.src == edu }
//   //   assert{ post.tgt == twt }

//   //   assertResult( Some(name("@eparejatobes")) ) {
//   //     twt in posted map { _.src } map { _ get name }
//   //   }

//   //   assert {
//   //     (edu out follows map { _.tgt }) ==
//   //     (edu  in follows map { _.src })
//   //   }

//   //   assertResult( Set(name("@eparejatobes"), name("@laughedelic"), name("@evdokim")) ) {
//   //     (edu out follows
//   //       map { _.tgt }
//   //       flatMap { _ out follows }
//   //       map { _.tgt }
//   //       map { _ get name }
//   //     ).toSet
//   //   }

//   // }

//   // // test("out + target vs. outV") {

//   // //   assert {
//   // //     (edu out  follows map { _.tgt }) ==
//   // //     (edu outV follows)
//   // //   }
//   // // }

}
