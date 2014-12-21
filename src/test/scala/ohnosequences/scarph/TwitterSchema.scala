package ohnosequences.scarph.test

object Twitter {

  import ohnosequences.cosas._, typeSets._

  import ohnosequences.{ scarph => s }
  import s.graphTypes._, s.steps._, s.combinators._, s.containers._, s.indexes._, s.schemas._


  case object user extends Vertex
  case object name extends PropertyOf(user) { type Raw = String }
  case object age  extends PropertyOf(user) { type Raw = Integer }

  case object tweet extends Vertex
  case object text  extends PropertyOf(tweet) { type Raw = String }

  case object posted extends Edge(user -> ManyOrNone.of(tweet))
  case object time extends PropertyOf(posted) { type Raw = String }
  case object url  extends PropertyOf(posted) { type Raw = String }

  case object follows extends Edge(ManyOrNone.of(user) -> ManyOrNone.of(user))
  case object liked   extends Edge(ManyOrNone.of(user) -> ManyOrNone.of(tweet))

  // case object posted extends Edge(user, tweet) with InArity[ExactlyOne] with OutArity[ManyOrNone]
  // case object time extends PropertyOf(posted) { type Raw = String }
  // case object url  extends PropertyOf(posted) { type Raw = String }

  implicitly[ follows.TargetV ≃ name.Owner ]

  // case object follows extends Edge(user, user) with InArity[ManyOrNone] with OutArity[ManyOrNone]

  // case object liked extends Edge(user, tweet) with InArity[ManyOrNone] with OutArity[ManyOrNone]

  // simple indexes
  case object userByName extends SimpleIndex(user, name)
  case object tweetByText extends SimpleIndex(tweet, text)
  case object postedByTime extends SimpleIndex(posted, time)

  // composite indexes
  case object userByNameAndAge extends CompositeIndex(user, name :~: age :~: ∅)

  // vertex-centric indexes
  case object postedByTimeAndUrlLocal extends LocalEdgeIndex(posted, OnlySourceCentric, time :~: url :~: ∅)

  val schema = Schema(label = "twitter",
    properties = name :~: age :~: text :~: time :~: url :~: ∅,
    vertices =  user :~: tweet :~: ∅,
    edges = posted :~: follows :~: liked :~: ∅,
    indexes = 
      // userByName :~: userByNameAndAge :~:
      // tweetByText :~: 
      // postedByTime :~: 
      // postedByTimeAndUrlLocal :~: 
      ∅
  )

}

// object StupidQueries {
//   import Twitter._

//   val uh = InV(follows) map Get(name)
//   val zz = Target(follows) >=> InE(follows)
//   val altSyntax = Target(follows) >=> InE(follows)
//   val ups = InE(posted)
//   // this is clunky right now, but it works
//   val uuuuh = InE(posted) map Target(posted)

//   val asdfadf = InV(follows) map InV(follows)
//   val asdfadf2 = InV(follows) map InV(follows)

//   val ohno = Par(
//     InV(follows) map InV(follows),
//     Target(follows) >=> InE(follows)
//   )

//   val sfdsd = ((InV(follows) map InV(follows)) ⨂ (Target(follows) >=> InE(follows))) ⨁ (InV(follows) map OutV(posted))

//   val yurj = rev( Target(follows) >=> InE(follows) )
// }
