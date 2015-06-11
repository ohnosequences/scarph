

# Formal construction

Let's consider the classic formal construction of a graph and then add to it some properties that are specific to our domain of interest. Let $G = (V, E)$ be a graph, where $V$ is a finite set of vertices and $E = \{ (s, t): s, t ∈ V \}$ a finite set of (directed) edges connecting vertices, where for every edge $e = (s, t)$, $s$ is called it's source and $t$ — it's target. For each vertex we can consider a set of incoming or outgoing edges (those which have it as target or source correspondingly).

All elements of this construction have types. Let's represent the set of vertices as a disjoint union $V = V_1 ⊔ V_2 ⊔ ... ⊔ V_n$, so that saying "a vertex $v$ has type $V_k$" or $v : V_k$ means just $v ∈ V_k$ and every vertex has a determined type.

For edges we consider a splitting $E = E_1 ⊔ E_2 ⊔ ... ⊔ E_m$, such that for every $E_i = \{ (s, t): s ∈ V_{i_s}, t ∈ V_{i_t} \}$, i.e. any two edges of the same type $E_i$ have sources of the same type $V_{i_s}$ and targets of the same type $V_{i_t}$.

Now let's fix some universe of primitive types $\mathbf{U}$. A property $p_T ∈ P$ is just a label for the type $T : \mathbf{U}$ and a value of this property is just a value of $T$ labeled with $p_T$. With every vertex type $V_k$ we associate a set of properties $P_{V_k} ⊂ P$ (same for the edge types).

<!-- `TODO: formalize queries?` -->

Now let's see how this construction can be defined in Scala code using the **Scarph** library.


# Methods

## Declaring a graph schema in **Scarph**

As we state that the data model is our main priority, this is the first thing that we want to define and then use in our queries. The data model is basically a graph schema. Let's take some simple variation of the Twitter data model, which should be familiar for any contemporary reader and define a **Scarph** graph schema for it.


### Properties  

Properties are defined in **Scarph** as singletons of the `Property` type, parametrized by a primitive Scala type. Actually any type, not only primitive, can be used, but depending on the limitations of a database backend, it may be needed to define a serialization/deserialization mechanisms for it.

```scala
// These will be the properties of users:
case object name extends Property[String]
case object age  extends Property[Integer]

// these will be the properties of tweets:
case object text extends Property[String]

// and these are the properties of an edge:
case object time extends Property[Date]
case object url  extends Property[String]
```


### Vertex types  

Vertex types are defined as singletons of `VertexType` with a string label and a set of properties:

```scala
case object User  extends VertexType("user", name :~: age :~: ∅)

case object Tweet extends VertexType("tweet", text :~: ∅)
```

Note that in **Scarph** we use `TypeSet` for constructions like these sets of properties. A `TypeSet` is basically a heterogeneous list allowing to store only one element of a type. This construction allows us to have lists of distinct singleton objects without loosing any type information about them. For more information about `TypeSet` refer to the **[Cosas][]** library.


### Edge types  

Edge types are a bit more complicated than vertex types, as they require some more information to be declared statically, besides the string label and the property set:

- source and target vertex types
- arities of source and target

Arity is just a particular type of container for a vertex type, which can be either `Option` or `List`, meaning "one" or "many" correspondingly. For example,

```scala
case object Posted  extends EdgeType(User, "posted", Tweet, time :~: url :~: ∅)
                            with OneIn with ManyOut

case object Follows extends EdgeType(User, "follows", User, ∅)
                            with ManyIn with ManyOut
```

It's easy to see from this definition, that the `Posted` edge type connects the `User` vertex type and the `Tweet` vertex type. And from the `with OneIn with ManyOut` mixin we know that _one user can post many tweets_. It means, that one tweet cannot be posted by different users and this is an important restriction on the data model. On the contrast, `Follows` edge can connect many users with many users — it's symmetric.


### Graph schema  

The schema that we wanted to define is basically the set of the objects that we defined. So we can just combine them all together:

```scala
val schema = GraphSchema("twitter",
  vertexTypes = User :~: Tweet :~: ∅,
  edgeTypes = Posted :~: Follows :~: ∅
)
```

This can be useful in general, but is not essential for working with the graph types.


## Type wrapping and type denotation

The reason that we use singleton objects for the graph types instead of some kind of OOP-style class hierarchy, is that we want to operate with these types as values. The fact that it is a singleton means that there is only one (Scala-)value of this type — the object: `User : User.type`. So now, when we have our graph types as represented by the Scala singletons, how do we create values of these types? For this there are two orthogonal concepts: type wrapping (similar to the widely known trick of type tagging) and type denotations.

> Note that the code here is given in a simplified form to shorten the explanation and focus on the discussed topic. For the actual code, refer to the sources of the **[Cosas][]** project.

One can think of the `Property`, `VertexType` and `EdgeType` types as some sort of labels. Now, to denote these types we use the following construction:

```scala
trait AnyDenotation extends AnyWrap {
  // the "label type" that we want to denote
  type Tpe
  val  tpe: Tpe
}
```

