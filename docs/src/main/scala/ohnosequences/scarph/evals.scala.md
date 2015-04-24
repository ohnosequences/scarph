
```scala
package ohnosequences.scarph

object evals {

  import ohnosequences.cosas.types._
  import objects._, morphisms._, implementations._


  trait AnyMorphismTransform {

    type InMorph <: AnyGraphMorphism
    type OutMorph

    def apply(morph: InMorph): OutMorph
  }
```

Transforms a morphism to a function

```scala
  trait AnyEval extends AnyMorphismTransform {

    type InVal
    type OutVal

    type Input = InMorph#In := InVal
    type Output = InMorph#Out := OutVal

    type OutMorph = Input => Output

    def present(morph: InMorph): String
  }

  @annotation.implicitNotFound(msg = "Cannot evaluate morphism ${M} on input ${I}, output ${O}")
  trait Eval[I, M <: AnyGraphMorphism, O] extends AnyEval {

    type InMorph = M
    type InVal = I
    type OutVal = O
  }
```

Transforms a morphism to another morphism with same domain/codomain

```scala
  trait AnyRewrite extends AnyMorphismTransform {

    type OutMorph <: InMorph#In --> InMorph#Out
  }

  @annotation.implicitNotFound(msg = "Cannot rewrite morphism ${M} to ${OM}")
  trait Rewrite[M <: AnyGraphMorphism, OM <: M#In --> M#Out] extends AnyRewrite {

    type InMorph = M
    type OutMorph = OM
  }


  object rewrite {

    def apply[M <: AnyGraphMorphism, OM <: M#In --> M#Out]
      (m: M)(implicit rewr: Rewrite[M, OM]): OM = rewr(m)
  }


  final class evaluate[I, M <: AnyGraphMorphism, O](val f: M, val eval: Eval[I, M, O]) {

    final def on(input: M#In := I): M#Out := O = eval(f).apply(input)

    // TODO: this should output the computational behavior of the eval here
    final def evalPlan: String = eval.present(f)
  }

  object evaluate {

    def apply[I, IM <: AnyGraphMorphism, OM <: IM#In --> IM#Out, O](m: IM)(implicit
      rewrite: Rewrite[IM, OM],
      eval: Eval[I, OM, O]
    ):  evaluate[I, OM, O] =
    new evaluate[I, OM, O](rewrite(m), eval)
  }


  trait DefaultEvals extends AfterRewritingEvals {

    implicit def id_rewrite[M <: AnyGraphMorphism]:
        Rewrite[M, M] =
    new Rewrite[M, M] { def apply(morph: InMorph): OutMorph = morph }
  }

  trait AfterRewritingEvals {

    // X = X (does nothing)
    implicit final def eval_id[
      I, X <: AnyGraphObject
    ]:  Eval[I, id[X], I] =
    new Eval[I, id[X], I] {

      final def apply(morph: InMorph): OutMorph = { input: Input => input }

      final def present(morph: InMorph): String = morph.label
    }


    // F >=> S
    implicit final def eval_composition[
      I,
      F <: AnyGraphMorphism,
      S <: AnyGraphMorphism { type In = F#Out },
      X, O
    ](implicit
      evalFirst:  Eval[I, F, X],
      evalSecond: Eval[X, S, O]
    ):  Eval[I, F >=> S, O] =
    new Eval[I, F >=> S, O] {

      def apply(morph: InMorph): OutMorph = { input: Input =>

        val firstResult = evalFirst(morph.first)(input)
        evalSecond(morph.second)(morph.second.in := firstResult.value)
      }

      def present(morph: InMorph): String = s"(${evalFirst.present(morph.first)} >=> ${evalSecond.present(morph.second)})"
    }

    // IL ⊗ IR → OL ⊗ OR
    implicit final def eval_tensor[
      IL, IR, I,
      L <: AnyGraphMorphism, R <: AnyGraphMorphism,
      OL, OR, O
    ](implicit
      inTens:  TensorImpl[I, IL, IR],
      outTens: TensorImpl[O, OL, OR],
      evalLeft:  Eval[IL, L, OL],
      evalRight: Eval[IR, R, OR]
    ):  Eval[I, TensorMorph[L, R], O] =
    new Eval[I, TensorMorph[L, R], O] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := outTens(
          evalLeft(morph.left)  ( (morph.left.in:  L#In) := inTens.leftProj(input.value) ).value,
          evalRight(morph.right)( (morph.right.in: R#In) := inTens.rightProj(input.value) ).value
        )
      }

      def present(morph: InMorph): String = s"(${evalLeft.present(morph.left)} ⊗ ${evalRight.present(morph.right)})"
    }

    // IL ⊕ IR → OL ⊕ OR
    implicit final def eval_biproduct[
      IL, IR, I,
      L <: AnyGraphMorphism, R <: AnyGraphMorphism,
      OL, OR, O
    ](implicit
      inBip:  BiproductImpl[I, IL, IR],
      outBip: BiproductImpl[O, OL, OR],
      evalLeft:  Eval[IL, L, OL],
      evalRight: Eval[IR, R, OR]
    ):  Eval[I, BiproductMorph[L, R], O] =
    new Eval[I, BiproductMorph[L, R], O] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := outBip(
          evalLeft(morph.left)  ( (morph.left.in:  L#In) := inBip.leftProj(input.value) ).value,
          evalRight(morph.right)( (morph.right.in: R#In) := inBip.rightProj(input.value) ).value
        )
      }

      def present(morph: InMorph): String = s"(${evalLeft.present(morph.left)} ⊕ ${evalRight.present(morph.right)})"
    }

    // △: X → X ⊗ X
    implicit final def eval_duplicate[
      I, T <: AnyGraphObject, O
    ](implicit
      outTens: TensorImpl[O, I, I]
    ):  Eval[I, duplicate[T], O] =
    new Eval[I, duplicate[T], O] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := outTens(input.value, input.value)
      }

      def present(morph: InMorph): String = morph.label
    }

    // ▽: X ⊗ X → X
    implicit final def eval_matchUp[
      I, T <: AnyGraphObject, O
    ](implicit
      tensImpl: TensorImpl[I, O, O],
      matchImpl: MatchUpImpl[O]
    ):  Eval[I, matchUp[T], O] =
    new Eval[I, matchUp[T], O] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := matchImpl.matchUp(tensImpl.leftProj(input.value), tensImpl.rightProj(input.value))
      }

      def present(morph: InMorph): String = morph.label
    }

    // X → X ⊕ X
    implicit final def eval_fork[
      I, T <: AnyGraphObject, O
    ](implicit
      outBip: BiproductImpl[O, I, I]
    ):  Eval[I, fork[T], O] =
    new Eval[I, fork[T], O] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := outBip(input.value, input.value)
      }

      def present(morph: InMorph): String = morph.label
    }

    // X ⊕ X → X
    implicit final def eval_merge[
      I, T <: AnyGraphObject, O
    ](implicit
      bipImpl: BiproductImpl[I, O, O],
      mergeImpl: MergeImpl[O]
    ):  Eval[I, merge[T], O] =
    new Eval[I, merge[T], O] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := mergeImpl.merge(bipImpl.leftProj(input.value), bipImpl.rightProj(input.value))
      }

      def present(morph: InMorph): String = morph.label
    }

    // L → L ⊕ R
    implicit final def eval_leftInj[
      L <: AnyGraphObject, R <: AnyGraphObject,
      I, OR, O
    ](implicit
      outBip: BiproductImpl[O, I, OR]
    ):  Eval[I, leftInj[L ⊕ R], O] =
    new Eval[I, leftInj[L ⊕ R], O] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := outBip.leftInj(input.value)
      }

      def present(morph: InMorph): String = morph.label
    }

    // R → L ⊕ R
    implicit final def eval_rightInj[
      L <: AnyGraphObject, R <: AnyGraphObject,
      OL, OR, O
    ](implicit
      outBip: BiproductImpl[O, OL, OR]
    ):  Eval[OR, rightInj[L ⊕ R], O] =
    new Eval[OR, rightInj[L ⊕ R], O] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := outBip.rightInj(input.value)
      }

      def present(morph: InMorph): String = morph.label
    }

    // L ⊕ R → L
    implicit final def eval_leftProj[
      IL, IR, I,
      L <: AnyGraphObject, R <: AnyGraphObject
    ](implicit
      outBip: BiproductImpl[I, IL, IR]
    ):  Eval[I, leftProj[L ⊕ R], IL] =
    new Eval[I, leftProj[L ⊕ R], IL] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := outBip.leftProj(input.value)
      }

      def present(morph: InMorph): String = morph.label
    }

    // L ⊕ R → R
    implicit final def eval_rightProj[
      IL, IR, I,
      L <: AnyGraphObject, R <: AnyGraphObject
    ](implicit
      outBip: BiproductImpl[I, IL, IR]
    ):  Eval[I, rightProj[L ⊕ R], IR] =
    new Eval[I, rightProj[L ⊕ R], IR] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := outBip.rightProj(input.value)
      }

      def present(morph: InMorph): String = morph.label
    }

    // 0 → X
    implicit final def eval_fromZero[
      I, X <: AnyGraphObject, O
    ](implicit
      inZero:  ZeroImpl[I],
      outZero: ZeroImpl[O]
    ):  Eval[I, fromZero[X], O] =
    new Eval[I, fromZero[X], O] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := outZero()
      }

      def present(morph: InMorph): String = morph.label
    }

    // X → 0
    implicit final def eval_toZero[
      I, T, X <: AnyGraphObject, O
    ](implicit
      inZero:  ZeroImpl[I],
      outZero: ZeroImpl[O]
    ):  Eval[I, toZero[X], O] =
    new Eval[I, toZero[X], O] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := outZero()
      }

      def present(morph: InMorph): String = morph.label
    }

    implicit final def eval_inE[
      I, E <: AnyEdge, IE, IV
    ](implicit
      vImpl:  VertexInImpl[E, I, IE, IV]
    ):  Eval[I, inE[E], IE] =
    new Eval[I, inE[E], IE] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := vImpl.inE(input.value, morph.edge)
      }

      def present(morph: InMorph): String = morph.label
    }

    implicit final def eval_inV[
      I, E <: AnyEdge, IE, IV
    ](implicit
      vImpl:  VertexInImpl[E, I, IE, IV]
    ):  Eval[I, inV[E], IV] =
    new Eval[I, inV[E], IV] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        (morph.out: InMorph#Out) := vImpl.inV(input.value, morph.edge)
      }

      def present(morph: InMorph): String = morph.label
    }

    implicit final def eval_outE[
      I, E <: AnyEdge, OE, OV
    ](implicit
      vImpl:  VertexOutImpl[E, I, OE, OV]
    ):  Eval[I, outE[E], OE] =
    new Eval[I, outE[E], OE] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := vImpl.outE(input.value, morph.edge)
      }

      def present(morph: InMorph): String = morph.label
    }



    implicit final def eval_outV[
      I, E <: AnyEdge, OE, OV
    ](implicit
      vImpl:  VertexOutImpl[E, I, OE, OV]
    ):  Eval[I, outV[E], OV] =
    new Eval[I, outV[E], OV] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        (morph.out: InMorph#Out) := vImpl.outV(input.value, morph.edge)
      }

      def present(morph: InMorph): String = morph.label
    }

    implicit final def eval_source[
      E <: AnyEdge, I, S, T
    ](implicit
      eImpl: EdgeImpl[I, S, T]
    ):  Eval[I, source[E], S] =
    new Eval[I, source[E], S] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        (morph.out: InMorph#Out) := eImpl.source(input.value)
      }

      def present(morph: InMorph): String = morph.label
    }

    implicit final def eval_target[
      E <: AnyEdge, I, S, T
    ](implicit
      eImpl: EdgeImpl[I, S, T]
    ):  Eval[I, target[E], T] =
    new Eval[I, target[E], T] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        (morph.out: InMorph#Out) := eImpl.target(input.value)
      }

      def present(morph: InMorph): String = morph.label
    }


    // I → X
    implicit final def eval_fromUnit[
      O <: AnyGraphObject, RawObj, RawUnit
    ](implicit
      unitImpl:  UnitImpl[O, RawObj, RawUnit]
    ):  Eval[RawUnit, fromUnit[O], RawObj] =
    new Eval[RawUnit, fromUnit[O], RawObj] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := unitImpl.fromUnit(input.value, morph.obj)
      }

      def present(morph: InMorph): String = morph.label
    }

    // X → I
    implicit final def eval_toUnit[
      O <: AnyGraphObject, RawObj, RawUnit
    ](implicit
      unitImpl:  UnitImpl[O, RawObj, RawUnit]
    ):  Eval[RawObj, toUnit[O], RawUnit] =
    new Eval[RawObj, toUnit[O], RawUnit] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        morph.out := unitImpl.toUnit(input.value)
      }

      def present(morph: InMorph): String = morph.label
    }


    implicit final def eval_get[
      P <: AnyProperty, RawElem, RawValue
    ](implicit
      propImpl: PropertyImpl[P, RawElem, RawValue]
    ):  Eval[RawElem, get[P], RawValue] =
    new Eval[RawElem, get[P], RawValue] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        (morph.out: InMorph#Out) := propImpl.get(input.value, morph.property)
      }

      def present(morph: InMorph): String = morph.label
    }

    implicit final def eval_lookup[
      P <: AnyProperty, RawElem, RawValue
    ](implicit
      propImpl: PropertyImpl[P, RawElem, RawValue]
    ):  Eval[RawValue, lookup[P], RawElem] =
    new Eval[RawValue, lookup[P], RawElem] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        (morph.out: InMorph#Out) := propImpl.lookup(input.value, morph.property)
      }

      def present(morph: InMorph): String = morph.label
    }


    implicit final def eval_quantify[
      P <: AnyPredicate, RawPred, RawElem
    ](implicit
      predImpl: PredicateImpl[P, RawPred, RawElem]
    ):  Eval[RawElem, quantify[P], RawPred] =
    new Eval[RawElem, quantify[P], RawPred] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        (morph.out: InMorph#Out) := predImpl.quantify(input.value, morph.predicate)
      }

      def present(morph: InMorph): String = morph.label
    }


    implicit final def eval_coerce[
      P <: AnyPredicate, RawPred, RawElem
    ](implicit
      predImpl: PredicateImpl[P, RawPred, RawElem]
    ):  Eval[RawPred, coerce[P], RawElem] =
    new Eval[RawPred, coerce[P], RawElem] {

      def apply(morph: InMorph): OutMorph = { input: Input =>
        (morph.out: InMorph#Out) := predImpl.coerce(input.value)
      }

      def present(morph: InMorph): String = morph.label
    }

  }

}

```


