package ohnosequences.scarph.restricted.test

import ohnosequences.scarph._

object SimpleSchema {
  
  /* Properties (that we will use only for edges) */
  case object name extends Property[String]
  case object phone extends Property[Integer]

  case object title extends Property[String]
  case object published extends Property[java.lang.Boolean]

  /* Vertex types and edge types representing them */
  case object Human extends VertexType("Human")
  case object Humans extends VertexType("Humans")
  case object HumanProps extends ManyToOne(Human, "HumanProps", Humans)
  implicit val human_name = HumanProps has name
  implicit val human_phone = HumanProps has phone


  case object Article extends VertexType("Article")
  case object Articles extends VertexType("Articles")
  case object ArticleProps extends ManyToOne(Article, "ArticleProps", Articles)
  implicit val article_title = ArticleProps has title
  implicit val article_published = ArticleProps has published

  /* Relationships */
  case object Author extends ManyToMany(Article, "author", Human)
  case object Knows extends ManyToMany(Human, "knows", Human)

}
