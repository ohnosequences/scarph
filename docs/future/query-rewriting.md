# Query rewriting

In a lot of cases one might want to use indexes, substitute queries by others known to be equal, etc.

With our design, I think that a lot of these operations can be done statically. Take the example of an "axiom" `q = s` with parallel `q` and `s`. It could be the case that I always want to substitute `q` by `s`. How? Exactly the same as how this is done for evaluation! it is _actually_ evaluation.

- user outV posted inV posted
- id(user)

We could write generic traits for substitution which would have top priority. This sounds nice.
