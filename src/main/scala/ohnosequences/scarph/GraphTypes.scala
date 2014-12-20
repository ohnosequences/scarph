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

  abstract class Edge[
    IC <: AnyContainer, IT <: AnyVertex,
    OC <: AnyContainer, OT <: AnyVertex
  ](val inC: IC,
    val inT: IT,
    val outC: OC,
    val outT: OT
  ) extends AnyEdge {

    type InC  = IC; type InT  = IT
    type OutC = OC; type OutT = OT

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
