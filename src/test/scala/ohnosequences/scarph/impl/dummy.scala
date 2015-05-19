package ohnosequences.scarph.test

import ohnosequences.scarph._, implementations._, objects._, evals._

case object dummy {

  trait Dummy

  case class DummyTensor[L <: Dummy, R <: Dummy](l: L, r: R) extends Dummy

  case object DummyUnit extends Dummy
  type DummyUnit = DummyUnit.type

  case object DummyEdge extends Dummy
  type DummyEdge = DummyEdge.type

  case object DummyVertex extends Dummy
  type DummyVertex = DummyVertex.type


  case object categoryStructure extends CategoryStructure {

    type RawObject = Dummy
  }

  case object tensorStructure extends TensorStructure {

    type RawObject = Dummy
    type RawTensor[L <: RawObject, R <: RawObject] = DummyTensor[L, R]
    type RawUnit = DummyUnit

    def construct[L <: RawObject, R <: RawObject](l: L, r: R): RawTensor[L, R] = DummyTensor(l, r)
    def leftProjRaw[L <: RawObject, R <: RawObject](t: RawTensor[L, R]): L = t.l
    def rightProjRaw[L <: RawObject, R <: RawObject](t: RawTensor[L, R]): R = t.r
    def matchUpRaw[X <: RawObject](t: RawTensor[X, X]): X = t.l
    //def fromUnitRaw[X <: RawObject](u: RawUnit): X*/
    def toUnitRaw[X <: RawObject](x: X): RawUnit = DummyUnit
  }

  case object graphStructure extends GraphStructure {

    type RawEdge = DummyEdge
    type RawSource = DummyVertex
    type RawTarget = DummyVertex

    def outVRaw(edge: AnyEdge)(v: RawSource): RawTarget = DummyVertex
    def inVRaw(edge: AnyEdge)(v: RawTarget): RawSource = DummyVertex
  }
}

/*
    implicit def unitImpl[O <: AnyGraphObject]:
        UnitImpl[O, Dummy, Dummy] =
    new UnitImpl[O, Dummy, Dummy] {

      def fromUnit(u: RawUnit, o: Object): RawObject = Dummy
      def toUnit(s: RawObject): RawUnit = Dummy
    }


    implicit def biproductImpl:
        BiproductImpl[Dummy, Dummy, Dummy] =
    new BiproductImpl[Dummy, Dummy, Dummy] {

      def apply(l: RawLeft, r: RawRight): RawBiproduct = Dummy
      def leftProj(b: RawBiproduct): RawLeft = Dummy
      def leftInj(l: RawLeft): RawBiproduct = Dummy
      def rightProj(b: RawBiproduct): RawRight = Dummy
      def rightInj(r: RawRight): RawBiproduct = Dummy
    }


    implicit def mergeImpl:
        MergeImpl[Dummy] =
    new MergeImpl[Dummy] {

      def merge(l: Raw, r: Raw): Raw = Dummy
    }


    implicit def zeroImpl:
        ZeroImpl[Dummy] =
    new ZeroImpl[Dummy] { def apply(): Raw = Dummy }


    implicit def edgeImpl:
        EdgeImpl[Dummy, Dummy, Dummy] =
    new EdgeImpl[Dummy, Dummy, Dummy] {

      def source(e: RawEdge): RawSource = Dummy
      def target(e: RawEdge): RawTarget = Dummy
    }


    implicit def vertexInImpl[E <: AnyEdge]:
        VertexInImpl[E, Dummy, Dummy, Dummy] =
    new VertexInImpl[E, Dummy, Dummy, Dummy] {

      def inE(v: RawVertex, e: Edge): RawInEdge = Dummy
      def inV(v: RawVertex, e: Edge): RawInVertex = Dummy
    }


    implicit def vertexOutImpl[E <: AnyEdge]:
        VertexOutImpl[E, Dummy, Dummy, Dummy] =
    new VertexOutImpl[E, Dummy, Dummy, Dummy] {

      def outE(v: RawVertex, e: Edge): RawOutEdge = Dummy
      def outV(v: RawVertex, e: Edge): RawOutVertex = Dummy
    }


    implicit def dummyPropertyImpl[P <: AnyProperty]:
        PropertyImpl[P, Dummy, Dummy] =
    new PropertyImpl[P, Dummy, Dummy] {

      def get(e: RawElement, p: Property): RawValue = Dummy
      def lookup(r: RawValue, p: Property): RawElement = Dummy
    }


    implicit def dummyPredicateImpl[P <: AnyPredicate]:
        PredicateImpl[P, Dummy, Dummy] =
    new PredicateImpl[P, Dummy, Dummy] {

      def quantify(e: RawElement, p: Predicate): RawPredicate = Dummy
      def coerce(p: RawPredicate): RawElement = Dummy
    }

  }

*/
