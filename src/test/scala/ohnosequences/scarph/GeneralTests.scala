package ohnosequences.scarph.test

import ohnosequences.scarph._
import ohnosequences.cosas._

/* Some general tests */
class GeneralSuite extends org.scalatest.FunSuite {

  test("Check full multiplication table for arities") {
    implicitly[(OneOrNone  x OneOrNone)  with Out[OneOrNone]]
    implicitly[(ExactlyOne x ExactlyOne) with Out[ExactlyOne]]
    implicitly[(ManyOrNone x ManyOrNone) with Out[ManyOrNone]]
    implicitly[(AtLeastOne x AtLeastOne) with Out[AtLeastOne]]

    implicitly[(ExactlyOne x OneOrNone)  with Out[OneOrNone]]
    implicitly[(ExactlyOne x ManyOrNone) with Out[ManyOrNone]]
    implicitly[(ExactlyOne x AtLeastOne) with Out[AtLeastOne]]

    implicitly[(OneOrNone  x ExactlyOne) with Out[OneOrNone]]
    implicitly[(ManyOrNone x ExactlyOne) with Out[ManyOrNone]]
    implicitly[(AtLeastOne x ExactlyOne) with Out[AtLeastOne]]

    implicitly[(OneOrNone x ManyOrNone) with Out[ManyOrNone]]
    implicitly[(OneOrNone x AtLeastOne) with Out[ManyOrNone]]

    implicitly[(ManyOrNone x OneOrNone) with Out[ManyOrNone]]
    implicitly[(AtLeastOne x OneOrNone) with Out[ManyOrNone]]

    implicitly[(AtLeastOne x ManyOrNone) with Out[AtLeastOne]]
    implicitly[(ManyOrNone x AtLeastOne) with Out[AtLeastOne]]
  }

}
