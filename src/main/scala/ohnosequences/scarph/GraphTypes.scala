package ohnosequences.scarph

object graphTypes {

  import ohnosequences.cosas._, types._, properties._
  import steps._, paths._, containers._


  trait AnyGraphType extends AnyType


  /* A graph element is either a vertex or an edge, only they can have properties */
  sealed trait AnyGraphElement extends AnyGraphType


  /* Vertex type is very simple */
  trait AnyVertex extends AnyGraphElement {}

  abstract class Vertex extends AnyVertex { val label = this.toString }


  /* Edges connect vertices and have in/out arities */
  // NOTE: this is the same as AnyPath but with restriction on InT/OutT
  trait AnyEdge extends AnyGraphElement {
    
    /* The source vertex for this edge */
    type InT <: AnyVertex
    val  inT: InT
    /* This is the arity for incoming edges */
    type InC <: AnyContainer
    val  inC: InC

    /* The target vertex for this edge */
    type OutT <: AnyVertex
    val  outT: OutT
    /* This is the arity for outgoing edges */
    type OutC <: AnyContainer
    val  outC: OutC
  }

  /* This constructor encourages to use this syntax: Edge( ExactlyOne.of(user) -> ManyOrNone.of(tweet) ) */
  abstract class Edge[
    In  <: AnyNestedGraphType { type Inside <: AnyVertex },
    Out <: AnyNestedGraphType { type Inside <: AnyVertex }
  ]( inout: (In, Out) ) extends AnyEdge {

    private[Edge] val in  = inout._1
    private[Edge] val out = inout._2

    type InC = In#Container
    val  inC = in.container
    type InT = In#Inside
    val  inT = in.inside

    type OutC = Out#Container
    val  outC = out.container
    type OutT = Out#Inside
    val  outT = out.inside

    val label = this.toString
  }


  /* Property is assigned to one element type and has a raw representation */
  trait AnyGraphProperty extends AnyGraphType with AnyProperty {

    type Owner <: AnyGraphElement
    val  owner: Owner
  }

  // TODO: something like edge constructor
  abstract class PropertyOf[O <: AnyGraphElement](val owner: O) extends AnyGraphProperty {
    
    type Owner = O

    val label = this.toString
  }

}
