package ohnosequences.scarph.test

import ohnosequences.scarph._, implementations._, graphTypes._, evals._, predicates._

object dummy extends DefaultEvals {

  case object Dummy
  type Dummy = Dummy.type

  implicit def tensorImpl:
      TensorImpl[Dummy, Dummy, Dummy] =
  new TensorImpl[Dummy, Dummy, Dummy] {

    def apply(l: RawLeft, r: RawRight): RawTensor = Dummy
    def leftProj(t: RawTensor): RawLeft = Dummy
    def rightProj(t: RawTensor): RawRight = Dummy
  }


  implicit def matchUpImpl:
      MatchUpImpl[Dummy] =
  new MatchUpImpl[Dummy] {

    def matchUp(l: Raw, r: Raw): Raw = Dummy
  }


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


  implicit def dummyPropertyImpl[P <: AnyGraphProperty]:
      PropertyImpl[P, Dummy, Dummy] =
  new PropertyImpl[P, Dummy, Dummy] {

    def get(e: RawElement, p: Property): RawValue = Dummy
    def lookup(r: RawValue, p: Property): RawElement = Dummy
  }


  implicit def dummyPredicateImpl:
      PredicateImpl[Dummy, Dummy] =
  new PredicateImpl[Dummy, Dummy] {

    def quantify[P <: AnyPredicate](e: RawElement, p: P): RawPredicate = Dummy
    def coerce(p: RawPredicate): RawElement = Dummy
  }

}
