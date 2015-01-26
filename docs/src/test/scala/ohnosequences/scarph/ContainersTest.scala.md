
```scala
package ohnosequences.scarph.test

import ohnosequences.cosas.fns._
import ohnosequences.scarph.containers._
```

Some general tests

```scala
class ContainersTest extends org.scalatest.FunSuite {

  test("Check full multiplication table for containers") {

    implicitly[(ExactlyOne × ExactlyOne) with Out[ExactlyOne]]
    implicitly[(ExactlyOne × OneOrNone)  with Out[OneOrNone]]
    implicitly[(ExactlyOne × AtLeastOne) with Out[AtLeastOne]]
    implicitly[(ExactlyOne × ManyOrNone) with Out[ManyOrNone]]

    implicitly[(OneOrNone × ExactlyOne) with Out[OneOrNone]]
    implicitly[(OneOrNone × OneOrNone)  with Out[OneOrNone]]
    implicitly[(OneOrNone × AtLeastOne) with Out[ManyOrNone]]
    implicitly[(OneOrNone × ManyOrNone) with Out[ManyOrNone]]

    implicitly[(AtLeastOne × ExactlyOne) with Out[AtLeastOne]]
    implicitly[(AtLeastOne × OneOrNone)  with Out[ManyOrNone]]
    implicitly[(AtLeastOne × AtLeastOne) with Out[AtLeastOne]]
    implicitly[(AtLeastOne × ManyOrNone) with Out[ManyOrNone]]

    implicitly[(ManyOrNone × ExactlyOne) with Out[ManyOrNone]]
    implicitly[(ManyOrNone × OneOrNone)  with Out[ManyOrNone]]
    implicitly[(ManyOrNone × AtLeastOne) with Out[ManyOrNone]]
    implicitly[(ManyOrNone × ManyOrNone) with Out[ManyOrNone]]
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