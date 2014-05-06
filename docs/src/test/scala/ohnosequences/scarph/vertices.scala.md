
```scala
package ohnosequences.scarph.test

import vertexTypes._
import properties._
import ohnosequences.scarph._
import shapeless.record.FieldType

object vertices {
```

A representation of the `User` vertex type; note how it is external to `User`, and it doesn't mention the corresponding type at all

```scala
  case class UserImpl(
    val id: String,
    val name: String,
    val since: Int
  )
  
  case object user extends Vertex(User) { self =>
```

Now users can be created with `user ->> UserImpl(...)`

```scala
    type Rep = UserImpl
```

Provide implicits here (or elsewhere) for all (or some) properties

```scala
    implicit object readId extends GetProperty(id) {
      def apply(rep: user.TaggedRep): id.Rep = (rep: user.Rep).id
    }
    implicit object readSince extends GetProperty(since) {
      def apply(rep: self.TaggedRep): since.Rep = (rep: self.Rep).since
    }
  }

  case object org extends Vertex(Org) { self =>
```


We are lazy, so we will use the same representation for orgs
even though we care only about the `name` property


```scala
    type Rep = UserImpl

    implicit object readName extends GetProperty(name) {
      def apply(rep: self.TaggedRep) = rep.name
    }
  }

}

class VertexSuite extends org.scalatest.FunSuite {

  import vertices._  

  test("retrieve vertex properties") {

    import vertices.user._
    import vertexTypes.User._
    val u = user ->> UserImpl(id = "1ad3a34df", name = "Robustiano Satr?stegui", since = 2349965)

    val u_id = u.get(id)
```


Too bad, no witness for `since`, so `u.get(since)` won't compile.
But we can add it here!


```scala
    implicit val userSince = User has since
    val u_since = u.get(since)
    val u_since_again = u get since
```


We can also add a retriever for the `name` property externally:


```scala
    implicit object readUserName extends GetProperty(name) {
      def apply(rep: user.TaggedRep) = rep.name
    }

    assert((u get id) === "1ad3a34df")
    assert((u get name) === "Robustiano Satr?stegui")
    assert((u get since) === 2349965)
```


Again, we are using user's representation for org, 
even though it has only the `name` property


```scala
    import vertices.org._
    import vertexTypes.Org._
    val o = org ->> UserImpl(id = "NYSE:ORCL", name = "Oracle Inc.", since = 1977)

    assert((o get name) === "Oracle Inc.")
```


Now we realized, that we can do more things with this 
representation of org so we just implement it in place:


```scala
    implicit val orgFounded = Org has since
    implicit object readOrgSince extends org.GetProperty(since) {
      def apply(rep: org.TaggedRep) = rep.since
    }
    assert((o get since) === 1977)

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
          + [Vertex.scala][main/scala/ohnosequences/scarph/Vertex.scala]
          + [VertexType.scala][main/scala/ohnosequences/scarph/VertexType.scala]
  + test
    + scala
      + ohnosequences
        + scarph
          + [edges.scala][test/scala/ohnosequences/scarph/edges.scala]
          + [edgeTypes.scala][test/scala/ohnosequences/scarph/edgeTypes.scala]
          + [expressions.scala][test/scala/ohnosequences/scarph/expressions.scala]
          + [properties.scala][test/scala/ohnosequences/scarph/properties.scala]
          + titan
            + [godsImplementation.scala][test/scala/ohnosequences/scarph/titan/godsImplementation.scala]
            + [godsSchema.scala][test/scala/ohnosequences/scarph/titan/godsSchema.scala]
            + [TEdge.scala][test/scala/ohnosequences/scarph/titan/TEdge.scala]
            + [TitanGodsTest.scala][test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]
            + [TitanGraphSchema.scala][test/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]
            + [TitanSchemaTest.scala][test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]
            + [TVertex.scala][test/scala/ohnosequences/scarph/titan/TVertex.scala]
          + [vertexTypes.scala][test/scala/ohnosequences/scarph/vertexTypes.scala]
          + [vertices.scala][test/scala/ohnosequences/scarph/vertices.scala]

[main/scala/ohnosequences/scarph/Denotation.scala]: ../../../../main/scala/ohnosequences/scarph/Denotation.scala.md
[main/scala/ohnosequences/scarph/Edge.scala]: ../../../../main/scala/ohnosequences/scarph/Edge.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: ../../../../main/scala/ohnosequences/scarph/EdgeType.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: ../../../../main/scala/ohnosequences/scarph/Expressions.scala.md
[main/scala/ohnosequences/scarph/HasProperties.scala]: ../../../../main/scala/ohnosequences/scarph/HasProperties.scala.md
[main/scala/ohnosequences/scarph/Property.scala]: ../../../../main/scala/ohnosequences/scarph/Property.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: ../../../../main/scala/ohnosequences/scarph/Vertex.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: ../../../../main/scala/ohnosequences/scarph/VertexType.scala.md
[test/scala/ohnosequences/scarph/edges.scala]: edges.scala.md
[test/scala/ohnosequences/scarph/edgeTypes.scala]: edgeTypes.scala.md
[test/scala/ohnosequences/scarph/expressions.scala]: expressions.scala.md
[test/scala/ohnosequences/scarph/properties.scala]: properties.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TEdge.scala]: titan/TEdge.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]: titan/TitanGraphSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: titan/TitanSchemaTest.scala.md
[test/scala/ohnosequences/scarph/titan/TVertex.scala]: titan/TVertex.scala.md
[test/scala/ohnosequences/scarph/vertexTypes.scala]: vertexTypes.scala.md
[test/scala/ohnosequences/scarph/vertices.scala]: vertices.scala.md