where `AnyWrap` is defined as follows:

```scala
trait AnyWrap {
  // some raw representation that we are wrapping
  type Raw
}
```

Here the `Raw` type is meant to be some normal Scala type from the backend-specific implementation. So a denotation basically binds together two things: a "label type" `Tpe` and an implementation type `Raw`.

To create a value of the wrapped type (the one that extends `AnyWrap`), there is the following construction:

```scala
// This binds together the wrapping and it's raw type:
trait AnyWrappedValue {
  type Wrap <: AnyWrap
  type Value = Wrap#Raw
}

// This is a constructor of a wrapped value:
class ValueOf[W <: AnyWrap](val raw: W#Raw)
  extends AnyWrappedValue { type Wrap = W }
```

For convenience values of the `AnyWrap` type have this method:

```scala
def apply(r: this.Raw): ValueOf[this.type] = new ValueOf[this.type](r)
```

To clarify this, let's consider a primitive example:

```scala
case object Answer extends AnyWrap { type Raw = Boolean }

val yes = Answer(true)
```

We know that `yes` is a value of the wrapped `Boolean` type: `yes: ValueOf[Answer.type]`, but we can still easily access the actual `Boolean` value: `yes.raw == true`.

A _denotation_ is also a wrapped type with the only difference: it stores a reference to a value of some "label type".


## Denoting graph types

Let's return to our graph types and consider how their denotations are defined:

```scala
// A vertex is a denotation of a vertex type
trait AnyVertex extends AnyDenotation {
  type Tpe <: AnyVertexType
}
```

This trait just bounds the label type, but it still doesn't define the `Raw` type member. This is made in one of the possible implementations. For example, in the [TitanDB][] implementation we have

```scala
trait AnyTitanVertex extends AnyVertex {
  type Raw = com.thinkaurelius.titan.core.TitanVertex
}

class TitanVertex[VT <: AnyVertexType](val tpe: VT) extends AnyTitanVertex
```

The construction for edge types denotation is similar and straightforward with the only difference that is refers to the denotations of source and target vertex types.

Properties are simpler, because they wrap primitive types and the flexibility of defining several implementations (with different `Raw` type) is not needed. So a property denotes itself and defines it's raw type immediately. For example, for the properties defined above, we can create values as simple as this:

```scala
val myName = name("Alexey")

val myAge = age(25)
```


## Implementing a schema

To start working with the schema we defined before, we have to declare denotations of those graph types binding the with a particular implementation. Let's use the TitanDB implementation for that:

```scala
case object user extends TitanVertex(User)
case object tweet extends TitanVertex(Tweet)

case object posted extends TitanEdge(user, Posted, tweet)
case object follows extends TitanEdge(user, Follows, user)
```

We don't need to do anything with properties here as they already have their implementations. In the above terminology `user` singleton _denotes_ the vertex type `User` _wrapping_ the raw TitanDB vertex representation. We use the naming convention to distinguish denotations from the "label" types: first are written with a row letter, while the last start with a capital letter. From the Scala perspective they are both same kind of things: singleton objects.

Now that we have all the components tied up together, we can use it for constructing safe queries to the database which was our original goal.


## Building queries

Let's imagine we've got a value of the `user` vertex from the database:

```scala
val martin: ValueOf[user.type] = ...
```

> Note here that the usual way one would get this node is by querying a database index by some predicate. This is also a part of the **Scarph** library, completed and usable, but we leave it outside of this write-up for the reasons of simplicity and to concentrate on the more important topics.

We know that `martin.Wrap.Tpe` is `User` and we know which properties are associated with this label type: `name :~: age :~: ∅`, so we can query it:

```scala
martin.get(name): ValueOf[name.type]

martin.get(age): ValueOf[age.type]
```

The explicit type signatures here are only for clarity. If we try to query for a property that is not associated with the `User` type, like `martin.get(text)`, we will get a _compilation error_, saying that there is _no evidence_ that `User` type has the `text` property. So the important implication is that we _can't construct_ this query, even though it has a syntactically correct structure (vertex-get-property), because it doesn't fit into the graph schema that we defined before. Therefore it won't be executed and failed in runtime.

Some other primitive queries are getting the source and target of an edge:

```scala
val e: ValueOf[posted.type] = ...

e.source: ValueOf[user.type]

e.target: ValueOf[tweet.type]
```

Here we know the exact types of the return values as they were declared in the schema. And finally for vertices we can query incoming and outgoing edges:

```scala
martin.inE(follows): List[ValueOf[follows.type]]

martin.outE(posted): List[ValueOf[posted.type]]
```

with these queries we get lists of edges because of the arity of these edges. On the contrast, if we have a tweet, we know that we will get only one user that has posted it:

```scala
val t: ValueOf[tweet.type] = ...

t.inE(posted): ValueOf[posted.type]
```

But this is not the author of this tweet yet. It's an edge connecting user vertex with the tweet vertex. So here we come the moment when we want to combine our primitive queries and as we know types on every step we can just use normal Scala to operate on the results as collections:

