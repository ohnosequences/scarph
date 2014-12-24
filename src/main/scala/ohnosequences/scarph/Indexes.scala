package ohnosequences.scarph

object indexes {

  import ohnosequences.cosas._, typeSets._, fns._
  import ohnosequences.cosas.ops.typeSets.MapSet
  import graphTypes._, predicates._, conditions._, containers._


  // TODO: add ordering
  // trait AnyOrder
  // case object Ascending extends AnyOrder
  // case object Descending extends AnyOrder


  /* ## Indexes */
  trait AnyIndex extends AnySimpleGraphType {

    type IndexedType <: AnyGraphElement
    val  indexedType: IndexedType

    /* Using this type member you can set a restriction on the predicates that 
       this index is capable handling of */
    type PredicateRestriction[Pred <: AnyPredicate] <: AnyPredicateRestriction
  }

  trait AnyPredicateRestriction

  /* This can be used when you don't want to restrict predicates */
  sealed class NoRestriction extends AnyPredicateRestriction
  object NoRestriction { implicit val ok: NoRestriction = new NoRestriction }

  object AnyIndex {

    type Over[IT] = AnyIndex { type IndexedType = IT }
  }


  /* `AnyCompositeIndex` reflects [TitanDB Composite Index](http://s3.thinkaurelius.com/docs/titan/0.5.0/indexes.html#_composite_index)
     
     > Composite indexes retrieve vertices or edges by one or a (fixed) composition of multiple keys
     > Note, that all keys of a composite graph index must be found in the query's equality conditions 
       for this index to be used.
     > Also note, that composite graph indexes can only be used for equality constraints.
  */
  @annotation.implicitNotFound(msg = "Can't prove that predicate ${Pred} consists only of equality conditions on properties ${Props}")
  trait  OnlyEqualitiesPredicate[Pred <: AnyPredicate, Props <: AnyTypeSet.Of[AnyGraphProperty]] extends AnyPredicateRestriction
  object OnlyEqualitiesPredicate {

    // NOTE: this poly returns property of a condition, but only if it's an equality condition
    import shapeless._, poly._
    case object eqConditionProperty extends Poly1 {
      implicit def getProp[P <: AnyGraphProperty] = at[Equal[P]] { _.property }
    }

    /* This check maps over the set of predicate's conditions, checking that all of them are 
       equalities and then compares result with the given set of properties (up to ordering) */
    implicit def yes[
      Pred <: AnyPredicate, 
      Props <: AnyTypeSet.Of[AnyGraphProperty], 
      PProps <: AnyTypeSet.Of[AnyGraphProperty]
    ](implicit
        pprops: MapSet[eqConditionProperty.type, Pred#Conditions] { type Out = PProps },
        same: Props ~:~ PProps
      ):  OnlyEqualitiesPredicate[Pred, Props] =
      new OnlyEqualitiesPredicate[Pred, Props] {}
  }


  sealed trait AnyUniqueness { val bool: Boolean }

  case object Unique extends AnyUniqueness { val bool = true }
  case object NonUnique extends AnyUniqueness { val bool = false }

  trait  IndexContainer[I <: AnyIndex] extends AnyFn0 with OutBound[AnyContainer] 

  object IndexContainer extends IndexContainer2 {

    implicit def unique[I <: AnyCompositeIndex { type Uniqueness = Unique.type }]: 
          IndexContainer[I] with Out[OneOrNone] =
      new IndexContainer[I] with Out[OneOrNone] { def apply(): Out = OneOrNone }
  }

  trait IndexContainer2 {

    implicit def any[I <: AnyIndex]: 
          IndexContainer[I] with Out[ManyOrNone] =
      new IndexContainer[I] with Out[ManyOrNone] { def apply(): Out = ManyOrNone }
  }


  trait AnyCompositeIndex extends AnyIndex {

    type Properties <: AnyTypeSet.Of[PropertyOf[IndexedType]]
    val  properties: Properties

    type PredicateRestriction[Pred <: AnyPredicate] = OnlyEqualitiesPredicate[Pred, Properties]

    type Uniqueness <: AnyUniqueness
    val  uniqueness: Uniqueness
  }

  class CompositeIndex[I <: AnyGraphElement, Props <: AnyTypeSet.Of[PropertyOf[I]], U <: AnyUniqueness]
    (val indexedType: I, val properties: Props, val uniqueness: U) extends AnyCompositeIndex {

    // NOTE: normally, you don't care about the index name, but it has to be unique
    val label = this.toString

    type IndexedType = I
    type Properties = Props
    type Uniqueness = U
  }


  /* Key-index is a composite index with only one property (Blueprints style) */
  trait AnyKeyIndex extends AnyCompositeIndex {

    type Property <: PropertyOf[IndexedType]
    val  property: Property

    type Properties = Property :~: ∅
    val  properties = property :~: ∅
  }

  class KeyIndex[I <: AnyGraphElement, P <: PropertyOf[I], U <: AnyUniqueness]
    (val indexedType: I, val property: P, val uniqueness: U) extends AnyKeyIndex {

    val label = this.toString

    type IndexedType = I
    type Property = P
    type Uniqueness = U
  }


  /* This is vertex-centric index, i.e. indexing edge type locally to some vertex type (source, target or both)
     See [Titan Documentation](http://s3.thinkaurelius.com/docs/titan/current/indexes.html#vertex-indexes)
  */
  trait AnyLocalEdgeIndex extends AnyIndex {

    type IndexedType <: AnyEdge

    type Properties <: AnyTypeSet.Of[PropertyOf[IndexedType]]
    val  properties: Properties

    type IndexType <: AnyLocalIndexType
    val  indexType: IndexType

    type PredicateRestriction[P] = NoRestriction
  }

  sealed trait AnyLocalIndexType
  case object OnlySourceCentric extends AnyLocalIndexType
  case object OnlyTargetCentric extends AnyLocalIndexType
  case object BothEndsCentric extends AnyLocalIndexType

  class LocalEdgeIndex[E <: AnyEdge, Props <: AnyTypeSet.Of[PropertyOf[E]], T <: AnyLocalIndexType]
    (val indexedType: E, val indexType: T, val properties: Props) extends AnyLocalEdgeIndex {

    val label = this.toString

    type IndexedType = E
    type IndexType = T
    type Properties = Props
  }

}
