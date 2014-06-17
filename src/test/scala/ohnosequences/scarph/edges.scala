package ohnosequences.scarph.test

import ohnosequences.scarph._, ops.default._

object edges {

  import edgeTypes._
  import vertexTypes._
  
  import vertices._
  import properties._

  case class MemberOfImpl(
    val source: user.Rep,
    val target: org.Rep
  )(
    val isPublic: Boolean,
    val since: Int,
    val validUntil: Int
  )

  case object memberOf extends Edge(MemberOf) {
    
    type Raw = MemberOfImpl

    // implicit case object sourceGetter extends GetSource[user.type](user) {
    //   def apply(rep: memberOf.Rep) = rep.source
    // }

    implicit object readSince extends GetProperty(since) {
      def apply(rep: Rep) = rep.since
    }
    implicit object readValidUntil extends GetProperty(validUntil) {
      def apply(rep: Rep) = rep.validUntil
    }
  }

  implicit case object memberSourceGetter extends memberOf.GetSource[user.type](user) {
      def apply(rep: memberOf.Rep) = rep.source
    }

  case class OwnsImpl(
    val source: user.Rep,
    val target: org.Rep
  )
  object owns extends Edge(Owns) { self =>
    
    type Raw = OwnsImpl

    implicit object sourceGetter extends GetSource[user.type](user) {
      def apply(rep: self.Rep) = rep.source
    }
    implicit object targetGetter extends GetTarget[org.type](org) {
      def apply(rep: self.Rep) = rep.target
    }
  }

}

class EdgeSuite extends org.scalatest.FunSuite {

  import edges._  
  import edgeTypes._

  import vertexTypes._
  import vertices._
  import properties._

  test("retrieve edge's sourde-edge-target") {

    import edges.memberOf._
    import edgeTypes.MemberOf._
    val u = user ->> UserImpl(id = "1ad3a34df", name = "Robustiano Satrústegui", since = 2349965)
    val oracle: org.Rep = org ->> UserImpl(id = "NYSE:ORCL", name = "Orcale Inc.", since = 1977)

    val m = memberOf ->> MemberOfImpl(source = u, target = oracle)(isPublic = true, since = 2349965, validUntil = 38724987)

    assert((m source) === u)

    // I think this is a bug; this import shouldn't be needed
    import vertexTypes.User._
    assert(
      (m.source get id) === "1ad3a34df"
    )

    // val followers_ids = (user out follows) map { _ get id }

    /* Adding target getter externally: */
    implicit object targetGetter extends GetTarget[org.type](org) {
      def apply(rep: memberOf.Rep) = rep.target
    }
    assert(m.target === oracle)

    /* Getting edge properties */
    assert(m.get(since) === 2349965)
    assert(m.get(validUntil) === 38724987)

    // NOTE: this works in scala-2.10, but not in scala-2.11:
    // implicit val weForgotToImportIt = memberOf.tpe has isPublic
    // these two work in both:
    // implicit val weForgotToImportIt = (memberOf.tpe: memberOf.Tpe) has isPublic
    implicit val weForgotToImportIt = MemberOf has isPublic

    implicit object readIsPublic extends GetProperty(isPublic) {
      def apply(rep: memberOf.Rep) = rep.isPublic
    }
    assert(m.get(isPublic) === true)

    /* More vartices and edges */
    val bob = user ->> UserImpl(id = "-1", name = "Bob", since = 0)
    val martin = user ->> UserImpl(id = "0", name = "Martin Odersky", since = 2011)
    val paulp = user ->> UserImpl(id = "1", name = "Paul Phillips", since = 2011)

    val typesafe = org ->> UserImpl(id = "martin123", name = "Typesafe Inc.", since = 2011)
    

    /* Just a static list of all `memberOf` edges */
    val members: List[memberOf.Rep] = List(
      memberOf ->> MemberOfImpl(u, oracle)(false, 0, 0),
      // every company has some bob
      memberOf ->> MemberOfImpl(bob, oracle)(true, 0, 0),
      memberOf ->> MemberOfImpl(bob, typesafe)(false, 0, 0),
      memberOf ->> MemberOfImpl(martin, typesafe)(true, 0, 0)
      // memberOf ->> MemberOfImpl(paulp, typesafe)(true, 0, 0) // not anymore
    )

    /* Retrieving edge */
    implicit def retrieveMemberOf(e: memberOf.type) = new user.GetOutEdge(memberOf) {
      def apply(rep: user.Rep) = members filter { _.source == rep }
    }
    assert(bob.out(memberOf).map(_.target) === List(oracle, typesafe))


    val owners: List[owns.Rep] = List(
      owns ->> OwnsImpl(u, oracle), // together with bob of course
      owns ->> OwnsImpl(bob, oracle),
      owns ->> OwnsImpl(martin, typesafe)
    )

    implicit def retrieveOwns(e: owns.type) = new user.GetOutEdge(owns) {
      def apply(rep: user.Rep) = owners find { _.source == rep }
    }
    assert(((martin out owns) map (_.target)) === Some(typesafe))
    // cool, martin owns some typesafe org

  }
}