```scala
val author: ValueOf[user.type] = t.inE(posted).source

val martinsFollowers: List[ValueOf[user.type]] = martin.inE(follows).map{ _.source }

val martinsTweets: List[ValueOf[tweet.type]] = martin.outE(posted).map{ _.target }
```

An example of a bit more complex and useful query would be to find all Martin's followers that have tweets posted at midnight:

```scala
martin.
  inE(follows).map{ _.source }.                // getting the list of followers
  filter{ follower =>
    follower.outE(posted).map{ _.get(time) }.  // for every follower getting the list of post times
      filter{ _.getHours == 0 }.nonEmpty       // filtering by condition that it's around midnight
  }
```

Same way as with the `get` method for properties, `inE` and `outE` check in compile time that the edge that we query for has this vertex as a source (or a target). For example `tweet.outE(follows)` won't compile because the `Follows` label type has been declared to have `User` vertex type as a source.

So with these basic queries: `get`, `source`, `target`, `inE`, `outE` we have shown that only the queries that conform to the graph schema can be constructed and they can be easily composed to build more complex queries.

<!-- ### Reusing abstract code -->


# Results

With this work we have shown that it is possible to create an Embedded Domain Specific Language in Scala for operating on a graph databases in a type-safe manner. **Scarph** is an extensible and flexible DSL, which allows one to define a graph schema and then use it for constructing compile-time safe queries, which is an essential requirement when it is used in highly scalable distributed systems.

As a proof-of-a-concept **Scarph** has an implementation for the **[TitanDB][]** backend, though staying backend independent and easily extensible. Quoting the official website

> Titan is a scalable graph database optimized for storing and querying graphs containing hundreds of billions of vertices and edges distributed across a multi-machine cluster. Titan is a transactional database that can support thousands of concurrent users executing complex graph traversals in real time.

Also as the graph schema is defined separately from the implementation bindings, it is important to emphasize that the _queries code is actually independent_ from the particular implementation used. Strictly speaking, it requires _some_ implementation to be compiled with, but if one wants to change implementation, they have to substitute only the schema implementation bindings, while _leaving the queries code unchanged_. This is a great result leading to maintainable and reusable client code.

One of the **Scarph** features which is left outside of this review is _extensibility of the schema_. Besides the properties, that are "built-in" with the vertex and edge types, one can provide (in _any_ suitable place) an implicit evidence of the form

```scala
case object email extends Property[String]

implicit val usersHaveEmails = User has email :~: ∅
```

This allows one to define a custom backend-dependent "getter" for this property and then use `user.get(email)` which will be as type-safe as for the built-in properties. This is possible, because vertex and edge types are actually _extensible records_. Same way as for all the other useful abstractions it is moved to the **[Cosas][]** library, to make it reusable for the other projects.

During the development process **Scarph** is continually tested using the [ScalaTest][] routine. These test work not only as a guarantee of the library consistency, but also as an important part of documentation, containing a lot of examples, which could help a potential user to understand the way library works and apply it for their own needs.


# Discussion

It worths mentioning that although currently **Scarph** has a backend implementation only for TitanDB, it was developed with implementation independence in mind. Therefore it is easy to add more implementations, making this library applicable for more use cases, when the backend is an important factor. Some examples of alternative implementations would be **[Neo4j][]**, **[OrientDB][]** and a generic **Gremlin**/**[Tinkerpop3][]**. The last is actually not a particular database technology, but a special abstraction layer, which has already implementations for the most popular databases such as Neo4j and Giraph. Also, one of our Google Summer of Code projects was aimed to design a **Scarph** implementation for Amazon DynamoDB. It is called **[Dynamograph][]** and is in the active development at the moment.

Different implementations lead not only to different use cases when the rest of the project is dependent on a database technology. It is also an opportunity to _mix different backends in one graph schema_. As every vertex or edge type has it's own `Raw` type binding and they are declared separately, one can create a schema, where some elements of the graph are stored in one database and others in another. As different databases use different low-level implementations for indexes, this could be a great opportunity to tune storage effectiveness and query performance.

One of the current development directions is to design a way to define all queries statically in a completely implementation-independent way. This approach leads at least to the following two advantages: firstly, the queries code become truly independent from implementation (i.e. doesn't need it for compilation), because it operates only on the "label types", and secondly, it allows more implementation-dependent optimizations for the way these queries are evaluated. For example, some databases have a primitive operation for retrieving the vertices connected by some outgoing edge. So one could define an evaluator for a composite query of the from `.outE(...).map{ _.target }` as a one-step operation instead of the current two-step traversal.

**Scarph** is not a self-sufficient graph query language like, for example, Gremlin, but rather an Embedded Domain Specific Language. It means that **Scarph** provides the means for building simple type-safe queries and it's possible to combine them using common Scala constructions. It is not a limitation, but a particularity of the use case that it is going to be applied: a Scala API for working with Bio4j. Nevertheless, we are investigating the relation of this approach to other graph query languages and researching the opportunities to design a query language (not necessarily in Scala), which would be complete and independent of the host language.
