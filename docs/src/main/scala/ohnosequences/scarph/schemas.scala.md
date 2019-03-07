
```scala
package ohnosequences.scarph

import scala.collection.mutable
```

You can use this trait together with AutoLabel for labeling. If you want to transform it (e.g. add a prefix) provide an implicit function for relabeling.

```scala
case class SchemaLabeler[Obj <: AnyGraphObject](val relabel: String => String) {
  final def apply(lbl: String): String = relabel(lbl)
}
```

It has to be instantiated as a `case object` and its name will be used as a base label

```scala
trait AutoLabel[Obj <: AnyGraphObject] extends AnyGraphType {
    _ : Singleton with Product =>

  val relabel: SchemaLabeler[Obj]

  final val label: String = relabel(this.productPrefix).replace('.', '_')
}
```

This is a very generic trait: a graph schema contains lists of its elements

```scala
trait AnyGraphSchema extends AnyGraphType {

  def vertices:   Set[AnyVertex]
  def edges:      Set[AnyEdge]
  def valueTypes: Set[AnyValueType]
  def properties: Set[AnyProperty]
}
```


This class provides some tricks to reduce boilerplate in the schema definition:

 - labels are issued automatically based on the objects names, but can be customized


```scala
abstract class GraphSchema extends AnyGraphSchema { schema =>
```

These traits automatically add given element to the corresponding list

```scala
  trait SchemaVertex    extends AnyVertex
  trait SchemaEdge      extends AnyEdge
  trait SchemaProperty  extends AnyProperty
  trait SchemaValueType extends AnyValueType
```

A defalt instance of SchemaLabeler which prepends each "local" label with the schema label (with a '.' separator)

```scala
  def defaultLabeler[Obj <: AnyGraphObject]: SchemaLabeler[Obj] = SchemaLabeler { lbl =>
    Seq(
      schema.label,
      lbl
    ).mkString("_")
  }
```

These classes are similar to ones defined in GraphObjects, but require their instances to be `case object`s (`Singleton with Product`) to take advantage of automatic labeling based on their object name

```scala
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
      Seq(source.label, this.productPrefix).mkString("_")
  }

  abstract class valueOfType[V](implicit
    val valueTag: reflect.ClassTag[V],
    val relabel: SchemaLabeler[AnyValueType] = defaultLabeler
  ) extends SchemaValueType
    with AutoLabel[AnyValueType] { _ : Singleton with Product =>

    type Val = V
  }
}

```




[main/scala/ohnosequences/scarph/axioms.scala]: axioms.scala.md
[main/scala/ohnosequences/scarph/tensor.scala]: tensor.scala.md
[main/scala/ohnosequences/scarph/predicates.scala]: predicates.scala.md
[main/scala/ohnosequences/scarph/impl/biproducts.scala]: impl/biproducts.scala.md
[main/scala/ohnosequences/scarph/impl/tensors.scala]: impl/tensors.scala.md
[main/scala/ohnosequences/scarph/impl/evals.scala]: impl/evals.scala.md
[main/scala/ohnosequences/scarph/impl/distributivity.scala]: impl/distributivity.scala.md
[main/scala/ohnosequences/scarph/impl/relations.scala]: impl/relations.scala.md
[main/scala/ohnosequences/scarph/impl/category.scala]: impl/category.scala.md
[main/scala/ohnosequences/scarph/rewrites.scala]: rewrites.scala.md
[main/scala/ohnosequences/scarph/package.scala]: package.scala.md
[main/scala/ohnosequences/scarph/arities.scala]: arities.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: objects.scala.md
[main/scala/ohnosequences/scarph/writes.scala]: writes.scala.md
[main/scala/ohnosequences/scarph/biproduct.scala]: biproduct.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: schemas.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/package.scala]: syntax/package.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: syntax/objects.scala.md
[main/scala/ohnosequences/scarph/syntax/writes.scala]: syntax/writes.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/isomorphisms.scala]: isomorphisms.scala.md
[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: ../../../../test/scala/ohnosequences/scarph/impl/dummy.scala.md
[test/scala/ohnosequences/scarph/impl/writes.scala]: ../../../../test/scala/ohnosequences/scarph/impl/writes.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: ../../../../test/scala/ohnosequences/scarph/impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[test/scala/ohnosequences/scarph/asserts.scala]: ../../../../test/scala/ohnosequences/scarph/asserts.scala.md
[test/scala/ohnosequences/scarph/SchemaCreation.scala]: ../../../../test/scala/ohnosequences/scarph/SchemaCreation.scala.md
[test/scala/ohnosequences/scarph/implicitSearch.scala]: ../../../../test/scala/ohnosequences/scarph/implicitSearch.scala.md