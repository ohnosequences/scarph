package ohnosequences.scarph.restricted.test

import ohnosequences.scarph._
import ohnosequences.scarph.titan._
import SimpleSchema._

object SimpleSchemaImplementation {
  
  case object human extends TVertex(Human)
  case object humans extends TVertex(Humans)
  case object humanProps extends TEdge(human, HumanProps, humans)

  case object article extends TVertex(Article)
  case object articles extends TVertex(Articles)
  case object articleProps extends TEdge(article, ArticleProps, articles)

  case object author extends TEdge(article, Author, human)
  case object knows extends TEdge(human, Knows, human)

}
