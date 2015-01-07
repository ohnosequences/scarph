
```scala
package ohnosequences.scarph.test

// NOTE: this is just copied from https://github.com/bvenners/equality-integration-demo
import scalaz._
import Scalaz.{ToEqualOps => _, _}

import org.scalactic._
import org.scalatest._
import TripleEqualsSupport.AToBEquivalenceConstraint
import TripleEqualsSupport.BToAEquivalenceConstraint
import scala.language.implicitConversions

final class ScalazEquivalence[T](equal: Equal[T]) extends Equivalence[T] {
  def areEquivalent(a: T, b: T): Boolean = equal.equal(a, b)
}

trait LowPriorityScalazConstraints extends TripleEquals {
implicit def lowPriorityScalazConstraint[A, B](implicit equalOfB: Equal[B], ev: A => B): Constraint[A, B] =
  new AToBEquivalenceConstraint[A, B](new ScalazEquivalence(equalOfB), ev)
}

trait ScalazEquality extends LowPriorityScalazConstraints {
  override def convertToEqualizer[T](left: T): Equalizer[T] = super.convertToEqualizer[T](left)
  implicit override def convertToCheckingEqualizer[T](left: T): CheckingEqualizer[T] = new CheckingEqualizer(left)
  override def unconstrainedEquality[A, B](implicit equalityOfA: Equality[A]): Constraint[A, B] = super.unconstrainedEquality[A, B]
  implicit def spireConstraint[A, B](implicit equalOfA: Equal[A], ev: B => A): Constraint[A, B] =
  new BToAEquivalenceConstraint[A, B](new ScalazEquivalence(equalOfA), ev)
}

object ScalazEquality extends ScalazEquality

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

[test/scala/ohnosequences/scarph/ContainersTest.scala]: ContainersTest.scala.md
[test/scala/ohnosequences/scarph/ScalazEquality.scala]: ScalazEquality.scala.md
[test/scala/ohnosequences/scarph/titan/TwitterTitanTest.scala]: titan/TwitterTitanTest.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: TwitterSchema.scala.md
[main/scala/ohnosequences/scarph/GraphTypes.scala]: ../../../../main/scala/ohnosequences/scarph/GraphTypes.scala.md
[main/scala/ohnosequences/scarph/Containers.scala]: ../../../../main/scala/ohnosequences/scarph/Containers.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Schema.scala]: ../../../../main/scala/ohnosequences/scarph/impl/titan/Schema.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Evals.scala]: ../../../../main/scala/ohnosequences/scarph/impl/titan/Evals.scala.md
[main/scala/ohnosequences/scarph/impl/titan/Predicates.scala]: ../../../../main/scala/ohnosequences/scarph/impl/titan/Predicates.scala.md
[main/scala/ohnosequences/scarph/Paths.scala]: ../../../../main/scala/ohnosequences/scarph/Paths.scala.md
[main/scala/ohnosequences/scarph/Indexes.scala]: ../../../../main/scala/ohnosequences/scarph/Indexes.scala.md
[main/scala/ohnosequences/scarph/Evals.scala]: ../../../../main/scala/ohnosequences/scarph/Evals.scala.md
[main/scala/ohnosequences/scarph/Conditions.scala]: ../../../../main/scala/ohnosequences/scarph/Conditions.scala.md
[main/scala/ohnosequences/scarph/Steps.scala]: ../../../../main/scala/ohnosequences/scarph/Steps.scala.md
[main/scala/ohnosequences/scarph/Predicates.scala]: ../../../../main/scala/ohnosequences/scarph/Predicates.scala.md
[main/scala/ohnosequences/scarph/Schemas.scala]: ../../../../main/scala/ohnosequences/scarph/Schemas.scala.md
[main/scala/ohnosequences/scarph/Combinators.scala]: ../../../../main/scala/ohnosequences/scarph/Combinators.scala.md
[main/scala/ohnosequences/scarph/syntax/GraphTypes.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/GraphTypes.scala.md
[main/scala/ohnosequences/scarph/syntax/Paths.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/Paths.scala.md
[main/scala/ohnosequences/scarph/syntax/Conditions.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/Conditions.scala.md
[main/scala/ohnosequences/scarph/syntax/Predicates.scala]: ../../../../main/scala/ohnosequences/scarph/syntax/Predicates.scala.md