package ohnosequences.scarph

object monoidalStructures {

  import graphTypes._

  sealed trait AnyTensorObj extends AnyGraphObject {

    type Left <: AnyGraphObject
    val  left: Left

    type Right <: AnyGraphObject
    val  right: Right
  }

  case class TensorObj[L <: AnyGraphObject, R <: AnyGraphObject](val left: L, val right: R) extends AnyTensorObj {

    type Left = L
    type Right = R

    lazy val label = s"(${left.label} ⊗ ${right.label})"
  }

  sealed trait AnyTensorMorph extends AnyGraphMorphism {

    type Left <: AnyGraphMorphism
    val  left: Left

    type Right <: AnyGraphMorphism
    val  right: Right

    type In  <: TensorObj[Left#In, Right#In]
    type Out <: TensorObj[Left#Out, Right#Out]

    type Dagger <: TensorMorph[Left#Dagger, Right#Dagger]
  }

  case class TensorMorph[L <: AnyGraphMorphism, R <: AnyGraphMorphism](val left: L, val right: R) 
  extends AnyGraphMorphism {

    type Left = L
    type Right = R

    type In  = TensorObj[Left#In, Right#In]
    lazy val in = TensorObj(left.in, right.in)
    type Out = TensorObj[Left#Out, Right#Out]
    lazy val out = TensorObj(left.out, right.out)

    type Self = TensorMorph[L,R]

    type Dagger = TensorMorph[L#Dagger, R#Dagger]
    lazy val dagger = TensorMorph(left.dagger, right.dagger)
  }


  case object unit extends AnyGraphObject {

    type     Self = this.type
    lazy val self = this: Self

    lazy val label = this.toString
  }
  type unit = unit.type

  implicit def leftUnit[T <: AnyGraphType](it: TensorObj[unit,T]): T = it.right
  implicit def rightUnit[T <: AnyGraphType](it: TensorObj[T,unit]): T = it.left


  /*
  ## Biproduct
  */

  sealed trait AnyBiproductObj extends AnyGraphObject {

    type Left <: AnyGraphObject
    val  left: Left

    type Right <: AnyGraphObject
    val  right: Right
  }

  case class BiproductObj[L <: AnyGraphObject, R <: AnyGraphObject](val left: L, val right: R)
  extends AnyBiproductObj {

    type Left = L
    type Right = R

    type Self = BiproductObj[L,R]
  }

  sealed trait AnyBiproductMorph extends AnyGraphMorphism { bp =>

    type Left <: AnyGraphMorphism
    val  left: Left

    type Right <: AnyGraphMorphism
    val  right: Right

    type In  <: BiproductObj[Left#In, Right#In]
    type Out <: BiproductObj[Left#Out, Right#Out]

    type Dagger <: AnyBiproductMorph {

      type Left = bp.Left#Dagger
      type Right = bp.Right#Dagger
    }
  }

  case class BiproductMorph[L <: AnyGraphMorphism, R <: AnyGraphMorphism](val left: L, val right: R) 
  extends AnyBiproductMorph {

    type Left = L
    type Right = R

    type     In = BiproductObj[Left#In, Right#In]
    lazy val in = BiproductObj(left.in, right.in): In

    type     Out = BiproductObj[Left#Out, Right#Out]
    lazy val out = BiproductObj(left.out, right.out): Out

    type Dagger = BiproductMorph[Left#Dagger, Right#Dagger]
    lazy val dagger = BiproductMorph(left, right)

    lazy val label = s"(${left.label} ⊕ ${right.label})"
  }

  case object zero extends AnyGraphObject {

    type     Self = zero
    lazy val self = zero

    lazy val label = this.toString
  }
  type zero = zero.type

  implicit def leftZero[T <: AnyGraphType](it: BiproductObj[zero, T]): T = it.right
  implicit def rightZero[T <: AnyGraphType](it: BiproductObj[T, zero]): T = it.left
}