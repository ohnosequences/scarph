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
  class Vertex(val label: String) extends AnyVertex


  trait AnyArity {
    type Vertex <: AnyVertex
    val  vertex: Vertex
  }
  abstract class Arity[V <:AnyVertex](val vertex: V)
    extends AnyArity { type Vertex = V }

  case class OneOrNone[V <: AnyVertex](v: V) extends Arity[V](v)
  case class AtLeastOne[V <: AnyVertex](v: V) extends Arity[V](v)
  case class ExactlyOne[V <: AnyVertex](v: V) extends Arity[V](v)
  case class ManyOrNone[V <: AnyVertex](v: V) extends Arity[V](v)


  /* Edges connect vertices and have in/out arities */
  trait AnyEdge extends AnyGraphElement {

    type SourceArity <: AnyArity
    val  sourceArity: SourceArity

    type SourceVertex <: SourceArity#Vertex
    val  sourceVertex: SourceVertex


    type TargetArity <: AnyArity
    val  targetArity: TargetArity

    type TargetVertex <: TargetArity#Vertex
    val  targetVertex: TargetVertex
  }

  class Edge[S <: AnyArity, T <: AnyArity]( st: (S, T))(val label: String)
    extends AnyEdge
{

    type SourceArity = S
    lazy val sourceArity = st._1
    type SourceVertex = SourceArity#Vertex
    lazy val sourceVertex = sourceArity.vertex

    type TargetArity = T
    lazy val targetArity = st._2
    type TargetVertex = TargetArity#Vertex
    lazy val targetVertex = targetArity.vertex
  }
  /* This constructor encourages to use this syntax: Edge(user -> tweet)("tweeted") */


  object AnyEdge {

    type From[S <: AnyVertex] = AnyEdge { type SourceVertex = S }
    type   To[T <: AnyVertex] = AnyEdge { type TargetVertex = T }
  }

  /* Property values have raw types that are covered as graph objects */
  trait AnyValueType extends AnyProperty with AnyGraphObject {

    def rawTag: scala.reflect.ClassTag[Raw]
  }

  class ValueOfType[R](val label: String)(implicit val rawTag: scala.reflect.ClassTag[R])
    extends AnyValueType { type Raw = R }

  /* This is like an edge between an element and a raw type */
  trait AnyGraphProperty extends AnyGraphType {

    type Owner <: AnyGraphElement
    val  owner: Owner

    type Value <: AnyValueType
    val  value: Value
  }

  class Property[O <: AnyGraphElement, V <: AnyValueType](val st: (O,V))(val label: String)
    extends AnyGraphProperty
  {

    type Owner = O
    val owner: O = st._1
    type Value = V
    val value: V = st._2
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

  case class Composition[
    F <: AnyGraphMorphism,
    S <: AnyGraphMorphism //{ type In = F#Out }
  ] (val first: F, val second: S) extends AnyComposition { cc =>

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

    def >=>[S <: AnyGraphMorphism { type In = F#Out }](s: S): F >=> S = Composition(f, s)

    import monoidalStructures._

    def ⊗[S <: AnyGraphMorphism](q: S): TensorMorph[F, S] = TensorMorph(f, q)
    def ⊕[S <: AnyGraphMorphism](q: S): BiproductMorph[F, S] = BiproductMorph(f, q)

    import evals._
    //def evalOn[I, O](input: F#In := I)
    //  (implicit eval: Eval[F] { type InVal = I; type OutVal = O }): F#Out := O = eval(f)(input)

    def evalOn[I, O](input: F#In := I)
      (implicit eval: EvalOn[I, F, O]): F#Out := O = eval(f)(input)

    def present(implicit eval: Eval[F]): String = eval.present(f)
  }

}
