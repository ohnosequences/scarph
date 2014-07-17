package ohnosequences.scarph.titan

import ohnosequences.scarph._
import com.thinkaurelius.titan.core._
import com.thinkaurelius.titan.core.attribute.Cmp._
import scala.collection.JavaConversions._

trait AnyTitanStandardIndex extends AnyStandardIndex { tindex =>

  type Tpe <: AnyStandardIndexType
  val tpe: Tpe

  type Raw = com.thinkaurelius.titan.core.TitanGraph

  // FIXME: couldn't avoid it: (should be just set once in AnyIndex type)
  // type IndexedType = tpe.IndexedType
  // type PredicateType = tindex.tpe.PredicateType
  // type Out = tindex.tpe.Out[tindex.item.Rep]

  // type Out = tpe.Out[item.Rep]

  implicit def lookupper[P <: tindex.PredicateType 
                         with AnyVertexPredicate
                         with AnyPredicate.HeadedBy[EQ[tpe.Property]] 
                  ](p: P): this.LookupItem[P] =
    new LookupItem[P](p) {

      def apply(rep: tindex.Rep) = {
        val it = rep.query().has(p.head.property.label, EQUAL, p.head.value).vertices.asInstanceOf[java.lang.Iterable[tindex.item.Rep]]
        it.toList
      }

    }

}

class TitanStandardIndex[I <: AnyDenotation.Of[T#IndexedType], T <: AnyStandardIndexType](
  val tpe: T,
  val item: I
  ) extends AnyTitanStandardIndex { tindex =>

  type Tpe = T
  type Item = I
}
