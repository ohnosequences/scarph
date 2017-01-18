package ohnosequences.scarph

import ohnosequences.cosas.{ zero => _, _ }, types._

/* Morphisms are spans */
trait AnyGraphMorphism extends AnyGraphType {

  type In <: AnyGraphObject
  val  in: In

  type Out <: AnyGraphObject
  val  out: Out

  type Dagger <: AnyGraphMorphism
  val  dagger: Dagger
}

/* Sequential sition of two morphisms */
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
