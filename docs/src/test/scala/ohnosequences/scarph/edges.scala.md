
```scala
package ohnosequences.scarph.test

import ohnosequences.scarph._, ops.default._

object edges {

  import edgeTypes._
  import vertexTypes._
  
  import vertices._
  import properties._

  case class MemberOfImpl(
    val source: user.Rep,
    val target: org.Rep
  )(
    val isPublic: Boolean,
    val since: Int,
    val validUntil: Int
  )

  case object memberOf extends Edge(user, MemberOf, org) {
    
    type Raw = MemberOfImpl

    // implicit case object sourceGetter extends GetSource[user.type](user) {
    //   def apply(rep: memberOf.Rep) = rep.source
    // }

    implicit object readSince extends PropertyGetter(since) {
      def apply(rep: Rep) = rep.since
    }
    implicit object readValidUntil extends PropertyGetter(validUntil) {
      def apply(rep: Rep) = rep.validUntil
    }
  }

  implicit val memberSourceGetter = new memberOf.GetSource {
      def apply(rep: memberOf.Rep) = rep.source
    }

  case class OwnsImpl(
    val source: user.Rep,
    val target: org.Rep
  )
  object owns extends Edge(user, Owns, org) { self =>
    
    type Raw = OwnsImpl

    implicit val sourceGetter = new GetSource { def apply(rep: self.Rep) = rep.source }
    implicit val targetGetter = new GetTarget { def apply(rep: self.Rep) = rep.target }
  }

}

class EdgeSuite extends org.scalatest.FunSuite {

  import edges._  
  import edgeTypes._

  import vertexTypes._
  import vertices._
  import properties._

  test("retrieve edge's sourde-edge-target") {

    import edges.memberOf._
    import edgeTypes.MemberOf._
    val u = user ->> UserImpl(id = "1ad3a34df", name = "Robustiano Satrústegui", since = 2349965)
    val oracle: org.Rep = org ->> UserImpl(id = "NYSE:ORCL", name = "Orcale Inc.", since = 1977)

    val m = memberOf ->> MemberOfImpl(source = u, target = oracle)(isPublic = true, since = 2349965, validUntil = 38724987)

    assert((m source) === u)

    // I think this is a bug; this import shouldn't be needed
    import vertexTypes.User._
    assert(
      (m.source get id) === "1ad3a34df"
    )

    // val followers_ids = (user out follows) map { _ get id }

```

Adding target getter externally:

```scala
    implicit val targetGetter = new GetTarget { def apply(rep: memberOf.Rep) = rep.target }
    assert(m.target === oracle)
```

Getting edge properties

```scala
    assert(m.get(since) === 2349965)
    assert(m.get(validUntil) === 38724987)

    // NOTE: this works in scala-2.10, but not in scala-2.11:
    // implicit val weForgotToImportIt = memberOf.tpe has isPublic
    // these two work in both:
    // implicit val weForgotToImportIt = (memberOf.tpe: memberOf.Tpe) has isPublic
    implicit val weForgotToImportIt = MemberOf has isPublic

    implicit object readIsPublic extends PropertyGetter(isPublic) {
      def apply(rep: memberOf.Rep) = rep.isPublic
    }
    assert(m.get(isPublic) === true)
```

More vartices and edges

```scala
    val bob = user ->> UserImpl(id = "-1", name = "Bob", since = 0)
    val martin = user ->> UserImpl(id = "0", name = "Martin Odersky", since = 2011)
    val paulp = user ->> UserImpl(id = "1", name = "Paul Phillips", since = 2011)

    val typesafe = org ->> UserImpl(id = "martin123", name = "Typesafe Inc.", since = 2011)
```

Just a static list of all `memberOf` edges

```scala
    val members: List[memberOf.Rep] = List(
      memberOf ->> MemberOfImpl(u, oracle)(false, 0, 0),
      // every company has some bob
      memberOf ->> MemberOfImpl(bob, oracle)(true, 0, 0),
      memberOf ->> MemberOfImpl(bob, typesafe)(false, 0, 0),
      memberOf ->> MemberOfImpl(martin, typesafe)(true, 0, 0)
      // memberOf ->> MemberOfImpl(paulp, typesafe)(true, 0, 0) // not anymore
    )
```

Retrieving edge

```scala
    implicit def retrieveMemberOf(e: memberOf.type) = new user.GetOutEdge(memberOf) {
      def apply(rep: user.Rep) = members filter { _.source == rep }
    }
    assert(bob.out(memberOf).map(_.target) === List(oracle, typesafe))


    val owners: List[owns.Rep] = List(
      owns ->> OwnsImpl(u, oracle), // together with bob of course
      owns ->> OwnsImpl(bob, oracle),
      owns ->> OwnsImpl(martin, typesafe)
    )

    implicit def retrieveOwns(e: owns.type) = new user.GetOutEdge(owns) {
      def apply(rep: user.Rep) = owners find { _.source == rep }
    }
    assert(((martin out owns) map (_.target)) === Some(typesafe))
    // cool, martin owns some typesafe org

  }
}

```


