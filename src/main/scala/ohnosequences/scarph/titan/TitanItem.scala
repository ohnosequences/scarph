package ohnosequences.scarph.titan

import ohnosequences.scarph._
import com.thinkaurelius.titan.core.{TitanGraph => TGraph}
import com.thinkaurelius.titan.core._

trait AnyTitanItem extends AnyItem { titem =>

  final type Graph = TGraph

}
