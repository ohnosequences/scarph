package ohnosequences.scarph.test

import ohnosequences.cosas.fns._
import ohnosequences.scarph.containers._

/* Some general tests */
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