------

### Index

+ src
  + test
    + scala
      + ohnosequences
        + scarph
          + [properties.scala][test/scala/ohnosequences/scarph/properties.scala]
          + [edges.scala][test/scala/ohnosequences/scarph/edges.scala]
          + [vertices.scala][test/scala/ohnosequences/scarph/vertices.scala]
          + titan
            + [expressions.scala][test/scala/ohnosequences/scarph/titan/expressions.scala]
            + [TitanSchemaTest.scala][test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]
            + [TitanOtherOpsTest.scala][test/scala/ohnosequences/scarph/titan/TitanOtherOpsTest.scala]
            + [TitanGodsTest.scala][test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]
            + [godsImplementation.scala][test/scala/ohnosequences/scarph/titan/godsImplementation.scala]
            + [godsSchema.scala][test/scala/ohnosequences/scarph/titan/godsSchema.scala]
          + [vertexTypes.scala][test/scala/ohnosequences/scarph/vertexTypes.scala]
          + [edgeTypes.scala][test/scala/ohnosequences/scarph/edgeTypes.scala]
  + main
    + scala
      + ohnosequences
        + scarph
          + [Expressions.scala][main/scala/ohnosequences/scarph/Expressions.scala]
          + ops
            + [typelevel.scala][main/scala/ohnosequences/scarph/ops/typelevel.scala]
            + [default.scala][main/scala/ohnosequences/scarph/ops/default.scala]
          + [Denotation.scala][main/scala/ohnosequences/scarph/Denotation.scala]
          + [EdgeType.scala][main/scala/ohnosequences/scarph/EdgeType.scala]
          + [VertexType.scala][main/scala/ohnosequences/scarph/VertexType.scala]
          + [Vertex.scala][main/scala/ohnosequences/scarph/Vertex.scala]
          + [Edge.scala][main/scala/ohnosequences/scarph/Edge.scala]
          + titan
            + [TitanImplementation.scala][main/scala/ohnosequences/scarph/titan/TitanImplementation.scala]
            + [TEdge.scala][main/scala/ohnosequences/scarph/titan/TEdge.scala]
            + [TVertex.scala][main/scala/ohnosequences/scarph/titan/TVertex.scala]
            + [TSchema.scala][main/scala/ohnosequences/scarph/titan/TSchema.scala]
          + [Property.scala][main/scala/ohnosequences/scarph/Property.scala]
          + [GraphSchema.scala][main/scala/ohnosequences/scarph/GraphSchema.scala]

[test/scala/ohnosequences/scarph/properties.scala]: properties.scala.md
[test/scala/ohnosequences/scarph/edges.scala]: edges.scala.md
[test/scala/ohnosequences/scarph/vertices.scala]: vertices.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: titan/expressions.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: titan/TitanSchemaTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanOtherOpsTest.scala]: titan/TitanOtherOpsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/vertexTypes.scala]: vertexTypes.scala.md
[test/scala/ohnosequences/scarph/edgeTypes.scala]: edgeTypes.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: ../../../../main/scala/ohnosequences/scarph/Expressions.scala.md
[main/scala/ohnosequences/scarph/ops/typelevel.scala]: ../../../../main/scala/ohnosequences/scarph/ops/typelevel.scala.md
[main/scala/ohnosequences/scarph/ops/default.scala]: ../../../../main/scala/ohnosequences/scarph/ops/default.scala.md
[main/scala/ohnosequences/scarph/Denotation.scala]: ../../../../main/scala/ohnosequences/scarph/Denotation.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: ../../../../main/scala/ohnosequences/scarph/EdgeType.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: ../../../../main/scala/ohnosequences/scarph/VertexType.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: ../../../../main/scala/ohnosequences/scarph/Vertex.scala.md
[main/scala/ohnosequences/scarph/Edge.scala]: ../../../../main/scala/ohnosequences/scarph/Edge.scala.md
[main/scala/ohnosequences/scarph/titan/TitanImplementation.scala]: ../../../../main/scala/ohnosequences/scarph/titan/TitanImplementation.scala.md
[main/scala/ohnosequences/scarph/titan/TEdge.scala]: ../../../../main/scala/ohnosequences/scarph/titan/TEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TVertex.scala]: ../../../../main/scala/ohnosequences/scarph/titan/TVertex.scala.md
[main/scala/ohnosequences/scarph/titan/TSchema.scala]: ../../../../main/scala/ohnosequences/scarph/titan/TSchema.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: ../../../../main/scala/ohnosequences/scarph/Property.scala.md
[main/scala/ohnosequences/scarph/GraphSchema.scala]: ../../../../main/scala/ohnosequences/scarph/GraphSchema.scala.md