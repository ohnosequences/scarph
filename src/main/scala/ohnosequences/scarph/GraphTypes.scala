package ohnosequences.scarph

object graphTypes {

  import ohnosequences.cosas._, types._, properties._
  import steps._, paths._, containers._


  /* This is a graph type containing another graph type */
  trait AnyGraphType extends AnyType {

    type Container <: AnyContainer
    val  container: Container

    type Inside <: AnyGraphType
    val  inside: Inside

    type Tpe = Container#Of[Inside]
  }

  /* This is a non-nested graph type */
  trait AnyPlainGraphType extends AnyGraphType {

    type Container = ExactlyOne
    val  container = ExactlyOne

    type Inside = this.type
    val  inside = this
  }

  /* A graph element is either a vertex or an edge, only they can have properties */
  sealed trait AnyGraphElement extends AnyPlainGraphType

  /* Vertex type is very simple */
  trait AnyVertex extends AnyGraphElement

  abstract class Vertex extends AnyVertex { val label = this.toString }


  /* Edges connect vertices and have in/out arities */
  // NOTE: this is the same as AnyPath but with restriction on InT/OutT
  trait AnyEdge extends AnyGraphElement {
    
    /* The source vertex for this edge */
    type Source <: AnyVertex
    val  source: Source

    /* The target vertex for this edge */
    type Target <: AnyVertex
    val  target: Target
  }

  /* This constructor encourages to use this syntax: Edge( ExactlyOne.of(user) -> ManyOrNone.of(tweet) ) */
  abstract class Edge[
    S <: AnyVertex,
    T <: AnyVertex
  ]( st: (S, T) ) extends AnyEdge {

    type Source = S
    val  source = st._1

    type Target = T
    val  target = st._2

    val label = this.toString
  }


  /* Property is assigned to one element type and has a raw representation */
  trait AnyGraphProperty extends AnyPlainGraphType with AnyProperty {

    type Owner <: AnyGraphElement
    val  owner: Owner
  }

  // TODO: something like edge constructor
  abstract class PropertyOf[O <: AnyGraphElement](val owner: O) extends AnyGraphProperty {
    
    type Owner = O

    val label = this.toString
  }

}
