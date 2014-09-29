package ohnosequences.scarph

import ohnosequences.pointless._, AnyTypeSet._, AnyFn._
import ohnosequences.pointless.ops.typeSet._

trait AnySchema extends Denotation[AnySchemaType]

class Schema[ST <: AnySchemaType](val tpe: ST)
  extends AnySchema { type Tpe = ST }

trait Implementation[S <: AnySchema, T <: AnyType] extends AnyDenotation {
  type Tpe = T
}
/*trait Implements[S <: AnySchema, T <: AnyType] extends Fn1[S] with OutBound[AnyDenotation]*/

/*object Implements {

  implicit def propImpl[S <: AnySchema, P <: AnyProperty, Ps <: AnyTypeSet]
    (implicit
      props: SchemaProperties[S#Tpe] { type Out = Ps },
      lookup: Lookup[Ps, P]
    ):  Implements[S, P] with Out[P] =
    new Implements[S, P] with Out[P] {
      def apply(s: In1): Out = lookup(props(s.tpe))
    }
}
*/
object AnySchema {

  implicit def schemaOps[S <: AnySchema](s: S):
        SchemaOps[S] =
    new SchemaOps[S](s)
}

class SchemaOps[S <: AnySchema](s: S) {

  /*def implements[E <: AnyElement](e: E):
        Implements[S, E#Tpe] with Out[E] =
    new Implements[S, E#Tpe] with Out[E] {
      def apply(s: In1): Out = e
    }
  def implementationOf[ET <: AnyElementType, E <: AnyElement](et: ET)
    (implicit impl: Implements[S, ET] { type Out = E }): E = impl.apply(s)
*/

  def implementationOf[ET <: AnyElementType, E <: Implementation[S, ET]](et: ET)
    (implicit impl: E): E = impl

  def eval[Q <: AnyQuery](q: Q)
    (implicit ev: EvalOnSchema[Q, S]): EvalOnSchema[Q, S] = ev

}
