package ohnosequences.scarph.test

import ohnosequences.cosas._
import ohnosequences.scarph._
import twitter._

class SchemaCreation extends org.scalatest.FunSuite {

  test("sets of types are there") {

    println { s"vertices: ${twitter.vertices.map(_.label)}" }
    println { s"edges: ${twitter.edges.map(_.label)}" }
    println { s"properties: ${twitter.properties.map(_.label)}" }
    println { s"value types: ${twitter.valueTypes.map(_.label)}" }
  }
}
