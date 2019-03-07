
```scala
package ohnosequences.scarph.impl

import ohnosequences.scarph._

trait DaggerCategory {

  implicit final def eval_id[X <: AnyGraphObject, I]:
      Eval[id[X], I, I] =
  new Eval[id[X], I, I]({ _ => raw_input => raw_input })

  // F >=> S
  implicit final def eval_composition[
    I, X, O,
    F <: AnyGraphMorphism,
    S <: AnyGraphMorphism {
      type In = F#Out
    }
  ](implicit
    evalFirst:  Eval[F, I, X],
    evalSecond: Eval[S, X, O]
  ):  Eval[F >=> S, I, O] =
  new Eval[F >=> S, I, O]({ morph => raw_input =>

        val firstResult = evalFirst.raw_apply(morph.first)(raw_input)
        evalSecond.raw_apply(morph.second)(firstResult)
      }) {

    override def present(morph: InMorph): Seq[String] =
      ("(" +: evalFirst.present(morph.first)) ++
      (" >=> " +: evalSecond.present(morph.second) :+ ")")
  }
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