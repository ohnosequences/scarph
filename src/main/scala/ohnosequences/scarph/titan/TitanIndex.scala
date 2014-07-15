package ohnosequences.scarph.titan

import ohnosequences.scarph._
import com.thinkaurelius.titan.core._
import com.thinkaurelius.titan.core.attribute.Cmp._
import scala.collection.JavaConversions._

trait AnyTitanStandardIndex extends AnyIndex { tindex =>

  type Tpe <: AnyStandardIndexType

  type Raw = com.thinkaurelius.titan.core.TitanGraph

  // FIXME: couldn't avoid it: (should be just set once in AnyIndex type)
  type IndexedType = tindex.tpe.IndexedType
  type PredicateType = tindex.tpe.PredicateType
  type Out = tindex.tpe.Out[tindex.item.Rep]

  implicit def lookupper[P <: tindex.PredicateType 
                         with AnyVertexPredicate
                         with AnyPredicate.HeadedBy[EQ[tpe.Property]] 
                  ](p: P): LookupItem[P] =
    new LookupItem[P](p) {

      def apply(rep: tindex.Rep): tindex.Out = {
        val it = rep.query().has(p.head.property.label, EQUAL, p.head.value).vertices.asInstanceOf[java.lang.Iterable[item.Rep]]
        it.toList
      }

    }

}

class TitanStandardIndex[I <: AnyDenotation.Of[T#IndexedType], T <: AnyStandardIndexType](
  val tpe: T,
  val item: I
  ) extends AnyTitanStandardIndex {

  type Tpe = T
  type Item = I
}
