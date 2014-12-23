package ohnosequences.scarph.impl.titan

object predicates {

  import shapeless._

  import com.thinkaurelius.titan.core._, schema._
  import scala.collection.JavaConversions._

  import com.tinkerpop.blueprints.Compare._
  import com.tinkerpop.blueprints.{ Query => BQuery }
  import com.thinkaurelius.titan.core.TitanVertexQuery

  import ohnosequences.cosas._, fns._ 
  import ohnosequences.cosas.ops.typeSets._

  import ohnosequences.{ scarph => s }
  import s.conditions._, s.predicates._

  case object toTitanCondition extends Poly1 {
    implicit def eq[Q <: BQuery, C <: AnyEqual]          = at[C] { c => { q: Q => q.has(c.property.label, EQUAL, c.value) } }
    implicit def ne[Q <: BQuery, C <: AnyNotEqual]       = at[C] { c => { q: Q => q.has(c.property.label, NOT_EQUAL, c.value) } }
    implicit def le[Q <: BQuery, C <: AnyLess]           = at[C] { c => { q: Q => q.has(c.property.label, LESS_THAN, c.value) } }
    implicit def lq[Q <: BQuery, C <: AnyLessOrEqual]    = at[C] { c => { q: Q => q.has(c.property.label, LESS_THAN_EQUAL, c.value) } }
    implicit def gr[Q <: BQuery, C <: AnyGreater]        = at[C] { c => { q: Q => q.has(c.property.label, GREATER_THAN, c.value) } }
    implicit def gq[Q <: BQuery, C <: AnyGreaterOrEqual] = at[C] { c => { q: Q => q.has(c.property.label, GREATER_THAN_EQUAL, c.value) } }
  }

  trait ToTitanPredicate[P <: AnyPredicate, Q <: BQuery] extends Fn2[P, Q] with Out[Q]

  object ToTitanPredicate {

    implicit def convert[P <: AnyPredicate, Q <: BQuery]
      (implicit m: MapFoldSet[toTitanCondition.type, P#Conditions, Q => Q]):
        ToTitanPredicate[P, Q] =
    new ToTitanPredicate[P, Q] {
      def apply(p: In1, q: In2): Out = {
        def id[A]: A => A = x => x
        def compose[A, B, C](f: A => B, g: B => C): A => C = x => g(f(x))
        val addConditions = m(p.conditions, id, compose)
        addConditions(q)
      }
    }
  }

}
