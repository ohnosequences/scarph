package ohnosequences.scarph

object graphTypes {

  import ohnosequences.cosas._, types._, properties._
  import steps._, paths._, containers._
  import shapeless.<:!<


  /* This is a graph type containing another graph type */
  trait AnyGraphType extends AnyType {

    type Container <: AnyContainer
    val  container: Container

    type Inside <: AnyGraphType
    val  inside: Inside
  }

  @annotation.implicitNotFound(msg = "Can't prove that these graph types are equivalent:\n\tfirst:  ${A}\n\tsecond: ${B}")
  trait Equality[A <: AnyGraphType, B <: AnyGraphType] { type Out >: A with B <: A with B }
  type EqualityContext[X <: AnyGraphType, Y <: AnyGraphType] = (X,Y)
  type ≃[A <: AnyGraphType, B <: AnyGraphType] = EqualityContext[A,B] => (A Equality B)
  trait Refl[A <: AnyGraphType] extends (A Equality A) { type Out = A }
  implicit def refl[A >: B <: B, B <: AnyGraphType]: EqualityContext[A,B] => A Equality B = { 

    (x: EqualityContext[A,B]) => new Refl[B] {} 
  }

  // implicit def coerce[A <: AnyGraphType, B <: AnyGraphType](a: A)(implicit eqWitness: Equality[A,B]): Equality[A,B]#Out = a

  // this is `\simeq` symbol
  object EqualityContext {

    // trait Refl[A <: AnyGraphType] extends (A Equality A) { type Out = A }
    // implicit def refl[A >: B <: B, B <: AnyGraphType, X <: EqualityContext[A,B]]: A Equality B = new Refl[B] {}
  }
  
  trait simeq2 extends simeq3 {
    // implicit def eq[A <: AnyGraphType, B <: AnyGraphType]
    //   (implicit 
    //     cont: A#Container =:= B#Container,
    //     insd: A#Inside ≃ B#Inside
    //   ): A ≃ B = new (A ≃ B) {} 
  }

  trait simeq3 {

    // implicit def eqSimple[A <: AnySimpleGraphType, B <: AnySimpleGraphType]
    //   (implicit
    //     insd: A#Inside ≃ B#Inside
    //   ): A ≃ B = new (A ≃ B) {}  

  }

  /* This is a non-nested graph type */
  trait AnySimpleGraphType extends AnyGraphType {

    type Container = ExactlyOne
    val  container = ExactlyOne

    type Inside >: this.type <: AnyGraphType//<: AnySimpleGraphType
    lazy val inside: Inside = this
  }


  /* A graph element is either a vertex or an edge, only they can have properties */
  sealed trait AnyGraphElement extends AnySimpleGraphType

  /* Vertex type is very simple */
  trait AnyVertex extends AnyGraphElement

  class Vertex extends AnyVertex { type Inside = this.type; lazy val label = this.toString }

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

    // this is wrong too!
    type Inside = this.type // Edge[S,T]
  }


  /* Property is assigned to one element type and has a raw representation */
  trait AnyGraphProperty extends AnyProperty with AnySimpleGraphType {

    type Owner <: AnyGraphElement
    val  owner: Owner
  }

  // TODO: something like edge constructor
  abstract class PropertyOf[O <: AnyGraphElement](val owner: O) extends AnyGraphProperty {
    
    type Owner = O

    lazy val label: String = this.toString

    // ???
    type Inside = this.type
  }

  trait AnyParType extends AnySimpleGraphType { par =>

    type First <: AnyGraphType
    val  first: First

    type Second <: AnyGraphType
    val  second: Second

    type Inside >: par.type <: AnyParType { type First = par.First; type Second = par.Second }
  }

  case class ParType[F <: AnyGraphType, S <: AnyGraphType]
    (val first: F, val second: S) extends AnyParType {

    type First = F
    type Second = S

    lazy val label = s"(first.label ⊗ second.label)"

    type Inside = ParType[First,Second]
  }


  trait AnyOrType extends AnySimpleGraphType {

    type Left <: AnyGraphType
    val  left: Left

    type Right <: AnyGraphType
    val  right: Right
  }

  case class OrType[L <: AnyGraphType, R <: AnyGraphType]
    (val left: L, val right: R) extends AnyOrType {

    type Left = L
    type Right = R

    lazy val label = s"(left.label ⊕ right.label)"

    type Inside = OrType[Left,Right]
  }

}
