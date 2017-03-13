package ohnosequences.scarph

import scala.collection.mutable

trait AnyGraphSchema extends AnyGraphType {

  val vertices: Set[AnyVertex]
  val edges: Set[AnyEdge]
  val valueTypes: Set[AnyValueType]
  val properties: Set[AnyProperty]
}

case class SchemaLabeler[Obj <: AnyGraphObject](val relabel: String => String) {
  final def apply(lbl: String): String = relabel(lbl)
}

abstract class GraphSchema extends AnyGraphSchema { schema =>

  private val _vertices:   mutable.Set[AnyVertex]    = mutable.Set()
  private val _edges:      mutable.Set[AnyEdge]      = mutable.Set()
  private val _valueTypes: mutable.Set[AnyValueType] = mutable.Set()
  private val _properties: mutable.Set[AnyProperty]  = mutable.Set()

  lazy final val vertices:   Set[AnyVertex]    = _vertices.toSet
  lazy final val edges:      Set[AnyEdge]      = _edges.toSet
  lazy final val valueTypes: Set[AnyValueType] = _valueTypes.toSet
  lazy final val properties: Set[AnyProperty]  = _properties.toSet

  def defaultLabeler[Obj <: AnyGraphObject]: SchemaLabeler[Obj] = SchemaLabeler { lbl =>
    Seq(
      schema.label,
      lbl
    ).mkString(".")
  }

  /* You can use this class to automize vertices labeling. It has to be instantiated as a `case object` and its name will be used as a base label. If you want to transform it (e.g. add a prefix) provide an implicit function for relabeling. */
  abstract class vertex(implicit relabel: SchemaLabeler[AnyVertex] = defaultLabeler)
    extends AnyVertex { _ : Singleton with Product =>

      final val label: String = relabel(this.productPrefix)
      schema._vertices + this
    }

  abstract class edge[
    S <: AnyArity.OfVertices,
    T <: AnyArity.OfVertices
  ](st: (S, T))(implicit
    relabel: SchemaLabeler[AnyEdge] = defaultLabeler
  ) extends AnyEdge { _ : Singleton with Product =>

    type SourceArity = S
    lazy val sourceArity: SourceArity = st._1

    type TargetArity = T
    lazy val targetArity: TargetArity = st._2

    final val label: String = relabel(this.productPrefix)
    schema._edges + this
  }

  abstract class valueOfType[V](implicit
    val valueTag: reflect.ClassTag[V],
    relabel: SchemaLabeler[AnyValueType] = defaultLabeler
  ) extends AnyValueType { _ : Singleton with Product =>

    type Val = V

    final val label: String = relabel(this.productPrefix)
    schema._valueTypes + this
  }

  abstract class property[
    O <: AnyArity.OfElements,
    V <: AnyArity.OfValueTypes
  ](val st: (O,V))(implicit
    relabel: SchemaLabeler[AnyValueType] = defaultLabeler
  ) extends AnyProperty { _ : Singleton with Product =>

    type SourceArity = O
    lazy val sourceArity: SourceArity = st._1

    type TargetArity = V
    lazy val targetArity: TargetArity = st._2

    final val label: String = relabel(
      Seq(
        source.label,
        this.productPrefix
      ).mkString(".")
    )
    schema._properties + this
  }

}
