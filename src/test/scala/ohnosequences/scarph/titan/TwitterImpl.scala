package ohnosequences.scarph.test.titan

import ohnosequences.pointless._, AnyTypeSet._
import ohnosequences.scarph._ //, impl.titan._
import ohnosequences.scarph.test._, TwitterSchema._
// import com.thinkaurelius.titan.core.{ TitanGraph => TGraph }

case object TitanTwitter { //extends Schema(TwitterSchema.schemaType) {

  // implementation of steps evaluation for some random types:
  // case class StringVertex(label: String)
  // case class CaseEdge(source: StringVertex, target: StringVertex)
  
  // implicit def evalGetSource[
  //   E <: AnyEdgeType
  // ]:  EvalStep[CaseEdge, GetSource[E], StringVertex] =
  // new EvalStep[CaseEdge, GetSource[E], StringVertex] {
  //   def apply(in: In, s: Traversal): Out = new LabeledBy[StringVertex, E#Source#T](in.value.source)
  // }

  // implicit def evalGetProperty[
  //   P <: AnyProp
  // ]:  EvalStep[StringVertex, GetProperty[P], P#Raw] =
  // new EvalStep[StringVertex, GetProperty[P], P#Raw] {
  //   def apply(in: In, s: Traversal): Out = ??? //new LabeledBy[P#Raw, P](in.value.)
  // }

  // implicit def evalGetOutEdges[
  //   E <: AnyEdgeType
  // ]:  EvalStep[StringVertex, GetOutEdges[E], CaseEdge] =
  // new EvalStep[StringVertex, GetOutEdges[E], CaseEdge] {
  //   def apply(in: In, s: Traversal): Out = ??? //new LabeledBy[P#Raw, P](in.value.)
  // }

  // implicit case object user extends TitanVertex(this, User)
  // implicit case object tweet extends TitanVertex(this, Tweet)

  // implicit case object posted extends TitanEdge(this, Posted)
  // implicit case object follows extends TitanEdge(this, Follows)

  // implicit case object name_ extends Implementation[this.type, name.type] { val tpe = name }

}

// class TraversalsTest extends org.scalatest.FunSuite {
//   import TwitterSchema._
//   import AnyTraversal._
//   import TitanTwitter._

//   // in Gremlin it would look like: `<likes>.source.outE(follows).target.age`
//   val t1 = GetSource(liked) >=> GetOutEdges(follows) >=> GetTarget(follows) >=> GetProperty(age)

//   val bob = StringVertex("Bob")
//   val ann = StringVertex("Ann")

//   test("evaluating queries") {
//     assert{ GetSource(follows).eval(follows(CaseEdge(bob, ann))) == user(bob) }

//     // (GetSource(follows) >=> GetProperty(name)).eval(follows(CaseEdge(bob, ann)))

//     // (GetOutEdges(follows) >=> GetSource(follows) >=> GetProperty(name)).eval(user(bob))
//   }
// }
