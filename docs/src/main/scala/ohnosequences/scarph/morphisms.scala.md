
```scala
package ohnosequences.scarph

import ohnosequences.cosas.{ zero => _, _ }, types._
```

Morphisms are spans

```scala
trait AnyGraphMorphism extends AnyGraphType {

  type In <: AnyGraphObject
  val  in: In

  type Out <: AnyGraphObject
  val  out: Out

  type Dagger <: AnyGraphMorphism
  val  dagger: Dagger
}

trait AnyMorphismTransform extends Any {

  type InMorph <: AnyGraphMorphism
  type OutMorph

  def apply(morph: InMorph): OutMorph
}
```

Sequential sition of two morphisms

```scala
sealed trait AnyComposition extends AnyGraphMorphism { composition =>

  type First <: AnyGraphMorphism
  type Second <: AnyGraphMorphism // NOTE should be { type In = First#Out }

  type In  >: First#In <: First#In
  type Out >: Second#Out <: Second#Out
}

case class Composition[
  F <: AnyGraphMorphism,
  S <: AnyGraphMorphism //{ type In = F#Out }
] (val first: F, val second: S) extends AnyComposition { cc =>

  type First = F
  type Second = S

  type     In = First#In
  lazy val in: In = first.in: In

  type     Out = Second#Out
  lazy val out: Out = second.out: Out

  type     Dagger = Composition[Second#Dagger, First#Dagger]
  lazy val dagger: Dagger = Composition(second.dagger, first.dagger)

  lazy val label: String = s"(${first.label} >=> ${second.label})"
}

trait AnyPrimitiveMorph extends AnyGraphMorphism { morph =>

  type Dagger <: AnyPrimitiveMorph {
    type Dagger >: morph.type <: AnyPrimitiveMorph
  }
}

// id: X → X
case class id[X <: AnyGraphObject](val obj: X) extends AnyPrimitiveMorph {

  type Obj = X

  type     In = Obj
  lazy val in: In = obj

  type     Out = Obj
  lazy val out: Out = obj

  type     Dagger = id[Obj]
  lazy val dagger: Dagger = id(obj)

  lazy val label: String = s"id(${obj.label})"
}


case class target[E <: AnyRelation](val relation: E) extends AnyPrimitiveMorph {

  type Relation = E

  type     In = Relation
  lazy val in: In = relation

  type     Out = Relation#Target
  lazy val out: Out = relation.target

  type     Dagger = inE[Relation]
  lazy val dagger: Dagger = inE(relation)

  lazy val label: String = s"target(${relation.label})"
}

case class inE[E <: AnyRelation](val relation: E) extends AnyPrimitiveMorph {

  type Relation = E

  type     Out = Relation
  lazy val out: Out = relation

  type     In = Relation#Target
  lazy val in: In = relation.target

  type     Dagger = target[Relation]
  lazy val dagger: Dagger = target(relation)


  lazy val label: String = s"inE(${relation.label})"
}

case class source[E <: AnyRelation](val relation: E) extends AnyPrimitiveMorph {

  type Relation = E

  type     In = Relation
  lazy val in: In = relation

  type     Out = Relation#Source
  lazy val out: Out = relation.source

  type     Dagger = outE[Relation]
  lazy val dagger: Dagger = outE(relation)

  lazy val label: String = s"source(${relation.label})"
}

case class outE[E <: AnyRelation](val relation: E) extends AnyPrimitiveMorph {

  type Relation = E

  type     Out = Relation
  lazy val out: Out = relation

  type     In = Relation#Source
  lazy val in: In = relation.source

  type     Dagger = source[Relation]
  lazy val dagger: Dagger = source(relation)

  lazy val label: String = s"outE(${relation.label})"
}


case class outV[E <: AnyRelation](val relation: E) extends AnyPrimitiveMorph {

  type Relation = E

  type     In = Relation#Source
  lazy val in: In = relation.source

  type     Out = Relation#Target
  lazy val out: Out = relation.target

  type     Dagger = inV[Relation]
  lazy val dagger: Dagger = inV(relation)

  lazy val label: String = s"outV(${relation.label})"
}

case class inV[E <: AnyRelation](val relation: E) extends AnyPrimitiveMorph {

  type Relation = E

  type     Out = Relation#Source
  lazy val out: Out = relation.source

  type     In = Relation#Target
  lazy val in: In = relation.target

  type     Dagger = outV[Relation]
  lazy val dagger: Dagger = outV(relation)

  lazy val label: String = s"inV(${relation.label})"
}

```




[test/scala/ohnosequences/scarph/asserts.scala]: ../../../../test/scala/ohnosequences/scarph/asserts.scala.md
[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: ../../../../test/scala/ohnosequences/scarph/impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: ../../../../test/scala/ohnosequences/scarph/impl/dummy.scala.md
[test/scala/ohnosequences/scarph/impl/writes.scala]: ../../../../test/scala/ohnosequences/scarph/impl/writes.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[test/scala/ohnosequences/scarph/implicitSearch.scala]: ../../../../test/scala/ohnosequences/scarph/implicitSearch.scala.md
[test/scala/ohnosequences/scarph/SchemaCreation.scala]: ../../../../test/scala/ohnosequences/scarph/SchemaCreation.scala.md
[main/scala/ohnosequences/scarph/arities.scala]: arities.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: schemas.scala.md
[main/scala/ohnosequences/scarph/predicates.scala]: predicates.scala.md
[main/scala/ohnosequences/scarph/package.scala]: package.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: objects.scala.md
[main/scala/ohnosequences/scarph/impl/distributivity.scala]: impl/distributivity.scala.md
[main/scala/ohnosequences/scarph/impl/tensors.scala]: impl/tensors.scala.md
[main/scala/ohnosequences/scarph/impl/evals.scala]: impl/evals.scala.md
[main/scala/ohnosequences/scarph/impl/category.scala]: impl/category.scala.md
[main/scala/ohnosequences/scarph/impl/biproducts.scala]: impl/biproducts.scala.md
[main/scala/ohnosequences/scarph/impl/relations.scala]: impl/relations.scala.md
[main/scala/ohnosequences/scarph/syntax/package.scala]: syntax/package.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: syntax/objects.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/writes.scala]: syntax/writes.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: morphisms.scala.md
[main/scala/ohnosequences/scarph/tensor.scala]: tensor.scala.md
[main/scala/ohnosequences/scarph/axioms.scala]: axioms.scala.md
[main/scala/ohnosequences/scarph/isomorphisms.scala]: isomorphisms.scala.md
[main/scala/ohnosequences/scarph/writes.scala]: writes.scala.md
[main/scala/ohnosequences/scarph/rewrites.scala]: rewrites.scala.md
[main/scala/ohnosequences/scarph/biproduct.scala]: biproduct.scala.md