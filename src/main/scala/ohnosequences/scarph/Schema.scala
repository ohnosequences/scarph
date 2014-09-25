package ohnosequences.scarph

import ohnosequences.pointless._, AnyTypeSet._, AnyFn._
import ohnosequences.pointless.ops.typeSet._

trait AnySchema extends Denotation[AnySchemaType]

class Schema[ST <: AnySchemaType](val tpe: ST) 
  extends AnySchema { type Tpe = ST }

trait Implements[S <: AnySchema, T <: AnyType] extends Fn1[S] with OutBound[AnyDenotation]

object Implements {

  implicit def propImpl[S <: AnySchema, P <: AnyProperty, Ps <: AnyTypeSet]
    (implicit 
      props: SchemaProperties[S#Tpe] { type Out = Ps },
      lookup: Lookup[Ps, P]
    ):  Implements[S, P] with Out[P] =
    new Implements[S, P] with Out[P] {
      def apply(s: In1): Out = lookup(props(s.tpe))
    }
}

object AnySchema {

  implicit def schemaOps[S <: AnySchema](s: S):
        SchemaOps[S] =
    new SchemaOps[S](s)
}

class SchemaOps[S <: AnySchema](s: S) {

  def implements[E <: AnyElement](e: E): 
        Implements[S, E#Tpe] with Out[E] = 
    new Implements[S, E#Tpe] with Out[E] {
      def apply(s: In1): Out = e
    }

  def implementationOf[ET <: AnyElementType, E <: AnyElement](et: ET)
    (implicit impl: Implements[S, ET] { type Out = E }): E = impl.apply(s)

  def eval[
      C <: AnyCompose,
      I <: AnyDenotation { type Tpe = C#InT },
      M <: AnyDenotation { type Tpe = C#MidT },
      O <: AnyDenotation { type Tpe = C#OutT }
    ](c: C, in: ValueOf[I])(implicit
      i: Implements[S, C#InT]  { type Out = I },
      m: Implements[S, C#MidT] { type Out = M },
      o: Implements[S, C#OutT] { type Out = O },
      eval1: AnyEvalQuery[C#Query1] { type InW = I; type OutW = M },
      eval2: AnyEvalQuery[C#Query2] { type InW = M; type OutW = O }
    ):  ValueOf[O] = {

        val o1 = eval1(c.query1, in.raw)
        eval2(c.query2, o1.raw)
    }

  def eval[
    Q <: AnyQuery,
    I <: AnyDenotation { type Tpe = Q#InT },
    O <: AnyDenotation { type Tpe = Q#OutT }
  ](q: Q, in: ValueOf[I])(implicit
    i: Implements[S, Q#InT] { type Out = I },
    o: Implements[S, Q#OutT] { type Out = O },
    ev: EvalQuery[Q, I, O]
  ): ValueOf[O] = ev(q, in.raw)

}
