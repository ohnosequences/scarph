package ohnosequences.scarph

object graphTypes {

  import ohnosequences.cosas._, types._, properties._
  import ohnosequences.cosas.equals._

  /* A graph type is kind of an n-morphism 

     The full hierarchy looks like this:

     - AnyGraphType
         - AnyGraphObject (AnyGraphType with itself as In/Out)
             - AnyGraphElement (sealed)
                 - AnyVertex
                 - AnyEdge
             - AnyGraphProperty
             - AnyPredicate
             - AnyIndex
             - AnyGraphSchema
         - AnyGraphMorphism (what was AnyStep before)
         - AnyComposition (sealed)
         - AnyTensor (sealed)
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
  // NOTE: this is kind of the same as AnyGraphMorphism but with restriction on InT/OutT
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


  /* Tensor product is the same for objects and morphisms */
  sealed trait AnyTensor extends AnyGraphElement {

    type Left <: AnyGraphElement
    val  left: Left

    type Right <: AnyGraphElement
    val  right: Right

    type In  <: Tensor[Left#In, Right#In]
    type Out <: Tensor[Left#Out, Right#Out]
  }

  case class Tensor[L <: AnyGraphType, R <: AnyGraphType]
    (val left: L, val right: R) extends AnyTensor {

    type Left = L
    type Right = R

    type     In = Tensor[Left#In, Right#In]
    lazy val in = Tensor(left.in, right.in): In

    type     Out = Tensor[Left#Out, Right#Out]
    lazy val out = Tensor(left.out, right.out): Out

    lazy val label = s"(${left.label} ⊗ ${right.label})"
  }

  case object unit extends AnyGraphObject {
    type     Self = this.type
    lazy val self = this: Self

    lazy val label = this.toString
  }
  type unit = unit.type

  implicit def leftUnit[T <: AnyGraphType](it: unit ⊗ T): T = it.right
  implicit def rightUnit[T <: AnyGraphType](it: T ⊗ unit): T = it.left


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

  case object zero extends AnyGraphObject {

    type     Self = zero
    lazy val self = zero

    lazy val label = this.toString
  }
  type zero = zero.type

  implicit def leftZero[T <: AnyGraphType](it: zero ⊕ T): T = it.right
  implicit def rightZero[T <: AnyGraphType](it: T ⊕ zero): T = it.left


  /* Morphisms are spans */
  trait AnyGraphMorphism extends AnyGraphType { morphism =>

    type In <: AnyGraphObject
    type Out <: AnyGraphObject

    type Dagger <: AnyGraphMorphism {

      type In = morphism.Out
      type Out = morphism.In
    }
    val dagger: Dagger
  }

  type -->[A <: AnyGraphType, B <: AnyGraphType] = AnyGraphType { type In = A; type Out = B }

  /* Sequential composition of two paths */
  sealed trait AnyComposition extends AnyGraphMorphism { composition =>

    type First <: AnyGraphMorphism
    type Second <: AnyGraphMorphism { 
      
      type In = First#Out
    }

    type In  <: First#In
    type Out <: Second#Out

    // type Dagger <: AnyComposition {

    //   type First <: composition.Second#Dagger
    //   type Second <: composition.First#Dagger
    // }

    // F#Dagger#In = Second#Dagger#Out
  }

  case class Composition[
    F <: AnyGraphMorphism,
    S <: AnyGraphMorphism { 
      
      type In = F#Out
    }
  ]
  (val first: F, val second: S) extends AnyComposition { cc =>

    lazy val label: String = s"(${first.label} >=> ${second.label})"

    type First = F
    type Second = S

    type     In = First#In
    lazy val in = first.in: In

    type     Out = Second#Out
    lazy val out = second.out: Out

    type Dagger = Composition[Second#Dagger, First#Dagger { type In = Second#Dagger#Out}] { type In = cc.Out; type Out = cc.In }
    lazy val dagger: Dagger = 
    Composition[Second#Dagger, First#Dagger { type In = Second#Dagger#Out}](
      second.dagger,
      first.dagger.asInstanceOf[First#Dagger { type In = Second#Dagger#Out}]
    ).asInstanceOf[Dagger]
  }


  /* Basic aliases */

  // \otimes symbol: f ⊗ s: F ⊗ S
  type ⊗[F <: AnyGraphType, S <: AnyGraphType] = Tensor[F, S]

  // \oplus symbol: f ⊕ s: F ⊕ S
  type ⊕[F <: AnyGraphType, S <: AnyGraphType] = Biproduct[F, S]

  type >=>[F <: AnyGraphMorphism, S <: AnyGraphMorphism { type In = F#Out }] = Composition[F, S]

  implicit def graphTypeOps[F <: AnyGraphType](f: F):
        GraphTypeOps[F] =
    new GraphTypeOps[F](f)

  class GraphTypeOps[F <: AnyGraphType](f: F) {

    def ⊗[S <: AnyGraphType](s: S):
      Tensor[F, S] =
      Tensor(f, s)

    def ⊕[S <: AnyGraphType](s: S):
      Biproduct[F, S] =
      Biproduct(f, s)

    // def >=>[S <: AnyGraphMorphism { type In = F#Out }](s: S):
    //   Composition[F, S] = 
    //   Composition(f, s)

    import evals._
    def evalOn[I, O](input: F#In := I)
      (implicit eval: EvalPathOn[I, F, O]): F#Out := O = eval(f)(input)
  }

  class GraphObjectOps[O <: AnyGraphObject](val obj: O) {

    import monoidalStructures._

    def ⊗[S <: AnyGraphObject](other: S): TensorObj[O, S] = TensorObj(obj, other)
    def ⊕[S <: AnyGraphObject](other: S): BiproductObj[O, S] = BiproductObj(obj, other)
  }

  implicit def graphMorphismOps[F <: AnyGraphMorphism](f: F):
        GraphMorphismOps[F] =
    new GraphMorphismOps[F](f)


  class GraphMorphismOps[P <: AnyGraphMorphism](val p: P) {

    def >=>[
      S <: AnyGraphMorphism {  
        
        type In = P#Out
      }
    ]
    (s: S): Composition[P, S] = Composition[P, S](p, s)

    import monoidalStructures._

    def ⊗[Q <: AnyGraphMorphism](q: Q): TensorMorph[P,Q] = TensorMorph(p,q)

    def ⊕[Q <: AnyGraphMorphism](q: Q): BiproductMorph[P,Q] = BiproductMorph(p,q)
  }

}
