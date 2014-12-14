package ohnosequences.scarph.test.titan

import org.scalatest

import ohnosequences.cosas._

import ohnosequences.scarph._, steps._
import ohnosequences.scarph.impl._, titan.schema._, titan.predicates._
import ohnosequences.scarph.test._, Twitter._

import com.thinkaurelius.titan.core._, Multiplicity._
import com.thinkaurelius.titan.core.schema.TitanManagement


trait AnyTitanTestSuite extends scalatest.FunSuite with scalatest.BeforeAndAfterAll {

  val g: TitanGraph = TitanFactory.open("inmemory")

  override def beforeAll() {
    g.createSchema(Twitter.schema)

    // loading data from a prepared GraphSON file
    import com.tinkerpop.blueprints.util.io.graphson._
    GraphSONReader.inputGraph(g, getClass.getResource("/twitter_graph.json").getPath)
  }

  override def afterAll() {
    g.shutdown

    // // NOTE: uncommend if you want to add data to the GraphSON:
    // import com.tinkerpop.blueprints.util.io.graphson._
    // GraphSONWriter.outputGraph(g, "graph_compact.json", GraphSONMode.COMPACT)
  }
}

// class TitanTestSuite extends scalatest.Suites(
//   new TitanSchemaTestSuite,
//   new TitanQueriesTestSuite
// ) with scalatest.SequentialNestedSuiteExecution

//////////////////////////////////////////////////////////////////////////////////////////////////

class TitanTestSuite extends AnyTitanTestSuite {

  // checks existence and arity
  def checkEdgeLabel[ET <: AnyEdgeType](mgmt: TitanManagement, et: ET)
    (implicit multi: EdgeTypeMultiplicity[ET]) = {

    assert{ mgmt.containsRelationType(et.label) }

    assertResult(multi(et)) {
      mgmt.getEdgeLabel(et.label).getMultiplicity
    }
  }

