
```scala
package ohnosequences.scarph.impl

import ohnosequences.scarph._

trait Relations {

  type RawEdge
  type RawSource
  type RawTarget

  def raw_outV(edge: AnyEdge)(v: RawSource): RawTarget
  def raw_inV(edge: AnyEdge)(v: RawTarget): RawSource

  def raw_outE(edge: AnyEdge)(v: RawSource): RawEdge
  def raw_source(edge: AnyEdge)(e: RawEdge): RawSource

  def raw_inE(edge: AnyEdge)(v: RawTarget): RawEdge
  def raw_target(edge: AnyEdge)(e: RawEdge): RawTarget


  implicit final def eval_outV[E <: AnyEdge]: Eval[outV[E], RawSource, RawTarget] = new Eval( morph => raw_outV(morph.relation) )
  implicit final def eval_inV[E <: AnyEdge]:  Eval[inV[E],  RawTarget, RawSource] = new Eval( morph => raw_inV(morph.relation) )

  implicit final def eval_inE[E <: AnyEdge]:  Eval[inE[E],  RawTarget, RawEdge] = new Eval( morph => raw_inE(morph.relation) )
  implicit final def eval_outE[E <: AnyEdge]: Eval[outE[E], RawSource, RawEdge] = new Eval( morph => raw_outE(morph.relation) )

  implicit final def eval_source[E <: AnyEdge]: Eval[source[E], RawEdge, RawSource] = new Eval( morph => raw_source(morph.relation) )
  implicit final def eval_target[E <: AnyEdge]: Eval[target[E], RawEdge, RawTarget] = new Eval( morph => raw_target(morph.relation) )

}

```




[main/scala/ohnosequences/scarph/axioms.scala]: ../axioms.scala.md
[main/scala/ohnosequences/scarph/tensor.scala]: ../tensor.scala.md
[main/scala/ohnosequences/scarph/predicates.scala]: ../predicates.scala.md
[main/scala/ohnosequences/scarph/impl/biproducts.scala]: biproducts.scala.md
[main/scala/ohnosequences/scarph/impl/tensors.scala]: tensors.scala.md
[main/scala/ohnosequences/scarph/impl/evals.scala]: evals.scala.md
[main/scala/ohnosequences/scarph/impl/distributivity.scala]: distributivity.scala.md
[main/scala/ohnosequences/scarph/impl/relations.scala]: relations.scala.md
[main/scala/ohnosequences/scarph/impl/category.scala]: category.scala.md
[main/scala/ohnosequences/scarph/rewrites.scala]: ../rewrites.scala.md
[main/scala/ohnosequences/scarph/package.scala]: ../package.scala.md
[main/scala/ohnosequences/scarph/arities.scala]: ../arities.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: ../objects.scala.md
[main/scala/ohnosequences/scarph/writes.scala]: ../writes.scala.md
[main/scala/ohnosequences/scarph/biproduct.scala]: ../biproduct.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: ../schemas.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: ../morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/package.scala]: ../syntax/package.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: ../syntax/objects.scala.md
[main/scala/ohnosequences/scarph/syntax/writes.scala]: ../syntax/writes.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: ../syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/isomorphisms.scala]: ../isomorphisms.scala.md
[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummy.scala.md
[test/scala/ohnosequences/scarph/impl/writes.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/writes.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: ../../../../../test/scala/ohnosequences/scarph/impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[test/scala/ohnosequences/scarph/asserts.scala]: ../../../../../test/scala/ohnosequences/scarph/asserts.scala.md
[test/scala/ohnosequences/scarph/SchemaCreation.scala]: ../../../../../test/scala/ohnosequences/scarph/SchemaCreation.scala.md
[test/scala/ohnosequences/scarph/implicitSearch.scala]: ../../../../../test/scala/ohnosequences/scarph/implicitSearch.scala.md