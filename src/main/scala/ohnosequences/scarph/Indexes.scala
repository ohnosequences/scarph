package ohnosequences.scarph

object indexes {

  import ohnosequences.cosas._, typeSets._
  import graphTypes._


  // trait AnyDirection
  // case object InDirection extends AnyDirection
  // case object OutDirection extends AnyDirection
  // case object BothDirections extends AnyDirection

  // trait AnyOrder
  // case object Ascending extends AnyOrder
  // case object Descending extends AnyOrder


  /* ## Indexes */
  trait AnyIndex extends AnyGraphType {

    type IndexedType <: AnyGraphElement
    val  indexedType: IndexedType
  }

  object AnyIndex {

    type Over[IT] = AnyIndex { type IndexedType = IT }
  }



  /* This reflects [TitanDB Composite Index](http://s3.thinkaurelius.com/docs/titan/0.5.0/indexes.html#_composite_index)
     
     > Composite indexes retrieve vertices or edges by one or a (fixed) composition of multiple keys
     > Note, that all keys of a composite graph index must be found in the query’s equality conditions 
       for this index to be used.
     > Also note, that composite graph indexes can only be used for equality constraints.
   */
  // TODO: so far implemented only for one property
  trait AnyCompositeIndex extends AnyIndex {

    type Properties <: AnyTypeSet.Of[PropertyOf[IndexedType]]
    val  properties: Properties
  }

  class CompositeIndex[I <: AnyGraphElement, Ps <: AnyTypeSet.Of[PropertyOf[I]]]
    (val indexedType: I, val properties: Ps) extends AnyCompositeIndex {

    // NOTE: normally, you don't care about the index name, but it has to be unique
    val label = this.toString

    type IndexedType = I
    type Properties = Ps
  }


  /* Simple index is the same as Composite, but with only one property (Blueprints style) */
  trait AnySimpleIndex extends AnyCompositeIndex {

    type Property <: PropertyOf[IndexedType]
    val  property: Property

    type Properties = Property :~: ∅
    val  properties = property :~: ∅

  }

  class SimpleIndex[I <: AnyGraphElement, P <: PropertyOf[I]]
    (val indexedType: I, val property: P) extends AnySimpleIndex {

    val label = this.toString

    type IndexedType = I
    type Property = P
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
  }

  sealed trait AnyLocalIndexType
  case object OnlySourceCentric extends AnyLocalIndexType
  case object OnlyTargetCentric extends AnyLocalIndexType
  case object BothEndsCentric extends AnyLocalIndexType

  class LocalEdgeIndex[E <: AnyEdge, Ps <: AnyTypeSet.Of[PropertyOf[E]], T <: AnyLocalIndexType]
    (val indexedType: E, val indexType: T, val properties: Ps) extends AnyLocalEdgeIndex {

    val label = this.toString

    type IndexedType = E
    type IndexType = T
    type Properties = Ps
  }

}
