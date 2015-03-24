package ohnosequences.scarph

object graphTypes {

  import monoidalStructures._
  import ohnosequences.cosas._, types._, properties._

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
  trait vertex extends AnyVertex
  class Vertex extends AnyVertex {

    lazy val label = this.toString
  }

  /* Edges connect vertices and have in/out arities */
  trait AnyEdge extends AnyGraphElement {
    
    type SourceVertex <: AnyVertex
    val  sourceVertex: SourceVertex

    type TargetVertex <: AnyVertex
    val  targetVertex: TargetVertex

    // TODO: add arities
  }

  abstract class edge[
    S <: AnyVertex,
    T <: AnyVertex
  ]( st: (S, T) ) extends AnyEdge {

    type SourceVertex = S
    lazy val sourceVertex = st._1: S

    type TargetVertex = T
    lazy val targetVertex = st._2: T
  }
  /* This constructor encourages to use this syntax: Edge(user -> tweet) */
  abstract class Edge[
    S <: AnyVertex,
    T <: AnyVertex
  ]( st: (S, T) ) extends AnyEdge {

    type SourceVertex = S
    lazy val sourceVertex = st._1: S

    type TargetVertex = T
    lazy val targetVertex = st._2: T

    lazy val label = this.toString
  }

  object AnyEdge {

    type From[S <: AnyVertex] = AnyEdge { type SourceVertex = S }
    type   To[T <: AnyVertex] = AnyEdge { type TargetVertex = T }
  }

  /* Property values have raw types that are covered as graph objects */
  trait AnyValueType extends AnyProperty with AnyGraphObject

  abstract class valueOfType[R] extends AnyValueType { type Raw = R }
  
  abstract class ValueType[R](val label: String) 
    extends AnyValueType { type Raw = R }

  /* This is like an edge between an element and a raw type */
  trait AnyGraphProperty extends AnyGraphType {

    type Owner <: AnyGraphElement
    val  owner: Owner

    type Value <: AnyValueType
    val  value: Value
  }

  abstract class property[O <: AnyGraphElement, V <: AnyValueType](val ow: (O,V)) extends AnyGraphProperty {

    type Owner = O
    val owner: O = ow._1
    type Value = V
    val value: V = ow._2
  }

  abstract class Property[O <: AnyGraphElement, V <: AnyValueType]
    (val owner: O, val value: V) extends AnyGraphProperty {

    type Owner = O
    type Value = V

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

  type -->[A <: AnyGraphObject, B <: AnyGraphObject] = AnyGraphMorphism { type In = A; type Out = B }

  /* Sequential composition of two morphisms */
  sealed trait AnyComposition extends AnyGraphMorphism {

    type First <: AnyGraphMorphism
    type Second <: AnyGraphMorphism //{ type In = First#Out }

    type In  <: First#In
    type Out <: Second#Out

    type Dagger <: Composition[Second#Dagger, First#Dagger]
  }

  class Composition[
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

    type     Dagger =     Composition[Second#Dagger, First#Dagger]
    lazy val dagger = new Composition(second.dagger, first.dagger): Dagger

    lazy val label: String = s"(${first.label} >=> ${second.label})"
  }


  /* Basic aliases */
  type >=>[F <: AnyGraphMorphism, S <: AnyGraphMorphism { type In = F#Out }] = Composition[F, S]


  implicit def graphObjectOps[O <: AnyGraphObject](o: O):
        GraphObjectOps[O] =
    new GraphObjectOps[O](o)

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
          new Composition[F, S](f, s)

    import monoidalStructures._

    def ⊗[S <: AnyGraphMorphism](q: S): TensorMorph[F, S] = TensorMorph(f, q)
    def ⊕[S <: AnyGraphMorphism](q: S): BiproductMorph[F, S] = BiproductMorph(f, q)

    import evals._
    def evalOn[I, O](input: F#In := I)
      (implicit eval: EvalPathOn[I, F, O]): F#Out := O = eval(f)(input)
  }

}
