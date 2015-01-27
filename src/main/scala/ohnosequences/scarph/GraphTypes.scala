package ohnosequences.scarph

object graphTypes {

  import ohnosequences.cosas._, types._, properties._
  import steps._, paths._
  import shapeless.<:!<


  /* This is a graph type containing another graph type */
  trait AnyGraphType extends AnyType {

    type In <: AnyGraphType
    val  in: In

    type Out <: AnyGraphType
    val  out: Out
  }

  // @annotation.implicitNotFound(msg = "Can't prove that these graph types are equivalent:\n\tfirst:  ${A}\n\tsecond: ${B}")
  // trait Equality[A <: AnyGraphType, B <: AnyGraphType] { type Out >: A with B <: A with B }
  // type EqualityContext[X <: AnyGraphType, Y <: AnyGraphType] = (X,Y)
  // @annotation.implicitNotFound(msg = "Can't prove that these graph types are equivalent:\n\tfirst:  ${A}\n\tsecond: ${B}")
  // type ≃[A <: AnyGraphType, B <: AnyGraphType] = EqualityContext[A,B] => (A Equality B)
  // trait Refl[A <: AnyGraphType] extends (A Equality A) { type Out = A }
  // implicit def refl[A >: B <: B, B <: AnyGraphType]: EqualityContext[A,B] => A Equality B = { 

  //   (x: EqualityContext[A,B]) => new Refl[B] {} 
  // }


  /* A graph element is either a vertex or an edge, only they can have properties */
  sealed trait AnyGraphElement extends AnyGraphType

  /* Vertex type is very simple */
  trait AnyVertex extends AnyGraphElement

  class Vertex extends AnyVertex {

    type     In = this.type
    lazy val in = this: In

    type     Out = this.type
    lazy val out = this: Out

    lazy val label = this.toString
  }

  /* Edges connect vertices and have in/out arities */
  // NOTE: this is the same as AnyPath but with restriction on InT/OutT
  trait AnyEdge extends AnyGraphElement {
    
    type Source <: AnyVertex
    val  source: Source

    type Target <: AnyVertex
    val  target: Target
  }

  /* This constructor encourages to use this syntax: Edge( ExactlyOne.of(user) -> ManyOrNone.of(tweet) ) */
  abstract class Edge[
    S <: AnyVertex,
    T <: AnyVertex
  ]( st: (S, T) ) extends AnyEdge {

    type     In = Edge[S, T]
    lazy val in = this: In

    type     Out = Edge[S, T]
    lazy val out = this: Out

    type Source = S
    lazy val source = st._1

    type Target = T
    lazy val target = st._2

    lazy val label = this.toString
  }

  object AnyEdge {

    type From[S <: AnyVertex] = AnyEdge { type Source = S }
    type   To[T <: AnyVertex] = AnyEdge { type Target = T }
  }

  /* Property is assigned to one element type and has a raw representation */
  trait AnyGraphProperty extends AnyProperty with AnyGraphType {

    type Owner <: AnyGraphElement
    val  owner: Owner
  }

  abstract class PropertyOf[O <: AnyGraphElement]
    (val owner: O) extends AnyGraphProperty {
    type Owner = O

    type     In = PropertyOf[O]
    lazy val in = this: In

    type     Out = PropertyOf[O]
    lazy val out = this: Out

    lazy val label = this.toString
  }


  trait AnyBiproduct extends AnyGraphType {

    type Left <: AnyGraphType
    val  left: Left

    type Right <: AnyGraphType
    val  right: Right

    type In <: Biproduct[Left#In, Right#In]
    type Out <: Biproduct[Left#Out, Right#Out]
  }

  case class Biproduct[L <: AnyGraphType, R <: AnyGraphType]
    (val left: L, val right: R) extends AnyBiproduct {

    type Left = L
    type Right = R

    type     In = Biproduct[Left#In, Right#In]
    lazy val in = Biproduct(left.in, right.in): In

    type     Out = Biproduct[Left#Out, Right#Out]
    lazy val out = Biproduct(left.out, right.out): Out

    lazy val label = s"(${left.label} ⊕ ${right.label})"
  }

  // \oplus symbol: F ⊕ S
  type ⊕[F <: AnyGraphType, S <: AnyGraphType] = Biproduct[F, S]

  implicit def graphTypeOps[T <: AnyGraphType](t: T):
        GraphTypeOps[T] =
    new GraphTypeOps[T](t)

  class GraphTypeOps[T <: AnyGraphType](t: T) {

    def ⊕[S <: AnyGraphType](s: S):
      Biproduct[T, S] =
      Biproduct(t, s)
  }

}
