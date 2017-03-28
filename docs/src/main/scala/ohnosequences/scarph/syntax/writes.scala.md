
```scala
package ohnosequences.scarph

import ohnosequences.cosas.types._

case class AddVertexSyntax[G](u: unit := G) extends AnyVal {

  def add[V <: AnyVertex, RV](v: V)
    (implicit
      adder: CanAddVertices[G, V, RV]
    ): V := RV = {
      adder.addVertex(u.value)(v)
    }
}

case class AddEdgeSyntax[E <: AnyEdge](e: E) {

  def add[RE, RS, RT](
    src: E#Source := RS,
    tgt: E#Target := RT
  )(implicit
    adder: CanAddEdges[RS, E, RE, RT]
  ): E := RE = {
    adder.addEdge(e)(src, tgt)
  }
}

case class SetPropertySyntax[E <: AnyGraphElement, RE](e: E := RE) {

  def set[P <: AnyProperty, V <: P#Target#Val](
    p: P,
    v: V
  )(implicit
    setter: CanSetProperties[E, RE, P, V]
  ): E := RE = {
    setter.setProperty(e, p, v)
  }
}

```




[test/scala/ohnosequences/scarph/asserts.scala]: ../../../../../test/scala/ohnosequences/scarph/asserts.scala.md
[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummy.scala.md
[test/scala/ohnosequences/scarph/impl/writes.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/writes.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[test/scala/ohnosequences/scarph/implicitSearch.scala]: ../../../../../test/scala/ohnosequences/scarph/implicitSearch.scala.md
[test/scala/ohnosequences/scarph/SchemaCreation.scala]: ../../../../../test/scala/ohnosequences/scarph/SchemaCreation.scala.md
[main/scala/ohnosequences/scarph/arities.scala]: ../arities.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: ../schemas.scala.md
[main/scala/ohnosequences/scarph/predicates.scala]: ../predicates.scala.md
[main/scala/ohnosequences/scarph/package.scala]: ../package.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: ../objects.scala.md
[main/scala/ohnosequences/scarph/impl/distributivity.scala]: ../impl/distributivity.scala.md
[main/scala/ohnosequences/scarph/impl/tensors.scala]: ../impl/tensors.scala.md
[main/scala/ohnosequences/scarph/impl/evals.scala]: ../impl/evals.scala.md
[main/scala/ohnosequences/scarph/impl/category.scala]: ../impl/category.scala.md
[main/scala/ohnosequences/scarph/impl/biproducts.scala]: ../impl/biproducts.scala.md
[main/scala/ohnosequences/scarph/impl/relations.scala]: ../impl/relations.scala.md
[main/scala/ohnosequences/scarph/syntax/package.scala]: package.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: objects.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/writes.scala]: writes.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: ../morphisms.scala.md
[main/scala/ohnosequences/scarph/tensor.scala]: ../tensor.scala.md
[main/scala/ohnosequences/scarph/axioms.scala]: ../axioms.scala.md
[main/scala/ohnosequences/scarph/isomorphisms.scala]: ../isomorphisms.scala.md
[main/scala/ohnosequences/scarph/writes.scala]: ../writes.scala.md
[main/scala/ohnosequences/scarph/rewrites.scala]: ../rewrites.scala.md
[main/scala/ohnosequences/scarph/biproduct.scala]: ../biproduct.scala.md