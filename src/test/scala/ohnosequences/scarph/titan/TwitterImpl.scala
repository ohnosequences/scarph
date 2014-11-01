package ohnosequences.scarph.test.titan

import ohnosequences.pointless._, AnyTypeSet._
import ohnosequences.scarph._, impl.titan._
import ohnosequences.scarph.test._, TwitterSchema._
import com.thinkaurelius.titan.core.{ TitanGraph => TGraph }

case object TitanTwitter extends Schema(TwitterSchema.schemaType) {

  implicit case object user extends TitanVertex(this, User)
  implicit case object tweet extends TitanVertex(this, Tweet)

  implicit case object posted extends TitanEdge(this, Posted)
  implicit case object follows extends TitanEdge(this, Follows)

  // implicit case object name_ extends Implementation[this.type, name.type] { val tpe = name }

}
