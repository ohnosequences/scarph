
```scala
package ohnosequences

package object scarph {
```

## Type aliases
### Objects

```scala
  // \otimes symbol: f ⊗ s: F ⊗ S
  type ⊗[F <: AnyGraphObject, S <: AnyGraphObject] = TensorObj[F, S]

  type unit = TensorUnit.type
  val  unit: unit = TensorUnit

  // \oplus symbol: f ⊕ s: F ⊕ S
  type ⊕[F <: AnyGraphObject, S <: AnyGraphObject] = BiproductObj[F, S]

  type zero = BiproductZero.type
  val  zero: zero = BiproductZero
```

### Morphisms

```scala
  // strict:
  type ==>[A <: AnyGraphObject, B <: AnyGraphObject] = AnyGraphMorphism { type In = A; type Out = B }

  // non-strict:
  type -->[A <: AnyGraphObject, B <: AnyGraphObject] = AnyGraphMorphism { type In <: A; type Out <: B }

  type DaggerOf[M <: AnyGraphMorphism] = M#Out --> M#In

  type >=>[F <: AnyGraphMorphism, S <: AnyGraphMorphism { type In = F#Out }] = Composition[F, S]
```

### Properties

```scala
  type get[P <: AnyProperty] = outV[P]
  def get[P <: AnyProperty](p: P): get[P] = outV[P](p)

  type lookup[P <: AnyProperty] = inV[P]
  def lookup[P <: AnyProperty](p: P): lookup[P] = inV[P](p)
```

## Minimal necessary syntax

```scala
  // implicit def graphObjectOps[O <: AnyGraphObject](o: O):
  //   GraphObjectOps[O] =
  //   GraphObjectOps[O](o)
  implicit class GraphObjectOps[O <: AnyGraphObject](val obj: O) extends AnyVal {

    def ⊗[S <: AnyGraphObject](other: S): O ⊗ S = TensorObj(obj, other)
    def ⊕[S <: AnyGraphObject](other: S): O ⊕ S = BiproductObj(obj, other)
  }

  // implicit def graphMorphismOps[M <: AnyGraphMorphism](m: M):
  //   GraphMorphismOps[M] =
  //   GraphMorphismOps[M](m)
  implicit class GraphMorphismOps[F <: AnyGraphMorphism](val f: F) extends AnyVal {

    def >=>[S <: AnyGraphMorphism { type In = F#Out }](s: S): F >=> S = Composition(f, s)

    def ⊗[S <: AnyGraphMorphism](q: S): TensorMorph[F, S] = TensorMorph(f, q)
    def ⊕[S <: AnyGraphMorphism](q: S): BiproductMorph[F, S] = BiproductMorph(f, q)
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