package ohnosequences.scarph.test

import ohnosequences.scarph._, implementations._, objects._, evals._

case object dummy {

  trait Dummy

  case object DummyEdge extends Dummy
  type DummyEdge = DummyEdge.type

  case object DummyVertex extends Dummy
  type DummyVertex = DummyVertex.type


  case object categoryStructure extends CategoryStructure {

    type RawObject = Dummy
  }

  case object graphStructure extends GraphStructure {

    type RawEdge = DummyEdge
    type RawSource = DummyVertex
    type RawTarget = DummyVertex

    def outVRaw(edge: AnyEdge)(v: RawSource): RawTarget = DummyVertex
    def inVRaw(edge: AnyEdge)(v: RawTarget): RawSource = DummyVertex

    def outERaw(edge: AnyEdge)(v: RawSource): RawEdge = DummyEdge
    def sourceRaw(edge: AnyEdge)(e: RawEdge): RawSource = DummyVertex

    def inERaw(edge: AnyEdge)(v: RawTarget): RawEdge = DummyEdge
    def targetRaw(edge: AnyEdge)(e: RawEdge): RawTarget = DummyVertex
  }


  case class DummyTensor[L <: Dummy, R <: Dummy](l: L, r: R) extends Dummy

  case object DummyUnit extends Dummy
  type DummyUnit = DummyUnit.type


  implicit def dummyMatch[T <: Dummy]:
      Matchable[T] =
  new Matchable[T] { def matchUp(l: T, r: T): T = l }

  implicit def dummyUnitToEdge[U]:
      FromUnit[U, DummyEdge] =
  new FromUnit[U, DummyEdge] { def fromUnit(u: U, e: AnyGraphObject): T = DummyEdge }

  implicit def dummyUnitToVertex[U]:
      FromUnit[U, DummyVertex] =
  new FromUnit[U, DummyVertex] { def fromUnit(u: U, e: AnyGraphObject): T = DummyVertex }

  implicit def dummyUnitToTensor[U, L <: Dummy, R <: Dummy]
  (implicit
    l: FromUnit[U, L],
    r: FromUnit[U, R]
  ):  FromUnit[U, DummyTensor[L, R]] =
  new FromUnit[U, DummyTensor[L, R]] {

    def fromUnit(u: U, e: AnyGraphObject): T =
      DummyTensor(
        l.fromUnit(u, e),
        r.fromUnit(u, e)
      )
  }

  case object tensorStructure extends TensorStructure {

    type RawObject = Dummy
    type RawTensor[L <: RawObject, R <: RawObject] = DummyTensor[L, R]
    type RawUnit = DummyUnit

    def construct[L <: RawObject, R <: RawObject](l: L, r: R): RawTensor[L, R] = DummyTensor(l, r)
    def leftProjRaw[L <: RawObject, R <: RawObject](t: RawTensor[L, R]): L = t.l
    def rightProjRaw[L <: RawObject, R <: RawObject](t: RawTensor[L, R]): R = t.r
    def toUnitRaw[X <: RawObject](x: X): RawUnit = DummyUnit
  }



  case class DummyBiproduct[L <: Dummy, R <: Dummy](l: L, r: R) extends Dummy

  case object DummyZero extends Dummy
  type DummyZero = DummyZero.type


  implicit def dummyMerge[T <: Dummy]:
      Mergeable[T] =
  new Mergeable[T] { def merge(l: T, r: T): T = r }

  implicit def dummyZeroForEdge:
      ZeroFor[DummyEdge] =
  new ZeroFor[DummyEdge] { def zero(e: AnyGraphObject): T = DummyEdge }

  implicit def dummyZeroForVertex:
      ZeroFor[DummyVertex] =
  new ZeroFor[DummyVertex] { def zero(e: AnyGraphObject): T = DummyVertex }

  case object biproductStructure extends BiproductStructure {

    type RawObject = Dummy
    type RawBiproduct[L <: RawObject, R <: RawObject] = DummyBiproduct[L, R]
    type RawZero = DummyZero

    def construct[L <: RawObject, R <: RawObject](l: L, r: R): RawBiproduct[L, R] =
      DummyBiproduct[L, R](l, r)

    def leftProjRaw[L <: RawObject, R <: RawObject](t: RawBiproduct[L, R]): L = t.l
    def rightProjRaw[L <: RawObject, R <: RawObject](t: RawBiproduct[L, R]): R = t.r

    def toZeroRaw[X <: RawObject](x: X): RawZero = DummyZero
  }


  case class vertexPropertyStructure[V](default: V) extends PropertyStructure {

    type RawObject = Dummy
    type RawElement = DummyVertex
    type RawValue = V

    def getRaw[P <: AnyProperty { type Value = RawValue }](p: P)(e: RawElement): RawValue = default
    def lookupRaw[P <: AnyProperty { type Value = RawValue }](p: P)(v: RawValue): RawElement = DummyVertex
  }

  case class edgePropertyStructure[V](default: V) extends PropertyStructure {

    type RawObject = Dummy
    type RawElement = DummyEdge
    type RawValue = V

    def getRaw[P <: AnyProperty { type Value = RawValue }](p: P)(e: RawElement): RawValue = default
    def lookupRaw[P <: AnyProperty { type Value = RawValue }](p: P)(v: RawValue): RawElement = DummyEdge
  }

}

/*
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
