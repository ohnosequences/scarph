package ohnosequences.scarph.titan

import ohnosequences.scarph._
import com.thinkaurelius.titan.core.{TitanGraph => TGraph}
import com.thinkaurelius.titan.core._

trait AnyTitanItem extends AnyItem { titem =>

  final type Graph = TGraph

  type Raw = TitanElement

  implicit def lookupper[I <: AnyIndex.Over[Tpe]](i: I) =
    new LookupItem[I](i) {

      def apply(p: index.PredicateType, rep: titem.Rep): Out = {
        val it = rep.query().has(p.head.property.label, EQUAL, p.head.value).vertices.asInstanceOf[java.lang.Iterable[tindex.item.Rep]]
        it.toList
      }

    }


}
