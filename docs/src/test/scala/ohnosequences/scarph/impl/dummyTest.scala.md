
```scala
package ohnosequences.scarph.test

import ohnosequences.scarph._, objects._, morphisms._, evals._
import syntax.morphisms._, syntax.objects._
import twitter._, dummy._

class DummyTests extends org.scalatest.FunSuite {

  test("dummy evaluators on sample queries") {

    val query1  = lookup(user.name)
    val query2  = duplicate(user)
    val query3  = inE(posted)
    val query4  = inE(posted) >=> source(posted)
    val query5  = fromZero(user) >=> toZero(user) >=> fromZero(tweet)
    val query6  = duplicate(user) >=> outV(posted) ⊗ outV(posted)
    val query7  = inV(follows) ⊕ outV(follows)
    val query8  = get(user.name)
    val query9  = lookup(user.name) >=> query8
    val query10 = fromUnit(user) >=> toUnit(user)
    val query11 = query6 >=> matchUp(tweet)
    val query12 = query7 >=> merge(user)
    val query13 = quantify(tweet ? (tweet.text === "foo") and (tweet.url === "www"))
    val query14 = coerce(query13.predicate)
    val query15 = (query6 ⊗ query7) distribute

    println("------------")
    println(evaluate(query2) evalPlan)
    println("------------")
    println(evaluate(query3) evalPlan)
    println("------------")
    println(evaluate(query4) evalPlan)
    println("------------")
    println(evaluate(query5) evalPlan)
    println("------------")
    println(evaluate(query6) evalPlan)
    println("------------")
    println(evaluate(query7) evalPlan)
    println("------------")
    println(evaluate(query8) evalPlan)
    println("------------")
    println(evaluate(query9) evalPlan)
    println("------------")
    println(evaluate(query10) evalPlan)
    println("------------")
    println(evaluate(query11) evalPlan)
    println("------------")
    println(evaluate(query12) evalPlan)
    println("------------")
    println(evaluate(query13) evalPlan)
    println("------------")
    println(evaluate(query14) evalPlan)
    println("------------")
    // no distribute eval
    // println(query15.present)*/

    val uh2 = query2 on (user := Dummy)

    val uh1 = query1 on (name := Dummy)

    val uh1a = (name := Dummy) :=>: query1

    assert( 
      query6.dagger.dagger === query6
    )
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
          + [TwitterQueries.scala][test/scala/ohnosequences/scarph/TwitterQueries.scala]
          + impl
            + [dummyTest.scala][test/scala/ohnosequences/scarph/impl/dummyTest.scala]
            + [dummy.scala][test/scala/ohnosequences/scarph/impl/dummy.scala]
          + [TwitterSchema.scala][test/scala/ohnosequences/scarph/TwitterSchema.scala]
  + main
    + scala
      + ohnosequences
        + scarph
          + [morphisms.scala][main/scala/ohnosequences/scarph/morphisms.scala]
          + [objects.scala][main/scala/ohnosequences/scarph/objects.scala]
          + [evals.scala][main/scala/ohnosequences/scarph/evals.scala]
          + [implementations.scala][main/scala/ohnosequences/scarph/implementations.scala]
          + [schemas.scala][main/scala/ohnosequences/scarph/schemas.scala]
          + [naturalIsomorphisms.scala][main/scala/ohnosequences/scarph/naturalIsomorphisms.scala]
          + syntax
            + [morphisms.scala][main/scala/ohnosequences/scarph/syntax/morphisms.scala]
            + [objects.scala][main/scala/ohnosequences/scarph/syntax/objects.scala]

[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: dummyTest.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: dummy.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../TwitterSchema.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: ../../../../../main/scala/ohnosequences/scarph/morphisms.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: ../../../../../main/scala/ohnosequences/scarph/objects.scala.md
[main/scala/ohnosequences/scarph/evals.scala]: ../../../../../main/scala/ohnosequences/scarph/evals.scala.md
[main/scala/ohnosequences/scarph/implementations.scala]: ../../../../../main/scala/ohnosequences/scarph/implementations.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: ../../../../../main/scala/ohnosequences/scarph/schemas.scala.md
[main/scala/ohnosequences/scarph/naturalIsomorphisms.scala]: ../../../../../main/scala/ohnosequences/scarph/naturalIsomorphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: ../../../../../main/scala/ohnosequences/scarph/syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: ../../../../../main/scala/ohnosequences/scarph/syntax/objects.scala.md