  // checks existence and dataType
  def checkPropertyKey[P <: AnyProp](mgmt: TitanManagement, p: P)
    (implicit cc: scala.reflect.ClassTag[P#Raw]) = {

    assert{ mgmt.containsRelationType(p.label) }

    assertResult(cc.runtimeClass.asInstanceOf[Class[P#Raw]]) {
      mgmt.getPropertyKey(p.label).getDataType
    }
  }

  // checks existence, type and the indexed property
  def checkSimpleIndex[Ix <: AnySimpleIndex](mgmt: TitanManagement, ix: Ix) = {

    assert{ mgmt.containsGraphIndex(ix.label) }

    val index = mgmt.getGraphIndex(ix.label)
    // TODO: check for mixed indexes and any other stuff
    assert{ index.isCompositeIndex }
    assert{ index.getFieldKeys.toSet == Set(mgmt.getPropertyKey(ix.property.label)) }
  }

  // TODO: make it a graph op: checkSchema
  test("check schema keys/labels") {

    val mgmt = g.getManagementSystem

    checkPropertyKey(mgmt, name)
    checkPropertyKey(mgmt, age)
    checkPropertyKey(mgmt, text)
    checkPropertyKey(mgmt, url)
    checkPropertyKey(mgmt, time)

    checkEdgeLabel(mgmt, posted)
    checkEdgeLabel(mgmt, follows)

    assert{ mgmt.containsVertexLabel(user.label) }
    assert{ mgmt.containsVertexLabel(tweet.label) }

    checkSimpleIndex(mgmt, userByName)
    checkSimpleIndex(mgmt, tweetByText)
    checkSimpleIndex(mgmt, postedByTime)

    mgmt.commit
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////

  import syntax.conditions._
  import syntax.predicates._

  object TestContext {
    val evals = impl.titan.evals(g); import evals._

    // predicates for quering vertices
    val askEdu = user ? (name === "@eparejatobes")
    val askAlexey = user ? (name === "@laughedelic")
    val askKim = user ? (name === "@evdokim")

    val askTweet = tweet ? (text === "back to twitter :)")

    // predicates for quering edges
    val askPost = posted ? (time === "13.11.2012")

    // low level querying:
    implicit class graphOps(tg: TitanGraph) {
      def vertex[V <: AnyVertexType, P <: AnyProp](v: V)(p: P)(pval: P#Raw): TitanVertex LabeledBy V = {
        ExactlyOne(v)( tg.getVertices(p.label, pval).iterator.next.asInstanceOf[TitanVertex] )
      }
      def edge[E <: AnyEdgeType, P <: AnyProp](e: E)(p: P)(pval: P#Raw): TitanEdge LabeledBy E = {
        ExactlyOne(e)( tg.getEdges(p.label, pval).iterator.next.asInstanceOf[TitanEdge] )
      }
    }

    // val edu = g.vertex(user)(name)("@eparejatobes")
    val alexey = g.vertex(user)(name)("@laughedelic")
    val kim = g.vertex(user)(name)("@evdokim")
    // val twt = g.vertex(tweet)(text)("back to twitter :)")
    // val post = g.edge(posted)(time)("13.11.2012")

    // prepared test queries (they can be reused for different tests)
    val userName = Get(name)
    val postAuthor = Source(posted)
    val postAuthorName = postAuthor >=> userName
    // val tweetAuthorName = InE(any(posted)) >=> postAuthorName

    val edu  = ExactlyOne(user)( Query(user).evalOn(askEdu).value.head )
    val post = ExactlyOne(posted)( Query(posted).evalOn(askPost).value.head )
    val twt  = ExactlyOne(tweet)( Query(tweet).evalOn(askTweet).value.head )
  }

  test("check what we got from the index queries") {
    import TestContext._, evals._

    // // assert{ edu == graph.vertex(user)(name)("@eparejatobes") }
    // // assert{ alexey == graph.vertex(user)(name)("@laughedelic") }
    // // assert{ kim == graph.vertex(user)(name)("@evdokim") }
    // // assert{ twt == graph.vertex(tweet)(text)("back to twitter :)") }
    // // assert{ post == graph.edge(posted)(time)("13.11.2012") }

    // /* Evaluating steps: */
    // assert{ Get(name).evalOn(edu) == name("@eparejatobes") }
    // assert{ Source(posted).evalOn(post) == edu }

    // /* Composing steps: */
    // val posterName = Source(posted) >=> Get(name)
    
    // assert{ posterName.evalOn(post) == name("@eparejatobes") }


    // import shapeless._, poly._

    assert{ Query(user).evalOn(user ? (name === "@eparejatobes") and (age === 5)).value == Stream() }
    assert{ Query(user).evalOn(user ? (name === "@eparejatobes") and (age === 95)).value == Stream(edu.value) }

    // assert{ (Query(user) >=> Get(age)).evalOn(user ? (age < 80)).toSet == Set(age(22), age(5)) }
    // assert{ (Query(user) >=> Get(age)).evalOn(user ? (age < 80) and (age > 10)).toSet == Set(age(22)) }

    assert{ userName.evalOn(edu) == name("@eparejatobes") }
    assert{ postAuthor.evalOn(post) == edu }

    assert{ postAuthorName.evalOn(post) == name("@eparejatobes") }

    // this query returns a list of 4 Edus, so we comare it as a set
    // assert{ (OutE(any(posted)) >=> postAuthorName).evalOn(edu).toSet == Set(name("@eparejatobes")) }

    // assert{ tweetAuthorName.evalOn(twt) == name("@eparejatobes") }
  }

  test("cool queries dsl") {
    import TestContext._, evals._
    import syntax.steps._

    // element op:
    val userName = user.get(name)
    assert{ userName.evalOn(edu) == name("@eparejatobes") }

    // edge op:
    val posterName = posted.source.get(name)
    // assert{ posterName.evalOn(post) == name("@eparejatobes") }

    // vertex op:
    val friendsPosts = user.outE(any(follows)).target.outE(any(posted)).target

    // testing vertex query
    val vertexQuery = user.outE(posted ? (time === "27.10.2013")).get(url)
    // assert{ vertexQuery.evalOn(edu) == Stream(url("https://twitter.com/eparejatobes/status/394430900051927041")) }
  }

  test("evaluating MapOver") {
    import TestContext._, evals._

    // option functor instance:
    import scalaz.std._, option._, list._

    assertResult( OneOrNone(user)(Option("@eparejatobes")) ){ 
      MapOver(Get(name), OneOrNone).evalOn( 
        OneOrNone(user)(Option(edu.value))
      )
    }

    assertResult( ManyOrNone(OneOrNone(user))(List(Option("@eparejatobes"))) ){ 
      MapOver(MapOver(Get(name), OneOrNone), ManyOrNone).evalOn( 
        ManyOrNone(OneOrNone(user))(List(Option(edu.value)))
      )
    }

  }

  // test("get vertex property") {
  //   import TestContext._, impl._

  //   // pure blueprints with string keys and casting:
  //   assert(edu.raw.getProperty[Int]("age") == 95)
  //   // safe and nifty:
  //   assert(edu.get(age).raw == 95)
  //   // and it's the same thing
  //   assert(edu.raw.getProperty[Int]("age") == edu.get(age).raw)
  // }

  // test("get OUTgoing edges and their property") {
  //   import TestContext._, impl._

  //   assertResult(List(time("15.2.2014"), time("7.2.2014"))) {
  //     alexey out posted map { _ get time }
  //   }

  // }

  // test("get INcoming edges and their property") {
  //   import TestContext._, impl._

  //   assertResult(Some(time("13.11.2012"))) {
  //     twt in posted map { _ get time }
  //   }
  // }

  // test("get target/source vertices of incoming/outgoing edges") {
  //   import TestContext._, impl._

  //   assert{ post.src == edu }
  //   assert{ post.tgt == twt }

  //   assertResult( Some(name("@eparejatobes")) ) {
  //     twt in posted map { _.src } map { _ get name }
  //   }

  //   assert {
  //     (edu out follows map { _.tgt }) ==
  //     (edu  in follows map { _.src })
  //   }

  //   assertResult( Set(name("@eparejatobes"), name("@laughedelic"), name("@evdokim")) ) {
  //     (edu out follows
  //       map { _.tgt }
  //       flatMap { _ out follows }
  //       map { _.tgt }
  //       map { _ get name }
  //     ).toSet
  //   }

  // }

  // // test("out + target vs. outV") {

  // //   assert {
  // //     (edu out  follows map { _.tgt }) ==
  // //     (edu outV follows)
  // //   }
  // // }
}
