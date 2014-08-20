
```scala
package ohnosequences.scarph.test

import ohnosequences.scarph._, ops.typelevel._
import ohnosequences.typesets._

object StupidSealedStuff {
  
  case object name extends Property[String]
  case object age extends Property[Integer]
  case object happiness extends Property[Long]
  case object id extends Property[Int]

  // records
  case object record extends Record(name :~: age :~: ?)
  case object anotherRecord extends Record(age :~: happiness :~: ?)
  case object myFavRecord extends Record(id :~: happiness :~: name :~: ?)

  // types
  object Types {
    case object Yuhu extends SealedVertexType("yuhu", record)
    case object OhNo extends SealedVertexType("ohno", anotherRecord)  
    case object SayItTo extends SealedEdgeType(Yuhu, "sayitto", myFavRecord, OhNo) with ManyIn with OneOut
  }

  case object yuhu extends SealedVertex(Types.Yuhu) {

    type Other = String
  }

  case object ohNo extends SealedVertex(Types.OhNo) {

    // why not?
    type Other = Integer
  }

  case object sayItTo extends SealedEdge(yuhu, Types.SayItTo, ohNo) { sayItTo =>

    type Other = (source.Rep, target.Rep)

    implicit val s: GetSource = new GetSource { def apply(edge: sayItTo.Rep) = edge.other._1 }
    implicit val t: GetTarget = new GetTarget { def apply(edge: sayItTo.Rep) = edge.other._2 } 
  }

  val y1 = yuhu ->> (

    yuhu.raw (

      yuhu fields (
        (name is "lalala") :~:
        (age is 12) :~: ?
      ),
      "rubbish"
    )
  )

  val o1 = ohNo ->> {

    ohNo.raw (

      ohNo fields (
        (age is 1231231)      :~:
        (happiness is 111111) :~: ?
      ),
      234234
    )
  }

  val s1 = sayItTo ->> {

    sayItTo.raw (

      sayItTo fields (

        (id is 12312)       :~: 
        (happiness is 23423) :~: 
        (name is "dfadfww")  :~: ?
      ),
      (y1, o1)
    )
  }


}

class CheckSealedTypes extends org.scalatest.FunSuite {

  import StupidSealedStuff._

  test("getting properties from sealed vertex instances") {

    assert (

      (y1 get name) === "lalala"
    )

    assert (

      (o1 get happiness) === 111111
    )
  }

  test("getting properties from sealed edge instances") {

    assert (

      (s1 get happiness) === 23423
    )
  }

  test ("naive source and target from sealed edge instances") {

    assert (

      (s1 src) === y1
    )

    assert (

      (s1 tgt) === o1
    )
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
          + [Edge.scala][main/scala/ohnosequences/scarph/Edge.scala]
          + [EdgeType.scala][main/scala/ohnosequences/scarph/EdgeType.scala]
          + [Expressions.scala][main/scala/ohnosequences/scarph/Expressions.scala]
          + [GraphSchema.scala][main/scala/ohnosequences/scarph/GraphSchema.scala]
          + ops
            + [default.scala][main/scala/ohnosequences/scarph/ops/default.scala]
            + [typelevel.scala][main/scala/ohnosequences/scarph/ops/typelevel.scala]
          + titan
            + [TitanEdge.scala][main/scala/ohnosequences/scarph/titan/TitanEdge.scala]
            + [TitanGraphSchema.scala][main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]
            + [TitanVertex.scala][main/scala/ohnosequences/scarph/titan/TitanVertex.scala]
          + [Vertex.scala][main/scala/ohnosequences/scarph/Vertex.scala]
          + [VertexType.scala][main/scala/ohnosequences/scarph/VertexType.scala]
  + test
    + scala
      + ohnosequences
        + scarph
          + [sealedStuff.scala][test/scala/ohnosequences/scarph/sealedStuff.scala]
          + titan
            + [expressions.scala][test/scala/ohnosequences/scarph/titan/expressions.scala]
            + [godsImplementation.scala][test/scala/ohnosequences/scarph/titan/godsImplementation.scala]
            + [godsSchema.scala][test/scala/ohnosequences/scarph/titan/godsSchema.scala]
            + [TitanGodsTest.scala][test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]
            + [TitanSchemaTest.scala][test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]

[main/scala/ohnosequences/scarph/Edge.scala]: ../../../../main/scala/ohnosequences/scarph/Edge.scala.md
[main/scala/ohnosequences/scarph/EdgeType.scala]: ../../../../main/scala/ohnosequences/scarph/EdgeType.scala.md
[main/scala/ohnosequences/scarph/Expressions.scala]: ../../../../main/scala/ohnosequences/scarph/Expressions.scala.md
[main/scala/ohnosequences/scarph/GraphSchema.scala]: ../../../../main/scala/ohnosequences/scarph/GraphSchema.scala.md
[main/scala/ohnosequences/scarph/ops/default.scala]: ../../../../main/scala/ohnosequences/scarph/ops/default.scala.md
[main/scala/ohnosequences/scarph/ops/typelevel.scala]: ../../../../main/scala/ohnosequences/scarph/ops/typelevel.scala.md
[main/scala/ohnosequences/scarph/titan/TitanEdge.scala]: ../../../../main/scala/ohnosequences/scarph/titan/TitanEdge.scala.md
[main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala]: ../../../../main/scala/ohnosequences/scarph/titan/TitanGraphSchema.scala.md
[main/scala/ohnosequences/scarph/titan/TitanVertex.scala]: ../../../../main/scala/ohnosequences/scarph/titan/TitanVertex.scala.md
[main/scala/ohnosequences/scarph/Vertex.scala]: ../../../../main/scala/ohnosequences/scarph/Vertex.scala.md
[main/scala/ohnosequences/scarph/VertexType.scala]: ../../../../main/scala/ohnosequences/scarph/VertexType.scala.md
[test/scala/ohnosequences/scarph/sealedStuff.scala]: sealedStuff.scala.md
[test/scala/ohnosequences/scarph/titan/expressions.scala]: titan/expressions.scala.md
[test/scala/ohnosequences/scarph/titan/godsImplementation.scala]: titan/godsImplementation.scala.md
[test/scala/ohnosequences/scarph/titan/godsSchema.scala]: titan/godsSchema.scala.md
[test/scala/ohnosequences/scarph/titan/TitanGodsTest.scala]: titan/TitanGodsTest.scala.md
[test/scala/ohnosequences/scarph/titan/TitanSchemaTest.scala]: titan/TitanSchemaTest.scala.md