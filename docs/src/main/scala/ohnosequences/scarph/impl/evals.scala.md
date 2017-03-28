
```scala
package ohnosequences.scarph.impl

import ohnosequences.scarph._
import ohnosequences.cosas._, types._
```

Transforms a morphism to a function

```scala
trait AnyEval extends AnyMorphismTransform {

  type RawInput
  type RawOutput

  def raw_apply(morph: InMorph): RawInput => RawOutput

  // Same but with tags:
  type Input  = InMorph#In  := RawInput
  type Output = InMorph#Out := RawOutput

  type OutMorph = Input => Output

  final def apply(morph: InMorph): OutMorph = { input: Input =>
    (morph.out: InMorph#Out) := raw_apply(morph)(input.value)
  }

  def present(morph: InMorph): Seq[String] = Seq(morph.label)
}

@annotation.implicitNotFound(msg = "Cannot evaluate morphism ${M} on input ${I}, output ${O}")
case class Eval[M <: AnyGraphMorphism, I, O](
  val raw: M => (I => O)
) extends AnyEval {

  type InMorph = M
  type RawInput = I
  type RawOutput = O

  final def raw_apply(morph: InMorph): RawInput => RawOutput = raw(morph)
}

final class evaluate[M <: AnyGraphMorphism, I, O](
  val f: M,
  val eval: Eval[M, I, O]
) {

  final def on(input: M#In := I): M#Out := O = eval(f).apply(input)

  // FIXME: this should output the computational behavior of the eval here
  final def evalPlan: String = eval.present(f).mkString("")
}

class evalWithIn[M <: AnyGraphMorphism, I] {

  def apply[O](m: M)(implicit
    eval: Eval[M, I, O]
  ):  evaluate[M, I, O] =
  new evaluate[M, I, O](m, eval)
}

class evalWithInOut[M <: AnyGraphMorphism, I, O] {

  def apply(m: M)(implicit
    eval: Eval[M, I, O]
  ):  evaluate[M, I, O] =
  new evaluate[M, I, O](m, eval)
}


case object evaluate {

  def apply[M <: AnyGraphMorphism, I, O](m: M)(i: M#In := I)
    (implicit eval: Eval[M, I, O]): M#Out := O =
      new evaluate[M, I, O](m, eval).on(i)

  def withIn[M <: AnyGraphMorphism, I]:
      evalWithIn[M, I] =
  new evalWithIn[M, I] {}

  def withInOut[M <: AnyGraphMorphism, I, O]:
      evalWithInOut[M, I, O] =
  new evalWithInOut[M, I, O] {}

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
[main/scala/ohnosequences/scarph/impl/distributivity.scala]: distributivity.scala.md
[main/scala/ohnosequences/scarph/impl/tensors.scala]: tensors.scala.md
[main/scala/ohnosequences/scarph/impl/evals.scala]: evals.scala.md
[main/scala/ohnosequences/scarph/impl/category.scala]: category.scala.md
[main/scala/ohnosequences/scarph/impl/biproducts.scala]: biproducts.scala.md
[main/scala/ohnosequences/scarph/impl/relations.scala]: relations.scala.md
[main/scala/ohnosequences/scarph/syntax/package.scala]: ../syntax/package.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: ../syntax/objects.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: ../syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/writes.scala]: ../syntax/writes.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: ../morphisms.scala.md
[main/scala/ohnosequences/scarph/tensor.scala]: ../tensor.scala.md
[main/scala/ohnosequences/scarph/axioms.scala]: ../axioms.scala.md
[main/scala/ohnosequences/scarph/isomorphisms.scala]: ../isomorphisms.scala.md
[main/scala/ohnosequences/scarph/writes.scala]: ../writes.scala.md
[main/scala/ohnosequences/scarph/rewrites.scala]: ../rewrites.scala.md
[main/scala/ohnosequences/scarph/biproduct.scala]: ../biproduct.scala.md