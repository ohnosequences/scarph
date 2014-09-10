package ohnosequences.scarph

import ohnosequences.pointless._, AnyTypeSet._, AnyProperty._, AnyFn._

// TODO: move all this to pointless
trait AnyPropertiesHolder {
  type Me = this.type

  type Properties <: AnyTypeSet.Of[AnyProperty]
  val  properties: Properties

  implicit val myOwnProperties: Me Has Properties = (this: Me) has properties
}

trait Properties[Props <: AnyTypeSet.Of[AnyProperty]]
  extends AnyPropertiesHolder { type Properties = Props }

object AnyPropertiesHolder {

  type PropertiesOf[H <: AnyPropertiesHolder] = H#Properties 
}
import AnyPropertiesHolder._


/* This is an op for aggregating properties from a vertex or an edge types set */
trait AggregateProperties[S <: AnyTypeSet] extends Fn1[S] with OutBound[AnyTypeSet]

object AggregateProperties {

  def apply[S <: AnyTypeSet](implicit uni: AggregateProperties[S]): AggregateProperties[S] = uni

  implicit def empty:
        AggregateProperties[∅] with Out[∅] =
    new AggregateProperties[∅] with Out[∅] { def apply(s: ∅) = ∅ }

  implicit def cons[H <: AnyPropertiesHolder, T <: AnyTypeSet, TOut <: AnyTypeSet, U <: AnyTypeSet]
    (implicit
      t: AggregateProperties[T] { type Out = TOut },
      u: (PropertiesOf[H] ∪ TOut) { type Out = U }
    ):  AggregateProperties[H :~: T] with Out[U] =
    new AggregateProperties[H :~: T] with Out[U] { 

      def apply(s: H :~: T) = u(s.head.properties, t(s.tail))
    }
}
