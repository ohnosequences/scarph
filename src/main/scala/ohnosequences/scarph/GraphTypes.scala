package ohnosequences.scarph

object graphTypes {

  import ohnosequences.cosas._, types._, properties._
  import ohnosequences.cosas.equals._

  /* A graph type is kind of an n-morphism 

     The full hierarchy looks like this:

     - AnyGraphType
         - AnyGraphObject
             - AnyGraphElement (sealed)
                 - AnyVertex
                 - AnyEdge
             - AnyGraphProperty
             - AnyPredicate
             - AnyIndex
             - AnyGraphSchema
         - AnyGraphMorphism (what was AnyGraphMorphism before)
         - AnyComposition (sealed)
         - AnyBiproduct (sealed)
  */
  trait AnyGraphType extends AnyType {

    type In <: AnyGraphType
    val  in: In

    type Out <: AnyGraphType
    val  out: Out
  }

  /* Graph objects are represented as their id-morphisms */
  trait AnyGraphObject extends AnyGraphType {

    // NOTE: this has to be set in a NON-abstract descendant
    type Self >: this.type <: AnyGraphObject // = this.type
    val  self: Self // = this

    type     In = Self
    lazy val in = self: In

    type     Out = Self
    lazy val out = self: Out
  }


  /* A graph element is either a vertex or an edge, only they can have properties */
  sealed trait AnyGraphElement extends AnyGraphObject

  /* Vertex type is very simple */
  trait AnyVertex extends AnyGraphElement

  class Vertex extends AnyVertex {
    type     Self = this.type
    lazy val self = this: Self

    lazy val label = this.toString
  }

  /* Edges connect vertices and have in/out arities */
  // NOTE: this is the same as AnyGraphMorphism but with restriction on InT/OutT
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

    type     Self = this.type
    lazy val self = this: Self

    type Source = S
    lazy val source = st._1: S

    type Target = T
    lazy val target = st._2: T

    lazy val label = this.toString
  }

  object AnyEdge {

    type From[S <: AnyVertex] = AnyEdge { type Source = S }
    type   To[T <: AnyVertex] = AnyEdge { type Target = T }
  }

  /* Property is assigned to one element type and has a raw representation */
  trait AnyGraphProperty extends AnyProperty with AnyGraphObject {

    type Owner <: AnyGraphElement
    val  owner: Owner
  }

  abstract class PropertyOf[O <: AnyGraphElement]
    (val owner: O) extends AnyGraphProperty {
    type Owner = O

    type     Self = this.type
    lazy val self = this: Self

    lazy val label = this.toString
  }


  /* Biproduct is the same for objects and morphisms */
  sealed trait AnyBiproduct extends AnyGraphType {

    type Left <: AnyGraphType
    val  left: Left

    type Right <: AnyGraphType
    val  right: Right

    type In  <: Biproduct[Left#In, Right#In]
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


  /* 
    _Path_ describes some graph traversal. It contains of steps that are combined in various ways.

    Note that `AnyGraphMorphism` hierarchy is sealed, meaning that a path is either a step or a composition of paths.
  */
  // sealed trait AnyGraphMorphism extends AnyGraphType


  /* A _step_ is a simple atomic _path_ which can be evaluated directly */
  trait AnyGraphMorphism extends AnyGraphType 

  /* Sequential composition of two paths */
  sealed trait AnyComposition extends AnyGraphType {

    type First <: AnyGraphType
    type Second <: AnyGraphType { type In = First#Out }

    // val composable: First#Out ≃ Second#In

    type In  <: First#In
    type Out <: Second#Out
  }

  case class Composition[F <: AnyGraphType, S <: AnyGraphType { type In = F#Out }]
    (val first: F, val second: S)
    // (implicit val composable: F#Out ≃ S#In) 
    extends AnyComposition {

    lazy val label: String = s"(${first.label} >=> ${second.label})"

    type First = F
    type Second = S

    type     In = First#In
    lazy val in = first.in: In

    type     Out = Second#Out
    lazy val out = second.out: Out
  }

  type >=>[F <: AnyGraphType, S <: AnyGraphType { type In = F#Out }] = Composition[F, S]

  implicit def CombinatorsSyntaxOps[F <: AnyGraphType](f: F):
        CombinatorsSyntaxOps[F] =
    new CombinatorsSyntaxOps[F](f)

  class CombinatorsSyntaxOps[F <: AnyGraphType](f: F) {

    def >=>[S <: AnyGraphType { type In = F#Out }](s: S): //(implicit cmp: F#Out ≃ S#In): 
      Composition[F, S] = 
      Composition(f, s) //(cmp)
  }


  /* Adding useful methods */
  object AnyGraphType {

    implicit def pathOps[T <: AnyGraphType](t: T) = PathOps(t)
  }

  case class PathOps[P <: AnyGraphType](val p: P) {

    import evals._
    def evalOn[I, O](input: P#In := I)
      (implicit eval: EvalPathOn[I, P, O]): P#Out := O = eval(p)(input)
  }

}
