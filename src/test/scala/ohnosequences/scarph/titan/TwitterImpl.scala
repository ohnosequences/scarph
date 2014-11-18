package ohnosequences.scarph.test.titan

import ohnosequences.cosas._, AnyTypeSet._
import ohnosequences.scarph._, impl.titan._
import ohnosequences.scarph.test._, TwitterSchema._
import com.thinkaurelius.titan.core.{ TitanGraph => TGraph }

case class TwitterImpl(graph: TGraph) {

  case object user extends TitanVertex(graph, User)
  case object tweet extends TitanVertex(graph, Tweet)

  case object posted extends TitanEdge(graph, user, Posted, tweet)
  case object follows extends TitanEdge(graph, user, Follows, user)

}