------

### Index

+ src
  + test
    + scala
      + ohnosequences
        + scarph
          + [TwitterQueries.scala][test/scala/ohnosequences/scarph/TwitterQueries.scala]
          + impl
            + [dummyTest.scala][test/scala/ohnosequences/scarph/impl/dummyTest.scala]
            + [dummy.scala][test/scala/ohnosequences/scarph/impl/dummy.scala]
          + [TwitterSchema.scala][test/scala/ohnosequences/scarph/TwitterSchema.scala]
  + main
    + scala
      + ohnosequences
        + scarph
          + [morphisms.scala][main/scala/ohnosequences/scarph/morphisms.scala]
          + [objects.scala][main/scala/ohnosequences/scarph/objects.scala]
          + [evals.scala][main/scala/ohnosequences/scarph/evals.scala]
          + [implementations.scala][main/scala/ohnosequences/scarph/implementations.scala]
          + [schemas.scala][main/scala/ohnosequences/scarph/schemas.scala]
          + syntax
            + [morphisms.scala][main/scala/ohnosequences/scarph/syntax/morphisms.scala]
            + [objects.scala][main/scala/ohnosequences/scarph/syntax/objects.scala]

[test/scala/ohnosequences/scarph/TwitterQueries.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterQueries.scala.md
[test/scala/ohnosequences/scarph/impl/dummyTest.scala]: ../../../../test/scala/ohnosequences/scarph/impl/dummyTest.scala.md
[test/scala/ohnosequences/scarph/impl/dummy.scala]: ../../../../test/scala/ohnosequences/scarph/impl/dummy.scala.md
[test/scala/ohnosequences/scarph/TwitterSchema.scala]: ../../../../test/scala/ohnosequences/scarph/TwitterSchema.scala.md
[main/scala/ohnosequences/scarph/morphisms.scala]: morphisms.scala.md
[main/scala/ohnosequences/scarph/objects.scala]: objects.scala.md
[main/scala/ohnosequences/scarph/evals.scala]: evals.scala.md
[main/scala/ohnosequences/scarph/implementations.scala]: implementations.scala.md
[main/scala/ohnosequences/scarph/schemas.scala]: schemas.scala.md
[main/scala/ohnosequences/scarph/syntax/morphisms.scala]: syntax/morphisms.scala.md
[main/scala/ohnosequences/scarph/syntax/objects.scala]: syntax/objects.scala.md