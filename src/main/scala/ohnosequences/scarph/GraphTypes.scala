package ohnosequences.scarph

object graphTypes {

  import monoidalStructures._
  import ohnosequences.cosas._, types._, properties._
  // import ohnosequences.cosas.equals._

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
         - AnyGraphMorphism
             - AnyComposition (sealed)
             - AnyTensor (sealed)
             - AnyBiproduct (sealed)
             - primitives
  */
  trait AnyGraphType extends AnyType

  /* Graph objects are represented as their id-morphisms */
  trait AnyGraphObject extends AnyGraphType

  /* A graph element is either a vertex or an edge, only they can have properties */
  sealed trait AnyGraphElement extends AnyGraphObject

  /* Vertex type is very simple */
  trait AnyVertex extends AnyGraphElement

  class Vertex extends AnyVertex {

    lazy val label = this.toString
  }

  /* Edges connect vertices and have in/out arities */
  trait AnyEdge extends AnyGraphObject {
    
    type Source <: AnyVertex
    val  source: Source

    type Target <: AnyVertex
    val  target: Target

    // TODO: add arities
  }

  /* This constructor encourages to use this syntax: Edge(user -> tweet) */
  abstract class Edge[
    S <: AnyVertex,
    T <: AnyVertex
  ]( st: (S, T) ) extends AnyEdge {

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

    lazy val label = this.toString
  }


  /* Morphisms are spans */
  trait AnyGraphMorphism extends AnyGraphType { morphism =>

    type In <: AnyGraphObject
    val  in: In

    type Out <: AnyGraphObject
    val  out: Out

    type Dagger <: AnyGraphMorphism
    val  dagger: Dagger
  }

  type -->[A <: AnyGraphType, B <: AnyGraphType] = AnyGraphMorphism { type In = A; type Out = B }

  /* Sequential composition of two morphisms */
  sealed trait AnyComposition extends AnyGraphMorphism {

    type First <: AnyGraphMorphism
    type Second <: AnyGraphMorphism //{ type In = First#Out }

    type In  <: First#In
    type Out <: Second#Out

    type Dagger <: Composition[Second#Dagger, First#Dagger]
  }

  case class Composition[
    F <: AnyGraphMorphism,
    S <: AnyGraphMorphism //{ type In = F#Out }
  ] (val first: F, val second: S) extends AnyComposition { cc =>

    type     Self = this.type
    lazy val self = this: Self

    type First = F
    type Second = S

    type     In = First#In
    lazy val in = first.in: In

    type     Out = Second#Out
    lazy val out = second.out: Out

    type     Dagger = Composition[Second#Dagger, First#Dagger]
    lazy val dagger = Composition(second.dagger, first.dagger): Dagger

    lazy val label: String = s"(${first.label} >=> ${second.label})"
  }


  /* Basic aliases */
  type >=>[F <: AnyGraphMorphism, S <: AnyGraphMorphism { type In = F#Out }] = Composition[F, S]


  class GraphObjectOps[O <: AnyGraphObject](val obj: O) {

    import monoidalStructures._

    def ⊗[S <: AnyGraphObject](other: S): TensorObj[O, S] = TensorObj(obj, other)
    def ⊕[S <: AnyGraphObject](other: S): BiproductObj[O, S] = BiproductObj(obj, other)
  }

  implicit def graphMorphismOps[F <: AnyGraphMorphism](f: F):
        GraphMorphismOps[F] =
    new GraphMorphismOps[F](f)

  class GraphMorphismOps[F <: AnyGraphMorphism](val f: F) {

    def >=>[S <: AnyGraphMorphism { type In = F#Out }]
      (s: S): Composition[F, S] = 
              Composition[F, S](f, s)

    import monoidalStructures._

    def ⊗[S <: AnyGraphMorphism](q: S): TensorMorph[F, S] = TensorMorph(f, q)
    def ⊕[S <: AnyGraphMorphism](q: S): BiproductMorph[F, S] = BiproductMorph(f, q)

    import evals._
    def evalOn[I, O](input: F#In := I)
      (implicit eval: EvalPathOn[I, F, O]): F#Out := O = eval(f)(input)
  }

}
