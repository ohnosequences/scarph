package ohnosequences.scarph

object implementations {

  import objects._


  trait AnyTensorImpl {

    type RawTensor
    def apply(l: RawLeft, r: RawRight): RawTensor

    type RawLeft
    def leftProj(t: RawTensor): RawLeft

    type RawRight
    def rightProj(t: RawTensor): RawRight
  }

  trait TensorImpl[T, L, R] extends AnyTensorImpl {

    type RawTensor = T
    type RawLeft = L
    type RawRight = R
  }


  trait AnyMatchUpImpl {

    type Raw

    def matchUp(l: Raw, r: Raw): Raw
  }

  trait MatchUpImpl[I] extends AnyMatchUpImpl { type Raw = I }


  trait AnyBiproductImpl {

    type RawBiproduct
    def apply(l: RawLeft, r: RawRight): RawBiproduct

    type RawLeft
    def leftProj(b: RawBiproduct): RawLeft
    def leftInj(l: RawLeft): RawBiproduct

    type RawRight
    def rightProj(b: RawBiproduct): RawRight
    def rightInj(r: RawRight): RawBiproduct
  }

  trait BiproductImpl[B, L, R] extends AnyBiproductImpl {

    type RawBiproduct = B
    type RawLeft = L
    type RawRight = R
  }


  trait AnyMergeImpl {

    type Raw

    def merge(l: Raw, r: Raw): Raw
  }

  trait MergeImpl[R] extends AnyMergeImpl { type Raw = R }


  trait AnyZeroImpl {

    type Raw

    def apply(): Raw
  }

  trait ZeroImpl[R] extends AnyZeroImpl { type Raw = R }


  trait AnyEdgeImpl {

    type RawEdge

    type RawSource
    def source(e: RawEdge): RawSource

    type RawTarget
    def target(e: RawEdge): RawTarget
  }

  trait EdgeImpl[E, S, T] extends AnyEdgeImpl {

    type RawEdge = E
    type RawSource = S
    type RawTarget = T
  }


  // TODO: probably it makes sense to separate it
  trait AnyVertexInImpl {

    type Edge <: AnyEdge
    type RawVertex

    type RawInEdge
    def inE(v: RawVertex, e: Edge): RawInEdge

    type RawInVertex
    def inV(v: RawVertex, e: Edge): RawInVertex
  }

  trait VertexInImpl[E <: AnyEdge, V, InE, InV] extends AnyVertexInImpl{

    type Edge = E
    type RawVertex = V
    type RawInEdge = InE
    type RawInVertex = InV
  }


  // TODO: probably it makes sense to separate it
  trait AnyVertexOutImpl {

    type Edge <: AnyEdge
    type RawVertex

    type RawOutEdge
    def outE(v: RawVertex, e: Edge): RawOutEdge

    type RawOutVertex
    def outV(v: RawVertex, e: Edge): RawOutVertex
  }

  trait VertexOutImpl[E <: AnyEdge, V, OutE, OutV] extends AnyVertexOutImpl {

    type Edge = E
    type RawVertex = V
    type RawOutEdge = OutE
    type RawOutVertex = OutV
  }


  trait AnyPropertyImpl {

    type Property <: AnyProperty
    type RawElement
    type RawValue

    def get(e: RawElement, p: Property): RawValue

    def lookup(r: RawValue, p: Property): RawElement
  }

  trait PropertyImpl[P <: AnyProperty, RE, RV] extends AnyPropertyImpl {

    type Property = P
    type RawElement = RE
    type RawValue = RV
  }


  trait AnyUnitImpl {

    // TODO: probably this should be an AnyGraphElement?
    type Object <: AnyGraphObject

    type RawObject
    def fromUnit(u: RawUnit, o: Object): RawObject

    type RawUnit
    def toUnit(s: RawObject): RawUnit
  }

  trait UnitImpl[O <: AnyGraphObject, RO, RU] extends AnyUnitImpl {

    type Object = O
    type RawObject = RO
    type RawUnit = RU
  }


  trait AnyPredicateImpl {

    type Predicate <: AnyPredicate

    type RawPredicate
    def quantify(e: RawElement, p: Predicate): RawPredicate

    type RawElement
    def coerce(p: RawPredicate): RawElement
  }

  trait PredicateImpl[P <: AnyPredicate, RP, RE] extends AnyPredicateImpl {

    type Predicate = P
    type RawPredicate = RP
    type RawElement = RE
  }

}
