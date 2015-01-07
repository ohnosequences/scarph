
```scala
package ohnosequences.scarph.impl.titan

object predicates {

  import shapeless._

  import com.thinkaurelius.titan.core._, schema._
  import scala.collection.JavaConversions._

  import com.tinkerpop.blueprints.Compare._
  import com.tinkerpop.blueprints.{ Query => BQuery }
  // import com.thinkaurelius.titan.core.{ TitanVertexQuery => BQuery }

  import ohnosequences.cosas._, fns._ 
  import ohnosequences.cosas.ops.typeSets._

  import ohnosequences.{ scarph => s}
  import s.conditions._, s.predicates._


  // TODO: names here are awful, rename it
  case object toBlueprintsCondition extends Poly1 {
    implicit def eq[C <: AnyEqual]          = at[C] { c => { q: BQuery => q.has(c.property.label, EQUAL, c.value) } }
    implicit def ne[C <: AnyNotEqual]       = at[C] { c => { q: BQuery => q.has(c.property.label, NOT_EQUAL, c.value) } }
    implicit def le[C <: AnyLess]           = at[C] { c => { q: BQuery => q.has(c.property.label, LESS_THAN, c.value) } }
    implicit def lq[C <: AnyLessOrEqual]    = at[C] { c => { q: BQuery => q.has(c.property.label, LESS_THAN_EQUAL, c.value) } }
    implicit def gr[C <: AnyGreater]        = at[C] { c => { q: BQuery => q.has(c.property.label, GREATER_THAN, c.value) } }
    implicit def gq[C <: AnyGreaterOrEqual] = at[C] { c => { q: BQuery => q.has(c.property.label, GREATER_THAN_EQUAL, c.value) } }

    implicit def interval[C <: AnyInterval] = at[C] { c => { q: BQuery => q.interval(c.property.label, c.start, c.end) } }
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

```


------

### Index

+ src
  + test
    + scala
      + ohnosequences
        + scarph
          + [ContainersTest.scala][test/scala/ohnosequences/scarph/ContainersTest.scala]
          + [ScalazEquality.scala][test/scala/ohnosequences/scarph/ScalazEquality.scala]
          + titan
            + [TwitterTitanTest.scala][test/scala/ohnosequences/scarph/titan/TwitterTitanTest.scala]
          + [TwitterSchema.scala][test/scala/ohnosequences/scarph/TwitterSchema.scala]
    + resources
  + main
    + scala
      + ohnosequences
        + scarph
          + [GraphTypes.scala][main/scala/ohnosequences/scarph/GraphTypes.scala]
          + [Containers.scala][main/scala/ohnosequences/scarph/Containers.scala]
          + impl
            + titan
              + [Schema.scala][main/scala/ohnosequences/scarph/impl/titan/Schema.scala]
              + [Evals.scala][main/scala/ohnosequences/scarph/impl/titan/Evals.scala]
              + [Predicates.scala][main/scala/ohnosequences/scarph/impl/titan/Predicates.scala]
          + [Paths.scala][main/scala/ohnosequences/scarph/Paths.scala]
          + [Indexes.scala][main/scala/ohnosequences/scarph/Indexes.scala]
          + [Evals.scala][main/scala/ohnosequences/scarph/Evals.scala]
          + [Conditions.scala][main/scala/ohnosequences/scarph/Conditions.scala]
          + [Steps.scala][main/scala/ohnosequences/scarph/Steps.scala]
          + [Predicates.scala][main/scala/ohnosequences/scarph/Predicates.scala]
          + [Schemas.scala][main/scala/ohnosequences/scarph/Schemas.scala]
          + [Combinators.scala][main/scala/ohnosequences/scarph/Combinators.scala]
          + syntax
            + [GraphTypes.scala][main/scala/ohnosequences/scarph/syntax/GraphTypes.scala]
            + [Paths.scala][main/scala/ohnosequences/scarph/syntax/Paths.scala]
            + [Conditions.scala][main/scala/ohnosequences/scarph/syntax/Conditions.scala]
            + [Predicates.scala][main/scala/ohnosequences/scarph/syntax/Predicates.scala]

[test/scala/ohnosequences/scarph/ContainersTest.scala]: ../../../../../../test/scala/ohnosequences/scarph/ContainersTest.scala.md
[test/scala/ohnosequences/scarph/ScalazEquality.scala]: ../../../../../../test/scala/ohnosequences/scarph/ScalazEquality.scala.md
[test/scala/ohnosequences/scarph/titan/TwitterTitanTest.scala]: ../../../../../../test/scala/ohnosequences/scarph/titan/TwitterTitanTest.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[main/scala/ohnosequences/scarph/GraphTypes.scala]: ../../GraphTypes.scala.md
[main/scala/ohnosequences/scarph/Containers.scala]: ../../Containers.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Schema.scala]: Schema.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Evals.scala]: Evals.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Predicates.scala]: Predicates.scala.md
[main/scala/ohnosequences/scarph/Paths.scala]: ../../Paths.scala.md
[main/scala/ohnosequences/scarph/Indexes.scala]: ../../Indexes.scala.md
[main/scala/ohnosequences/scarph/Evals.scala]: ../../Evals.scala.md
[main/scala/ohnosequences/scarph/Conditions.scala]: ../../Conditions.scala.md
[main/scala/ohnosequences/scarph/Steps.scala]: ../../Steps.scala.md
[main/scala/ohnosequences/scarph/Predicates.scala]: ../../Predicates.scala.md
[main/scala/ohnosequences/scarph/Schemas.scala]: ../../Schemas.scala.md
[main/scala/ohnosequences/scarph/Combinators.scala]: ../../Combinators.scala.md
[main/scala/ohnosequences/scarph/syntax/GraphTypes.scala]: ../../syntax/GraphTypes.scala.md
[main/scala/ohnosequences/scarph/syntax/Paths.scala]: ../../syntax/Paths.scala.md
[main/scala/ohnosequences/scarph/syntax/Conditions.scala]: ../../syntax/Conditions.scala.md
[main/scala/ohnosequences/scarph/syntax/Predicates.scala]: ../../syntax/Predicates.scala.md