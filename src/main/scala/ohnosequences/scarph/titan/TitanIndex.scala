package ohnosequences.scarph.titan

import ohnosequences.scarph._
import com.thinkaurelius.titan.core._
import com.thinkaurelius.titan.core.attribute.Cmp._
import scala.collection.JavaConversions._

trait AnyTitanStandardIndex extends AnyIndex { index =>

  type Tpe <: AnyStandardIndexType

  type Raw = com.thinkaurelius.titan.core.TitanGraph

  implicit def lookupper[P <: PredicateType with AnyVertexPredicate
                  //      AnyPredicate.On[tpe.IndexedType] 
                  with AnyPredicate.HeadedBy[EQ[tpe.Property]] 
                  ](p: P): LookupItem[P] =
    new LookupItem[P](p) {

      def apply(rep: index.Rep): Out[item.Rep] = {
        val it = rep.query().has(p.head.property.label, EQUAL, p.head.value).vertices.asInstanceOf[java.lang.Iterable[item.Rep]]
        it.toList: Out[item.Rep]
      }

    }

}
