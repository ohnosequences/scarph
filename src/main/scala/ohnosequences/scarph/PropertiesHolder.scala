package ohnosequences.scarph

import ohnosequences.pointless._, AnyTypeSet._, AnyProperty._

trait AnyPropertiesHolder {
  type Me = this.type

  type Properties <: AnyTypeSet.Of[AnyProperty]
  val  properties: Properties

  implicit val myOwnProperties: Me Has Properties = (this: Me) has properties
}

trait Properties[Props <: AnyTypeSet.Of[AnyProperty]]
  extends AnyPropertiesHolder { type Properties = Props }

// class properties[Props <: AnyTypeSet.Of[AnyProperty]](
//   val properties: Props
// ) extends AnyPropertiesHolder { type Properties = Props }

object AnyPropertiesHolder {

  type PropertiesOf[H <: AnyPropertiesHolder] = H#Properties 
}
