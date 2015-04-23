
```scala
package ohnosequences.scarph

import objects._

object implementations {

  trait AnyTensorImpl extends Any {

    type RawTensor
    def apply(l: RawLeft, r: RawRight): RawTensor

    type RawLeft
    def leftProj(t: RawTensor): RawLeft

    type RawRight
    def rightProj(t: RawTensor): RawRight
  }

  trait TensorImpl[T, L, R] extends Any with AnyTensorImpl {

    type RawTensor = T
    type RawLeft = L
    type RawRight = R
  }


  trait AnyMatchUpImpl extends Any {

    type Raw

    def matchUp(l: Raw, r: Raw): Raw
  }

  trait MatchUpImpl[I] extends Any with AnyMatchUpImpl { type Raw = I }


  trait AnyBiproductImpl extends Any {

    type RawBiproduct
    def apply(l: RawLeft, r: RawRight): RawBiproduct

    type RawLeft
    def leftProj(b: RawBiproduct): RawLeft
    def leftInj(l: RawLeft): RawBiproduct

    type RawRight
    def rightProj(b: RawBiproduct): RawRight
    def rightInj(r: RawRight): RawBiproduct
  }

  trait BiproductImpl[B, L, R] extends Any with AnyBiproductImpl {

    type RawBiproduct = B
    type RawLeft = L
    type RawRight = R
  }


  trait AnyMergeImpl extends Any {

    type Raw

    def merge(l: Raw, r: Raw): Raw
  }

  trait MergeImpl[R] extends Any with AnyMergeImpl { type Raw = R }


  trait AnyZeroImpl extends Any {

    type Raw

    def apply(): Raw
  }

  trait ZeroImpl[R] extends Any with AnyZeroImpl { type Raw = R }


  trait AnyEdgeImpl extends Any {

    type RawEdge

    type RawSource
    def source(e: RawEdge): RawSource

    type RawTarget
    def target(e: RawEdge): RawTarget
  }

  trait EdgeImpl[E, S, T] extends Any with AnyEdgeImpl {

    type RawEdge = E
    type RawSource = S
    type RawTarget = T
  }


  // TODO: probably it makes sense to separate it
  trait AnyVertexInImpl extends Any {

    type Edge <: AnyEdge
    type RawVertex

    type RawInEdge
    def inE(v: RawVertex, e: Edge): RawInEdge

    type RawInVertex
    def inV(v: RawVertex, e: Edge): RawInVertex
  }

  trait VertexInImpl[E <: AnyEdge, V, InE, InV] extends Any with AnyVertexInImpl{

    type Edge = E
    type RawVertex = V
    type RawInEdge = InE
    type RawInVertex = InV
  }


  // TODO: probably it makes sense to separate it
  trait AnyVertexOutImpl extends Any {

    type Edge <: AnyEdge
    type RawVertex

    type RawOutEdge
    def outE(v: RawVertex, e: Edge): RawOutEdge

    type RawOutVertex
    def outV(v: RawVertex, e: Edge): RawOutVertex
  }

  trait VertexOutImpl[E <: AnyEdge, V, OutE, OutV] extends Any with AnyVertexOutImpl {

    type Edge = E
    type RawVertex = V
    type RawOutEdge = OutE
    type RawOutVertex = OutV
  }


  trait AnyPropertyImpl extends Any {

    type Property <: AnyGraphProperty
    type RawElement
    type RawValue

    def get(e: RawElement, p: Property): RawValue

    def lookup(r: RawValue, p: Property): RawElement
  }

  trait PropertyImpl[P <: AnyGraphProperty, RE, RV] extends Any with AnyPropertyImpl {

    type Property = P
    type RawElement = RE
    type RawValue = RV
  }


  trait AnyUnitImpl extends Any {

    // TODO: probably this should be an AnyGraphElement?
    type Object <: AnyGraphObject

    type RawObject
    def fromUnit(u: RawUnit, o: Object): RawObject

    type RawUnit
    def toUnit(s: RawObject): RawUnit
  }

  trait UnitImpl[O <: AnyGraphObject, RO, RU] extends Any with AnyUnitImpl {

    type Object = O
    type RawObject = RO
    type RawUnit = RU
  }


  trait AnyPredicateImpl extends Any {

    type Predicate <: AnyPredicate

    type RawPredicate
    def quantify(e: RawElement, p: Predicate): RawPredicate

    type RawElement
    def coerce(p: RawPredicate): RawElement
  }

  trait PredicateImpl[P <: AnyPredicate, RP, RE] extends Any with AnyPredicateImpl {

    type Predicate = P
    type RawPredicate = RP
    type RawElement = RE
  }

}

```


------

### Index

+ src
  + test
    + scala
      + ohnosequences
        + scarph
          + [TwitterQueries.scala][test/scala/ohnosequences/scarph/TwitterQueries.scala]
          + impl
            + [dummyTest.scala][test/scala/ohnosequences/scarph/impl/dummyTest.scala]
            + [dummy.scala][test/scala/ohnosequences/scarph/impl/dummy.scala]
          + [TwitterSchema.scala][test/scala/ohnosequences/scarph/TwitterSchema.scala]
  + main
    + scala
      + ohnosequences
        + scarph
          + [morphisms.scala][main/scala/ohnosequences/scarph/morphisms.scala]
          + [objects.scala][main/scala/ohnosequences/scarph/objects.scala]
          + [evals.scala][main/scala/ohnosequences/scarph/evals.scala]
          + [implementations.scala][main/scala/ohnosequences/scarph/implementations.scala]
          + [schemas.scala][main/scala/ohnosequences/scarph/schemas.scala]
          + [naturalIsomorphisms.scala][main/scala/ohnosequences/scarph/naturalIsomorphisms.scala]
          + syntax
            + [morphisms.scala][main/scala/ohnosequences/scarph/syntax/morphisms.scala]
            + [objects.scala][main/scala/ohnosequences/scarph/syntax/objects.scala]

[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: ../../../../test/scala/ohnosequences/scarph/impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: ../../../../test/scala/ohnosequences/scarph/impl/dummy.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: morphisms.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: objects.scala.md
[main/scala/ohnosequences/scarph/evals.scala]: evals.scala.md
[main/scala/ohnosequences/scarph/implementations.scala]: implementations.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: schemas.scala.md
[main/scala/ohnosequences/scarph/naturalIsomorphisms.scala]: naturalIsomorphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: syntax/objects.scala.md