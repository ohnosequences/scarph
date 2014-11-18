package ohnosequences.scarph.impl.titan

import shapeless._
import ohnosequences.cosas._, AnyFn._ 
import ohnosequences.cosas.ops.typeSet._
import ohnosequences.scarph._
import com.thinkaurelius.titan.core._, schema._
import scala.collection.JavaConversions._

object predicates {

  import com.tinkerpop.blueprints.Compare._
  import com.tinkerpop.blueprints.{ Query => BQuery }
  // import com.thinkaurelius.titan.core.{ TitanVertexQuery => BQuery }

  // TODO: names here are awful, rename it
  case object toBlueprintsCondition extends Poly1 {
    implicit def eq[C <: AnyEqual]          = at[C] { c => { q: BQuery => q.has(c.property.label, EQUAL, c.value) } }
    implicit def ne[C <: AnyNotEqual]       = at[C] { c => { q: BQuery => q.has(c.property.label, NOT_EQUAL, c.value) } }
    implicit def le[C <: AnyLess]           = at[C] { c => { q: BQuery => q.has(c.property.label, LESS_THAN, c.value) } }
    implicit def lq[C <: AnyLessOrEqual]    = at[C] { c => { q: BQuery => q.has(c.property.label, LESS_THAN_EQUAL, c.value) } }
    implicit def gr[C <: AnyGreater]        = at[C] { c => { q: BQuery => q.has(c.property.label, GREATER_THAN, c.value) } }
    implicit def gq[C <: AnyGreaterOrEqual] = at[C] { c => { q: BQuery => q.has(c.property.label, GREATER_THAN_EQUAL, c.value) } }
  }

  trait ToBlueprintsPredicate[P <: AnyPredicate] extends Fn2[P, BQuery] with Out[BQuery]

  object ToBlueprintsPredicate {

    implicit def convert[P <: AnyPredicate]
      (implicit m: MapFoldSet[toBlueprintsCondition.type, P#Conditions, BQuery => BQuery]):
        ToBlueprintsPredicate[P] =
    new ToBlueprintsPredicate[P] {
      def apply(p: In1, q: In2): Out = {
        def id[A]: A => A = x => x
        def compose[A, B, C](f: A => B, g: B => C): A => C = x => g(f(x))
        val addConditions = m(p.conditions, id, compose)
        addConditions(q)
      }
    }
  }

}
