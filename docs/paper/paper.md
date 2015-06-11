---
title: 'Scarph: an implementation of the dagger categorical model for graph data'
authors:
- name: Alexey Alekhin
  affiliation: "_[oh no sequences!](http://ohnosequences.com)_ research group, [Era7 bioinformatics](http://www.era7bioinformatics.com)"
  email: "aalekhin@ohnosequences.com"
  position: 1
- name: "Eduardo Pareja--Tobes"
  affiliation: "_[oh no sequences!](http://ohnosequences.com)_ research group, [Era7 bioinformatics](http://www.era7bioinformatics.com)"
  email: "eparejatobes@ohnosequences.com"
  position: 2

abstract: |
  What is _Scarph_?  
  Baby, don't hurt me,  
  Don't hurt me no more!

keywords: [graph databases, DSLs, categories, dagger categories, Scala, functional programming]
date: \date{\currenttime \today}
...


# Introduction

At the moment the standard way of accessing a graph database is either by using Blueprints Java API or one of the specialized graph query languages, such as [Cypher][] or [Gremlin][]. The common problem of all such approaches is that they don't take into account the graph schema on the language level. So using these query languages one can easily make a mistake and write a _syntactically correct_ query, which doesn't make any sense from the point of view of the data model and therefore will _fail in runtime_.

As most of our work is concentrated on using Bio4j in the cloud distributed systems, it is really important to be able to formulate a query to the database in such way that will strictly conform to the data model, making the safety of evaluating this query known in advance.

While there are several frameworks to work with SQL databases from Scala _in a type-safe manner_ (see [ScalaQuery][], [Squeryl][], [Sqltyped][] and [Slick][]), there is nothing similar for graph databases. Scala is our language of choice because of it's effective combination of powerful type system, functional programming paradigm and compatibility with Java. So we want to take advantage of the host language type system to be able to work with Bio4j in a way that is clearly and strictly determined by the graph schema. This the aim of the **Scarph** project which obviously target the second global objective of our project: to develop a new system to extract a particular type of data from the Bio4j graph database.

So, in short, **Scarph** is an API / DSL (domain specific language) for accessing graph DB and building queries in a type-safe manner.



<!--  -->

[statika]: http://ohnosequences.com/statika/
[tabula]: https://github.com/ohnosequences/tabula
[scarph]: https://github.com/ohnosequences/scarph
[cosas]: https://github.com/ohnosequences/cosas
[cypher]: http://docs.neo4j.org/chunked/stable/cypher-introduction.html
[gremlin]: https://github.com/tinkerpop/gremlin/wiki
[scalaQuery]: http://scalaquery.org/
[squeryl]: http://squeryl.org/
[sqltyped]: https://github.com/jonifreeman/sqltyped
[slick]: http://slick.typesafe.com/
[titandb]: http://thinkaurelius.github.io/titan/
[tinkerpop3]: http://www.tinkerpop.com/docs/3.0.0.M2/
[neo4j]: http://www.neo4j.org/
[orientdb]: http://www.orientechnologies.com/orientdb/
[bio4j/scala-model]: https://github.com/bio4j/scala-model
[dynamograph]: https://github.com/bio4j/dynamograph
[scalatest]: http://scalatest.org/
