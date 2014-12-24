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
  }

  @annotation.implicitNotFound(msg = "Can't prove that these graph types are equivalent:\n\t${A}\n\t${B}")
  trait ≃[A <: AnyGraphType, B <: AnyGraphType]

  // this is `\simeq` symbol
  object ≃ extends simeq2 {
    // implicit def refl[A <: AnySimpleGraphType]: A ≃ A = new (A ≃ A) {}

    implicit def eq[A <: AnyGraphType, B <: AnyGraphType]
      (implicit eq: A#Container#Of[A#Inside] =:= B#Container#Of[B#Inside]): A ≃ B = new (A ≃ B) {}
  }

  trait simeq2 {
    // implicit def eq[A <: AnyGraphType, B <: AnyGraphType]
    //   (implicit 
    //     cont: A#Container =:= B#Container,
    //     insd: A#Inside ≃ B#Inside
    //   ): A ≃ B = new (A ≃ B) {}
  }


  /* This is a non-nested graph type */
  trait AnySimpleGraphType extends AnyGraphType {

    type Container = ExactlyOne
    val  container = ExactlyOne

    type Inside = this.type
    lazy val inside: Inside = this: this.type
  }


  /* A graph element is either a vertex or an edge, only they can have properties */
  sealed trait AnyGraphElement extends AnySimpleGraphType


  /* Vertex type is very simple */
  trait AnyVertex extends AnyGraphElement

  abstract class Vertex extends AnyVertex { val label = this.toString }


  /* Edges connect vertices and have in/out arities */
  // NOTE: this is the same as AnyPath but with restriction on InT/OutT
  trait AnyEdge extends AnyGraphElement {
    
    type Source <: AnyGraphType { type Inside <: AnyVertex }
    val  source: Source

    type     SourceV = Source#Inside
    lazy val sourceV = source.inside: SourceV


    type Target <: AnyGraphType { type Inside <: AnyVertex }
    val  target: Target

    type     TargetV = Target#Inside
    lazy val targetV = target.inside: TargetV
  }

  /* This constructor encourages to use this syntax: Edge( ExactlyOne.of(user) -> ManyOrNone.of(tweet) ) */
  abstract class Edge[
    S <: AnyGraphType { type Inside <: AnyVertex },
    T <: AnyGraphType { type Inside <: AnyVertex }
  ]( st: (S, T) ) extends AnyEdge {

    type Source = S
    lazy val source = st._1

    type Target = T
    lazy val target = st._2

    val label = this.toString
  }


  /* Property is assigned to one element type and has a raw representation */
  trait AnyGraphProperty extends AnySimpleGraphType with AnyProperty {

    type Owner <: AnyGraphElement
    val  owner: Owner
  }

  // TODO: something like edge constructor
  abstract class PropertyOf[O <: AnyGraphElement](val owner: O) extends AnyGraphProperty {
    
    type Owner = O

    val label = this.toString
  }

}