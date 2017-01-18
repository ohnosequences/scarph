package ohnosequences

package object scarph {

  /* ## Type aliases */

  /* ### Objects */
  // \otimes symbol: f ⊗ s: F ⊗ S
  type ⊗[F <: AnyGraphObject, S <: AnyGraphObject] = TensorObj[F, S]

  type unit = TensorUnit.type
  val  unit: unit = TensorUnit

  // \oplus symbol: f ⊕ s: F ⊕ S
  type ⊕[F <: AnyGraphObject, S <: AnyGraphObject] = BiproductObj[F, S]

  type zero = BiproductZero.type
  val  zero: zero = BiproductZero


  /* ### Morphisms */
  // strict:
  type ==>[A <: AnyGraphObject, B <: AnyGraphObject] = AnyGraphMorphism { type In = A; type Out = B }

  // non-strict:
  type -->[A <: AnyGraphObject, B <: AnyGraphObject] = AnyGraphMorphism { type In <: A; type Out <: B }

  type DaggerOf[M <: AnyGraphMorphism] = M#Out --> M#In

  type >=>[F <: AnyGraphMorphism, S <: AnyGraphMorphism { type In = F#Out }] = Composition[F, S]


  /* ### Properties */
  type get[P <: AnyProperty] = outV[P]
  def get[P <: AnyProperty](p: P): get[P] = outV[P](p)

  type lookup[P <: AnyProperty] = inV[P]
  def lookup[P <: AnyProperty](p: P): lookup[P] = inV[P](p)


  /* ## Minimal necessary syntax */

  // implicit def graphObjectOps[O <: AnyGraphObject](o: O):
  //   GraphObjectOps[O] =
  //   GraphObjectOps[O](o)
  implicit class GraphObjectOps[O <: AnyGraphObject](val obj: O) extends AnyVal {

    def ⊗[S <: AnyGraphObject](other: S): O ⊗ S = TensorObj(obj, other)
    def ⊕[S <: AnyGraphObject](other: S): O ⊕ S = BiproductObj(obj, other)
  }

  // implicit def graphMorphismOps[M <: AnyGraphMorphism](m: M):
  //   GraphMorphismOps[M] =
  //   GraphMorphismOps[M](m)
  implicit class GraphMorphismOps[F <: AnyGraphMorphism](val f: F) extends AnyVal {

    def >=>[S <: AnyGraphMorphism { type In = F#Out }](s: S): F >=> S = Composition(f, s)

    def ⊗[S <: AnyGraphMorphism](q: S): TensorMorph[F, S] = TensorMorph(f, q)
    def ⊕[S <: AnyGraphMorphism](q: S): BiproductMorph[F, S] = BiproductMorph(f, q)
  }

}
