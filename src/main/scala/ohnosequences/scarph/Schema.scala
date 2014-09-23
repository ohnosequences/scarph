package ohnosequences.scarph

import ohnosequences.pointless._, AnyTypeSet._, AnyFn._
import ohnosequences.pointless.ops.typeSet._

trait AnySchema extends Denotation[AnySchemaType]

class Schema[ST <: AnySchemaType](val tpe: ST) 
  extends AnySchema { type Tpe = ST }

trait Implements[S <: AnySchema, ET <: AnyElementType] extends AnyFn0 with OutBound[AnyElement { type Tpe <: ET }]


object AnySchema {

  implicit def schemaOps[S <: AnySchema](s: S):
        SchemaOps[S] =
    new SchemaOps[S](s)
}

class SchemaOps[S <: AnySchema](s: S) {

  def implements[E <: AnyElement](e: E): 
        Implements[S, E#Tpe] with Out[E] = 
    new Implements[S, E#Tpe] with Out[E] {
      def apply(): Out = e
    }

  def implementationOf[ET <: AnyElementType, E <: AnyElement](et: ET)
    (implicit impl: Implements[S, ET] { type Out = E }): E = impl.apply

  def eval[
    Q <: AnyQuery { type InT <: AnyElementType; type OutT <: AnyElementType },
    I <: AnyElement { type Tpe = Q#InT },
    O <: AnyElement { type Tpe = Q#OutT }
  ](q: Q)(implicit
    i: Implements[S, Q#InT] { type Out = I },
    o: Implements[S, Q#OutT] { type Out = O },
    ev: EvalQuery[Q, I, O]
  ): EvalQuery[Q, I, O] = ev

}
