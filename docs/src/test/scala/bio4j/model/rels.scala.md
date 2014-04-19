
```scala
package bio4j.model.test

import bio4j.model._

object rels {

  import relTypes._
  import edgeTypes._
  import edges._
  import vertexTypes._
  import vertices._

  case class UMOImpl(
    val source: user.TaggedRep,
    val edge: memberOf.TaggedRep,
    val target: org.TaggedRep
  )

  object usersMembersOfOrgs extends Rel(UsersMembersOfOrgs) { self =>
    type Rep = UMOImpl

    implicit object sourceGetter extends GetSource[user.type](user) {
      def apply(rep: self.TaggedRep) = rep.source
    }
    implicit object edgeGetter extends GetEdge[memberOf.type](memberOf) {
      def apply(rep: self.TaggedRep) = rep.edge
    }
  }
```


object source extends Source(memberOf, user) {

@Override  
def apply(relRep: memberOf.RelRep): user.VertexRep = 
  user ->> UserImpl(
                      id = "1ad3a34df",
                      name = "Robustiano Satrústegui",
                      since = 2349965
                    )
}


```scala
}

class RelSuite extends org.scalatest.FunSuite {

  import rels._  
  import relTypes._

  import edgeTypes._
  import edges._
  import vertexTypes._
  import vertices._
  import properties._

  test("retrieve rel's sourde-edge-target") {

    import rels.usersMembersOfOrgs._
    import relTypes.UsersMembersOfOrgs._
    val u = user ->> UserImpl(id = "1ad3a34df", name = "Robustiano Satrústegui", since = 2349965)
    val m: memberOf.TaggedRep = memberOf ->> MemberOfImpl(isPublic = true, since = 2349965, validUntil = 38724987)
    val o: org.TaggedRep = org ->> UserImpl(id = "NYSE:ORCL", name = "Orcale Inc.", since = 1977)

    val r = usersMembersOfOrgs ->> UMOImpl(u, m, o)

    assert((r source) === u)
    assert(r.edge === m)

    // I think this is a bug; this import shouldn't be needed
    import vertexTypes.User._
    assert(
      (r.source get id) === "1ad3a34df"
    )

    // val followers_ids = (user out follows) map { _ get id }

```

Adding target getter externally:

```scala
    implicit object targetGetter extends GetTarget[org.type](org) {
      def apply(rep: usersMembersOfOrgs.TaggedRep) = rep.target
    }
    assert(r.target === o)
```

Getting edge properties

```scala
    assert(r.edge.get(since) === 2349965)
    assert(r.edge.get(validUntil) === 38724987)

    implicit val weForgotToImportIt = memberOf.edgeType has isPublic
    assert(r.edge.get(isPublic) === true)
  }
}

```


------

### Index

+ src
  + test
    + scala
      + bio4j
        + model
          + [properties.scala][test/scala/bio4j/model/properties.scala]
          + [edges.scala][test/scala/bio4j/model/edges.scala]
          + [vertices.scala][test/scala/bio4j/model/vertices.scala]
          + [rels.scala][test/scala/bio4j/model/rels.scala]
          + [vertexTypes.scala][test/scala/bio4j/model/vertexTypes.scala]
          + [relTypes.scala][test/scala/bio4j/model/relTypes.scala]
          + [edgeTypes.scala][test/scala/bio4j/model/edgeTypes.scala]
  + main
    + scala
      + bio4j
        + model
          + [properties.scala][main/scala/bio4j/model/properties.scala]
          + [reps.scala][main/scala/bio4j/model/reps.scala]
          + [edges.scala][main/scala/bio4j/model/edges.scala]
          + [vertices.scala][main/scala/bio4j/model/vertices.scala]
          + [relationships.scala][main/scala/bio4j/model/relationships.scala]
          + [relationshipTypes.scala][main/scala/bio4j/model/relationshipTypes.scala]
          + [vertexTypes.scala][main/scala/bio4j/model/vertexTypes.scala]
          + [edgeTypes.scala][main/scala/bio4j/model/edgeTypes.scala]

[test/scala/bio4j/model/properties.scala]: properties.scala.md
[test/scala/bio4j/model/edges.scala]: edges.scala.md
[test/scala/bio4j/model/vertices.scala]: vertices.scala.md
[test/scala/bio4j/model/rels.scala]: rels.scala.md
[test/scala/bio4j/model/vertexTypes.scala]: vertexTypes.scala.md
[test/scala/bio4j/model/relTypes.scala]: relTypes.scala.md
[test/scala/bio4j/model/edgeTypes.scala]: edgeTypes.scala.md
[main/scala/bio4j/model/properties.scala]: ../../../../main/scala/bio4j/model/properties.scala.md
[main/scala/bio4j/model/reps.scala]: ../../../../main/scala/bio4j/model/reps.scala.md
[main/scala/bio4j/model/edges.scala]: ../../../../main/scala/bio4j/model/edges.scala.md
[main/scala/bio4j/model/vertices.scala]: ../../../../main/scala/bio4j/model/vertices.scala.md
[main/scala/bio4j/model/relationships.scala]: ../../../../main/scala/bio4j/model/relationships.scala.md
[main/scala/bio4j/model/relationshipTypes.scala]: ../../../../main/scala/bio4j/model/relationshipTypes.scala.md
[main/scala/bio4j/model/vertexTypes.scala]: ../../../../main/scala/bio4j/model/vertexTypes.scala.md
[main/scala/bio4j/model/edgeTypes.scala]: ../../../../main/scala/bio4j/model/edgeTypes.scala.md