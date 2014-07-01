
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
    type Raw = UserImpl
```

Provide implicits here (or elsewhere) for all (or some) properties

```scala
    implicit object readId extends PropertyGetter(id) {
      def apply(rep: user.Rep): id.Raw = (rep: user.Rep).id
    }
    implicit object readSince extends PropertyGetter(since) {
      def apply(rep: self.Rep): since.Raw = (rep: self.Rep).since
    }
  }

  case object org extends Vertex(Org) { self =>
```


We are lazy, so we will use the same representation for orgs
even though we care only about the `name` property


```scala
    type Raw = UserImpl

    implicit object readName extends PropertyGetter(name) {
      def apply(rep: self.Rep) = rep.name
    }
  }

}

class VertexSuite extends org.scalatest.FunSuite {

  import vertices._  

  test("retrieve vertex properties") {

    import vertices.user._
    import vertexTypes.User._
    val u = user ->> UserImpl(id = "1ad3a34df", name = "Robustiano Satrústegui", since = 2349965)

    val u_id = u.get(id)
```


Too bad, no witness for `since`, so `u.get(since)` won't compile.
But we can add it here!


```scala
    implicit val userSince = User has since
    val u_since = u.get(since)
    val u_since_again = u get since
```


We can also add a getter for the `name` property externally:


```scala
    implicit object readUserName extends PropertyGetter(name) {
      def apply(rep: user.Rep) = rep.name
    }

    assert((u get id) === "1ad3a34df")
    assert((u get name) === "Robustiano Satrústegui")
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
    implicit object readOrgSince extends org.PropertyGetter(since) {
      def apply(rep: org.Rep) = rep.since
    }
    assert((o get since) === 1977)

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