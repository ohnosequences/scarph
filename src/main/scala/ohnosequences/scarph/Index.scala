package ohnosequences.scarph

import ohnosequences.pointless._
import scala.reflect.ClassTag

/* ## Indexes */
trait AnyIndex {

  val label: String

  type IndexedType <: AnyElementType
  val  indexedType: IndexedType

  /* The type of predicates that this index can be queried for */
  // type QueryType <: AnyQuery.On[IndexedType]

  // type Out[X]
}

object AnyIndex {

  type Over[IT] = AnyIndex { type IndexedType = IT }
  // type WithQuery[P] = AnyIndex { type QueryType = P }
}



/* This reflects [TitanDB Composite Index](http://s3.thinkaurelius.com/docs/titan/0.5.0/indexes.html#_composite_index)
   
   > Composite indexes retrieve vertices or edges by one or a (fixed) composition of multiple keys
   > Note, that all keys of a composite graph index must be found in the query’s equality conditions 
     for this index to be used.
   > Also note, that composite graph indexes can only be used for equality constraints.
*/
// TODO: so far implemented only for one property
trait AnyCompositeIndex extends AnyIndex {

  type Property <: AnyProperty
  val  property: Property

  // should be provided implicitly:
  val indexedTypeHasProperty: IndexedType HasProperty Property

  // type Out[X] = List[X]

  // type QueryType = AnySimpleQuery {
  //   type ElementType = IndexedType
  //   type Head = EQ[Property]
  // }
}

class CompositeIndex[ET <: AnyElementType, P <: AnyProperty](
  val indexedType: ET, 
  val property: P
)(implicit 
  val indexedTypeHasProperty: ET HasProperty P
) extends AnyCompositeIndex {

  // NOTE: normally, you don't care about the index name, but it has to be unique
  val label = this.toString

  type IndexedType = ET
  type Property = P
}
