package ohnosequences.scarph

import scala.collection.mutable

/* You can use this trait together with AutoLabel for labeling. If you want to transform it (e.g. add a prefix) provide an implicit function for relabeling. */
case class SchemaLabeler[Obj <: AnyGraphObject](val relabel: String => String) {
  final def apply(lbl: String): String = relabel(lbl)
}

/* It has to be instantiated as a `case object` and its name will be used as a base label */
trait AutoLabel[Obj <: AnyGraphObject] extends AnyGraphType {
    _ : Singleton with Product =>

  val relabel: SchemaLabeler[Obj]

  final val label: String = relabel(this.productPrefix)
}

/* This is a very generic trait: a graph schema contains lists of its elements */
trait AnyGraphSchema extends AnyGraphType {

  def vertices:   Set[AnyVertex]
  def edges:      Set[AnyEdge]
  def valueTypes: Set[AnyValueType]
  def properties: Set[AnyProperty]
}

/*
  This class provides some tricks to reduce boilerplate in the schema definition:

   - labels are issued automatically based on the objects names, but can be customized
*/
abstract class GraphSchema extends AnyGraphSchema { schema =>

  /* These traits automatically add given element to the corresponding list */
  trait SchemaVertex    extends AnyVertex
  trait SchemaEdge      extends AnyEdge
  trait SchemaProperty  extends AnyProperty
  trait SchemaValueType extends AnyValueType

  /* A defalt instance of SchemaLabeler which prepends each "local" label with the schema label (with a '.' separator) */
  def defaultLabeler[Obj <: AnyGraphObject]: SchemaLabeler[Obj] = SchemaLabeler { lbl =>
    Seq(
      schema.label,
      lbl
    ).mkString(".")
  }

  /* These classes are similar to ones defined in GraphObjects, but require their instances to be `case object`s (`Singleton with Product`) to take advantage of automatic labeling based on their object name */
  abstract class vertex(implicit val relabel: SchemaLabeler[AnyVertex] = defaultLabeler)
    extends SchemaVertex
    with AutoLabel[AnyVertex] { _ : Singleton with Product => }

  abstract class edge[
    S <: AnyArity.OfVertices,
    T <: AnyArity.OfVertices
  ](st: (S, T))(implicit
    val relabel: SchemaLabeler[AnyEdge] = defaultLabeler
  ) extends Relation[S, T](st)
    with SchemaEdge
    with AutoLabel[AnyEdge] { _ : Singleton with Product => }

  abstract class property[
    O <: AnyArity.OfElements,
    V <: AnyArity.OfValueTypes
  ](val ov: (O,V))(implicit
    val relabel: SchemaLabeler[AnyValueType] = defaultLabeler
  ) extends Relation[O, V](ov)
    with SchemaProperty { _ : Singleton with Product =>

    // NOTE: this is not AutoLabeled, but similar: we just prepend the owner label
    final val label: String =
      Seq(source.label, this.productPrefix).mkString(".")
  }

  abstract class valueOfType[V](implicit
    val valueTag: reflect.ClassTag[V],
    val relabel: SchemaLabeler[AnyValueType] = defaultLabeler
  ) extends SchemaValueType
    with AutoLabel[AnyValueType] { _ : Singleton with Product =>

    type Val = V
  }
}
