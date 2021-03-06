
```scala
package ohnosequences.scarph.test

import ohnosequences.scarph._, syntax._
import ohnosequences.scarph.test.twitter._

case object queries {

  class TestBlock(val label: String)


  object categoryStructure extends TestBlock("Categorical structure"){

    val q_id = id(user)
    val q_comp1 = q_id >=> q_id
    val q_comp2 = q_comp1 >=> q_id >=> q_comp1
  }

  object tensorStructure extends TestBlock("Tensor structure") {

    val q_symmetry = symmetry(user, tweet)
    val q_fromUnit = fromUnit(user)
    val q_toUnit = toUnit(user)
    val q_tensor = id(user) ⊗ id(user) ⊗ id(user)
    val q_dupl = duplicate(user) ⊗ id(user)
    val q_match = matchUp(user)
    val q_comp =
      q_dupl >=>
      q_tensor >=>
      (id(user ⊗ user) ⊗ duplicate(user)) >=>
      (q_match ⊗ q_match) >=>
      q_match
    val q_trace = tensorTrace(toUnit(user) ⊗ fromUnit(user) >=> symmetry(unit, user))
    // val q_trace_Wrong = tensorTrace(q_match)
  }

  object biproductStructure extends TestBlock("Biproduct structure") {

    val q_inj   = rightInj((user ⊕ user) ⊕ tweet)
    val q_bip   = id(user) ⊕ id(user) ⊕ id(tweet)
    val q_fork  = fork(user) ⊕ id(tweet)
    val q_merge = merge(user)
    val q_comp  =
      q_fork >=>
      q_bip >=>
      (id(user ⊕ user) ⊕ fork(tweet)) >=>
      (merge(user) ⊕ merge(tweet)) >=>
      rightProj(user ⊕ tweet)
  }

  object graphStructure extends TestBlock("Graph structure") {

    val q_outV  = outV(posted)
    val q_inV   = inV(posted)
    val q_compV = q_outV >=> q_inV

    val q_outE  = outE(posted) >=> target(posted)
    val q_inE   = inE(posted) >=> source(posted)
    val q_compE = q_outE >=> q_inE
  }

  object propertyStructure extends TestBlock("Property structure") {

    val q_getV = get(user.age)
    val q_lookupV = inV(user.name)
    val q_compV = q_lookupV >=> q_getV

    val q_getE = get(posted.time)
    val q_lookupE = lookup(posted.time)
    val q_compE = q_getE >=> q_lookupE
  }

  object predicateStructure extends TestBlock("Predicate structure") {

    val pred = user ? (user.age > 10) and (user.name =/= "")

    val q_quant = quantify(pred)
    val q_coerce = coerce(pred)
    val q_comp = q_quant >=> q_coerce
  }
}

```




[main/scala/ohnosequences/scarph/axioms.scala]: ../../../../main/scala/ohnosequences/scarph/axioms.scala.md
[main/scala/ohnosequences/scarph/tensor.scala]: ../../../../main/scala/ohnosequences/scarph/tensor.scala.md
[main/scala/ohnosequences/scarph/predicates.scala]: ../../../../main/scala/ohnosequences/scarph/predicates.scala.md
[main/scala/ohnosequences/scarph/impl/biproducts.scala]: ../../../../main/scala/ohnosequences/scarph/impl/biproducts.scala.md
[main/scala/ohnosequences/scarph/impl/tensors.scala]: ../../../../main/scala/ohnosequences/scarph/impl/tensors.scala.md
[main/scala/ohnosequences/scarph/impl/evals.scala]: ../../../../main/scala/ohnosequences/scarph/impl/evals.scala.md
[main/scala/ohnosequences/scarph/impl/distributivity.scala]: ../../../../main/scala/ohnosequences/scarph/impl/distributivity.scala.md
[main/scala/ohnosequences/scarph/impl/relations.scala]: ../../../../main/scala/ohnosequences/scarph/impl/relations.scala.md
[main/scala/ohnosequences/scarph/impl/category.scala]: ../../../../main/scala/ohnosequences/scarph/impl/category.scala.md
[main/scala/ohnosequences/scarph/rewrites.scala]: ../../../../main/scala/ohnosequences/scarph/rewrites.scala.md
[main/scala/ohnosequences/scarph/package.scala]: ../../../../main/scala/ohnosequences/scarph/package.scala.md
[main/scala/ohnosequences/scarph/arities.scala]: ../../../../main/scala/ohnosequences/scarph/arities.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: ../../../../main/scala/ohnosequences/scarph/objects.scala.md
[main/scala/ohnosequences/scarph/writes.scala]: ../../../../main/scala/ohnosequences/scarph/writes.scala.md
[main/scala/ohnosequences/scarph/biproduct.scala]: ../../../../main/scala/ohnosequences/scarph/biproduct.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: ../../../../main/scala/ohnosequences/scarph/schemas.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: ../../../../main/scala/ohnosequences/scarph/morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/package.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/package.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/objects.scala.md
[main/scala/ohnosequences/scarph/syntax/writes.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/writes.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/isomorphisms.scala]: ../../../../main/scala/ohnosequences/scarph/isomorphisms.scala.md
[test/scala/ohnosequences/scarph/TwitterQueries.scala]: TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: impl/dummy.scala.md
[test/scala/ohnosequences/scarph/impl/writes.scala]: impl/writes.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: TwitterSchema.scala.md
[test/scala/ohnosequences/scarph/asserts.scala]: asserts.scala.md
[test/scala/ohnosequences/scarph/SchemaCreation.scala]: SchemaCreation.scala.md
[test/scala/ohnosequences/scarph/implicitSearch.scala]: implicitSearch.scala.md