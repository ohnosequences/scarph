package ohnosequences.scarph

import ohnosequences.pointless._
import scala.reflect.ClassTag

/* ## Indexes */
trait AnyIndex extends AnyLabelType {

  type IndexedType <: AnyElementType
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
trait AnySimpleIndex extends AnyIndex {

  type Property <: AnyProp
  val  property: Property

  type IndexedType = Property#Owner
  val  indexedType = property.owner
}

class SimpleIndex[P <: AnyProp](val property: P) extends AnySimpleIndex {

  // NOTE: normally, you don't care about the index name, but it has to be unique
  val label = this.toString

  type Property = P
}
