# Witnesses for axioms

I want to have here a way of representing axioms of the different theories we are working with. The base notion is a dagger category, with axioms those of a category plus

- (f^†)^† = f
- (g f)^† = f^† g^†
- 1^† = 1

In the case of our query language, it should be easy to "prove" this, in the sense of checking both type and value equality. What do we mean by a proof here? It should mean that these equations are true up to rewriting using the axioms. This should relate with rewriting in monoidal categories and similar subjects; see for example http://arxiv.org/pdf/1405.2618.pdf.

We can then build the other theories on top: dagger biproducts, dagger separable frobenius, traces, etc.

### A naive design

``` scala
trait AnyAxiom {

  // with the same source and target?
  type First <: AnyGraphMorphism

  type Second <: AnyGraphMorphism
}
```

A problem we could have with this encoding is that `F <: AnyGraphMorphism { type Source = F#Source }`. We can

1. leave source and target free
2. introduce a bias and extract it from say the `First` morphism

The second option above is attractive in that it could be useful for writing directed rewritings which prefer one side of a particular axiom.

#### daggers

Taking the dagger structure as primitive, axioms should relate the respective daggers. This can be done through a dagger type member for `AnyAxiom`.

### Uses

There's an obvious application: implementing static query rewriting for a given schema. If there's a particular (atomic) pattern that you *always* want to rewrite to something else, it would be good to have a way of recursing through a particular query and do just that. For non-primitive patterns, more care should be taken and a truly recursive solution would be needed.

In the case of generic axioms, they are also useful for implementations. We can provide a set of generic tests which implementations can use for "proving" their correctness. This also calls for assuming that the types used in implementations would have an equality for morphisms, which in most cases would have the form of an uncheckable extensional function equality. In the Titan case, for example, we are essentially working in spans viewed as the kleisli category of the free commutative monoid monad, with `Iterable[X]`s representing the free commutative monoid on `X`. Equality for them is defined by equality in `X` up to reordering; and function equality by extensionality.

For non-generic axioms an API for writing recursive rewritings would be really useful.

#### Internalizing axioms

If we assume a dagger monoidal closed structure, we can reduce checking equality between parallel morphisms to the case of morphisms with target (or source) $I$ (take names or conames). That amounts to an internal version of extensionality. In spans it basically says that.

### Axioms and rewriting

Shall we prohibit rewritings not based on axioms? sounds attractive, but would it make something useful impossible? Another point to consider is how to *use* axioms. If they're going to be part of the implicit scope, we need to

1. find a place for generic axioms, so that they don't need to be explicitly imported
2. morphism-specific axioms (like being dagger mono) could be part of the graph schema

It would be nice if we could derive a set of rules for composing rewritings which would amount to naturality.
