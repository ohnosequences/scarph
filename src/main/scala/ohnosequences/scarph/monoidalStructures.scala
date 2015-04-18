package ohnosequences.scarph

object monoidalStructures {

  import graphTypes._


  /* ## Tensor product */
  sealed trait AnyTensorObj extends AnyGraphObject {

    type Left <: AnyGraphObject
    val  left: Left

    type Right <: AnyGraphObject
    val  right: Right
  }

  case class TensorObj[L <: AnyGraphObject, R <: AnyGraphObject]
    (val left: L, val right: R) extends AnyTensorObj {

    type Left = L
    type Right = R

    lazy val label = s"(${left.label} ⊗ ${right.label})"
  }

  // \otimes symbol: f ⊗ s: F ⊗ S
  type ⊗[F <: AnyGraphObject, S <: AnyGraphObject] = TensorObj[F, S]


  sealed trait AnyTensorMorph extends AnyGraphMorphism { tensor =>

    type Left <: AnyGraphMorphism
    val  left: Left

    type Right <: AnyGraphMorphism
    val  right: Right

    type In  <: AnyTensorObj { type Left = tensor.Left#In; type Right = tensor.Right#In }
    type Out <: AnyTensorObj { type Left = tensor.Left#Out; type Right = tensor.Right#Out }

    type Dagger <: AnyTensorMorph { 
      type Left = tensor.Left#Dagger; 
      type Right = tensor.Right#Dagger
    }
  }

  case class TensorMorph[L <: AnyGraphMorphism, R <: AnyGraphMorphism]
    (val left: L, val right: R) extends AnyTensorMorph { tensor =>

    type Left = L
    type Right = R

    type     In = TensorObj[Left#In, Right#In]
    lazy val in = TensorObj(left.in, right.in): In

    type     Out = TensorObj[Left#Out, Right#Out]
    lazy val out = TensorObj(left.out, right.out): Out

    type     Dagger = TensorMorph[Left#Dagger, Right#Dagger]
    lazy val dagger = TensorMorph(left.dagger: Left#Dagger, right.dagger: Right#Dagger)

    lazy val label = s"(${left.label} ⊗ ${right.label})"
  }


  case object unit extends AnyGraphObject {

    lazy val label = this.toString
  }
  type unit = unit.type



  /* ## Biproduct */
  sealed trait AnyBiproductObj extends AnyGraphObject {

    type Left <: AnyGraphObject
    val  left: Left

    type Right <: AnyGraphObject
    val  right: Right
  }

  case class BiproductObj[L <: AnyGraphObject, R <: AnyGraphObject]
    (val left: L, val right: R) extends AnyBiproductObj {

    type Left = L
    type Right = R

    lazy val label = s"(${left.label} ⊕ ${right.label})"
  }

  // \oplus symbol: f ⊕ s: F ⊕ S
  type ⊕[F <: AnyGraphObject, S <: AnyGraphObject] = BiproductObj[F, S]

  sealed trait AnyBiproductMorph extends AnyGraphMorphism { biprod =>

    type Left <: AnyGraphMorphism
    val  left: Left

    type Right <: AnyGraphMorphism
    val  right: Right

    type In  <: BiproductObj[Left#In, Right#In]
    type Out <: BiproductObj[Left#Out, Right#Out]

    type Dagger <: AnyBiproductMorph {
      type Left = biprod.Left#Dagger 
      type Right = biprod.Right#Dagger
    }
  }

  case class BiproductMorph[L <: AnyGraphMorphism, R <: AnyGraphMorphism]
    (val left: L, val right: R) extends AnyBiproductMorph { biprod =>

    type Left = L
    type Right = R

    type     In = BiproductObj[Left#In, Right#In]
    lazy val in = BiproductObj(left.in, right.in): In

    type     Out = BiproductObj[Left#Out, Right#Out]
    lazy val out = BiproductObj(left.out, right.out): Out

    type     Dagger = BiproductMorph[Left#Dagger, Right#Dagger]
    lazy val dagger = BiproductMorph(left.dagger: Left#Dagger, right.dagger: Right#Dagger)

    lazy val label = s"(${left.label} ⊕ ${right.label})"
  }


  case object zero extends AnyGraphObject {

    lazy val label = this.toString
  }
  type zero = zero.type

}
