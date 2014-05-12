
```scala
package ohnosequences.scarph.test

import ohnosequences.scarph._

object edges {

  import edgeTypes._
  import vertexTypes._
  
  import vertices._
  import properties._

  case class MemberOfImpl(
    val source: user.TaggedRep,
    val target: org.TaggedRep
  )(
    val isPublic: Boolean,
    val since: Int,
    val validUntil: Int
  )

  case object memberOf extends Edge(MemberOf) {
    
    type Rep = MemberOfImpl

    // implicit case object sourceGetter extends GetSource[user.type](user) {
    //   def apply(rep: memberOf.TaggedRep) = rep.source
    // }

    implicit object readSince extends GetProperty(since) {
      def apply(rep: TaggedRep) = rep.since
    }
    implicit object readValidUntil extends GetProperty(validUntil) {
      def apply(rep: TaggedRep) = rep.validUntil
    }
  }

  implicit case object memberSourceGetter extends memberOf.GetSource[user.type](user) {
      def apply(rep: memberOf.TaggedRep) = rep.source
    }

  case class OwnsImpl(
    val source: user.TaggedRep,
    val target: org.TaggedRep
  )
  object owns extends Edge(Owns) { self =>
    type Rep = OwnsImpl

    implicit object sourceGetter extends GetSource[user.type](user) {
      def apply(rep: self.TaggedRep) = rep.source
    }
    implicit object targetGetter extends GetTarget[org.type](org) {
      def apply(rep: self.TaggedRep) = rep.target
    }
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
    val u = user ->> UserImpl(id = "1ad3a34df", name = "Robustiano Satr?stegui", since = 2349965)
    val oracle: org.TaggedRep = org ->> UserImpl(id = "NYSE:ORCL", name = "Orcale Inc.", since = 1977)

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
    implicit object targetGetter extends GetTarget[org.type](org) {
      def apply(rep: memberOf.TaggedRep) = rep.target
    }
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

    implicit object readIsPublic extends GetProperty(isPublic) {
      def apply(rep: memberOf.TaggedRep) = rep.isPublic
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
    val members: List[memberOf.TaggedRep] = List(
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
    implicit def retrieveMemberOf(e: memberOf.type) = new user.RetrieveOutEdge(memberOf) {
      def apply(rep: user.TaggedRep) = members filter { _.source == rep }
    }
    assert(bob.out(memberOf).map(_.target) === List(oracle, typesafe))


    val owners: List[owns.TaggedRep] = List(
      owns ->> OwnsImpl(u, oracle), // together with bob of course
      owns ->> OwnsImpl(bob, oracle),
      owns ->> OwnsImpl(martin, typesafe)
    )

    implicit def retrieveOwns(e: owns.type) = new user.RetrieveOutEdge(owns) {
      def apply(rep: user.TaggedRep) = owners find { _.source == rep }
    }
    assert(((martin out owns) map (_.target)) === Some(typesafe))
    // cool, martin owns some typesafe org

  }
}

```


------

### Index

+ src
  + main
    + scala
      + ohnosequences
        + scarph
          + [Denotation.scala][main/scala/ohnosequences/scarph/Denotation.scala]
          + [Edge.scala][main/scala/ohnosequences/scarph/Edge.scala]
          + [EdgeType.scala][main/scala/ohnosequences/scarph/EdgeType.scala]
          + [Expressions.scala][main/scala/ohnosequences/scarph/Expressions.scala]
          + [HasProperties.scala][main/scala/ohnosequences/scarph/HasProperties.scala]
          + [Property.scala][main/scala/ohnosequences/scarph/Property.scala]
          + titan
            + [TEdge.scala][main/scala/ohnosequences/scarph/titan/TEdge.scala]
            + [TitanGraphSchema.scala][main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]
            + [TVertex.scala][main/scala/ohnosequences/scarph/titan/TVertex.scala]
          + [Vertex.scala][main/scala/ohnosequences/scarph/Vertex.scala]
          + [VertexType.scala][main/scala/ohnosequences/scarph/VertexType.scala]
  + test
    + scala
      + ohnosequences
        + scarph
          + [edges.scala][test/scala/ohnosequences/scarph/edges.scala]
          + [edgeTypes.scala][test/scala/ohnosequences/scarph/edgeTypes.scala]
          + [properties.scala][test/scala/ohnosequences/scarph/properties.scala]
          + titan
            + [expressions.scala][test/scala/ohnosequences/scarph/titan/expressions.scala]
            + [godsImplementation.scala][test/scala/ohnosequences/scarph/titan/godsImplementation.scala]
            + [godsSchema.scala][test/scala/ohnosequences/scarph/titan/godsSchema.scala]
            + [TitanGodsTest.scala][test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]
            + [TitanSchemaTest.scala][test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]
          + [vertexTypes.scala][test/scala/ohnosequences/scarph/vertexTypes.scala]
          + [vertices.scala][test/scala/ohnosequences/scarph/vertices.scala]

[main/scala/ohnosequences/scarph/Denotation.scala]: ../../../../main/scala/ohnosequences/scarph/Denotation.scala.md
[main/scala/ohnosequences/scarph/Edge.scala]: ../../../../main/scala/ohnosequences/scarph/Edge.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: ../../../../main/scala/ohnosequences/scarph/EdgeType.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: ../../../../main/scala/ohnosequences/scarph/Expressions.scala.md
[main/scala/ohnosequences/scarph/HasProperties.scala]: ../../../../main/scala/ohnosequences/scarph/HasProperties.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: ../../../../main/scala/ohnosequences/scarph/Property.scala.md
[main/scala/ohnosequences/scarph/titan/TEdge.scala]: ../../../../main/scala/ohnosequences/scarph/titan/TEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]: ../../../../main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala.md
[main/scala/ohnosequences/scarph/titan/TVertex.scala]: ../../../../main/scala/ohnosequences/scarph/titan/TVertex.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: ../../../../main/scala/ohnosequences/scarph/Vertex.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: ../../../../main/scala/ohnosequences/scarph/VertexType.scala.md
[test/scala/ohnosequences/scarph/edges.scala]: edges.scala.md
[test/scala/ohnosequences/scarph/edgeTypes.scala]: edgeTypes.scala.md
[test/scala/ohnosequences/scarph/properties.scala]: properties.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: titan/expressions.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: titan/TitanSchemaTest.scala.md
[test/scala/ohnosequences/scarph/vertexTypes.scala]: vertexTypes.scala.md
[test/scala/ohnosequences/scarph/vertices.scala]: vertices.scala.md